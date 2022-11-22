INSERT INTO CUSTOMERS
VALUES (1, 'Vasya'),
       (2, 'Petya'),
       (3, 'Vova');

INSERT INTO RFID_TAGS
VALUES (1, 'VASYA_RFID_1', 1),
       (2, 'VASYA_RFID_2', 1),
       (3, 'VASYA_RFID_3', 1),
       (4, 'PETYA_RFID_1', 2),
       (5, 'VOVA_RFID_1', 3),
       (6, 'VOVA_RFID_2', 3);


INSERT INTO VEHICLES
VALUES ('0001UZ', 'COBALT', 1),
       ('0002UZ', 'COBALT', 2),
       ('0003UZ', 'COBALT', 4),
       ('0004UZ', 'COBALT', 6);

INSERT INTO CHARGE_POINTS
values (1, 'VASYA_CP1', 1),
       (2, 'VASYA_CP2', 1),
       (3, 'PETYA_CP1', 2),
       (4, 'VOVA_CP1', 3);

INSERT INTO CONNECTORS
values (1, 1),
       (2, 1),
       (3, 3),
       (4, 4);

INSERT INTO USERS
values ('admin', '$2a$12$NQ/uKmV93orJpORJHNVoRuua.3ZRfetUyldra/V9YAX.gaE2VVcUe', null, 1), -- admin
       ('Vasya', '$2a$10$.6PbWBxaRTYS3MyMkCaXfOOnlpKrBC8E25GrLbg.yJZnLYtoHFk9C', 1, 2), -- 1234
       ('Petya', '$2a$10$.6PbWBxaRTYS3MyMkCaXfOOnlpKrBC8E25GrLbg.yJZnLYtoHFk9C', 2, 2),
       ('Vova', '$2a$10$.6PbWBxaRTYS3MyMkCaXfOOnlpKrBC8E25GrLbg.yJZnLYtoHFk9C', 3, 2);