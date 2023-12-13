package com.github.peco2282.excel.workbook.file

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import java.awt.Component
import java.beans.PropertyChangeListener
import javax.swing.JComponent

class ExcelFileEditor(private val component: Component, private val virtualFile: VirtualFile) : UserDataHolderBase(),
  FileEditor {
  /**
   * Usually not invoked directly, see class javadoc.
   */
  override fun dispose() = Unit

  /**
   * Returns a component which represents the editor in UI.
   */
  override fun getComponent(): JComponent = component as JComponent

  /**
   * Returns a component to be focused when the editor is opened.
   */
  override fun getPreferredFocusedComponent(): JComponent = getComponent()

  /**
   * Returns editor's name - a string that identifies the editor among others
   * (e.g.: "GUI Designer" for graphical editing and "Text" for textual representation of a GUI form editor).
   */
  override fun getName(): String = "Excel File"

  /**
   * Applies a given state to the editor.
   */
  override fun setState(state: FileEditorState) = Unit

  /**
   * Returns `true` when editor's content differs from its source (e.g. a file).
   */
  override fun isModified(): Boolean = false

  /**
   * An editor is valid if its contents still exist.
   * For example, an editor displaying the contents of some file stops being valid if the file is deleted.
   * An editor can also become invalid after being disposed of.
   */
  override fun isValid() = true

  /**
   * Adds specified listener.
   */
  override fun addPropertyChangeListener(listener: PropertyChangeListener) = Unit

  /**
   * Removes specified listener.
   */
  override fun removePropertyChangeListener(listener: PropertyChangeListener) = Unit


  /**
   * Returns the file for which {@link FileEditorProvider#createEditor} was called.
   * The default implementation is temporary, and shall be dropped in the future.
   */
  override fun getFile() = EXCEL_FILE.get(this) ?: virtualFile


  @Suppress("PrivatePropertyName")
  private val EXCEL_FILE = Key.create<VirtualFile>("EXCEL_FILE").also { it.set(this, virtualFile) }
}