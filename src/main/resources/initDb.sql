CREATE TABLE Account (
  id    LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
  name  VARCHAR(255)                    NOT NULL,
  money INT,
  UNIQUE KEY acc_name_UNIQUE (name)
);