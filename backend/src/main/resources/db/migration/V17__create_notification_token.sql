CREATE TABLE notification_token (
                                    id BIGINT NOT NULL AUTO_INCREMENT,
                                    member_id BIGINT NOT NULL,
                                    token TEXT NOT NULL,
                                    created_at TIMESTAMP(6) DEFAULT NULL,
                                    updated_at TIMESTAMP(6) DEFAULT NULL,
                                    PRIMARY KEY (id),
                                    CONSTRAINT fk_notification_token_member FOREIGN KEY (member_id) REFERENCES member(id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
