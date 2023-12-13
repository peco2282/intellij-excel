package com.github.peco2282.excel.workbook

import com.github.peco2282.excel.Constant

class CellData(
  private val row: String,
  private val col: Int,
  private val data: String,
  private val referenced: Boolean = false,
  private val func: String = ""
) :
  Comparable<CellData> {

  /**
   * Compares this object with the specified object for order. Returns zero if this object is equal
   * to the specified [other] object, a negative number if it's less than [other], or a positive number
   * if it's greater than [other].
   */
  override fun compareTo(other: CellData): Int {
    // int: 48 ~ 57
    // upper: 65 ~ 90
    // lower: 97 ~ 122
    return if (value(other.row) + other.col > value(row) + col) 1 else 0
  }

  fun value(string: String): Int {
    var value = 0
    for (c in string.uppercase()) value += c.code
    return value
  }

  fun getRowAsInt(): Int {
    var value = 0
    if (row.isEmpty()) return 0
    return when (row.length) {
      1 -> this.row.uppercase()[0].code - 65
      else -> {
        for (i in row.indices) {
          value += if (i == 0) this.row.uppercase()[i].code - 65
          else this.row.uppercase()[i].code
        }
        value
      }
    }
  }

  fun getValue(): String {
    return try {
      if (referenced) Constant.TABLE[data.toInt()].value else data
    } catch (ex: IndexOutOfBoundsException) {
      "UNRESOLVED"
    } catch (ex: NumberFormatException) {
      if (data == "") "" else "NULL"
    }
  }

  fun getFunction() = func
  fun getColumn() = col - 1
}