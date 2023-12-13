package com.github.peco2282.excel.workbook.component

import com.intellij.ui.components.JBTabbedPane
import java.util.stream.Collectors

class WorkBookComponent(@Suppress("LocalVariableName") _components: List<SheetComponent>) : IComponent {
  private val components = _components.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList())
  override fun toComponent(): JBTabbedPane {
    val pane = JBTabbedPane(JBTabbedPane.BOTTOM)
    components.forEach { pane.add(it.sheetName, it.toComponent()) }
    return pane
  }
}
