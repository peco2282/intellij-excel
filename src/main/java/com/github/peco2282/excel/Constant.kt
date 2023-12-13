package com.github.peco2282.excel

import com.github.peco2282.excel.workbook.SheetTable
import com.intellij.openapi.util.IconLoader
import org.jdom.Element
import org.jdom.Namespace
import java.io.File
import javax.swing.Icon
import java.util.function.Function

object Constant {
  val TABLE = SheetTable(ArrayList())

  val EMPTY_ELEMENT = Element("EMPTY")

  val NAMESPACES: HashMap<String, Namespace> = HashMap()

  val EXCEL_ICON: Icon = IconLoader.getIcon("icon/excel.png", Constant::class.java)

  val WORKSHEET =
    Function<String, File> { num: String -> Script("worksheets" + File.separator + "sheet" + num + ".xml") }


  // primitive and String
  const val RESOURCE_BUNDLE = "message/messages"
  const val STATE_PATH = "exceldata.xml"
}