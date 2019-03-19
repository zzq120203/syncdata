package cn.ac.iie.syncdata.extend

import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * 扩展函数属性
 */

val gson: Gson by lazy { GsonBuilder().disableHtmlEscaping().serializeNulls().create() }