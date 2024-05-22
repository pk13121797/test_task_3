insert into application_user(id, email, enabled, locked, name, password, user_role) values
(1, 'heleg14940@dovesilo.com', true, false, 'heleg14940', '$2a$10$CUVW/Sf1zS8b6XypEWp7TezzruHczPyCvYGrYmLmtenHLkFTx0Bd2', 'ADMIN'),
(2, 'conatag114@dovesilo.com', true, false, 'conatag114', '$2a$10$l0POEslGXfwloh8KoePMUemRBKE0D/sqrgfp5DlYhqrVmbVvVqiqS', 'USER');

insert into employee (id, middle_name, name, post, surname) values
(1,'Petrovich','Petr','Middle','Smirnov'),
(2,'Yegorovich','Alexey','Junior','Petrov'),
(3,'Vyacheslavovich','Ivan','Middle','Markov'),
(4,'Yuryevich','Fedor','Junior','Efimov'),
(5,'Mikhaylovich','Petr','Senior','Gromov'),
(6,'Vasilyevich','Vladimir','TeamLead','Lukin'),
(7,'Valeryevich','Oleg','TeamLead','Potapov'),
(8,'Vladimirovich','Yegor','Junior','Nosov'),
(9,'Aleksandrovich','Nikita','Senior','Grishin'),
(10,'Edouardovich','Vladislav','TeamLead','Naumov'),
(11,'Yevgenyevich','Sergey','Junior','Orlov'),
(12,'Viktorovich','Timofey','Middle','Avdeev'),
(13,'Sergeyevich','Yan','TeamLead','Isaev'),
(14,'Alekseevich','Nikolai','Junior','Nazarov'),
(15,'Igorevich','Mikhail','Middle','Matveev');

insert into project (id, abbreviation, description, title) values
(1,'AAN','description 1','Java'),
(2,'AB','description 3','Kotlin'),
(3,'AC','description 5','Spring'),
(4,'ADC','description 101','Postgres'),
(5,'BA','description 14','Android'),
(6,'BB','description 41','Mysql'),
(7,'BCT','description 521','Scala'),
(8,'BD','description 100','C++'),
(9,'CA','description 93','JSF'),
(10,'CB','description 35','Python');

insert into task (id, end_date, hours, start_date, status, title, project_id) VALUES
(1,'2025-04-15',4,'2021-04-13','postponed','write code',1),
(2,'2025-04-23',5,'2020-04-30','not_started','read article',2),
(3,'2025-05-27',12,'2021-09-19','completed','add validation',2),
(4,'2025-04-14',48,'2020-01-01','not_started','add config',2),
(5,'2025-10-02',15,'2020-09-22','postponed','sleep',3),
(6,'2025-04-21',1,'2021-10-16','not_started','read book',3),
(7,'2025-11-09',100,'2020-12-11','in_process','populate db',4),
(8,'2025-04-26',31,'2021-04-03','postponed','watch tutorial',5),
(9,'2025-04-11',16,'2019-07-22','not_started','send emails',5),
(10,'2025-03-12',7,'2021-02-28','in_process','learn english',6),
(11,'2025-08-04',6,'2020-04-19','completed','update information',7),
(12,'2025-04-28',37,'2021-10-25','not_started','download program',7),
(13,'2025-01-17',70,'2020-06-11','postponed','write algorithm',8),
(14,'2025-04-05',3,'2021-03-23','completed','change database',8),
(15,'2025-12-09',5,'2021-08-08','not_started','launch new project version',9),
(16,'2025-08-30',4,'2020-09-17','not_started','add new framework',9),
(17,'2025-11-06',20,'2021-01-18','completed','make a code refactoring',9),
(18,'2025-04-17',1,'2020-10-20','in_process','write tests',9),
(19,'2025-05-22',15,'2021-04-04','not_started','add bootstrap',10),
(20,'2025-07-28',2,'2020-07-27','completed','handle errors',10);

insert into task_employee (employee_id, task_id) VALUES
(1,1),(1,2),(2,3),(3,4),
(3,5),(3,6),(4,7),(4,8),
(5,9),(6,10),(7,11),(8,12)
,(8,13),(9,14),(9,15),(10,16),
(11,17),(11,18),(11,19),(12,20),
(13,11),(13,10),(14,9);