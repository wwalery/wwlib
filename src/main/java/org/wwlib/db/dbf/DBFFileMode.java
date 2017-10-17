package org.wwlib.db.dbf;

/**
 * DBF file open mode.
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public enum DBFFileMode {

 /**
  * Create only. Remove old if exist.
  */
  CREATE,
  
  /**
   * Open existing file. Exception if not exist.
   */
  OPEN,
  
  /**
   * Open existing file in Read Only mode. Exception if not exist.
   */
  OPEN_RO,
  
  /**
   * Open existing file. Create if not exist.
   */
  OPEN_THEN_CREATE;
 
}
