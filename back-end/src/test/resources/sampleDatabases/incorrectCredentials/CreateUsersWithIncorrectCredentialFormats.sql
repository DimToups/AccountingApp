INSERT INTO users(username, password, enabled)
VALUES ('', '$2a$10$FmFwcS0Yy14LjxG9iYRIuehWuixtkPrjQH0YFPP63aH0E7HRPAVF.', true),
       (' ', '$2a$10$oPMLjIPbJ6Cm3Rfu0YAm8eoumMrrGZVHSEmXxxm34/3afYoWBFypu', true),
       ('#-/\"', '$2a$10$s41ufiuBwvxMbDLqTYhofeSQKWVCRNW6cXow.nOQzA2BmjdUgOzDK', true),
       ('VeryLongUsernameWithMoreThanSixtyFourCharactersToTestMyApplication', '$2a$10$DhHYHw3p5tTwC4g8ES98rens2FL/8Q/yXJMQqXqhiKM1wkPS2Iezu', true);
/*
  Blank username's password is 'Password'
  space username's password is ' '
  #-/\" password is #-/\Password
  VeryLongUsernameWithMoreThanSixtyFourCharactersToTestMyApplication's password is 'VeryLongPasswordWithMoreThanSixtyFourCharactersToTestMyApplication'
*/

INSERT INTO authorities (authority, username)
VALUES ('P', ''),
       ('P', ' '),
       ('P', '#-/\"'),
       ('P', 'VeryLongUsernameWithMoreThanSixtyFourCharactersToTestMyApplication');
