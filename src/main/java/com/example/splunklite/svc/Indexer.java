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

  public Indexer(JdbcTemplate jdbc){ 
    this.jdbc = jdbc; 
  }

  public void put(LogEvent e) throws Exception {
    long ts = (e.getTs() == null ? Instant.now() : e.getTs()).toEpochMilli();
    String extra = e.fields().isEmpty() ? null : om.writeValueAsString(e.fields());
      jdbc.update(
        "INSERT INTO logs(ts, source, env, level, message, extra) VALUES(?,?,?,?,?,?)",
        ts, e.getSource(), e.getEnv(), e.getLevel(), e.getMessage(), extra
      );
  }
}
