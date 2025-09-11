package com.example.splunklite.svc;

import java.time.Instant;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.splunklite.model.LogEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class Indexer {
  private final JdbcTemplate jdbc;
  private final ObjectMapper om = new ObjectMapper();
  private final SplunkService splunk;

  public Indexer(JdbcTemplate jdbc, SplunkService splunk){ 
    this.jdbc = jdbc; 
    this.splunk = splunk;
  }

  public void put(LogEvent e) throws Exception {
    long ts = (e.getTs() == null ? Instant.now() : e.getTs()).toEpochMilli();
    String extra = e.fields().isEmpty() ? null : om.writeValueAsString(e.fields());
      jdbc.update(
        "INSERT INTO logs(ts, source, env, level, message, extra) VALUES(?,?,?,?,?,?)",
        ts, e.getSource(), e.getEnv(), e.getLevel(), e.getMessage(), extra
      );
      // forward to Splunk HEC if configured (best-effort, async)
      try { splunk.sendEvent(e); } catch (Exception ignored) {}
  }
}
