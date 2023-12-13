package com.github.peco2282.excel.workbook.component

import com.github.peco2282.excel.workbook.CellData
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import javax.swing.table.DefaultTableModel

class SheetComponent(
  private val sheetID: String,
  val sheetName: String, /* list[cells] */
  private val cells: List<CellData>
) : Comparable<SheetComponent>, IComponent {
  private var row: Int = 0
  private var col: Int = 0

  init {
    cells.forEach {
      col = maxOf(col, it.getColumn())
      row = maxOf(row, it.getRowAsInt())
    }
  }

  override fun toComponent(): JBScrollPane {
    val table = JBTable(DefaultTableModel(col + 1, row + 1))
    table.autoResizeMode = JBTable.AUTO_RESIZE_OFF
    table.name = sheetName
    cells.forEach {
      table.setValueAt(it.getValue(), it.getColumn(), it.getRowAsInt())
    }
    return JBScrollPane(table)
  }

  /**
   * Compares this object with the specified object for order. Returns zero if this object is equal
   * to the specified [other] object, a negative number if it's less than [other], or a positive number
   * if it's greater than [other].
   */
  override fun compareTo(other: SheetComponent): Int {
    return Integer.parseInt(this.sheetID).compareTo(Integer.parseInt(other.sheetID))
  }
}