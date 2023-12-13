package com.github.peco2282.excel

import com.intellij.AbstractBundle
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipFile
import org.apache.commons.compress.utils.IOUtils
import org.jetbrains.annotations.Contract
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.jetbrains.annotations.PropertyKey
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


object Utils {
  fun join(separator: CharSequence, vararg strings: String): String {
    return strings.joinToString(separator)
  }
  @Contract(value = "null, _ -> param2")
  fun <T> default(@Nullable value: T?, @NotNull default: T): T {
    return value ?: default
  }
  private fun bundle(@PropertyKey(resourceBundle = Constant.RESOURCE_BUNDLE) key: String, vararg any: Any): String {
    return AbstractBundle.message(ResourceBundle.getBundle(Constant.RESOURCE_BUNDLE), key, any)
  }

  fun exist(path: String) = File(path).exists()

  fun walk(dirPath: String) : HashMap<String, File> {
    val files = HashMap<String, File>()
    val path = Path.of(dirPath)
    Files.walk(path).forEach {
      val file = it.toFile()
      files[file.name] = if (file.isDirectory) Directory(file) else Script(file)
    }
    return files
  }

  @JvmStatic
  @Throws(NullPointerException::class)
  fun <T> notNull(@Nullable any: T?, msg: String): @NotNull T {
    if (any == null || if (any is CharSequence) any.isEmpty() else false) throw NullPointerException(msg)
    return any
  }

  @JvmStatic
  fun <T> notNull(any: T?) = notNull(any, "given object is null or \"\"")

  @Throws(IOException::class)
  fun unzip(zipFilePath: String, destDirectory: String): HashMap<String, File> {
    val destDir = File(destDirectory)
    if (!destDir.exists()) destDir.mkdir()
    return unzip(zipFilePath, destDir)
  }

  @Throws(IOException::class)
  fun unzip(zipFilePath: String, destDirectory: File): HashMap<String, File> {
    val files = ArrayList<File>()
    val zipFile = ZipFile(zipFilePath)
    val entries: Enumeration<ZipArchiveEntry> = zipFile.entries
    while (entries.hasMoreElements()) {
      val entry = entries.nextElement()
      val filePath = join(File.separator, destDirectory.absolutePath, entry.name)
      if (!entry.isDirectory) {
        // if the entry is a file, extracts it
        files.add(extract(zipFile, entry, filePath))
      } else {
        // if the entry is a directory, make the directory
        val dir = Directory(filePath)
        dir.mkdir()
        files.add(dir)
      }
    }
    zipFile.close()
    val map = HashMap<String, File>()
    files.forEach {
      map[it.name] = it
    }
    return map
  }

  @Throws(IOException::class)
  private fun extract(zipFile: ZipFile, entry: ZipArchiveEntry, filePath: String): Script {
    val file = File(filePath)
    file.parentFile.mkdirs()
    FileOutputStream(file).use { os -> IOUtils.copy(zipFile.getInputStream(entry), os) }
    return Script(file)
  }

  fun information(
    message: String,
    @PropertyKey(resourceBundle = Constant.RESOURCE_BUNDLE) bundleKey: String,
    vararg args: String,
  ) = Notifications.Bus.notify(
    Notification(
      "Excel Plugin",
      bundle(bundleKey, args),
      message,
      NotificationType.INFORMATION
    )
  )

  fun warning(
    message: String,
    @PropertyKey(resourceBundle = Constant.RESOURCE_BUNDLE) bundleKey: String,
    vararg args: String,
  ) = Notifications.Bus.notify(
    Notification(
      "Excel Plugin",
      bundle(bundleKey, args),
      message,
      NotificationType.WARNING
    )
  )
  fun warning(
    message: Throwable,
    @PropertyKey(resourceBundle = Constant.RESOURCE_BUNDLE) bundleKey: String,
    vararg args: String,
  ) = warning(message.message ?: message.localizedMessage, bundleKey, *args)

  fun error(
    message: Throwable,
    @PropertyKey(resourceBundle = Constant.RESOURCE_BUNDLE) bundleKey: String,
    vararg args: String
  ) = error(message.message ?: message.localizedMessage, bundleKey, *args)

  fun error(
    message: String,
    @PropertyKey(resourceBundle = Constant.RESOURCE_BUNDLE) bundleKey: String,
    vararg args: String,
  ) = Notifications.Bus.notify(
    Notification(
      "Excel Plugin",
      bundle(bundleKey, args),
      message,
      NotificationType.ERROR
    )
  )
}