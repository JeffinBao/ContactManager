package com.jeffin.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: baojianfeng
 * Date: 2017-10-21
 * Usage: Contact instance to store contact information
 */
public class Contact {
    /**
     * unique id for every contact
     */
    private int contactId;
    /**
     * unique id for every contact's address
     */
    private int addressId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String address;
    private String city;
    private String state;
    private String phone;
    private String email;
    private java.sql.Date birthday;
    /**
     * when does the contact first meet the ContactManager user
     */
    private java.sql.Date firstMetDate;

    /**
     * set unique id to a contact
     * @param contactId id
     */
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    /**
     * retrieve unique id of a contact
     * @return contact_id
     */
    public int getContactId() {
        return contactId;
    }

    /**
     * set unique id to a contact's address
     * @param addressId address id
     */
    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    /**
     * retrieve address id
     * @return address_id
     */
    public int getAddressId() {
        return addressId;
    }

    /**
     * set first name to a contact
     * @param firstName first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * retrieve first name of a contact
     * @return first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * set middle name to a contact
     * @param middleName middle name
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * retrieve middle name of a contact
     * @return middle name
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * set last name to a contact
     * @param lastName last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * retrieve last name of a contact
     * @return last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * set gender to a contact
     * @param gender gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * retrieve gender of a contact
     * @return gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * set aggregated address to a contact
     * @param address address which is an aggregated value
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * retrieve address of a contact
     * @return address value
     */
    public String getAddress() {
        return address;
    }

    /**
     * set city to a contact
     * @param city city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * retrieve city of a contact
     * @return city value
     */
    public String getCity() {
        return city;
    }

    /**
     * set state to a contact
     * @param state state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * retrieve state of a contact
     * @return state value
     */
    public String getState() {
        return state;
    }

    /**
     * set phone number to a contact
     * @param phone phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * retrieve phone number of a contact
     * @return phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * set email address to a contact
     * @param email email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * retrieve email address of a contact
     * @return email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * set birthday to a contact, if string is null, set null to a contact
     * @param bString birthday string
     */
    public void setBirthday(String bString) {
        if (bString == null || bString.isEmpty())
            this.birthday = null;
        else {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(bString);
                birthday = new java.sql.Date(date.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * retrieve birthday of a contact
     * @return birthday, if birthday is null, return null
     */
    public java.sql.Date getBirthday() {
        return birthday;
    }

    /**
     * set first met date to a contact
     * @param firstMetDateString first met date string
     */
    public void setFirstMetDate(String firstMetDateString) {
        if (firstMetDateString == null || firstMetDateString.isEmpty())
            this.firstMetDate = null;
        else {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(firstMetDateString);
                firstMetDate = new java.sql.Date(date.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * retrieve first met date of a contact
     * @return first met date, null if first met date is null
     */
    public java.sql.Date getFirstMetDate() {
        return firstMetDate;
    }

}
