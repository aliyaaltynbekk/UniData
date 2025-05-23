GRANT ALL ON SCHEMA public TO "UniData";

GRANT CREATE ON SCHEMA public TO "UniData";

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "UniData";
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO "UniData";

GRANT ALL PRIVILEGES ON TABLE system_logs TO "UniData";

ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT ALL ON TABLES TO "UniData";

ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT ALL ON SEQUENCES TO "UniData";

CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE,
                       description TEXT,
                       role_type VARCHAR(20) NOT NULL,
                       CONSTRAINT role_type_check CHECK (role_type IN ('ADMIN', 'TEACHER', 'STUDENT', 'GUEST'))
);

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(100) NOT NULL,
                       full_name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       phone_number VARCHAR(20),
                       registration_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       last_login_time TIMESTAMP,
                       active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            role_id BIGINT NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                            CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

CREATE TABLE messages (
                          id BIGSERIAL PRIMARY KEY,
                          sender_id BIGINT,
                          receiver_id BIGINT,
                          subject VARCHAR(255) NOT NULL,
                          content TEXT NOT NULL,
                          sent_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          read_time TIMESTAMP,
                          read BOOLEAN NOT NULL DEFAULT FALSE,
                          message_type VARCHAR(20) NOT NULL,
                          CONSTRAINT message_type_check CHECK (message_type IN ('PERSONAL', 'GROUP', 'SYSTEM', 'ANNOUNCEMENT')),
                          CONSTRAINT fk_messages_sender FOREIGN KEY (sender_id) REFERENCES users (id) ON DELETE SET NULL,
                          CONSTRAINT fk_messages_receiver FOREIGN KEY (receiver_id) REFERENCES users (id) ON DELETE SET NULL
);

CREATE TABLE data (
                      id BIGSERIAL PRIMARY KEY,
                      faculty_name VARCHAR(100) NOT NULL,
                      academic_year_start DATE,
                      academic_year_end DATE,
                      education_form VARCHAR(50),
                      education_level VARCHAR(20),
                      CONSTRAINT education_level_check CHECK (education_level IN ('BACHELOR', 'MASTER', 'PHD'))
);

CREATE TABLE audit_logs (
                            id BIGSERIAL PRIMARY KEY,
                            user_id BIGINT,
                            action_type VARCHAR(50) NOT NULL,
                            description TEXT,
                            timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            ip_address VARCHAR(50),
                            browser VARCHAR(255),
                            successful BOOLEAN NOT NULL,
                            object_type VARCHAR(50),
                            object_id BIGINT,
                            CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL
);

CREATE TABLE departments (
                             data_id BIGINT NOT NULL,
                             department_name VARCHAR(100) NOT NULL,
                             PRIMARY KEY (data_id, department_name),
                             CONSTRAINT fk_departments_data FOREIGN KEY (data_id) REFERENCES data (id) ON DELETE CASCADE
);

CREATE TABLE specialties (
                             data_id BIGINT NOT NULL,
                             code VARCHAR(20) NOT NULL,
                             name VARCHAR(100) NOT NULL,
                             PRIMARY KEY (data_id, code),
                             CONSTRAINT fk_specialties_data FOREIGN KEY (data_id) REFERENCES data (id) ON DELETE CASCADE
);

CREATE TABLE data_teachers (
                               data_id BIGINT NOT NULL,
                               user_id BIGINT NOT NULL,
                               PRIMARY KEY (data_id, user_id),
                               CONSTRAINT fk_data_teachers_data FOREIGN KEY (data_id) REFERENCES data (id) ON DELETE CASCADE,
                               CONSTRAINT fk_data_teachers_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE data_students (
                               data_id BIGINT NOT NULL,
                               user_id BIGINT NOT NULL,
                               PRIMARY KEY (data_id, user_id),
                               CONSTRAINT fk_data_students_data FOREIGN KEY (data_id) REFERENCES data (id) ON DELETE CASCADE,
                               CONSTRAINT fk_data_students_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE system_logs (
                             id BIGSERIAL PRIMARY KEY,
                             timestamp TIMESTAMP NOT NULL,
                             level VARCHAR(255) NOT NULL,
                             username VARCHAR(100),
                             user_id BIGINT,
                             action VARCHAR(255) NOT NULL,
                             ip_address VARCHAR(255),
                             user_agent VARCHAR(500),
                             session_id VARCHAR(255),
                             details VARCHAR(1000),
                             stack_trace VARCHAR(4000),
                             CONSTRAINT fk_system_logs_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL
);




CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_messages_sender ON messages (sender_id);
CREATE INDEX idx_messages_receiver ON messages (receiver_id);
CREATE INDEX idx_messages_read ON messages (read);
CREATE INDEX idx_audit_logs_user ON audit_logs (user_id);
CREATE INDEX idx_audit_logs_timestamp ON audit_logs (timestamp);
CREATE INDEX idx_audit_logs_action_type ON audit_logs (action_type);




