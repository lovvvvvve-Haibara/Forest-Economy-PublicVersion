-- High-concurrency data integrity baseline (MySQL)
-- Execute after cleaning duplicate dirty data.

ALTER TABLE `User`
    ADD UNIQUE KEY `uk_user_username` (`username`);

ALTER TABLE `User`
    ADD UNIQUE KEY `uk_user_phone_number` (`phoneNumber`);

-- Optional: if login mainly uses phone, this helps point query performance.
CREATE INDEX `idx_user_phone` ON `User` (`phoneNumber`);
