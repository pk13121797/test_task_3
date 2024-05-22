create table application_user (
    id int not null,
    email varchar(255) not null,
    enabled boolean not null,
    locked boolean not null,
    name varchar(255) not null,
    password varchar(255) not null,
    user_role varchar(255) not null,
    primary key (id)
);

create table employee (
    id int not null,
    middle_name varchar(255) not null,
    name varchar(255) not null,
    post varchar(255) not null,
    surname varchar(255) not null,
    primary key (id)
);

create table images (
    user_id int not null,
    file_name varchar(255)
);

create table project (
    id int not null,
    abbreviation varchar(255) not null,
    description varchar(255) not null,
    title varchar(255) not null,
    primary key (id)
);

create table task (
    id int not null,
    end_date date not null,
    hours int not null,
    start_date date not null,
    status varchar(255) not null,
    title varchar(255) not null,
    project_id int,
    primary key (id)
);

create table task_employee (
    task_id int not null,
    employee_id int not null,
    primary key (task_id, employee_id)
);

create table verification_token (
    id int not null,
    confirmed_at timestamp,
    created timestamp not null,
    expired timestamp not null,
    token varchar(255) not null,
    user_id int,
    primary key (id)
);

create sequence employee_sequence start 1 increment 1;
create sequence project_sequence start 1 increment 1;
create sequence task_sequence start 1 increment 1;
create sequence token_sequence start 1 increment 1;
create sequence user_sequence start 1 increment 1;

alter table if exists images
    add constraint employee_image_fk
    foreign key (user_id)
    references employee;

alter table if exists task
    add constraint project_fk
    foreign key (project_id)
    references project;

alter table if exists task_employee
    add constraint employee_fk
    foreign key (employee_id)
    references employee;

alter table if exists task_employee
    add constraint task_fk
    foreign key (task_id)
    references task;

alter table if exists verification_token
    add constraint application_user_fk
    foreign key (user_id)
    references application_user;