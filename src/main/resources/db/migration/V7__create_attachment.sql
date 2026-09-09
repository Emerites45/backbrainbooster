-- MCD ATTACHMENT — non-destructif
CREATE TABLE IF NOT EXISTS attachment (
    id              BIGSERIAL PRIMARY KEY,
    task_id         BIGINT NOT NULL,
    file_name       VARCHAR(255) NOT NULL,
    file_path       VARCHAR(1024) NOT NULL,
    mime_type       VARCHAR(255),
    file_size       BIGINT NOT NULL DEFAULT 0,
    uploaded_by     BIGINT NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
    deleted_at      TIMESTAMPTZ,
    CONSTRAINT fk_attachment_task FOREIGN KEY (task_id) REFERENCES task (id),
    CONSTRAINT fk_attachment_uploaded_by FOREIGN KEY (uploaded_by) REFERENCES user_app (id)
);

CREATE INDEX IF NOT EXISTS idx_attachment_task ON attachment (task_id);
CREATE INDEX IF NOT EXISTS idx_attachment_task_active ON attachment (task_id) WHERE deleted_at IS NULL;
