CREATE DEFINER=`root`@`localhost` PROCEDURE `insert_new_contact`(
  fn varchar(15), mn varchar(5), ln varchar(15), gender char(1), birthday date, fmd date, 
  email varchar(25), phone varchar(15), city varchar(20), state varchar(20), addrline text,
  OUT result int, OUT contactId int, OUT addressId int)
BEGIN
declare id_contact bigint unsigned;
declare id_address bigint unsigned;
declare location int;
# should use text data type instead of char
declare line_street text;
start transaction;
  
  # the check null process for first name and last name are implemented in the java code
  insert into CONTACTS(first_name, middle_name, last_name, gender, birthday, first_met_date)
    values(fn, mn, ln, gender, birthday, fmd);
  select last_insert_id() into id_contact;
  select last_insert_id() into contactId;
    
  # insert email address into EMAIL if email is not null  
  if(email is not null and not exists(select email_id from EMAIL where email_addr = email)) then
  insert into EMAIL(contact_id, email_addr)
    values(id_contact, email);
  end if;
    
  # insert phone number into PHONE if phone number is not null
  if(phone is not null and not exists(select phone_id from PHONE where phone_number = phone)) then
  insert into PHONE(contact_id, phone_number)
    values(id_contact, phone);
  end if;
  
  # insert city and state into ADDRESS, city and state could be null
  insert into ADDRESS(contact_id, city, state)
    values(id_contact, city, state);
  select last_insert_id() into id_address;
  select last_insert_id() into addressId;

  # insert address lines into STREET
  if(addrline is not null) then
    repeat
      set location = LOCATE('\n', addrline);
      if(location = 0) then
        insert into STREET(contact_id, address_id, street_line)
          values(id_contact, id_address, addrline);
	  else
        set line_street = SUBSTRING(addrline, 1, location - 1);
        set addrline = SUBSTRING(addrline, location + 1);
        insert into STREET(contact_id, address_id, street_line)
          values(id_contact, id_address, line_street);
	  end if;
	until (location = 0) end repeat;
  end if;

  # to check whether an insert is successful
  select row_count() into result;
  
commit;
END