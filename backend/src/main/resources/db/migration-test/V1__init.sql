
CREATE TABLE member (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        created_at TIMESTAMP(6) DEFAULT NULL,
                        updated_at TIMESTAMP(6) DEFAULT NULL,
                        image_url TEXT DEFAULT NULL,
                        is_deleted BIT(1) DEFAULT NULL,
                        nickname VARCHAR(20) NOT NULL,
                        code VARCHAR(36) NOT NULL UNIQUE,
                        PRIMARY KEY (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

CREATE TABLE category (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          created_at TIMESTAMP(6) DEFAULT NULL,
                          updated_at TIMESTAMP(6) DEFAULT NULL,
                          description TEXT DEFAULT NULL,
                          end_at DATE NULL,
                          start_at DATE NULL,
                          thumbnail_url TEXT DEFAULT NULL,
                          title VARCHAR(50) NOT NULL,
                          color ENUM(
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
    ) NOT NULL DEFAULT 'GRAY',
                          PRIMARY KEY (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

CREATE TABLE category_member (
                                 id BIGINT NOT NULL AUTO_INCREMENT,
                                 created_at TIMESTAMP(6) DEFAULT NULL,
                                 updated_at TIMESTAMP(6) DEFAULT NULL,
                                 member_id BIGINT NOT NULL,
                                 category_id BIGINT NOT NULL,
                                 PRIMARY KEY (id),
                                 CONSTRAINT fk_category_member_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                                 CONSTRAINT fk_category_member_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

CREATE TABLE staccato (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          created_at TIMESTAMP(6) DEFAULT NULL,
                          updated_at TIMESTAMP(6) DEFAULT NULL,
                          title VARCHAR(255) NOT NULL,
                          address VARCHAR(255) NOT NULL,
                          place_name VARCHAR(255) NOT NULL,
                          latitude DECIMAL(16,14) NOT NULL,
                          longitude DECIMAL(17,14) NOT NULL,
                          visited_at DATETIME(6) NOT NULL,
                          category_id BIGINT NOT NULL,
                          feeling ENUM('ANGRY', 'EXCITED', 'HAPPY', 'NOTHING', 'SAD', 'SCARED') NOT NULL,
                          PRIMARY KEY (id),
                          CONSTRAINT fk_staccato_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

CREATE TABLE staccato_image (
                                id BIGINT NOT NULL AUTO_INCREMENT,
                                image_url TEXT DEFAULT NULL,
                                staccato_id BIGINT NOT NULL,
                                PRIMARY KEY (id),
                                CONSTRAINT fk_staccato_image_staccato FOREIGN KEY (staccato_id) REFERENCES staccato(id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

CREATE TABLE comment (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         created_at TIMESTAMP(6) DEFAULT NULL,
                         updated_at TIMESTAMP(6) DEFAULT NULL,
                         content TEXT NOT NULL,
                         member_id BIGINT NOT NULL,
                         staccato_id BIGINT NOT NULL,
                         PRIMARY KEY (id),
                         CONSTRAINT fk_comment_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                         CONSTRAINT fk_comment_staccato FOREIGN KEY (staccato_id) REFERENCES staccato(id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

CREATE INDEX idx_nickname ON member (nickname);
CREATE INDEX idx_code ON member (code);
CREATE INDEX idx_title ON category (title);
CREATE INDEX idx_member_id_category_id ON category_member (member_id, category_id);
CREATE INDEX idx_category_id_visited_at ON staccato (category_id, visited_at);
