-- Permission
INSERT INTO permissions (id, resource, verb, description, created_at)
VALUES ('01K94V2FM7165A762YR3V29M84', 'sessions', 'revoke', 'Permissions to revoke a session', CURRENT_TIMESTAMP);

INSERT INTO permissions (id, resource, verb, description, created_at)
VALUES ('01K94W6MAV906W5EPR63RB7K77', 'sessions', 'read', 'Permissions to read a session', CURRENT_TIMESTAMP);

-- Roles
INSERT INTO roles (id, name, description, created_at)
VALUES ('01K94W96M6KN86HKV7FAA2CGNH', 'USER', 'Role that encompasses user permissions', CURRENT_TIMESTAMP);

-- Roles <> Permissions
INSERT INTO roles_permissions (role_id, permission_id)
VALUES ('01K94W96M6KN86HKV7FAA2CGNH', '01K94V2FM7165A762YR3V29M84');

INSERT INTO roles_permissions (role_id, permission_id)
VALUES ('01K94W96M6KN86HKV7FAA2CGNH', '01K94W6MAV906W5EPR63RB7K77');