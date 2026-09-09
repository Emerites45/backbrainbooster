-- MCD ACTION_HISTORY (polymorphe) — append-only
CREATE TABLE IF NOT EXISTS action_history (
    id              BIGSERIAL PRIMARY KEY,
    entity_type     VARCHAR(50) NOT NULL,
    entity_id       BIGINT NOT NULL,
    action_type     VARCHAR(50) NOT NULL,
    field_name      VARCHAR(100),
    old_value       TEXT,
    new_value       TEXT,
    performed_by    BIGINT NOT NULL,
    performed_at    TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
    CONSTRAINT fk_action_history_performed_by FOREIGN KEY (performed_by) REFERENCES user_app (id)
);

CREATE INDEX IF NOT EXISTS idx_action_history_entity
    ON action_history (entity_type, entity_id, performed_at DESC);
CREATE INDEX IF NOT EXISTS idx_action_history_performed_at
    ON action_history (performed_at DESC);
