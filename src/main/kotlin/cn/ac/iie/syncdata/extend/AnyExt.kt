package cn.ac.iie.syncdata.extend

/**
 * Any扩展
 */

fun Any.toJson(): String = gson.toJson(this)

fun Any.println() = println(this)
