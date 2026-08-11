-- =============================================================================
-- MCD : PROJECT + TASK (table tasks pour GET /projects/{id}/tasks)
-- Non-destructif : CREATE IF NOT EXISTS uniquement
-- =============================================================================

CREATE TABLE IF NOT EXISTS project (
    id              BIGSERIAL PRIMARY KEY,
    department_id   BIGINT NOT NULL,
    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    status          VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_by      BIGINT NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
    deleted_at      TIMESTAMPTZ,
    CONSTRAINT fk_project_department FOREIGN KEY (department_id) REFERENCES department (id),
    CONSTRAINT fk_project_created_by FOREIGN KEY (created_by) REFERENCES user_app (id)
);

CREATE INDEX IF NOT EXISTS idx_project_department ON project (department_id);
CREATE INDEX IF NOT EXISTS idx_project_deleted_at ON project (deleted_at);
CREATE UNIQUE INDEX IF NOT EXISTS uk_project_dept_name_active
    ON project (department_id, lower(name))
    WHERE deleted_at IS NULL;

CREATE TABLE IF NOT EXISTS task (
    id              BIGSERIAL PRIMARY KEY,
    parent_task_id  BIGINT,
    root_task_id    BIGINT,
    project_id      BIGINT NOT NULL,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    status          VARCHAR(50) NOT NULL DEFAULT 'TODO',
    priority        VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    level           INT NOT NULL DEFAULT 0,
    order_index     INT NOT NULL DEFAULT 0,
    due_date        TIMESTAMPTZ,
    created_by      BIGINT NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
    deleted_at      TIMESTAMPTZ,
    CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES project (id),
    CONSTRAINT fk_task_created_by FOREIGN KEY (created_by) REFERENCES user_app (id),
    CONSTRAINT fk_task_parent FOREIGN KEY (parent_task_id) REFERENCES task (id),
    CONSTRAINT fk_task_root FOREIGN KEY (root_task_id) REFERENCES task (id)
);

CREATE INDEX IF NOT EXISTS idx_task_project ON task (project_id);
CREATE INDEX IF NOT EXISTS idx_task_deleted_at ON task (deleted_at);
CREATE INDEX IF NOT EXISTS idx_task_parent ON task (parent_task_id);
