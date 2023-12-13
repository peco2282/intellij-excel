package com.github.peco2282.excel

import java.io.File
import java.nio.file.Path

operator fun Path.plus(path: Path): Path {
  return Path.of(this.toFile().path + path.toFile().path)
}

operator fun Path.div(path: Path): Path {
  return Path.of(this.toFile().path + File.separator + path.toFile().path)
}

fun interface ReturnableRunnable</**/T> {
  fun run(): T
}
abstract class ToStringFile : File {
  constructor(pathname: String) : super(pathname)
  constructor(file: File) : super(file.toURI()) {
    if (file is ToStringFile) throw IllegalArgumentException()
  }

  override fun toString(): String {
    return "name = ${this.name} path = ${this.absolutePath} isFile = ${this.isFile}"
  }
}

class Script : ToStringFile {
  /**
   * Creates a new `File` instance by converting the given
   * pathname string into an abstract pathname.  If the given string is
   * the empty string, then the result is the empty abstract pathname.
   *
   * @param pathname A pathname string
   * @throws NullPointerException If the `pathname` argument is `null`
   */
  constructor(pathname: String) : super(pathname)
  constructor(file: File) : super(file)
}

class Directory : ToStringFile {
  /**
   * Creates a new `File` instance by converting the given
   * pathname string into an abstract pathname.  If the given string is
   * the empty string, then the result is the empty abstract pathname.
   *
   * @param pathname A pathname string
   * @throws NullPointerException If the `pathname` argument is `null`
   */
  constructor(pathname: String) : super(pathname)
  constructor(file: File) : super(file)
}