CREATE TABLE users (
    id                  VARCHAR(26)  PRIMARY KEY,
    email               VARCHAR(255) NOT NULL,
    password            VARCHAR(255),
    authentication_type VARCHAR(25)  NOT NULL,
    is_verified         BOOLEAN      NOT NULL,
    created_at          TIMESTAMP    NOT NULL,
    updated_at          TIMESTAMP
);

CREATE TABLE resource_servers (
    id               VARCHAR(26)  PRIMARY KEY,
    name             VARCHAR(50)  NOT NULL,
    description      VARCHAR(255),
    created_at       TIMESTAMP    NOT NULL,
    updated_at       TIMESTAMP
);

CREATE TABLE roles (
    id            VARCHAR(26)  PRIMARY KEY,
    name          VARCHAR(25)  NOT NULL,
    description   VARCHAR(255),
    created_at    TIMESTAMP    NOT NULL,
    updated_at    TIMESTAMP
);

CREATE TABLE permissions (
    id                 VARCHAR(26)  PRIMARY KEY,
    resource           VARCHAR(25)  NOT NULL,
    verb               VARCHAR(25)  NOT NULL,
    description        VARCHAR(255),
    resource_server_id VARCHAR(26)  NOT NULL,
    created_at         TIMESTAMP    NOT NULL,
    updated_at         TIMESTAMP,
    CONSTRAINT fk_permissions_resource_server
        FOREIGN KEY (resource_server_id) REFERENCES resource_servers(id) ON DELETE CASCADE
);

CREATE TABLE policies (
    id                 VARCHAR(26)  PRIMARY KEY,
    method             VARCHAR(10)  NOT NULL,
    uri                VARCHAR(255) NOT NULL,
    is_public          BOOLEAN      NOT NULL,
    resource_server_id VARCHAR(26)  NOT NULL,
    created_at         TIMESTAMP    NOT NULL,
    updated_at         TIMESTAMP,
    CONSTRAINT fk_policies_resource_server
        FOREIGN KEY (resource_server_id) REFERENCES resource_servers(id) ON DELETE CASCADE
);

CREATE TABLE clients (
    id          VARCHAR(26)  PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL,
    description VARCHAR(255),
    secret      VARCHAR(64)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP
);

CREATE TABLE sessions (
    id            VARCHAR(26)  PRIMARY KEY,
    refresh_token VARCHAR(255) NOT NULL,
    user_id       VARCHAR(26)  NOT NULL,
    client_id     VARCHAR(26)  NOT NULL,
    expire_at     TIMESTAMP    NOT NULL,
    created_at    TIMESTAMP    NOT NULL,
    revoked_at    TIMESTAMP,
    updated_at    TIMESTAMP,
    CONSTRAINT fk_sessions_user   FOREIGN KEY (user_id)   REFERENCES users(id)   ON DELETE CASCADE,
    CONSTRAINT fk_sessions_client FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
);

CREATE TABLE users_roles (
    user_id VARCHAR(26),
    role_id VARCHAR(26),
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE roles_permissions (
    role_id       VARCHAR(26),
    permission_id VARCHAR(26),
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id)       REFERENCES roles(id),
    FOREIGN KEY (permission_id) REFERENCES permissions(id)
);

CREATE TABLE policies_roles (
    policy_id VARCHAR(26),
    role_id   VARCHAR(26),
    PRIMARY KEY (policy_id, role_id),
    FOREIGN KEY (policy_id) REFERENCES policies(id),
    FOREIGN KEY (role_id)   REFERENCES roles(id)
);

CREATE TABLE policies_permissions (
    policy_id     VARCHAR(26),
    permission_id VARCHAR(26),
    PRIMARY KEY (policy_id, permission_id),
    FOREIGN KEY (policy_id)     REFERENCES policies(id),
    FOREIGN KEY (permission_id) REFERENCES permissions(id)
);

CREATE TABLE clients_resource_servers (
    client_id          VARCHAR(26),
    resource_server_id VARCHAR(26),
    PRIMARY KEY (client_id, resource_server_id),
    FOREIGN KEY (client_id)          REFERENCES clients(id),
    FOREIGN KEY (resource_server_id) REFERENCES resource_servers(id)
);