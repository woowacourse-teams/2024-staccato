CREATE TABLE comment (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         created_at TIMESTAMP(6) DEFAULT NULL,
                         updated_at TIMESTAMP(6) DEFAULT NULL,
                         content TEXT NOT NULL,
                         member_id BIGINT NOT NULL,
                         moment_id BIGINT NOT NULL,
                         PRIMARY KEY (id),
                         KEY member_id_idx (member_id),
                         KEY moment_id_idx (moment_id)
);

CREATE TABLE member (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        created_at TIMESTAMP(6) DEFAULT NULL,
                        updated_at TIMESTAMP(6) DEFAULT NULL,
                        image_url TEXT DEFAULT NULL,
                        is_deleted BIT(1) DEFAULT NULL,
                        nickname VARCHAR(20) NOT NULL,
                        PRIMARY KEY (id)
);

CREATE TABLE memory (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        created_at TIMESTAMP(6) DEFAULT NULL,
                        updated_at TIMESTAMP(6) DEFAULT NULL,
                        description TEXT DEFAULT NULL,
                        end_at DATE NOT NULL,
                        start_at DATE NOT NULL,
                        thumbnail_url TEXT DEFAULT NULL,
                        title VARCHAR(50) NOT NULL,
                        PRIMARY KEY (id)
);

CREATE TABLE memory_member (
                               id BIGINT NOT NULL AUTO_INCREMENT,
                               created_at TIMESTAMP(6) DEFAULT NULL,
                               updated_at TIMESTAMP(6) DEFAULT NULL,
                               member_id BIGINT NOT NULL,
                               memory_id BIGINT NOT NULL,
                               PRIMARY KEY (id),
                               KEY member_id_idx (member_id),
                               KEY memory_id_idx (memory_id)
);

CREATE TABLE moment (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        created_at TIMESTAMP(6) DEFAULT NULL,
                        updated_at TIMESTAMP(6) DEFAULT NULL,
                        place_name VARCHAR(255) NOT NULL,
                        address VARCHAR(255) NOT NULL,
                        latitude DECIMAL(16,14) NOT NULL,
                        longitude DECIMAL(17,14) NOT NULL,
                        visited_at DATETIME(6) NOT NULL,
                        memory_id BIGINT NOT NULL,
                        feeling ENUM('ANGRY', 'EXCITED', 'HAPPY', 'NOTHING', 'SAD', 'SCARED') NOT NULL,
                        PRIMARY KEY (id),
                        KEY memory_id_idx (memory_id)
);

CREATE TABLE moment_image (
                              id BIGINT NOT NULL AUTO_INCREMENT,
                              image_url TEXT DEFAULT NULL,
                              moment_id BIGINT NOT NULL,
                              PRIMARY KEY (id),
                              KEY moment_id_idx (moment_id)
);
