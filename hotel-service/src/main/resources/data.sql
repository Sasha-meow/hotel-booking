INSERT INTO hotels (name, address) VALUES ('Test Hotel 1', 'ул. Тестовая, 1');
INSERT INTO hotels (name, address) VALUES ('Super test hotel', 'пр. Тестовый, 15');
INSERT INTO rooms (hotel_id, number, available, times_booked) VALUES (1, '101', true, 5);
INSERT INTO rooms (hotel_id, number, available, times_booked) VALUES (1, '102', true, 0);
INSERT INTO rooms (hotel_id, number, available, times_booked) VALUES (1, '103', true, 2);
INSERT INTO rooms (hotel_id, number, available, times_booked) VALUES (2, '201', true, 1);
INSERT INTO rooms (hotel_id, number, available, times_booked) VALUES (2, '202', false, 10); -- недоступен (ремонт)