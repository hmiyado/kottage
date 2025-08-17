CREATE TABLE OidcTokens (
    `user` BIGINT NOT NULL,
    issuer longtext NOT NULL,
    subject VARCHAR(255) NOT NULL,
    audience longtext NOT NULL,
    expiration DATETIME(6) NOT NULL,
    issuedAt DATETIME(6) NOT NULL,
    CONSTRAINT fk_OidcTokens_user_id FOREIGN KEY (`user`) REFERENCES Users(id) ON DELETE CASCADE
);
