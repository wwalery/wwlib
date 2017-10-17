package org.wwlib.FreeMarker;

import java.util.Map;


public class Initialize {

  public static void functions(Map map) {
   map.put("connection", new FMConnectionMethod());
   map.put("driver", new FMDriverMethod());
   map.put("query", new FMQueryMethod());
   map.put("queryOne", new FMQueryOneMethod());
   map.put("debug", new FMLogDebugMethod());
   map.put("info", new FMLogInfoMethod());
   map.put("warn", new FMLogWarnMethod());
   map.put("error", new FMLogErrorMethod());
   map.put("trace", new FMLogTraceMethod());
  }

}
