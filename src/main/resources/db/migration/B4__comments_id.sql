CREATE TABLE Users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    screenName VARCHAR(100) NOT NULL
);
CREATE TABLE Entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    body longtext NOT NULL,
    dateTime DATETIME(6) NOT NULL,
    author BIGINT NOT NULL,
    CONSTRAINT fk_Entries_author_id FOREIGN KEY (author) REFERENCES Users(id)
);
CREATE TABLE Passwords (
    `user` BIGINT NOT NULL,
    password longtext NOT NULL,
    salt longtext NOT NULL,
    CONSTRAINT fk_Passwords_user_id FOREIGN KEY (`user`) REFERENCES Users(id) ON DELETE CASCADE
);
CREATE TABLE Admins (
    `user` BIGINT NOT NULL,
    CONSTRAINT fk_Admins_user_id FOREIGN KEY (`user`) REFERENCES Users(id) ON DELETE CASCADE
);
CREATE TABLE Comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    idByEntry BIGINT NOT NULL,
    body longtext NOT NULL,
    createdAt DATETIME(6) NOT NULL,
    entry BIGINT NOT NULL,
    author BIGINT DEFAULT NULL,
    `name` varchar(100) NOT NULL,
    CONSTRAINT fk_Comments_entry_id FOREIGN KEY (entry) REFERENCES Entries(id) ON DELETE CASCADE,
    CONSTRAINT fk_Comments_author_id FOREIGN KEY (author) REFERENCES Users(id) ON DELETE CASCADE
);
