-- M7 Lens additions for PostgreSQL.
-- Hibernate ddl-auto=update creates these automatically in development.
-- Run the ALTER statements only when migrating an existing schema manually.
ALTER TABLE IF EXISTS employees ADD COLUMN IF NOT EXISTS separation_reason VARCHAR(120);

CREATE TABLE IF NOT EXISTS open_requisitions (
  id BIGSERIAL PRIMARY KEY,
  opened_date DATE NOT NULL,
  target_hire_date DATE,
  open BOOLEAN NOT NULL DEFAULT TRUE,
  positions INTEGER NOT NULL DEFAULT 1,
  department VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS planned_separations (
  id BIGSERIAL PRIMARY KEY,
  planned_date DATE NOT NULL,
  confirmed BOOLEAN NOT NULL DEFAULT TRUE,
  reason VARCHAR(120)
);

CREATE TABLE IF NOT EXISTS report_defs (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  fields_json TEXT NOT NULL,
  filters_json TEXT,
  group_by_json TEXT,
  format VARCHAR(10),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS scheduled_reports (
  id BIGSERIAL PRIMARY KEY,
  report_id BIGINT NOT NULL REFERENCES report_defs(id),
  cron_expression VARCHAR(100) NOT NULL,
  recipients VARCHAR(500) NOT NULL,
  format VARCHAR(10) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  last_run_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS analytics_report_jobs (
  id BIGSERIAL PRIMARY KEY,
  report_id BIGINT NOT NULL REFERENCES report_defs(id),
  status VARCHAR(20) NOT NULL,
  result_json TEXT,
  error_message VARCHAR(1000),
  created_at TIMESTAMP,
  completed_at TIMESTAMP
);
