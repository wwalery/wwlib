package org.wwlib.db;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author walery
 */
public abstract class AbstractScriptParser {

 public AbstractScriptParser() {
 }

 public String[] parseFile(String fileName) throws FileNotFoundException, IOException {
  BufferedReader reader = new BufferedReader(new FileReader(fileName));
  String[] result = parse(reader);
  reader.close();
  return result;
 }

 public abstract String[] parse(BufferedReader reader) throws IOException;

}
