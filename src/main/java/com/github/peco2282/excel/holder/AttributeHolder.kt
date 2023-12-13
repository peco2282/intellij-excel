package com.github.peco2282.excel.holder

import org.jdom.Attribute
import org.jdom.Namespace

@JvmRecord
data class AttributeHolder(val ns: Namespace, val attribute: Attribute) : Holder<Namespace, Attribute> {
  private fun attributeValue(): String {
    return attribute.value
  }

  override fun getLeft(): Namespace {
    return this.ns
  }

  override fun getRight(): Attribute {
    return this.attribute
  }

  override fun apply(l: Namespace): Attribute {
    return if (l == ns) attribute else Attribute("", "")
  }

  override fun toString(): String {
    return "namespace = ${ns.prefix}, attribute ~ ${attributeValue()}"
  }
}
