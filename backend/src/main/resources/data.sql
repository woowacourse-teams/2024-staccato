-- 데이터 삽입 스크립트
-- member 테이블 데이터 삽입
INSERT INTO member (nickname, image_url, created_at, updated_at, is_deleted)
VALUES ('staccato', 'https://kin-phinf.pstatic.net/20201211_170/1607677307926ezvSd_JPEG/1607677307483.jpg?type=w750',
        '2024-08-06T14:50:30.260267', '2024-08-06T14:50:30.260267', FALSE),
       ('Bob', 'https://kin-phinf.pstatic.net/20201211_170/1607677307926ezvSd_JPEG/1607677307483.jpg?type=w750',
        '2024-08-06T14:50:31.755855', '2024-08-06T14:50:31.755855', FALSE);
-- travel 테이블 데이터 삽입
INSERT INTO travel (title, description, thumbnail_url, start_at, end_at, created_at, updated_at)
VALUES ('Trip to Paris', 'A wonderful trip to Paris',
        'https://as2.ftcdn.net/v2/jpg/05/26/12/07/1000_F_526120779_zO3klHfSGF7X6k0UZUbtXJuXo7KUJibv.jpg', '2024-08-01',
        '2024-08-10', NOW(), NOW()),
       ('Mountain Hiking', 'Hiking in the mountains',
        'https://as2.ftcdn.net/v2/jpg/05/26/12/07/1000_F_526120779_zO3klHfSGF7X6k0UZUbtXJuXo7KUJibv.jpg', '2024-09-01',
        '2024-09-05', NOW(), NOW()),
       ('Beach Vacation', 'Relaxing on the beach',
        'https://as2.ftcdn.net/v2/jpg/05/26/12/07/1000_F_526120779_zO3klHfSGF7X6k0UZUbtXJuXo7KUJibv.jpg', '2024-07-15',
        '2024-07-20', NOW(), NOW()),
       ('City Tour', 'Exploring the city',
        'https://as2.ftcdn.net/v2/jpg/05/26/12/07/1000_F_526120779_zO3klHfSGF7X6k0UZUbtXJuXo7KUJibv.jpg', '2024-06-10',
        '2024-06-12', NOW(), NOW()),
       ('Desert Adventure', 'An adventure in the desert',
        'https://as2.ftcdn.net/v2/jpg/05/26/12/07/1000_F_526120779_zO3klHfSGF7X6k0UZUbtXJuXo7KUJibv.jpg', '2024-05-01',
        '2024-05-05', NOW(), NOW());
-- travel_member 테이블 데이터 삽입
INSERT INTO travel_member (member_id, travel_id, created_at, updated_at)
VALUES (1, 1, NOW(), NOW()),
       (1, 2, NOW(), NOW()),
       (2, 1, NOW(), NOW()),
       (2, 3, NOW(), NOW()),
       (1, 3, NOW(), NOW()),
       (2, 4, NOW(), NOW());
