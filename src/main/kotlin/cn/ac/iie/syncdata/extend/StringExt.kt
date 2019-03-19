package cn.ac.iie.syncdata.extend

import java.lang.reflect.Type

/**
 * String 扩展
 */

inline fun <reified T : Any> String.fromJson(): T = gson.fromJson(this, T::class.java)

fun <T> String.fromJson(type: Type): T = gson.fromJson(this, type)
