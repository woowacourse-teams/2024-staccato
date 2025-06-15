-- add created_by, modified_by column
ALTER TABLE staccato
    ADD COLUMN created_by BIGINT,
ADD COLUMN modified_by BIGINT;

-- set existing staccato's created_by, modified_by with category host's id
UPDATE staccato s
    JOIN (
    SELECT cm.category_id, cm.member_id
    FROM category_member cm
    WHERE cm.role = 'HOST'
    ) host_cm ON s.category_id = host_cm.category_id
    SET s.created_by = host_cm.member_id,
        s.modified_by = host_cm.member_id;

-- add not null constraint
ALTER TABLE staccato
    MODIFY created_by BIGINT NOT NULL,
    MODIFY modified_by BIGINT NOT NULL;
