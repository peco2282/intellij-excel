@file:Suppress("InvalidBundleOrProperty")

package com.github.peco2282.excel.workbook.file

import com.github.peco2282.excel.Constant
import com.github.peco2282.excel.Utils
import com.github.peco2282.excel.workbook.CellData
import com.github.peco2282.excel.tasks.ProgressTaskModal
import com.github.peco2282.excel.workbook.DocumentNodes
import com.github.peco2282.excel.workbook.DocumentNodes.SharedStringTable.StringIndex
import com.github.peco2282.excel.workbook.ExcelWorkbookState
import com.github.peco2282.excel.workbook.SheetMap
import com.github.peco2282.excel.workbook.component.SheetComponent
import com.github.peco2282.excel.workbook.component.WorkBookComponent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.AsyncFileEditorProvider
import com.intellij.openapi.fileEditor.AsyncFileEditorProvider.Builder
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.JDOMUtil
import com.intellij.openapi.vfs.VirtualFile
import org.jdom.Element
import org.jdom.JDOMException
import org.jdom.Namespace
import org.jetbrains.annotations.NotNull
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.HashMap


//import com.intellij.ide.FileEditorProvider

@Suppress(
  "unused",
  "UnstableApiUsage",
  "PrivatePropertyName"
)
class ExcelFileEditorProvider : AsyncFileEditorProvider, DumbAware {
  private val LOGGER = Logger.getInstance(this::class.java)

  /**
   * The method is expected to run fast.
   *
   * @param file file to be tested for acceptance.
   * @return `true` if provider can create valid editor for the specified `file`.
   */
  override fun accept(project: Project, file: VirtualFile): Boolean {
    if (file.name.startsWith('~')) return false // cache file
    return when (file.fileType) {
      ExcelFileType.INSTANCE, ExcelFileType.XLSX_INSTANCE, ExcelFileType.XLS_INSTANCE -> true
      else -> false
    }
  }

  /**
   * Creates editor for the specified file.
   *
   *
   * This method is called only if the provider has accepted this file (i.e. method [.accept] returned
   * `true`).
   * The provider should return only valid editor.
   *
   * @return created editor for specified file.
   */
  override fun createEditor(project: Project, file: VirtualFile): FileEditor {
    return createEditorAsync(project, file).build()
  }

  /**
   * @return editor type ID for the editors created with this FileEditorProvider. Each FileEditorProvider should have
   * a unique nonnull ID. The ID is used for saving/loading of EditorStates.
   */
  override fun getEditorTypeId(): String {
    return "excel-editor"
//    TODO("Not yet implemented")
  }

  /**
   * @return a policy that specifies how an editor created via this provider should be opened.
   * @see FileEditorPolicy.NONE
   *
   * @see FileEditorPolicy.HIDE_DEFAULT_EDITOR
   *
   * @see FileEditorPolicy.HIDE_OTHER_EDITORS
   *
   * @see FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR
   *
   * @see FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR
   */
  override fun getPolicy(): FileEditorPolicy {
    return FileEditorPolicy.HIDE_DEFAULT_EDITOR
  }

  /**
   * This method is intended to be called from background thread. It should perform all time-consuming tasks required to build an editor,
   * and return a builder instance that will be called in EDT to create UI for the editor.
   *
   *
   * Currently, this method is called from a background thread only when editors are reopened on IDE startup. In other cases, it's still
   * invoked on EDT, so executing time-consuming tasks in it will block the UI.
   */
  @NotNull
  override fun createEditorAsync(@NotNull project: Project, @NotNull virtualFile: VirtualFile): Builder {
    return object : Builder() {
      override fun build(): FileEditor {

        val values = sheetMaps(virtualFile, project)
        if (values.size != 0) Utils.information(
          "Selected file opened",
          "message.success.display"
        )
        val sheets = ArrayList<SheetComponent>()
        values.forEach { sheets.add(SheetComponent(it.id, it.name, it.data)) }
        return ExcelFileEditor(WorkBookComponent(sheets).toComponent(), virtualFile)
      }
    }
  }


