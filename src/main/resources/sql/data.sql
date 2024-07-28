insert into locality (id, metro_availability, population, title) values
(1,false,0,'Belovezhskaya Pushcha'),
(2,false,16000,'Nesvizh'),
(3,true,2000000,'Minsk');

insert into attraction (id, attraction_type, creation_date, description, title, locality_id) values
(1,'NATURE_RESERVE','1991-12-25','some description','Aviaries with bisons', 1),
(2,'MUSEUM','2003-12-16','some description','The estate of the Belarusian Santa Claus', 1),
(3,'PALACE','1583-05-07','some description','Nesvizh Castle', 2),
(4,'MUSEUM','1957-01-01','some description','National Historical Museum', 3);

insert into service (id, abbreviation, title) VALUES
(1,'AA','buy souvenirs'),
(2,'AB','take a picture of a bison'),
(3,'ABC','a walk through the parks'),
(4,'DE','listen to the audio guide');

insert into attraction_service (attraction_id, service_id) VALUES
(1,1),(1,2),
(3,3),(4,4);