create extension if not exists pgcrypto;

insert into account (id, role, name, first_name, phone_number, created_at, version, password)
select gen_random_uuid(),
       'ADMIN',
       'admin',
       'admin',
       '0000000000',
       now(),
       0,
       crypt('AdminEstHotelPro2024!', gen_salt('bf'))
where not exists (select 1 from account where role = 'ADMIN' and name = 'admin');
