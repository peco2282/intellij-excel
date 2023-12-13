package com.github.peco2282.excel.tasks

import com.github.peco2282.excel.ReturnableRunnable
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task.Modal
import com.intellij.openapi.project.Project

class ProgressTaskModal<T : Any>(
  project: Project,
  private val runnable: ReturnableRunnable<T>
): Modal(
  project,
  "Excel Task Modal",
  false
) {
  private var result = Any()
  override fun run(indicator: ProgressIndicator) {
    indicator.text = "Start extracting"
    indicator.fraction = 0.0
    result = runnable.run()
    indicator.fraction = 1.0
  }
  @Suppress("UNCHECKED_CAST")
  fun lastResult(): T {
    return result as T
  }
}