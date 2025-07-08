-- 기존 외래 키 제약 조건 제거
ALTER TABLE category_invitation
DROP FOREIGN KEY fk_category_invitation_category;

-- ON DELETE CASCADE 옵션으로 외래 키 다시 생성
ALTER TABLE category_invitation
ADD CONSTRAINT fk_category_invitation_category
FOREIGN KEY (category_id)
REFERENCES category(id)
ON DELETE CASCADE;