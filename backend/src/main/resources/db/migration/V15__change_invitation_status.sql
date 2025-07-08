-- 기존 'APPROVED' 값을 'ACCEPTED'로 업데이트
UPDATE category_invitation
SET status = 'ACCEPTED'
WHERE status = 'APPROVED';

-- ENUM 정의 변경
ALTER TABLE category_invitation
    MODIFY COLUMN status ENUM('REQUESTED', 'CANCELED', 'ACCEPTED', 'REJECTED') NOT NULL;
