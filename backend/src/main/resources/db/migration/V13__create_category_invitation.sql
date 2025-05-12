CREATE TABLE category_invitation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    inviter_id BIGINT NOT NULL,
    invitee_id BIGINT NOT NULL,
    status ENUM('REQUESTED', 'APPROVED', 'REJECTED', 'CANCELED') NOT NULL,
    created_at TIMESTAMP(6) DEFAULT NULL,
    updated_at TIMESTAMP(6) DEFAULT NULL,

    FOREIGN KEY (category_id) REFERENCES category(id),
    FOREIGN KEY (inviter_id) REFERENCES member(id),
    FOREIGN KEY (invitee_id) REFERENCES member(id)
);
