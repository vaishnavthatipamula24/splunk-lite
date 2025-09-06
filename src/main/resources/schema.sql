
CREATE TABLE IF NOT EXISTS logs (
  id       BIGINT PRIMARY KEY AUTO_INCREMENT,
  ts       BIGINT      NOT NULL,        -- epoch millis
  source   VARCHAR(255),
  env      VARCHAR(64),
  level    VARCHAR(32),
  message  TEXT,
  extra    JSON                             -- holds arbitrary fields as JSON
) ENGINE=InnoDB;

-- users table for auth
CREATE TABLE IF NOT EXISTS users (
  id VARCHAR(36) PRIMARY KEY,
  username VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  created_ts BIGINT
) ENGINE=InnoDB;


