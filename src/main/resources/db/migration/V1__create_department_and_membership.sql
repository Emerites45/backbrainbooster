-- =============================================================================
-- Baseline non-destructive + MCD DEPARTMENT / USER_DEPARTMENT
-- Jamais de DROP ; CREATE TABLE / INDEX IF NOT EXISTS uniquement
-- =============================================================================

-- Legacy auth (aligné sur entité User / table user_app)
CREATE TABLE IF NOT EXISTS user_app (
    id                          BIGSERIAL PRIMARY KEY,
    nom                         VARCHAR(255) NOT NULL,
    email                       VARCHAR(255) NOT NULL,
    password_hash               VARCHAR(255) NOT NULL,
    role                        VARCHAR(50)  DEFAULT 'USER',
    password_reset_token        VARCHAR(255),
    password_reset_token_expiry TIMESTAMPTZ,
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
    date_modification           TIMESTAMPTZ,
    date_suppression            TIMESTAMPTZ
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_user_app_email ON user_app (email);

CREATE TABLE IF NOT EXISTS department (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(150) NOT NULL,
    description     TEXT,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC')
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_department_name_active
    ON department (lower(name))
    WHERE active = TRUE;

CREATE TABLE IF NOT EXISTS user_department (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    department_id   BIGINT NOT NULL,
    start_date      TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
    end_date        TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
    CONSTRAINT fk_user_department_user
        FOREIGN KEY (user_id) REFERENCES user_app (id),
    CONSTRAINT fk_user_department_department
        FOREIGN KEY (department_id) REFERENCES department (id)
);

CREATE INDEX IF NOT EXISTS idx_user_department_user
    ON user_department (user_id);

CREATE INDEX IF NOT EXISTS idx_user_department_department
    ON user_department (department_id);

CREATE UNIQUE INDEX IF NOT EXISTS uk_user_department_active
    ON user_department (user_id, department_id)
    WHERE end_date IS NULL;
