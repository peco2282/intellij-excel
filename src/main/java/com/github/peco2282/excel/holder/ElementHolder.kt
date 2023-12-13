package com.github.peco2282.excel.holder

import org.jdom.Element
import org.jdom.Namespace

@JvmRecord
data class ElementHolder(val namespace: Namespace, @JvmField val element: Element): Holder<Namespace, Element> {
  override fun getLeft(): Namespace {
    return this.namespace
  }

  override fun getRight(): Element {
    return this.element
  }

  override fun apply(l: Namespace): Element {
    return if (l == namespace) element else Element("")
  }
}
