CREATE INDEX idx_nickname ON member (nickname);
CREATE INDEX idx_code ON member (code);
CREATE INDEX idx_title ON category (title);
CREATE INDEX idx_member_id_category_id ON category_member (member_id, category_id);
CREATE INDEX idx_category_id_visited_at ON staccato (category_id, visited_at);
