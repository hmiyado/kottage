ALTER TABLE Comments
    ADD name VARCHAR(100) NOT NULL,
    MODIFY author BIGINT NULL,
    ADD UNIQUE KEY commentIdUniqueKey (entry, idByEntry);
