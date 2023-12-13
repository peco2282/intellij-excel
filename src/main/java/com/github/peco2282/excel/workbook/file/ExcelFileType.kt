package com.github.peco2282.excel.workbook.file

import com.github.peco2282.excel.Constant
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.util.NlsSafe
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

@Suppress("UnstableApiUsage", "CompanionObjectInExtension", "PrivatePropertyName")
open class ExcelFileType : FileType {
    private val EXTENTIONS = arrayOf("xlsx", "xls")

    /**
     * Returns the name of the file type. The name must be unique among all file types registered in the system.
     */
    override fun getName(): @NonNls String {
        return "excel"
    }

    /**
     * Returns the user-readable description of the file type.
     */
    override fun getDescription(): @NlsContexts.Label String {
        return "An excel file"
    }

    /**
     * Returns the default extension for files of the type, *not* including the leading '.'.
     */
    override fun getDefaultExtension(): @NlsSafe String {
        return EXTENTIONS[0]
    }

    /**
     * Returns the icon used for showing files of the type, or `null` if no icon should be shown.
     */
    override fun getIcon(): Icon {
        return Constant.EXCEL_ICON
    }

    /**
     * Returns `true` if files of the specified type contain binary data, `false` if the file is plain text.
     * Used for source control, to-do items scanning and other purposes.
     */
    override fun isBinary(): Boolean {
        return false
    }

    companion object {
        val XLSX_INSTANCE: ExcelFileType = object : ExcelFileType() {
            override fun getDefaultExtension(): String {
                return "xlsx"
            }
        }
        val XLS_INSTANCE: ExcelFileType = object : ExcelFileType() {
            override fun getDefaultExtension(): String {
                return "xls"
            }
        }
        val INSTANCE = ExcelFileType()
    }
}
