CREATE SEQUENCE CUSTOMER_ID_SEQ;
CREATE SEQUENCE rfid_tag_id_seq;
CREATE SEQUENCE cp_id_seq;
CREATE SEQUENCE connectors_id_seq;

CREATE TABLE CUSTOMERS(
    id   bigint auto_increment PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS RFID_TAGS(
    id          bigint PRIMARY KEY,
    tag_name       VARCHAR(100)   not null,
    customer_id bigint not null,
    PRIMARY KEY(id),
    FOREIGN KEY (customer_id) REFERENCES CUSTOMERS(id)
);

CREATE TABLE IF NOT EXISTS VEHICLES(
    reg_number text primary key,
    vehicle_name       VARCHAR(100) not null,
    rfid       bigint unique,
    FOREIGN KEY (rfid) REFERENCES RFID_TAGS(id)
);

CREATE TABLE IF NOT EXISTS CHARGE_POINTS(
    id   bigint AUTO_INCREMENT,
    cp_name VARCHAR(100) NOT NULL,
    customer_id bigint not null,
    PRIMARY KEY(id),
    FOREIGN KEY (customer_id) REFERENCES CUSTOMERS(id)
);


CREATE TABLE IF NOT EXISTS CONNECTORS(
    id   bigint AUTO_INCREMENT,
    cpid bigint NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY (cpid) REFERENCES CHARGE_POINTS(id)
);

CREATE TABLE IF NOT EXISTS CHARGE_SESSIONS(
    id UUID primary key,
    start_time TIMESTAMP NOT NULL,
    start_meter FLOAT NOT NULL,
    end_time TIMESTAMP,
    end_meter FLOAT,
    is_completed BOOLEAN NOT NULL,
    is_error BOOLEAN NOT NULL,
    message varchar (255),
    rfid_tag_id BIGINT NOT NULL,
    connector_id BIGINT NOT NULL,
    current_rfid BIGINT as (case is_completed when TRUE then null else rfid_tag_id end) unique,
    FOREIGN KEY (connector_id) REFERENCES CONNECTORS(id),
    FOREIGN KEY (rfid_tag_id) REFERENCES RFID_TAGS(id),
    FOREIGN KEY (current_rfid) REFERENCES RFID_TAGS(id)
    );