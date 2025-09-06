package com.example.splunklite.util;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SchemaFixer implements ApplicationRunner {
  private final JdbcTemplate jdbc;
  public SchemaFixer(JdbcTemplate jdbc){ this.jdbc = jdbc; }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    try {
      jdbc.execute("ALTER TABLE logs MODIFY ts BIGINT NOT NULL");
      System.out.println("SchemaFixer: ALTER TABLE executed (ts -> BIGINT)");
    } catch (Exception ex) {
      // ignore errors (column already BIGINT or other harmless failures)
      System.out.println("SchemaFixer: ALTER TABLE skipped or failed: " + ex.getMessage());
    }
    try {
      // show current table DDL for diagnostics
      try {
        // SHOW CREATE TABLE returns two columns: Table, Create Table. Read as a map and pull the DDL.
        java.util.Map<String, Object> row = jdbc.queryForMap("SHOW CREATE TABLE logs");
        Object ddlObj = row.get("Create Table");
        if (ddlObj == null) ddlObj = row.values().stream().skip(1).findFirst().orElse(null);
        System.out.println("SchemaFixer: SHOW CREATE TABLE logs -> " + String.valueOf(ddlObj));
      } catch (Exception e) {
        System.out.println("SchemaFixer: could not get CREATE TABLE: " + e.getMessage());
      }

      // Try a safe two-step approach to make id AUTO_INCREMENT.
      // First try MODIFY with AUTO_INCREMENT alone.
      try {
        jdbc.execute("ALTER TABLE logs MODIFY id BIGINT NOT NULL AUTO_INCREMENT");
        System.out.println("SchemaFixer: ALTER TABLE executed (id -> BIGINT AUTO_INCREMENT)");
      } catch (Exception e1) {
        System.out.println("SchemaFixer: first ALTER for id failed: " + e1.getMessage());
        // fallback: try CHANGE which sometimes succeeds when MODIFY fails
        try {
          jdbc.execute("ALTER TABLE logs CHANGE id id BIGINT NOT NULL AUTO_INCREMENT");
          System.out.println("SchemaFixer: CHANGE executed (id -> BIGINT AUTO_INCREMENT)");
        } catch (Exception e2) {
          System.out.println("SchemaFixer: CHANGE for id failed too: " + e2.getMessage());
        }
      }
      // If primary key is missing, try adding it (ignore failures)
      try {
        jdbc.execute("ALTER TABLE logs ADD PRIMARY KEY (id)");
        System.out.println("SchemaFixer: ADD PRIMARY KEY (id) executed");
      } catch (Exception e3) {
        System.out.println("SchemaFixer: ADD PRIMARY KEY skipped/failed: " + e3.getMessage());
      }
    } catch (Exception ex) {
      System.out.println("SchemaFixer: id-fix path failed: " + ex.getMessage());
    }
  }
}
