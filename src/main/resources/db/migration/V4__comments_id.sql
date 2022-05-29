ALTER TABLE Comments
    ADD id BIGINT AUTO_INCREMENT PRIMARY KEY FIRST,
    # to drop commentIdUniqueKey, drop and re-create foreign key fk_Comments_entry_id
    DROP FOREIGN KEY fk_Comments_entry_id,
    DROP INDEX commentIdUniqueKey;
ALTER TABLE Comments
    DROP idByEntry,
    ADD CONSTRAINT fk_Comments_entry_id FOREIGN KEY (entry) REFERENCES `Entries` (`id`) ON DELETE CASCADE;
