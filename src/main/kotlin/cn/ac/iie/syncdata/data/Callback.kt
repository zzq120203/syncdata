package cn.ac.iie.syncdata.data

/**
 * 返回数据类型
 */

data class SysCallback(var id: String? = null, var status: Status? = null)

data class Status(var code: String? = null, var failed: String? = null)