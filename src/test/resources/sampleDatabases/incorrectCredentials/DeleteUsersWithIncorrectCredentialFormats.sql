DELETE FROM authorities
WHERE username='P'
   OR username=''
   OR username=' '
   OR username='#-/\"'
   OR username='VeryLongUsernameWithMoreThanSixtyFourCharactersToTestMyApplication';

DELETE FROM users
WHERE username='P'
   OR username=''
   OR username=' '
   OR username='#-/\"'
   OR username='VeryLongUsernameWithMoreThanSixtyFourCharactersToTestMyApplication';
