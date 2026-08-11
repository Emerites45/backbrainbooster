-- MCD TASK_ASSIGNMENT — non-destructif
CREATE TABLE IF NOT EXISTS task_assignment (
    id              BIGSERIAL PRIMARY KEY,
    task_id         BIGINT NOT NULL,
    user_id         BIGINT NOT NULL,
    assigned_by     BIGINT NOT NULL,
    assigned_at     TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
    unassigned_at   TIMESTAMPTZ,
    is_primary      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
    CONSTRAINT fk_task_assignment_task FOREIGN KEY (task_id) REFERENCES task (id),
    CONSTRAINT fk_task_assignment_user FOREIGN KEY (user_id) REFERENCES user_app (id),
    CONSTRAINT fk_task_assignment_assigned_by FOREIGN KEY (assigned_by) REFERENCES user_app (id)
);

CREATE INDEX IF NOT EXISTS idx_task_assignment_task ON task_assignment (task_id);
CREATE INDEX IF NOT EXISTS idx_task_assignment_user ON task_assignment (user_id);

-- Une seule assignation active par (task, user)
CREATE UNIQUE INDEX IF NOT EXISTS uk_task_assignment_active
    ON task_assignment (task_id, user_id)
    WHERE unassigned_at IS NULL;

-- Un seul primary actif par tâche
CREATE UNIQUE INDEX IF NOT EXISTS uk_task_assignment_primary_active
    ON task_assignment (task_id)
    WHERE is_primary = TRUE AND unassigned_at IS NULL;
