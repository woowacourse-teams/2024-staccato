-- 1. 기존 NULL 값이 있다면 GRAY로 채우기
UPDATE category
SET color = 'GRAY'
WHERE color IS NULL;

-- 2. 컬럼 DROP & 재추가 방식으로 ENUM 변경
ALTER TABLE category
    MODIFY COLUMN color ENUM(
    'LIGHT_RED', 'RED',
    'LIGHT_ORANGE', 'ORANGE',
    'LIGHT_YELLOW', 'YELLOW',
    'LIGHT_GREEN', 'GREEN',
    'LIGHT_MINT', 'MINT',
    'LIGHT_BLUE', 'BLUE',
    'LIGHT_INDIGO', 'INDIGO',
    'LIGHT_PURPLE', 'PURPLE',
    'LIGHT_PINK', 'PINK',
    'LIGHT_BROWN', 'BROWN',
    'LIGHT_GRAY', 'GRAY'
    ) NOT NULL DEFAULT 'GRAY';
