package cn.ac.iie.syncdata.extend

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Atomic* 扩展
 */

fun AtomicBoolean.isStart() = this.get()
fun AtomicBoolean.isStop() = !this.get()