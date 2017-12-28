CREATE DEFINER=`root`@`localhost` PROCEDURE `search_contact`(  
  fn varchar(15), mn varchar(5), ln varchar(15), gender char(1), birthday date, fmd date, 
  email varchar(25), phone varchar(15), city varchar(20), state varchar(20), addrline1 varchar(50), addrline2 varchar(50), addrline3 varchar(50))
BEGIN

start transaction;
  
  select c.*, e.email_addr, p.phone_number, a.address_id, a.city, a.state, GROUP_CONCAT(s.street_line order by s.street_id asc) AS street
  from CONTACTS c 
  left outer join EMAIL e on c.id = e.contact_id
  left outer join PHONE p on c.id = p.contact_id
  left outer join ADDRESS a on c.id = a.contact_id
  left outer join STREET s on c.id = s.contact_id #and a.address_id = s.address_id
  where 
  (fn is null or c.first_name = fn) and
  (mn is null or c.middle_name = mn) and
  (ln is null or c.last_name = ln) and
  (gender is null or c.gender = gender) and
  (birthday is null or c.birthday = birthday) and
  (fmd is null or c.first_met_date = fmd) and
  (email is null or e.email_addr = email) and
  (phone is null or p.phone_number = phone) and 
  (city is null or a.city = city) and
  (state is null or a.state = state) and
  (addrline1 is null or s.street_line = addrline1) and
  (addrline2 is null or s.street_line = addrline2) and 
  (addrline3 is null or s.street_line = addrline3)
  group by c.id, e.email_addr, p.phone_number, a.address_id, a.city, a.state;

commit;
END