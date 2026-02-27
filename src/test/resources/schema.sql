CREATE TABLE IF NOT EXISTS audit_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  entity_name VARCHAR(100) NOT NULL,
  entity_id VARCHAR(100) NOT NULL,
  parent_entity_name VARCHAR(100),
  parent_entity_id VARCHAR(100),
  operation VARCHAR(20) NOT NULL,
  changed_at TIMESTAMP NOT NULL,
  actor VARCHAR(100) NOT NULL,
  data JSON
);
