package cn.ac.iie.syncdata.db

import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

/**
 * 对数据的增删改查
 */
object DBUtil {

    private val log = LoggerFactory.getLogger(DBUtil::class.java)

    fun <T> selectMysql(sql: String, print: Boolean = true, resultFun: (resultSet: ResultSet) -> T): T? {
        val mysql = JDBCPool.getMysqlDataSource()

        var connection: Connection? = null
        var statement: Statement? = null
        var resultSet: ResultSet? = null

        try {
            connection = mysql.connection
            statement = connection.createStatement()
            if (print) log.info("sql -> $sql")
            resultSet = statement.executeQuery(sql)
            return resultFun(resultSet)
        } catch (e: SQLException) {
            log.error("e -> ${e.javaClass}:${e.message}, sql -> $sql")
        } finally {
            resultSet?.close()
            statement?.close()
            connection?.close()
        }
        return null
    }

    fun updateMysql(sql: String, print: Boolean = true): Int {
        val mysql = JDBCPool.getMysqlDataSource()

        var connection: Connection? = null
        var statement: Statement? = null

        try {
            connection = mysql.connection
            statement = connection.createStatement()
            if (print) log.info("sql -> $sql")
            return statement.executeUpdate(sql)
        } catch (e: SQLException) {
            log.error("e -> ${e.javaClass}:${e.message}, sql -> $sql")
        } finally {
            statement?.close()
            connection?.close()
        }
        return -1
    }

    fun insertMysql(sql: String, print: Boolean = true): Int {
        return updateMysql(sql, print)
    }

    fun deleteMysql(sql: String, print: Boolean = true): Int {
        return updateMysql(sql, print)
    }

}