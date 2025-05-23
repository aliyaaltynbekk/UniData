GRANT ALL ON SCHEMA public TO "UniData";

GRANT CREATE ON SCHEMA public TO "UniData";

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "UniData";
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO "UniData";


ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT ALL ON TABLES TO "UniData";

ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT ALL ON SEQUENCES TO "UniData";

-- (Пароль: admin123)
INSERT INTO users (username, password, full_name, email, phone_number, registration_date, active)
VALUES ('admin', '$2a$10$dIBYkTXQivMI6jXbxFwT8.ILiLhJtvRo5fCGXPdxdHvNT/yO2/i8y', 'Жүйе Әкімшісі', 'admin@unidata.kz', '+77001112233', CURRENT_TIMESTAMP, TRUE);

INSERT INTO user_roles (user_id, role_id)
VALUES (
           (SELECT id FROM users WHERE username = 'admin'),
           (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
       );

INSERT INTO roles (name, description, role_type) VALUES
                                                     ('ADMIN', 'Администратор жүйесі', 'ADMIN'),
                                                     ('TEACHER', 'Мұғалім', 'TEACHER'),
                                                     ('STUDENT', 'Студент', 'STUDENT'),
                                                     ('GUEST', 'Қонақ', 'GUEST');