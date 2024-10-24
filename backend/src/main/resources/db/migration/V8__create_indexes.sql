create index idx_nickname on member (nickname);
create index idx_code on member (code);
create index idx_title on memory (title);
create index idx_member_id_memory_id on memory_member (member_id, memory_id);
create index idx_memory_id_visited_at on moment (memory_id, visited_at);
