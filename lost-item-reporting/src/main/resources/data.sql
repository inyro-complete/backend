INSERT INTO users (email, password, name) VALUES ('email1@naver.com', '$2a$10$7QlxmFyYEr0BdHXoh7PYYePqgNDR7iTYfb2KfCz8Ql8jt5h3gb6qG', 'user1');
INSERT INTO users (email, password, name) VALUES ('email2@naver.com', '$2b$12$pEsP9Vehxkk9NtdheGA2HeLsf7UfEXDrhNX/e6AkvzD0X0pIYXDeq', 'user2');


INSERT INTO lost_item (title, description, lost_location, lost_date, image_url, status, loser_id)
VALUES
('지갑', '7016버스에서 지갑을 잃어버렸습니다.', '7016버스', '2025-03-23 00:00:00', 'https://example.com/images/image.png', 'FINDING', 1),
('휴대폰', '지하철에서 휴대폰을 잃어버렸습니다.', '2호선', '2025-03-24 14:30:00', 'https://example.com/images/phone.png', 'FINDING', 2);
