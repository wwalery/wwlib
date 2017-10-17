package org.wwlib.db.mysql;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import org.wwlib.StrTool;
import org.wwlib.db.AbstractScriptParser;

/**
 *
 * @author walery
 */
public class ScriptParser extends AbstractScriptParser {

 private final static String COMMENT_START = "/*";
 private final static String COMMENT_END = "*/";
 private final static String STATEMENT_END = ";";

 @Override
 public String[] parse(BufferedReader reader) throws IOException {
  String line;
  StringBuilder sql = null;
  ArrayList<String> result = new ArrayList();
  boolean inComment = false;
  boolean isEndOfStatement = false;

  while ((line = reader.readLine()) != null) {
// Skip empty lines
   if (StrTool.isEmpty(line)) continue;

// skip comment body
   if (inComment) {
    if (!line.contains(COMMENT_END)) continue;
    line = line.substring(line.indexOf(COMMENT_END)+COMMENT_END.length());
    inComment = false;
   }

// check comment start and possibly end
   if (line.contains(COMMENT_START)) {
    if (line.contains(COMMENT_END)) {
     line = line.substring(0,line.indexOf(COMMENT_START)) +
            line.substring(line.indexOf(COMMENT_END)+COMMENT_END.length());
    } else {
     line = line.substring(0,line.indexOf(COMMENT_START));
     inComment = true;
    }
   }
   if (StrTool.isEmpty(line)) continue;

// check statement end
   if (line.contains(STATEMENT_END)) {
    line = line.substring(0,line.indexOf(STATEMENT_END));
// yes, skip any after end of statement - use one statement in string
    isEndOfStatement = true;
   }
   if (sql==null) {
    sql = new StringBuilder(line);
   } else {
    sql.append(" ");
    sql.append(line);
   }
   if (isEndOfStatement) {
    result.add(sql.toString());
    sql = null;
    isEndOfStatement = false;
   }
  }
  return result.toArray(new String[result.size()]);
 }

}
