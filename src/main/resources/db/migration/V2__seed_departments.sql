-- Seed minimal pour tests API DEPARTMENT (idempotent)
INSERT INTO department (name, description, active, created_at, updated_at)
SELECT 'Data Sciences et IA', 'Département Data / IA', TRUE,
       NOW() AT TIME ZONE 'UTC', NOW() AT TIME ZONE 'UTC'
WHERE NOT EXISTS (
    SELECT 1 FROM department WHERE lower(name) = lower('Data Sciences et IA')
);

INSERT INTO department (name, description, active, created_at, updated_at)
SELECT 'Ingénierie Logicielle', 'Développement et architecture', TRUE,
       NOW() AT TIME ZONE 'UTC', NOW() AT TIME ZONE 'UTC'
WHERE NOT EXISTS (
    SELECT 1 FROM department WHERE lower(name) = lower('Ingénierie Logicielle')
);
