-- Index soft-delete USER (non-destructif)
CREATE INDEX IF NOT EXISTS idx_user_app_date_suppression
    ON user_app (date_suppression);

CREATE INDEX IF NOT EXISTS idx_user_app_role
    ON user_app (role);
