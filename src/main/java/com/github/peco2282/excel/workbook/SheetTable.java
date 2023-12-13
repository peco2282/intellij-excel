package com.github.peco2282.excel.workbook;

import com.github.peco2282.excel.workbook.DocumentNodes.SharedStringTable.StringIndex;

import java.util.List;

public class SheetTable {
  private List<StringIndex> strings;

  public SheetTable(List<StringIndex> strings) {
    this.strings = strings;
  }

  public void update(List<StringIndex> update) {
    this.strings = update;
  }

  public StringIndex get(int count) throws IndexOutOfBoundsException {
    return this.strings.get(count);
  }
}
