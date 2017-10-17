/*
 *
 *   *
 */
package org.wwlib.json;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.diff.JsonDiff;
import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public class GenPatch {

 private final static Logger LOG = LoggerFactory.getLogger(GenPatch.class);
 private String sourceName;
 private String targetName;
 private String diffName;

 public static void main(String... args) throws IOException {
  if (args.length < 3) {
   LOG.warn(" Generate diff between two JSON files");
   LOG.warn("Usage: " + GenPatch.class.getName() + " sourceFile targetFile outputFile");
   System.exit(1);
  }
  File f = new File(args[0]);
  if (!f.exists()) {
   LOG.error("File " + args[0] + " not found");
   System.exit(1);
  }
  f = new File(args[1]);
  if (!f.exists()) {
   LOG.error("File " + args[1] + " not found");
   System.exit(1);
  }
  GenPatch patch = new GenPatch(args[0], args[1], args[2]);
  patch.run();
 }

 public GenPatch(String sourceName, String targetName, String diffName) {
  this.sourceName = sourceName;
  this.targetName = targetName;
  this.diffName = diffName;
 }

 public void run() throws IOException {
  ObjectMapper mapper = new ObjectMapper();
  mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
  JsonNode source = mapper.readTree(new File(sourceName));
  JsonNode target = mapper.readTree(new File(targetName));
//  final JsonPatch patch = JsonDiff.asJsonPatch(source, target);
  LOG.info("Generate diff");
  final JsonNode patchNode = JsonDiff.asJson(source, target);
  JsonFactory jFact = new JsonFactory();
  JsonGenerator jgen = jFact.createGenerator(new File(diffName), JsonEncoding.UTF8);
  jgen.setPrettyPrinter(new DefaultPrettyPrinter());
  LOG.info("Write diff to "+diffName);
  mapper.writeTree(jgen, patchNode);
  jgen.close();
 }

}
