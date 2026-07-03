--- ADD USER FK TO ORDER ---
ALTER TABLE tb_order ADD CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES tb_user(id);

--- USERS ---
INSERT INTO tb_user (name, email, phone, date_of_birth, password) VALUES ('Maria Brown', 'maria@gmail.com', '98888888888', '2001-07-25', '$argon2id$v=19$m=16384,t=2,p=1$e2/Vof4sdyYV9DadKYgE2Q$OwD7aYMKPCfevm7fgxmIjc0pDcb0dRHoBb1pHx9YU28');
INSERT INTO tb_user (name, email, phone, date_of_birth, password) VALUES ('Alex Green', 'alex@gmail.com', '97777777777', '1987-12-13', '$argon2id$v=19$m=16384,t=2,p=1$e2/Vof4sdyYV9DadKYgE2Q$OwD7aYMKPCfevm7fgxmIjc0pDcb0dRHoBb1pHx9YU28');

--- ORDERS ---
INSERT INTO tb_order(created_at, status, user_id) VALUES ('2020-07-25 13:00:00', 'WAITING_PAYMENT', 1);
INSERT INTO tb_order(created_at, status, user_id) VALUES ('2020-07-29 15:50:00', 'WAITING_PAYMENT', 2);
INSERT INTO tb_order(created_at, status, user_id) VALUES ('2020-08-03 14:20:00', 'WAITING_PAYMENT', 1);
