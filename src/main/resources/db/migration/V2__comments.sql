CREATE TABLE Comments (
    idByEntry BIGINT NOT NULL,
    body longtext NOT NULL,
    createdAt DATETIME(6) NOT NULL,
    entry BIGINT NOT NULL,
    author BIGINT NOT NULL,
    CONSTRAINT fk_Comments_entry_id FOREIGN KEY (entry) REFERENCES Entries(id) ON DELETE CASCADE,
    CONSTRAINT fk_Comments_author_id FOREIGN KEY (author) REFERENCES Users(id) ON DELETE CASCADE
);
