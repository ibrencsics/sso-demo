
DROP TABLE IF EXISTS User;

CREATE TABLE User(
ID INTEGER AUTO_INCREMENT PRIMARY KEY ,
NAME VARCHAR(50),
ADDRESS VARCHAR (50),
AGE INTEGER
);

INSERT INTO User (ID, NAME, ADDRESS, AGE) VALUES (1, 'Jevgeni', 'Rae XX' , '34'); 
