create table employee (
    id bigint not null constraint employee_pkey primary key,
    middle_name varchar(50) not null,
    name varchar(50) not null,
    post varchar(20) not null,
    surname varchar(50) not null
);

create table project (
    id bigint not null constraint project_pkey primary key,
    abbreviation varchar(5) not null,
    description text not null,
    title varchar(50) not null
);

create type status_state as enum ('not_started', 'in_process', 'completed', 'postponed');

create table task (
    id bigint not null constraint task_pkey primary key,
    end_date date not null,
    hours integer not null,
    start_date date not null,
    status varchar(255) not null,
    task varchar(50) not null,
    project_id bigint constraint project_fk references project
);

create table task_employee (
    employee_id bigint not null constraint employee_id_fk references employee,
    task_id bigint not null constraint task_id_fk references task,
    constraint task_employee_pkey primary key (employee_id, task_id)
);