CREATE TABLE notification_token
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    token       TEXT         NOT NULL,
    member_id   BIGINT       NOT NULL,
    device_type VARCHAR(255) NOT NULL,
    device_id   VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP(6) DEFAULT NULL,
    updated_at  TIMESTAMP(6) DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_notification_token_member FOREIGN KEY (member_id) REFERENCES member (id),
    CONSTRAINT uq_notification_token_unique UNIQUE (device_type, device_id, member_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
