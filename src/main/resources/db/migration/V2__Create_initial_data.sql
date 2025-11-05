-- Resources servers
INSERT INTO resource_servers (id, name, description, created_at)
VALUES ('01K94R96STA0H1677RDZ73QBT9', 'kt-authentication-api', 'Resource server for authorization and authentication', CURRENT_TIMESTAMP);

-- Clients
INSERT INTO clients (id, name, description, secret, created_at)
VALUES ('01K97V58JAZ2F2C5BGEJQVJVT7', 'kt-external-gateway', 'Client for external calls from web and mobile', 'J9rRrOOj0YWwwMokjj15FenrBLbs0uqa2wN2yuLBd60', CURRENT_TIMESTAMP);

-- External/Internal policies
INSERT INTO policies (id, method, uri, is_public, resource_server_id, created_at)
VALUES ('01K94SXK3JFXT4JCPHWMV6V0EM','POST', '/v1/oauth/token', true, '01K94R96STA0H1677RDZ73QBT9', CURRENT_TIMESTAMP);

-- External policies
INSERT INTO policies (id, method, uri, is_public, resource_server_id, created_at)
VALUES ('01K94SXQAF3J1BADBCXFQ29WXR', 'DELETE', '/v1/sessions/[^/]+', false, '01K94R96STA0H1677RDZ73QBT9', CURRENT_TIMESTAMP);

INSERT INTO policies (id, method, uri, is_public, resource_server_id, created_at)
VALUES ('01K94SPYCSRY5H3VZ7TA9YTZPR', 'GET', '/v1/sessions', false, '01K94R96STA0H1677RDZ73QBT9', CURRENT_TIMESTAMP);

INSERT INTO policies (id, method, uri, is_public, resource_server_id, created_at)
VALUES ('01K94SZRR7Y71TGXEKQ4JE1HAH', 'POST', '/v1/users', true, '01K94R96STA0H1677RDZ73QBT9', CURRENT_TIMESTAMP);

-- Internal policies

-- Permission
INSERT INTO permissions (id, resource, verb, description, resource_server_id, created_at)
VALUES ('01K94V2FM7165A762YR3V29M84', 'sessions', 'revoke', 'Permissions to revoke a session', '01K94R96STA0H1677RDZ73QBT9', CURRENT_TIMESTAMP);

INSERT INTO permissions (id, resource, verb, description, resource_server_id, created_at)
VALUES ('01K94W6MAV906W5EPR63RB7K77', 'sessions', 'read', 'Permissions to read a session', '01K94R96STA0H1677RDZ73QBT9', CURRENT_TIMESTAMP);

-- Roles
INSERT INTO roles (id, name, description, created_at)
VALUES ('01K94W96M6KN86HKV7FAA2CGNH', 'USER', 'Role that encompasses user permissions', CURRENT_TIMESTAMP);

-- Roles <> Permissions
INSERT INTO roles_permissions (role_id, permission_id)
VALUES ('01K94W96M6KN86HKV7FAA2CGNH', '01K94V2FM7165A762YR3V29M84');

INSERT INTO roles_permissions (role_id, permission_id)
VALUES ('01K94W96M6KN86HKV7FAA2CGNH', '01K94W6MAV906W5EPR63RB7K77');

-- Policies <> Permissions
INSERT INTO policies_permissions (policy_id, permission_id)
VALUES ('01K94SXQAF3J1BADBCXFQ29WXR', '01K94V2FM7165A762YR3V29M84');

INSERT INTO policies_permissions (policy_id, permission_id)
VALUES ('01K94SPYCSRY5H3VZ7TA9YTZPR', '01K94W6MAV906W5EPR63RB7K77');

-- Policies <> Roles
INSERT INTO policies_roles (policy_id, role_id)
VALUES ('01K94SXQAF3J1BADBCXFQ29WXR', '01K94W96M6KN86HKV7FAA2CGNH');

INSERT INTO policies_roles (policy_id, role_id)
VALUES ('01K94SPYCSRY5H3VZ7TA9YTZPR', '01K94W96M6KN86HKV7FAA2CGNH');

-- Clients <> Policies
INSERT INTO clients_policies (client_id, policy_id)
VALUES ('01K97V58JAZ2F2C5BGEJQVJVT7', '01K94SXQAF3J1BADBCXFQ29WXR');

INSERT INTO clients_policies (client_id, policy_id)
VALUES ('01K97V58JAZ2F2C5BGEJQVJVT7', '01K94SPYCSRY5H3VZ7TA9YTZPR');

INSERT INTO clients_policies (client_id, policy_id)
VALUES ('01K97V58JAZ2F2C5BGEJQVJVT7', '01K94SZRR7Y71TGXEKQ4JE1HAH');