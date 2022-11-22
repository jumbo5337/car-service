


CREATE TABLE IF NOT EXISTS USERS(
    username varchar(20) NOT NULL PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    customer_id BIGINT UNIQUE,
    role int NOT NULL,
    foreign key (customer_id) references CUSTOMERS(id)
)