CREATE TABLE users (
    id                  VARCHAR(26)  PRIMARY KEY,
    email               VARCHAR(255) NOT NULL UNIQUE,
    password            VARCHAR(255),
    authentication_type VARCHAR(25)  NOT NULL,
    is_verified         BOOLEAN      NOT NULL,
    created_at          TIMESTAMP    NOT NULL,
    updated_at          TIMESTAMP
);

CREATE TABLE authorization_codes (
    code           VARCHAR(255) PRIMARY KEY,
    redirect_uri   VARCHAR(255) NOT NULL,
    code_challenge VARCHAR(255) NOT NULL,
    user_id        VARCHAR(26)  NOT NULL,
    created_at     TIMESTAMP    NOT NULL,
    expire_at      TIMESTAMP    NOT NULL,
    updated_at     TIMESTAMP,
    CONSTRAINT fk_auth_code_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE sessions (
    id            VARCHAR(26)  PRIMARY KEY,
    refresh_token VARCHAR(255) NOT NULL UNIQUE,
    user_id       VARCHAR(26)  NOT NULL,
    expire_at     TIMESTAMP    NOT NULL,
    created_at    TIMESTAMP    NOT NULL,
    revoked_at    TIMESTAMP,
    updated_at    TIMESTAMP,
    CONSTRAINT fk_sessions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
