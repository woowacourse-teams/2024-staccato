-- 0. 기존 외래키 제거
ALTER TABLE comment DROP FOREIGN KEY comment_ibfk_1;
ALTER TABLE comment DROP FOREIGN KEY comment_ibfk_2;
ALTER TABLE memory_member DROP FOREIGN KEY memory_member_ibfk_1;
ALTER TABLE memory_member DROP FOREIGN KEY memory_member_ibfk_2;
ALTER TABLE moment DROP FOREIGN KEY moment_ibfk_1;
ALTER TABLE moment_image DROP FOREIGN KEY moment_image_ibfk_1;

-- 1. 기존 인덱스 제거
DROP INDEX idx_nickname ON member;
DROP INDEX idx_code ON member;
DROP INDEX idx_title ON memory;
DROP INDEX idx_member_id_memory_id ON memory_member;
DROP INDEX idx_memory_id_visited_at ON moment;

-- 2. 테이블명 변경 (memory -> category, moment -> staccato)
RENAME TABLE memory TO category;
RENAME TABLE moment TO staccato;
RENAME TABLE memory_member TO category_member;
RENAME TABLE moment_image TO staccato_image;

-- 3. 컬럼명 변경 (memory_id -> category_id, moment_id -> staccato_id)
ALTER TABLE comment CHANGE moment_id staccato_id BIGINT NOT NULL;
ALTER TABLE comment DROP INDEX moment_id, ADD KEY staccato_id (staccato_id);

ALTER TABLE category_member CHANGE memory_id category_id BIGINT NOT NULL;
ALTER TABLE category_member DROP INDEX memory_id, ADD KEY category_id (category_id);

ALTER TABLE staccato CHANGE memory_id category_id BIGINT NOT NULL;
ALTER TABLE staccato DROP INDEX memory_id, ADD KEY category_id (category_id);

ALTER TABLE staccato_image CHANGE moment_id staccato_id BIGINT NOT NULL;
ALTER TABLE staccato_image DROP INDEX moment_id, ADD KEY staccato_id (staccato_id);

-- 4. 외래키 재설정
ALTER TABLE comment
    ADD CONSTRAINT fk_comment_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_comment_staccato FOREIGN KEY (staccato_id) REFERENCES staccato(id) ON DELETE CASCADE;

ALTER TABLE category_member
    ADD CONSTRAINT fk_category_member_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_category_member_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE;

ALTER TABLE staccato
    ADD CONSTRAINT fk_staccato_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE;

ALTER TABLE staccato_image
    ADD CONSTRAINT fk_staccato_image_staccato FOREIGN KEY (staccato_id) REFERENCES staccato(id) ON DELETE CASCADE;

-- 5. 인덱스 재설정
create index idx_nickname on member (nickname);
create index idx_code on member (code);
create index idx_title on category (title);
create index idx_member_id_category_id on category_member (member_id, category_id);
create index idx_category_id_visited_at on staccato (category_id, visited_at);