  private fun sheetMaps(virtualFile: VirtualFile, project: Project): ArrayList<SheetMap> {
    ExcelWorkbookState.setProject(project)
    try {
      val files: HashMap<String, File> =
        if (ExcelWorkbookState.has(virtualFile.path)) {
          val uuid = ExcelWorkbookState.get(virtualFile.path)!!
          if (Utils.exist(destDirectory(uuid))) {
            LOGGER.info("savedUUID: $uuid")
            Utils.walk(destDirectory(uuid))
          } else HashMap()
        } else {
          val uuid = UUID.nameUUIDFromBytes(virtualFile.path.toByteArray())
          LOGGER.info("not contain. create at " + destDirectory(uuid.toString()))
          val modal = ProgressTaskModal(project) {
            Utils.unzip(
              virtualFile.path,
              destDirectory(uuid.toString())
            )
          }
          ExcelWorkbookState.put(virtualFile.path, uuid.toString())
          ProgressManager.getInstance().run(modal)
          modal.lastResult()
        }
      ExcelWorkbookState.getAll().forEach { println("${it.key} = ${it.value}") }
      val file = files["workbook.xml"]
      if (file == null) {
        Utils.error("Workbook file cannot find in target file.", "message.notfound.workbook")
        return ArrayList()
      }
      val elem = file.let { JDOMUtil.load(it) }
      elem.getAdditionalNamespaces().forEach { namespace: Namespace ->
        Constant.NAMESPACES[namespace.prefix] = namespace
      }
      var sheets = Constant.EMPTY_ELEMENT
      elem.children.forEach {
        if (it.name == "sheets") sheets = it
      }
      val sheetList: MutableList<DocumentNodes.Sheets.Sheet> = ArrayList()
      sheets.children.forEach { it: Element ->
        sheetList.add(
          DocumentNodes.Sheets.Sheet(it)
        )
      }
      val map: MutableMap<Int, List<DocumentNodes.SheetData.Row>> = HashMap()
      val c = AtomicInteger()
      val sheet = ArrayList<SheetMap>()
      sheetList.forEach { it: DocumentNodes.Sheets.Sheet ->
        try {
          JDOMUtil.load(files[it.sheetFileName()]!!).children.forEach { element: Element ->
            if (element.name == "sheetData") {
              map[c.get()] = parseRowList(element)
              sheet.add(SheetMap(it.name, it.sheetId, parseCellData(element)))
            }
          }
        } catch (ex: JDOMException) {
          Utils.error(ex, "message.jdom.parser")
        } catch (ex: IOException) {
          Utils.error(ex, "message.file.open")
        } finally {
          c.getAndIncrement()
        }
      }
      val indexList: MutableList<StringIndex> = ArrayList()
      val sst = JDOMUtil.load(files["sharedStrings.xml"]!!)
      sst.children.forEach { element: Element? ->
        indexList.add(
          StringIndex(
            element
          )
        )
      }
      Constant.TABLE.update(indexList)
      return sheet
    } catch (ex: NullPointerException) {
      LOGGER.error(ex)
    } catch (ex: Throwable) {
      Utils.error(ex, "message.error")
    }
    return ArrayList(0)
    // TODO: insert action logic here
  }

//  private fun checkCahce(path: @NonNls String): Boolean = ExcelWorkBookState.has(path)

  private fun destDirectory(dirName: String): String {
    return ExcelWorkbookState.getSavedPath(dirName)
  }

  private fun parseCellData(element: Element): ArrayList<CellData> {
    val cellDatas = ArrayList<CellData>()
    element.children.forEach {
      if (it.name == "row") {
        it.children.forEach { c ->
          run {
            var reference = false
            var cell = ""
            var rCell = ""
            var f = ""
            var v = ""
            // row
            it.attributes.forEach { attr ->
              when (attr.name) {
                "r" -> rCell = attr.value
              }
            }
            //cell
            c.attributes.forEach { attr ->
              run {
                when (attr.name) {
                  "t" -> reference = (attr.value != null || attr.value != "")
                  "r" -> cell = attr.value //(attr.value != null || attr.value != "")
                }
              }
            }
            c.children.forEach { e ->
              run {
                when (e.name) {
                  "f" -> f = e.value
                  "v" -> v = e.value
                }
              }
            }
            cellDatas.add(CellData(cell.replace(rCell, ""), rCell.toInt(), v, reference, f))
          }
        }
      }
    }
    return cellDatas
  }

  private fun parseRow(element: Element): DocumentNodes.SheetData.Row {
    return DocumentNodes.SheetData.Row(element, parseCell(element))
  }

  private fun parseRowList(element: Element): List<DocumentNodes.SheetData.Row> {
    val rows: MutableList<DocumentNodes.SheetData.Row> = ArrayList()
    element.children.forEach { e: Element ->
      rows.add(parseRow(e))
    }
    return rows
  }

  private fun parseCell(element: Element): List<DocumentNodes.SheetData.Row.Cell> {
    val cells: MutableList<DocumentNodes.SheetData.Row.Cell> = ArrayList()
    element.children.forEach { e: Element? ->
      cells.add(
        DocumentNodes.SheetData.Row.Cell(
          e
        )
      )
    }
    return cells
  }
}
