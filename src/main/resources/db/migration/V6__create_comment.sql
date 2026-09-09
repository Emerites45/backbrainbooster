-- MCD COMMENT — non-destructif
CREATE TABLE IF NOT EXISTS comment (
    id              BIGSERIAL PRIMARY KEY,
    task_id         BIGINT NOT NULL,
    content         TEXT NOT NULL,
    created_by      BIGINT NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
    deleted_at      TIMESTAMPTZ,
    CONSTRAINT fk_comment_task FOREIGN KEY (task_id) REFERENCES task (id),
    CONSTRAINT fk_comment_created_by FOREIGN KEY (created_by) REFERENCES user_app (id)
);

CREATE INDEX IF NOT EXISTS idx_comment_task ON comment (task_id);
CREATE INDEX IF NOT EXISTS idx_comment_task_active ON comment (task_id) WHERE deleted_at IS NULL;
