DELETE FROM authorities
WHERE username=''
   OR username=' '
   OR username='#-/\"'
   OR username='VeryLongUsernameWithMoreThanSixtyFourCharactersToTestMyApplication';

DELETE FROM users
WHERE username=''
   OR username=' '
   OR username='#-/\"'
   OR username='VeryLongUsernameWithMoreThanSixtyFourCharactersToTestMyApplication';