-- visit 테이블 데이터 삽입
INSERT INTO visit (place_name, address, latitude, longitude, visited_at, travel_id, created_at, updated_at)
VALUES ('Eiffel Tower', 'Champ de Mars, 5 Avenue Anatole France, 75007 Paris, France', 48.8588443, 2.2943506,
        '2024-08-06T14:50:31.755855', 1, NOW(), NOW()),
       ('Louvre Museum', 'Rue de Rivoli, 75001 Paris, France', 48.8606111, 2.337644, '2024-08-07T14:50:31.755855', 1, NOW(), NOW()),
       ('Notre Dame', '6 Parvis Notre-Dame - Pl. Jean-Paul II, 75004 Paris, France', 48.85296820000001, 2.3499021,
        '2024-08-08T14:50:31.755855', 1, NOW(), NOW()),
       ('Mont Blanc', 'Mont Blanc, France', 45.832622, 6.865150, '2024-08-09T14:50:31.755855', 2, NOW(), NOW()),
       ('Matterhorn', 'Matterhorn, Zermatt, Switzerland', 45.9763, 7.6586, '2024-09-03T14:50:31.755855', 2, NOW(), NOW()),
       ('Beachside Resort', '123 Beach Rd, Malibu, CA', 34.025922, -118.779757, '2024-07-16T14:50:31.755855', 3, NOW(), NOW()),
       ('Venice Beach', 'Venice Beach, Los Angeles, CA', 33.9850, -118.4695, '2024-07-17T14:50:31.755855', 3, NOW(), NOW()),
       ('Old City', '456 Old City St, Example City, EX', 35.6895, 139.6917, '2024-06-11T14:50:31.755855', 4, NOW(), NOW()),
       ('Tokyo Tower', '4 Chome-2-8 Shibakoen, Minato City, Tokyo 105-0011, Japan', 35.6585805, 139.7454329,
        '2024-06-12T14:50:31.755855', 4, NOW(), NOW()),
       ('Desert Dunes', '789 Desert Rd, Sahara', 23.4162, 25.6628, '2024-05-02T14:50:31.755855', 5, NOW(), NOW());
-- visit_image 테이블 데이터 삽입
INSERT INTO visit_image (visit_id, image_url)
VALUES (1, 'https://as2.ftcdn.net/v2/jpg/05/26/12/07/1000_F_526120779_zO3klHfSGF7X6k0UZUbtXJuXo7KUJibv.jpg'),
       (2, 'https://as2.ftcdn.net/v2/jpg/05/26/12/07/1000_F_526120779_zO3klHfSGF7X6k0UZUbtXJuXo7KUJibv.jpg'),
       (3, 'https://as2.ftcdn.net/v2/jpg/05/26/12/07/1000_F_526120779_zO3klHfSGF7X6k0UZUbtXJuXo7KUJibv.jpg'),
       (4, 'https://as2.ftcdn.net/v2/jpg/05/26/12/07/1000_F_526120779_zO3klHfSGF7X6k0UZUbtXJuXo7KUJibv.jpg'),
       (5, 'https://as2.ftcdn.net/v2/jpg/05/26/12/07/1000_F_526120779_zO3klHfSGF7X6k0UZUbtXJuXo7KUJibv.jpg'),
       (6, 'https://as2.ftcdn.net/v2/jpg/05/26/12/07/1000_F_526120779_zO3klHfSGF7X6k0UZUbtXJuXo7KUJibv.jpg'),
       (7, 'https://as2.ftcdn.net/v2/jpg/05/26/12/07/1000_F_526120779_zO3klHfSGF7X6k0UZUbtXJuXo7KUJibv.jpg'),
       (8, 'https://as2.ftcdn.net/v2/jpg/05/26/12/07/1000_F_526120779_zO3klHfSGF7X6k0UZUbtXJuXo7KUJibv.jpg'),
       (9, 'https://as2.ftcdn.net/v2/jpg/05/26/12/07/1000_F_526120779_zO3klHfSGF7X6k0UZUbtXJuXo7KUJibv.jpg'),
       (10, 'https://as2.ftcdn.net/v2/jpg/05/26/12/07/1000_F_526120779_zO3klHfSGF7X6k0UZUbtXJuXo7KUJibv.jpg');
-- visit_log 테이블 데이터 삽입
INSERT INTO visit_log (member_id, visit_id, content, created_at, updated_at)
VALUES (1, 1, 'Visited the Eiffel Tower today. Amazing view!', NOW(), NOW()),
       (2, 2, 'Spent the day at the Louvre. Incredible art!', NOW(), NOW()),
       (1, 3, 'Hiking Mont Blanc was a great experience.', NOW(), NOW()),
       (2, 4, 'Enjoyed a sunny day at the Matterhorn.', NOW(), NOW()),
       (1, 5, 'Relaxed at the Beachside Resort. Beautiful!', NOW(), NOW()),
       (2, 6, 'Explored the Old City. So much history.', NOW(), NOW());
