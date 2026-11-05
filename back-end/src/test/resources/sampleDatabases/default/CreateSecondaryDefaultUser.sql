INSERT INTO users(username, password, enabled, firstname, lastname)
    VALUES ('Perpustakaan', '$2a$10$16EleubfQ7SHgjO36pi73ueAp43rD.mutGOLnjJkJM65Wdz4Du8aW', true, 'Perpus', 'Takaan');
/*
 Perpustakaan's password is ILikeJavaSwing
 */

INSERT INTO authorities (authority, username)
    VALUES ('ROLE_USER', 'Perpustakaan');
