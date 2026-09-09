-- =============================================================================
-- Seed membership pour tester GET /departments et GET /departments/{id}/users
-- Prérequis : avoir créé un user via POST /api/v1/auth/signup
-- Usage : remplacer EMAIL ci-dessous puis exécuter sur Neon / psql
-- =============================================================================

-- Option A : rendre l'utilisateur ADMIN (voit tous les départements seed)
UPDATE user_app
SET role = 'ADMIN',
    date_modification = NOW() AT TIME ZONE 'UTC'
WHERE email = 'demo@example.com';

-- Option B : rattacher l'utilisateur aux départements seed (vue membre)
INSERT INTO user_department (user_id, department_id, start_date, created_at, updated_at)
SELECT u.id, d.id, NOW() AT TIME ZONE 'UTC', NOW() AT TIME ZONE 'UTC', NOW() AT TIME ZONE 'UTC'
FROM user_app u
CROSS JOIN department d
WHERE u.email = 'demo@example.com'
  AND d.active = TRUE
  AND NOT EXISTS (
      SELECT 1 FROM user_department ud
      WHERE ud.user_id = u.id
        AND ud.department_id = d.id
        AND ud.end_date IS NULL
  );
