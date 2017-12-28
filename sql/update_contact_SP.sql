CREATE DEFINER=`root`@`localhost` PROCEDURE `update_contact`(
  fn varchar(15), mn varchar(5), ln varchar(15), gender char(1), birthday date, fmd date, 
  email varchar(25), phone varchar(15), city varchar(20), state varchar(20), addrline text,
  id_contact int, id_address int,
  OUT result int)
BEGIN
declare location int;
declare line_street text;
start transaction;
  
  update CONTACTS
  set first_name = fn, middle_name = mn, last_name = ln, CONTACTS.gender = gender, CONTACTS.birthday = birthday, first_met_date = fmd
  where id = id_contact;
  
  if(email is not null) then
    if not exists(select contact_id from EMAIL where contact_id = id_contact) then
      insert into EMAIL(contact_id, email_addr)
      values(id_contact, email);
	else
      update EMAIL
      set email_addr = email
      where contact_id = id_contact;
	end if;
  end if;
  
  if(phone is not null) then
    if not exists(select contact_id from PHONE where contact_id = id_contact) then
      insert into PHONE(contact_id, phone_number)
      values(id_contact, phone);
	else
	  update PHONE
      set phone_number = phone
      where contact_id = id_contact;
	end if;
  end if;
  
  if(city is not null or state is not null) then
    update ADDRESS
    set ADDRESS.city = city, ADDRESS.state = state
    where contact_id = id_contact;
  end if;
  
  if(addrline is not null) then
    # first delete all related street lines, then insert new street lines
    # in this way, can avoid a lot of possible scenarios
    delete from STREET
    where contact_id = id_contact and address_id = id_address;
    
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

  select row_count() into result;
commit;
END