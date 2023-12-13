package com.github.peco2282.excel.workbook

import com.github.peco2282.excel.Constant
import com.github.peco2282.excel.Utils
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.Service.Level
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Property
import com.intellij.util.xmlb.annotations.Transient
import com.intellij.util.xmlb.annotations.XMap
import java.io.File
import java.io.Serial
import java.io.Serializable

@Service(Level.APP)
@State(name = "excel", storages = [Storage(Constant.STATE_PATH)])
class ExcelWorkbookState : PersistentStateComponent<ExcelWorkbookState>,
  Serializable {
  @Suppress("PropertyName")
  @Transient
  val LOGGER: Logger = Logger.getInstance(this::class.java)

  /**
   * This method is called when a new component state is loaded.
   * The method can and will be called several times if config files are externally changed while the IDE is running.
   *
   *
   * State object should be used directly, defensive copying is not required.
   *
   * @param state loaded component state
   * @see com.intellij.util.xmlb.XmlSerializerUtil.copyBean
   */
  override fun loadState(state: ExcelWorkbookState) {
    XmlSerializerUtil.copyBean(state, this)
  }

  @Property
//  @MapAnnotation//(keyAttributeName = "ts", valueAttributeName = "value", entryTagName = "directories")
  @XMap(propertyElementName = "excel", keyAttributeName = "path", valueAttributeName = "uuid")
  private val directories = HashMap<String, String>()

  @Property
  private val containPath: String =
    Utils.default(System.getProperty("idea.config.path"), System.getProperty("user.home"))

  fun getPath() = containPath

  fun getSavedPath(uuid: String) = Utils.join(
    File.separator,
    containPath,
    "Jetbrains",
    "plugin",
    "cache",
    "excel",
    uuid
  )

  fun put(key: String, value: String) {
    if (has(key)) LOGGER.warn("Already exists as : " + key + " = " + this.directories[key])
    else {
      if (File(key).exists()) this.directories[key] = value
      else LOGGER.warn("")
    }
  }
//  fun add(key: String, value: String) = put(key, value)

  fun has(key: String) = this.directories.containsKey(key)
  fun get(key: String): String? = this.directories[key]
  fun pop(key: String): String? = this.directories.remove(key)

  fun getAll(): HashMap<String, String> = this.directories
  fun removeAll() = this.directories.clear()

  companion object {
    fun setProject(project: Project) {
      ExcelWorkbookState.project = project
    }

    private var project: Project? = null

    @Suppress("RetrievingService")
    // TODO: CHANGE SERVICE TYPE
    private fun getInstance(): ExcelWorkbookState? =
      project!!.getService(ExcelWorkbookState::class.java)

    fun put(key: String, value: String) = getInstance()!!.put(key, value)
    fun has(key: String) = getInstance()!!.has(key)
    fun get(key: String) = getInstance()!!.get(key)
    fun getAll() = getInstance()!!.getAll()
    fun pop(key: String) = getInstance()!!.pop(key)
    fun removeAll() = getInstance()!!.removeAll()

    fun getPath() = getInstance()!!.getPath()
    fun getSavedPath(uuid: String) = getInstance()!!.getSavedPath(uuid)


    @Serial
    private val serialVersionUID: Long = 123456789L
  }

  /**
   * @return a component state. All properties, public and annotated fields are serialized.
   * Only values which differ from the default (i.e. the value of newly instantiated class) are serialized.
   * `null` value indicates that the returned state won't be stored, as a result previously stored state will be used.
   * @see com.intellij.util.xmlb.XmlSerializer
   */
  override fun getState(): ExcelWorkbookState {
    return this
  }
}