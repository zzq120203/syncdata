package cn.ac.iie.syncdata.db

import cn.ac.iie.syncdata.configs.config
import com.alibaba.druid.pool.DruidDataSourceFactory
import javax.sql.DataSource

/**
 * 采用阿里druid的连接池
 */
object JDBCPool {

    private val mysql = DruidDataSourceFactory.createDataSource(config().mysqlConf)

    fun getMysqlDataSource(): DataSource {
        return mysql
    }
}