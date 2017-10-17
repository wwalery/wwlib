package org.wwlib.db;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * Common database operation
 *
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public class Database {

  private Connection conn;
  private DataSource source;
  boolean singleConnection;

  public Database(Connection conn) {
    this.conn = conn;
    singleConnection = true;
  }

  public Database(DataSource source) {
    this.source = source;
    singleConnection = false;
  }

  public Connection startTransation() throws SQLException {
    if (!singleConnection) {
      if (conn != null) {
        throw new SQLException("Start transaction inside other");
      }
      conn = source.getConnection();
    }
    return conn;
  }

  public Connection startTransation(String user, String password) throws SQLException {
    if (!singleConnection) {
      if (conn != null) {
        throw new SQLException("Start transaction inside other");
      }
      conn = source.getConnection(user, password);
    }
    return conn;
  }

  public Connection getConnection() throws SQLException {
    if (conn == null) {
      throw new SQLException("Transaction not started");
    }
    return conn;
  }

  public void commit() throws SQLException {
    conn.commit();
    if (!singleConnection) {
      conn.close();
      conn = null;
    }
  }

  public void rollback() throws SQLException {
    conn.rollback();
    if (!singleConnection) {
      conn.close();
      conn = null;
    }
  }

}
