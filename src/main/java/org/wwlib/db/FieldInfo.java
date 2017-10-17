package org.wwlib.db;

/**
 * @author Walery Wysotsky
 * @since 07/07/2005
 * @version 0.2.0
 */
public class FieldInfo extends CommonFieldInfo {

 protected boolean primaryKey;

 public boolean isPrimaryKey() {
  return primaryKey;
 }

 public void setPrimaryKey(boolean primaryKey) {
  this.primaryKey = primaryKey;
 }

	@Override
	public String toString() {
		return "FieldInfo{" 
						+ "primaryKey=" + primaryKey 
						+ ',' + super.toString()
						+ '}';
	}

 
 
}
