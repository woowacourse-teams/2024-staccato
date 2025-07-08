CREATE TABLE category_invitation (
                                     id BIGINT NOT NULL AUTO_INCREMENT,
                                     category_id BIGINT NOT NULL,
                                     inviter_id BIGINT NOT NULL,
                                     invitee_id BIGINT NOT NULL,
                                     status ENUM('REQUESTED', 'APPROVED', 'REJECTED', 'CANCELED') NOT NULL,
                                     created_at TIMESTAMP(6) DEFAULT NULL,
                                     updated_at TIMESTAMP(6) DEFAULT NULL,
                                     PRIMARY KEY (id),
                                     CONSTRAINT fk_category_invitation_category FOREIGN KEY (category_id) REFERENCES category(id),
                                     CONSTRAINT fk_category_invitation_inviter FOREIGN KEY (inviter_id) REFERENCES member(id),
                                     CONSTRAINT fk_category_invitation_invitee FOREIGN KEY (invitee_id) REFERENCES member(id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
