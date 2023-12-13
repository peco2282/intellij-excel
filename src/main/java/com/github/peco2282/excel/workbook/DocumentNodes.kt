package com.github.peco2282.excel.workbook

import com.github.peco2282.excel.Constant
import com.github.peco2282.excel.annotations.*
import com.github.peco2282.excel.holder.AttributeHolder
import com.github.peco2282.excel.holder.CellHolder
import com.github.peco2282.excel.holder.ElementHolder
import org.jdom.Namespace
import java.io.File

private typealias JDomElement = org.jdom.Element
internal interface Node {
  companion object {
    val attributes: MutableMap<String, AttributeHolder> = HashMap()
    val elements: MutableMap<String, ElementHolder> = HashMap()
    val EMPTY = JDomElement("body", Namespace.getNamespace("w"))
  }
}

@Suppress("UNUSED")
class DocumentNodes {
  class Sheets(element: JDomElement?, val sheets: Map<String, String>) : NodeChild(element) {
    class Sheet(element: JDomElement?) : NodeChild(element), Comparable<Sheet> {
      @Attribute("name")
      var name: String = ""
        private set

      @Attribute("sheetId")
      var sheetId: String = ""
        private set

      @Attribute("id")
      var id: String = ""
        private set

      init {
        element?.attributes?.forEach {
          when (it.name) {
            "name" -> name = it.value
            "sheetId" -> sheetId = it.value
            "id" -> id = it.value
          }
        }
      }

      fun sheetPath(): String = sheetFile().path // "worksheets${File.separator}sheet${sheetId}.xml"
      @Suppress("MemberVisibilityCanBePrivate")
      fun sheetFile(): File = Constant.WORKSHEET.apply(sheetId)
      fun sheetName() = "worksheet$sheetId"
      fun sheetFileName() = "sheet$sheetId.xml"

      /**
       * Compares this object with the specified object for order. Returns zero if this object is equal
       * to the specified [other] object, a negative number if it's less than [other], or a positive number
       * if it's greater than [other].
       */
      override fun compareTo(other: Sheet): Int {
        return Integer.parseInt(sheetId).compareTo(Integer.parseInt(other.sheetId))
      }
    }
  }

  class SheetData(element: JDomElement?, @Element("", true) val rows: List<Row>) : NodeChild(element) {
    class Row(element: JDomElement?, val cells: List<Cell>) : NodeChild(element) {
      @Attribute("r")
      var row = ""
        private set

      @Attribute("spans")
      var span = ""
        private set

      class Cell(element: JDomElement?) : NodeChild(element) {
        private val cell: CellHolder

        @Attribute("r")
        var reference = ""
          private set

        @Attribute("s")
        var style = ""
          private set

        @Attribute("t")
        var table = ""
          private set

        @ValuableTag("f")
        var function = ""
          private set

        @ValuableTag("v")
        var value = ""
          private set

        private fun usingTable() = table != ""

        private fun getString(): String {
          if (usingTable()) return Constant.TABLE[value.toInt()].value
          throw UnsupportedOperationException("this cell does not use `sst`.")
        }

        fun getReferencedValue(): String {
          return if (usingTable()) getString() else value
        }

        init {
          element?.attributes?.forEach {
            when (it.name) {
              "r" -> reference = it.value
              "s" -> style = it.value
              "t" -> table = it.value
            }
          }
          element?.children?.forEach {
            when (it.name) {
              "f" -> function = it.value
              "v" -> value = it.value
            }
          }
          val cells = reference.split(Regex("(?<=\\D)(?=\\d)"))
          if (cells.size != 2) throw RuntimeException("not 2 $cells")
          cell = CellHolder(cells[0], cells[1])
        }
      }
    }
  }

  class DefinedNames(element: JDomElement?, val names: List<DefinedName>) : NodeChild(element) {
    class DefinedName(element: JDomElement?) : NodeChild(element) {
      @Attribute("localSheetId")
      var localSheetId: String = ""
        private set

      @Attribute("hidden")
      var hiddenValue: String = ""
        private set

      @Value
      var text: String = ""
        private set

      fun hidden() = hiddenValue == "1"

      init {
        if (element != null) {
          element.attributes?.forEach {
            when (it.name) {
              "localSheetId" -> localSheetId = it.value
              "hidden" -> hiddenValue = it.value
            }
          }
          text = element.text
        }
      }

      fun text() {
        val lists = text.split("!")
        if (lists.size != 2) return
        val targets = lists[1].split(":")
        if (targets.size != 2) return
      }
    }
  }

  @Tag("sst")
  @Suppress("UNUSED_PARAMETER")
  class SharedStringTable(element: JDomElement, indexes: List<StringIndex>) : NodeRoot(element) {
    var count: String = ""
    var uique: String = ""

    class StringIndex(element: JDomElement?) : NodeChild(element) {
      private var count = -1

      @ValuableTag("t")
      var value = ""
        private set

      init {
        element?.children?.forEach {
          if (it.name == "t") {
            value = it.value
          }
        }
        count = time
        time++
      }

      companion object {
        var time = 0
      }
    }
  }
}

abstract class NodeChild(element: JDomElement?) : Node {
  private val element: JDomElement

  init {
    this.element = element ?: Node.EMPTY
    this.element.attributes.forEach {
      Node.attributes[it.name] = AttributeHolder(it.namespace, it)
    }
    this.element.children.forEach {
      Node.elements[it.name] = ElementHolder(it.namespace, it)
    }
  }

  override fun toString(): String {
    val builder = StringBuilder()
    val fields = this.javaClass.declaredFields
    var time = 0
    fields.forEach {
      builder.append(it.name, " = ", it.get(this))
      time++
      if (fields.size != time) builder.append(", ")
    }
    return builder.toString()
  }
}

abstract class NodeRoot(element: JDomElement) : Node {
  init {
    element.attributes.forEach {
      Node.attributes[it.name] = AttributeHolder(it.namespace, it)
    }
    element.children.forEach {
      Node.elements[it.name] = ElementHolder(it.namespace, it)
    }
  }
}
