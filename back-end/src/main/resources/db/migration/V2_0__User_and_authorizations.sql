CREATE TABLE users (
	username varchar(255) NOT NULL,
	enabled bool NOT NULL,
	firstname varchar(255) NULL,
	lastname varchar(255) NULL,
	"password" varchar(255) NOT NULL,
	CONSTRAINT users_pkey PRIMARY KEY (username)
);

CREATE TABLE authorities (
	authority varchar(255) NOT NULL,
	username varchar(255) NOT NULL,
	CONSTRAINT authorities_pkey PRIMARY KEY (authority, username),
	CONSTRAINT fk_UserAuthorities FOREIGN KEY (username) REFERENCES users(username)
);
