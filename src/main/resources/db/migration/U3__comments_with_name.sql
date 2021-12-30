ALTER TABLE Comments
    DROP name,
    MODIFY author BIGINT NOT NULL,
    DROP KEY commentIdUniqueKey;
