package com.github.peco2282.excel.holder

@JvmRecord
data class CellHolder(val row: String, val col: String): Holder<String, String> {

  override fun getLeft(): String {
    return this.row
  }

  override fun getRight(): String {
    return this.col
  }

  override fun apply(l: String): String {
    return if (row == l) col else ""
  }
}