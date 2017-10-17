package org.wwlib.db;

import java.sql.SQLException;
import java.sql.Connection;
import javax.sql.DataSource;
import java.util.List;

public interface IDB {

 public List getLastResultSet();

 public Connection getConnection();
 public void setConnection(Connection conn);

 public void setDataSource(DataSource source);

 public boolean isSelected();
 public void setSelected(boolean value);

 public boolean isChanged();

 public int insert() throws SQLException;

 public int update() throws SQLException;

 public int delete() throws SQLException;

}
