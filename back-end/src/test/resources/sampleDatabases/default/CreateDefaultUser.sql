INSERT INTO users(username, password, enabled, firstname, lastname)
    VALUES ('Pizza', '$2a$10$4i9lo2GJmA.45ZMCuHYgIO4e6Zzla6KllILrRL0wiAHYNEZG8OJyS', true, 'Pizza', 'Romarin');
/*
 Pizza's password is git_add_.env.production
 */

INSERT INTO authorities (authority, username)
    VALUES ('ROLE_USER', 'Pizza');
