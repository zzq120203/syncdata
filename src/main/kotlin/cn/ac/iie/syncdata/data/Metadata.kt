package cn.ac.iie.syncdata.data

data class Metadata(
        var serverName: String? = "mmSync",
        var uuid: String? = null,
        var url: String? = null,
        var mppConf: MppConf? = null
)

data class MppConf(
        val table: String? = null,
        val m_chat_room: String? = null,
        val u_ch_id: String? = null,
        val m_ch_id: String? = null
)

data class MMData(
        var key: String? = null,
        var g_id: String? = null,
        var m_chat_room: String? = null,
        var u_ch_id: String? = null,
        var m_ch_id: String? = null,
        var table: String? = null,
        var q_mm_md5_rk: String? = null,
        var type: String? = null,
        var q_id: String? = null
)