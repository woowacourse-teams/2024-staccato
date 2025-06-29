-- add created_by, modified_by column
ALTER TABLE staccato
    ADD COLUMN created_by BIGINT,
ADD COLUMN modified_by BIGINT;

-- TODO: add not null constraint after batch update for setting existing staccato's created_by, modified_by with category host's id
