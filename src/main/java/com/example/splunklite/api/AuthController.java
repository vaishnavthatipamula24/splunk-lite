package com.example.splunklite.api;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

  private static final Logger log = LoggerFactory.getLogger(AuthController.class);

  private final JdbcTemplate jdbc;
  private final PasswordEncoder pwEncoder = new BCryptPasswordEncoder();
  @Value("${app.jwt.secret}")
  private String jwtSecret;

  public AuthController(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
    // create a portable users table (UUID primary key) so it works across DBs
    try {
      jdbc.execute("CREATE TABLE IF NOT EXISTS users (id VARCHAR(36) PRIMARY KEY, username VARCHAR(255) UNIQUE, password VARCHAR(255), created_ts BIGINT)");
      log.info("Ensured users table exists");
    } catch (Exception e) {
      log.warn("Failed to ensure users table exists: {}", e.getMessage());
    }
  }

  record AuthRequest(String username, String password) {}

  @PostMapping("/signup")
  public ResponseEntity<?> signup(@RequestBody AuthRequest req) {
    if (req == null || req.username == null || req.password == null) return ResponseEntity.badRequest().body(Map.of("error", "username and password required"));
    String username = req.username.trim();
    if (username.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "username required"));
  String hashed = pwEncoder.encode(req.password);
    String id = UUID.randomUUID().toString();
    try {
      jdbc.update("INSERT INTO users(id,username,password,created_ts) VALUES(?,?,?,?)", id, username, hashed, Instant.now().toEpochMilli());
      log.info("Created user {} with id {}", username, id);
      return ResponseEntity.ok(Map.of("ok", true));
    } catch (Exception e) {
      log.warn("Failed to create user {}: {}", username, e.getMessage());
      return ResponseEntity.status(409).body(Map.of("error", "user exists or db error"));
    }
  }

  @PostMapping("/signin")
  public ResponseEntity<?> signin(@RequestBody AuthRequest req) {
    if (req == null || req.username == null || req.password == null) return ResponseEntity.badRequest().body(Map.of("error","username and password required"));
    try {
  var rows = jdbc.queryForList("SELECT password FROM users WHERE username = ?", req.username);
      if (rows.isEmpty()) {
        return ResponseEntity.status(401).body(Map.of("error", "invalid credentials"));
      }
      String stored = String.valueOf(rows.get(0).get("password"));
  if (!pwEncoder.matches(req.password, stored)) {
        return ResponseEntity.status(401).body(Map.of("error", "invalid credentials"));
      }
      // create JWT valid for 24 hours
      Date now = new Date();
      Date exp = Date.from(Instant.ofEpochMilli(now.getTime()).plusSeconds(24 * 3600));
      String jwt = Jwts.builder()
          .setSubject(req.username)
          .setIssuedAt(now)
          .setExpiration(exp)
          .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
          .compact();
      log.info("User {} signed in", req.username);
      return ResponseEntity.ok(Map.of("token", jwt, "username", req.username));
    } catch (Exception e) {
      log.error("Signin error for {}: {}", req.username, e.getMessage());
      return ResponseEntity.status(500).body(Map.of("error", "server error"));
    }
  }

  @GetMapping("/me")
  public ResponseEntity<?> me(@RequestHeader(name = "Authorization", required = false) String auth) {
    if (auth == null || !auth.startsWith("Bearer ")) return ResponseEntity.status(401).body(Map.of("error","missing token"));
    String token = auth.substring(7);
    try{
      var claims = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8))).build().parseClaimsJws(token).getBody();
      String username = claims.getSubject();
      return ResponseEntity.ok(Map.of("username", username));
    }catch(Exception e){
      return ResponseEntity.status(401).body(Map.of("error","invalid token"));
    }
  }

  @SuppressWarnings("unused")
  private static String hash(String s) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
      return UUID.nameUUIDFromBytes(d).toString();
    } catch (NoSuchAlgorithmException e) {
      return Integer.toHexString(s.hashCode());
    }
  }
}
