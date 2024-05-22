delete from verification_token;
delete from application_user;

insert into application_user(id, email, enabled, locked, name, password, user_role) values
(101, 'heleg14940@dovesilo.com', true, false, 'heleg14940', '$2a$10$CUVW/Sf1zS8b6XypEWp7TezzruHczPyCvYGrYmLmtenHLkFTx0Bd2', 'ADMIN'),
(102, 'conatag114@dovesilo.com', true, false, 'conatag114', '$2a$10$l0POEslGXfwloh8KoePMUemRBKE0D/sqrgfp5DlYhqrVmbVvVqiqS', 'USER');