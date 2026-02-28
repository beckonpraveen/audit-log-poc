CREATE DATABASE IF NOT EXISTS helpdesk_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE helpdesk_db;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  username VARCHAR(255) NULL,
  password_hash VARCHAR(255) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_email (email),
  UNIQUE KEY uk_users_username (username)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT NOT NULL,
  role VARCHAR(50) NOT NULL,
  PRIMARY KEY (user_id, role),
  CONSTRAINT fk_user_roles_user
    FOREIGN KEY (user_id)
    REFERENCES users (id)
    ON UPDATE RESTRICT
    ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS priorities (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(1000) NULL,
  active BIT(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (id),
  UNIQUE KEY uk_priorities_name (name)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS impacts (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(1000) NULL,
  active BIT(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (id),
  UNIQUE KEY uk_impacts_name (name)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS sla_rules (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(1000) NULL,
  priority_id BIGINT NOT NULL,
  response_time_minutes INT NOT NULL,
  resolution_time_minutes INT NOT NULL,
  sort_order INT NOT NULL DEFAULT 100,
  active BIT(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sla_rules_name (name),
  KEY idx_sla_rules_priority_id (priority_id),
  KEY idx_sla_rules_active_order (active, sort_order, id),
  CONSTRAINT fk_sla_rules_priority
    FOREIGN KEY (priority_id)
    REFERENCES priorities (id)
    ON UPDATE RESTRICT
    ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tickets (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL,
  description VARCHAR(2000) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
  user_id BIGINT NOT NULL,
  priority_id BIGINT NULL,
  impact_id BIGINT NULL,
  sla_rule_id BIGINT NULL,
  PRIMARY KEY (id),
  KEY idx_tickets_user_id (user_id),
  KEY idx_tickets_priority_id (priority_id),
  KEY idx_tickets_impact_id (impact_id),
  KEY idx_tickets_sla_rule_id (sla_rule_id),
  CONSTRAINT fk_tickets_user_id
    FOREIGN KEY (user_id)
    REFERENCES users (id)
    ON UPDATE RESTRICT
    ON DELETE RESTRICT,
  CONSTRAINT fk_tickets_priority_id
    FOREIGN KEY (priority_id)
    REFERENCES priorities (id)
    ON UPDATE RESTRICT
    ON DELETE RESTRICT,
  CONSTRAINT fk_tickets_impact_id
    FOREIGN KEY (impact_id)
    REFERENCES impacts (id)
    ON UPDATE RESTRICT
    ON DELETE RESTRICT,
  CONSTRAINT fk_tickets_sla_rule_id
    FOREIGN KEY (sla_rule_id)
    REFERENCES sla_rules (id)
    ON UPDATE RESTRICT
    ON DELETE RESTRICT,
  CONSTRAINT chk_tickets_status
    CHECK (status IN ('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'))
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS ticket_comments (
  id BIGINT NOT NULL AUTO_INCREMENT,
  ticket_id BIGINT NOT NULL,
  comment_text VARCHAR(2000) NOT NULL,
  created_by VARCHAR(255) NOT NULL,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  KEY idx_ticket_comments_ticket_id (ticket_id),
  CONSTRAINT fk_ticket_comments_ticket_id
    FOREIGN KEY (ticket_id)
    REFERENCES tickets (id)
    ON UPDATE RESTRICT
    ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS audit_log (
  id BIGINT NOT NULL AUTO_INCREMENT,
  entity_name VARCHAR(100) NOT NULL,
  entity_id VARCHAR(100) NOT NULL,
  parent_entity_name VARCHAR(100) NULL,
  parent_entity_id VARCHAR(100) NULL,
  operation VARCHAR(20) NOT NULL,
  changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  actor VARCHAR(100) NOT NULL,
  data JSON NULL,
  PRIMARY KEY (id),
  KEY idx_audit_entity (entity_name, entity_id),
  KEY idx_audit_parent_entity (parent_entity_name, parent_entity_id),
  KEY idx_audit_changed_at (changed_at),
  CONSTRAINT chk_audit_operation
    CHECK (
      operation IN (
        'CREATE',
        'UPDATE',
        'DELETE'
      )
    )
) ENGINE=InnoDB;
