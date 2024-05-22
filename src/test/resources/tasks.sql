delete from task_employee;
delete from task;
delete from employee;
delete from project;

insert into project (id, abbreviation, description, title) values
(1,'AAN','description 1','Java'),
(2,'AB','description 3','Kotlin');

insert into employee (id, middle_name, name, post, surname) values
(1,'Petrovich','Petr','Middle','Smirnov'),
(2,'Yegorovich','Alexey','Junior','Petrov'),
(3,'Vyacheslavovich','Ivan','Middle','Markov');

insert into task (id, end_date, hours, start_date, status, title, project_id) VALUES
(101,now() +  interval '1' day,4,'2021-04-13','postponed','write code',1),
(102,now() +  interval '1' day,5,'2020-04-30','not_started','read article',2),
(103,now() +  interval '1' day,12,'2021-09-19','completed','add validation',2);

insert into task_employee (employee_id, task_id) VALUES
(1,101),(1,102),(2,103),(3,103),(3,101);