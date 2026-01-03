CREATE TABLE users (
    id                  VARCHAR(26)  PRIMARY KEY,
    email               VARCHAR(255) NOT NULL,
    password            VARCHAR(255),
    authentication_type VARCHAR(25)  NOT NULL,
    is_verified         BOOLEAN      NOT NULL,
    created_at          TIMESTAMP    NOT NULL,
    updated_at          TIMESTAMP
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
    created_at         TIMESTAMP    NOT NULL,
    updated_at         TIMESTAMP
);

CREATE TABLE sessions (
    id            VARCHAR(26)  PRIMARY KEY,
    refresh_token VARCHAR(255) NOT NULL,
    user_id       VARCHAR(26)  NOT NULL,
    expire_at     TIMESTAMP    NOT NULL,
    created_at    TIMESTAMP    NOT NULL,
    revoked_at    TIMESTAMP,
    updated_at    TIMESTAMP,
    CONSTRAINT fk_sessions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE users_roles (
    user_id VARCHAR(26),
    role_id VARCHAR(26),
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE roles_permissions (
    role_id       VARCHAR(26),
    permission_id VARCHAR(26),
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id)       REFERENCES roles(id)       ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);