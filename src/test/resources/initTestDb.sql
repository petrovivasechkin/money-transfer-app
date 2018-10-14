CREATE TABLE Account (
  id    LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
  name  VARCHAR(255)                    NOT NULL,
  money INT,
  UNIQUE KEY acc_name_UNIQUE (name)
);

INSERT INTO Account (name, money) values ('Vasya',55);
INSERT INTO Account (name, money) values ('Petya',60);
INSERT INTO Account (name, money) values ('Vova',30);
INSERT INTO Account (name, money) values ('Tolya',100);