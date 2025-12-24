CREATE DATABASE eventdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE eventdb;

CREATE TABLE events (
  event_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  date DATETIME NULL,
  end_date DATETIME NULL,
  location VARCHAR(255),
  description TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE guests (
  guest_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255),
  job VARCHAR(100)
);

CREATE TABLE employees (
  employee_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  role VARCHAR(100)
);

CREATE TABLE participants (
  participant_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  check_in DATETIME NULL,
  check_out DATETIME NULL
);

CREATE TABLE users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'STAFF'
);

CREATE TABLE guest_event (
  guest_id INT, event_id INT,
  PRIMARY KEY (guest_id, event_id),
  FOREIGN KEY (guest_id) REFERENCES guests(guest_id) ON DELETE CASCADE,
  FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE
);

CREATE TABLE event_employee (
  event_id INT, employee_id INT,
  PRIMARY KEY (event_id, employee_id),
  FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
  FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE
);

CREATE TABLE participant_event (
  participant_id INT, event_id INT,
  PRIMARY KEY (participant_id, event_id),
  FOREIGN KEY (participant_id) REFERENCES participants(participant_id) ON DELETE CASCADE,
  FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE
);

INSERT INTO users (username, password, role) VALUES
('admin', '$2a$12$Gyq0jiIb/k6rtsaG4jca5./YsMv8svShvC5U4n0EoV4bZAd0FpuyG', 'admin'),
('staff', '$2a$12$Qt5VRdsbYOxixN2xGN.FduQbZk4RkgUvatOda9iMT7HzpDrpZL6ze', 'staff');