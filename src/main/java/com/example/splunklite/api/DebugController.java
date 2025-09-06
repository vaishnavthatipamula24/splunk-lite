package com.example.splunklite.api;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

  private final JdbcTemplate jdbc;

  public DebugController(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  // Returns recent rows from the logs table. Useful for quick debugging.
  @GetMapping("/logs")
  public List<Map<String,Object>> recentLogs(@RequestParam(name = "size", defaultValue = "50") int size) {
    // Order by ts descending to get newest first
    try {
      return jdbc.queryForList("SELECT * FROM logs ORDER BY ts DESC LIMIT ?", size);
    } catch (Exception e) {
      // On error return an empty list; client can inspect backend logs for details.
      return List.of();
    }
  }
}
