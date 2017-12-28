CREATE DEFINER=`root`@`localhost` PROCEDURE `delete_contact`(id_contact int, OUT result int)
BEGIN
start transaction;
    
  delete from STREET
  where contact_id = id_contact;
  
  delete from ADDRESS
  where contact_id = id_contact;
  
  delete from EMAIL
  where contact_id = id_contact;
  
  delete from PHONE
  where contact_id = id_contact;
  
  delete from CONTACTS
  where id = id_contact;
  
  # to check whether delete operation is successful
  select row_count() into result;


commit;
END