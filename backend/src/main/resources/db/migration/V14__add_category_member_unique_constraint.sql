ALTER TABLE category_member
    ADD CONSTRAINT uk_category_member
        UNIQUE (category_id, member_id);
