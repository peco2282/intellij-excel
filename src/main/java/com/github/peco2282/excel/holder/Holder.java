package com.github.peco2282.excel.holder;

import org.jetbrains.annotations.NotNull;

public interface Holder<L, R> {
  @NotNull L getLeft();
  @NotNull R getRight();
  @NotNull R apply(@NotNull L l);
  default L getKey() {
    return getLeft();
  }
  default R getValue() {
    return getRight();
  }
}
