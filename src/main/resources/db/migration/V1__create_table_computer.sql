create table computer (
  id                            bigint not null AUTO_INCREMENT,
  name                          varchar(255) not null,
  introduced                    date(255),
  discontinued                  date(255),
  constraint pk_computer primary key (id)
);


