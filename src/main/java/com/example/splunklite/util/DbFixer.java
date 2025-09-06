package com.example.splunklite.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DbFixer {
  public static void main(String[] args) throws Exception {
    String url = "jdbc:mysql://localhost:3306/splunklite?useSSL=false&serverTimezone=UTC";
    String user = "root";
    String pass = "Ts07gk3867@";
    try (Connection c = DriverManager.getConnection(url, user, pass); Statement s = c.createStatement()) {
      System.out.println("Connected, attempting ALTER TABLE logs MODIFY ts to BIGINT...");
      s.executeUpdate("ALTER TABLE logs MODIFY ts BIGINT NOT NULL");
      System.out.println("ALTER TABLE executed successfully.");
    } catch (Exception ex) {
      System.err.println("ALTER TABLE failed: " + ex.getMessage());
      ex.printStackTrace();
      System.exit(1);
    }
  }
}
