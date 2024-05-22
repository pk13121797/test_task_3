delete from task_employee;
delete from task;
delete from project;

insert into task(id, end_date, hours, start_date, status, title) values
(1, now() +  interval '1' day, 3, '2023-12-14', 'completed', 'task1'),
(2, now() +  interval '1' day, 3, '2023-12-13', 'postponed', 'task2'),
(3, now() +  interval '1' day, 3, '2023-12-12', 'not_started', 'task3');

insert into project(id, abbreviation, description, title) values
(101, 'AA', 'test123', 'hello'),
(102, 'AB', 'sss', 'qwerty');