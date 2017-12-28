package com.jeffin.ui;

import com.jeffin.constant.ConstantUI;
import com.jeffin.db.DBUtil;
import com.jeffin.model.Contact;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Author: baojianfeng
 * Date: 2017-10-21
 * Usage: ContactManager main window which allows different operations
 */
public class ContactManager {
    private Contact contact;
    private JFrame jFrame;
    private JPanel mainPanel;
    private JTextField firstName;
    private JTextField middleName;
    private JTextField lastName;
    private JComboBox<String> gender;
    private JTextField addressLine1;
    private JTextField addressLine2;
    private JTextField addressLine3;
    private JTextField city;
    private JTextField state;
    private JTextField phone;
    private JTextField email;
    private JTextField birthday;
    private JTextField firstMetDate;
    private JButton buttonSearch;
    /**
     * JTable to display result of search operation
     */
    private JTable tableDisplay;
    private DefaultTableModel dtm;
    private JButton buttonSave;
    private JButton buttonNewContact;
    private JButton buttonDelete;
    /**
     * ArrayList to store contacts after search operation
     */
    private ArrayList<Contact> contactArrayList;
    /**
     * Store selected row index
     */
    private int selectedRowIndex;

    /**
     * Contact Manager constructor
     */
    public ContactManager() {
        initialization();
        addListener();
    }

    /**
     * initialize ui layout
     */
    private void initialization() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.ipady = 20;
        c.weightx = 1;
        mainPanel.add(getBasicInfoPanel(), c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        c.ipady = 20;
        c.weightx = 1;
        mainPanel.add(getAddressPanel(), c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        c.ipady = 20;
        c.weightx = 1;
        mainPanel.add(getOtherInfoPanel(), c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 4;
        c.ipady = 10;
        c.weightx = 5;
        mainPanel.add(getSearchPanel(), c);
        c.gridx = 0;
        c.gridy = 5;
        c.ipady = 50;
        c.weightx = 5;
        mainPanel.add(getContactDisplayPanel(), c);
        c.gridx = 0;
        c.gridy = 6;
        c.ipady = 10;
        c.weightx = 5;
        mainPanel.add(getOperationPanel(), c);

        jFrame = new JFrame("Contact Manager");
        jFrame.setBounds(ConstantUI.MAIN_WINDOW_X, ConstantUI.MAIN_WINDOW_Y, ConstantUI.MAIN_WINDOW_WIDTH, ConstantUI.MAIN_WINDOW_HEIGHT);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.add(mainPanel);
    }

    /**
     * add listeners to JButton and JTable
     */
    private void addListener() {
        buttonSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> arrayList = composeSearchContent();
                if (composeSearchContent() != null) {
                    ResultSet resultSet = DBUtil.getInstance().executeSearch(arrayList);

                    if (resultSet == null)
                        JOptionPane.showMessageDialog(jFrame, "no search result");
                    else
                        displaySearchResult(resultSet);
                }
            }
        });

        buttonSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contact = composeContact();

                if (contact == null)
                    return;
                else if (contact.getContactId() == 0)
                    contact = DBUtil.getInstance().executeInsert(contact);
                else {
                    int result = DBUtil.getInstance().executeUpdate(contact);
                    if (result > 0) {
                        JOptionPane.showMessageDialog(jFrame, "update contact succeeded");
                        clearContent();
                        contact = null;
                    }
                }
            }

        });

        buttonNewContact.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contact = composeContact();

                if (contact == null)
                    return;
                else if (contact.getContactId() == 0) {
                    contact = DBUtil.getInstance().executeInsert(contact);
                    if (contact.getContactId() > 0) {
                        JOptionPane.showMessageDialog(jFrame, "insert new contact succeeded");
                        clearContent();
                        contact = null;
                    }
                } else {
                    JOptionPane.showMessageDialog(jFrame, "contact already exists");
                }

            }
        });

        buttonDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (contact == null || contact.getContactId() == 0) {
                    JOptionPane.showMessageDialog(jFrame, "please insert contact first");
                } else {
                    int result = DBUtil.getInstance().executeDelete(contact);
                    if (result > 0) {
                        JOptionPane.showMessageDialog(jFrame, "delete contact succeeded");
                        clearContent();
                        contact = null;
                        // remove selected contact first, then call dtm.removeRow, because after dtm.removeRow, selectedRowIndex will change to -1
                        contactArrayList.remove(selectedRowIndex);
                        dtm.removeRow(selectedRowIndex);
                    }
                }
            }
        });

        tableDisplay.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectedRowIndex = tableDisplay.getSelectedRow();
                if (selectedRowIndex >= 0) {
                    contact = contactArrayList.get(selectedRowIndex);
                    displaySelectedContact(contact);
                }
            }
        });
    }

    /**
     * initialize contact's basic info panel
     * @return JPanel
     */
    private JPanel getBasicInfoPanel() {
        JPanel basicInfoPanel = new JPanel();
        basicInfoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        firstName = new JTextField(8);
        firstName.setDocument(new JTextFieldLimit(15));
        middleName = new JTextField(8);
        middleName.setDocument(new JTextFieldLimit(5));
        lastName = new JTextField(8);
        lastName.setDocument(new JTextFieldLimit(15));
        String[] genderList = {"", "M", "F"};
        gender = new JComboBox<>(genderList);

        JLabel labelFN = new JLabel("First Name");
        JLabel labelMN = new JLabel("Middle Name");
        JLabel labelLN = new JLabel("Last Name");
        JLabel labelGender = new JLabel("Gender");

        basicInfoPanel.add(labelFN);
        basicInfoPanel.add(firstName);
        basicInfoPanel.add(labelMN);
        basicInfoPanel.add(middleName);
        basicInfoPanel.add(labelLN);
        basicInfoPanel.add(lastName);
        basicInfoPanel.add(labelGender);
        basicInfoPanel.add(gender);

        return basicInfoPanel;
    }

    /**
     * initialize contact's address panel
     * @return JPanel
     */
    private JPanel getAddressPanel() {
        JPanel addressPanel = new JPanel();
        addressPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        addressLine1 = new JTextField(10);
        addressLine1.setDocument(new JTextFieldLimit(50));
        addressLine2 = new JTextField(10);
        addressLine2.setDocument(new JTextFieldLimit(50));
        addressLine3 = new JTextField(10);
        addressLine3.setDocument(new JTextFieldLimit(50));
        city = new JTextField(5);
        city.setDocument(new JTextFieldLimit(20));
        state = new JTextField(5);
        state.setDocument(new JTextFieldLimit(20));

        JLabel labelAddr1 = new JLabel("Address Line1");
        JLabel labelAddr2 = new JLabel("Line2");
        JLabel labelAddr3 = new JLabel("Line3");
        JLabel labelCity = new JLabel("City");
        JLabel labelState = new JLabel("State");

        addressPanel.add(labelAddr1);
        addressPanel.add(addressLine1);
        addressPanel.add(labelAddr2);
        addressPanel.add(addressLine2);
        addressPanel.add(labelAddr3);
        addressPanel.add(addressLine3);
        addressPanel.add(labelCity);
        addressPanel.add(city);
        addressPanel.add(labelState);
        addressPanel.add(state);

        return addressPanel;
    }

    /**
     * initialize contact's other info panel, including phone, email, birthday, first met date
     * @return JPanel
     */
    private JPanel getOtherInfoPanel() {
        JPanel otherInfoPanel = new JPanel();
        otherInfoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        phone = new JTextField(10);
        phone.setDocument(new JTextFieldLimit(15));
        email = new JTextField(10);
        email.setDocument(new JTextFieldLimit(25));
        birthday = new JTextField(10);
        birthday.setDocument(new JTextFieldLimit(10));
        firstMetDate = new JTextField(10);
        firstMetDate.setDocument(new JTextFieldLimit(10));

        JLabel labelPhone = new JLabel("Phone");
        JLabel labelEmail = new JLabel("Email");
        JLabel labelBirthday = new JLabel("Birthday");
        JLabel labelFirstMetDate = new JLabel("First Met Date");

        otherInfoPanel.add(labelPhone);
        otherInfoPanel.add(phone);
        otherInfoPanel.add(labelEmail);
        otherInfoPanel.add(email);
        otherInfoPanel.add(labelBirthday);
        otherInfoPanel.add(birthday);
        otherInfoPanel.add(labelFirstMetDate);
        otherInfoPanel.add(firstMetDate);

        return otherInfoPanel;
    }

    /**
     * initialize search button
     * @return JPanel
     */
    private JPanel getSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        buttonSearch = new JButton("Search");
        searchPanel.add(buttonSearch);

        return searchPanel;
    }

    /**
     * initialize contact display panel where will display contacts after search operation
     * @return JPanel
     */
    private JPanel getContactDisplayPanel() {
        JPanel contactDisplayPanel = new JPanel();
        contactDisplayPanel.setLayout(new BorderLayout());

        String[] contactAttributes = {"First Name", "Middle Name", "Last Name", "Gender",
                "Address", "City", "State", "Phone", "Email", "Birthday", "First Met Date"};
        Object[][] data = {};

        dtm = new DefaultTableModel() {
            @Override
            public String getColumnName(int column) {
                return contactAttributes[column];
            }
        };
        tableDisplay = new JTable(dtm);
        tableDisplay.setShowHorizontalLines(true);
        tableDisplay.setShowVerticalLines(true);
        tableDisplay.setPreferredScrollableViewportSize(new Dimension(500, 100));
        tableDisplay.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(tableDisplay);
        contactDisplayPanel.add(scrollPane);

        return contactDisplayPanel;
    }

    /**
     * initialize save, new contact, delete operation panel
     * @return JPanel
     */
    private JPanel getOperationPanel() {
        JPanel operationPanel = new JPanel();
        operationPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        buttonSave = new JButton("Save");
        buttonNewContact = new JButton("New Contact");
        buttonDelete = new JButton("Delete");

        operationPanel.add(buttonSave);
        operationPanel.add(buttonNewContact);
        operationPanel.add(buttonDelete);

        return operationPanel;
    }

    /**
     * compose a contact before insert or update operation
     * @return contact
     */
    private Contact composeContact() {
        if (contact == null)
            contact = new Contact();

        if (!firstName.getText().isEmpty())
            contact.setFirstName(firstName.getText());
        else {
            JOptionPane.showMessageDialog(jFrame, "first name should not be null");
            return null;
        }

        if (!lastName.getText().isEmpty())
            contact.setLastName(lastName.getText());
        else {
            JOptionPane.showMessageDialog(jFrame, "last name should not be null");
        }

        contact.setMiddleName(middleName.getText());
        contact.setGender(String.valueOf(gender.getSelectedItem()));
        // if address line content is an empty string, don't add it into address
        String address = null;
        String addressLine1Str = addressLine1.getText();
        if (!addressLine1Str.isEmpty()) {
            // doesn't support input address line contains comma,
            // since it may cause abnormal behaviour whether display selected search result
            if (addressLine1Str.contains(",")) {
                JOptionPane.showMessageDialog(jFrame, "each address line should not contain comma, please use space instead");
                return null;
            } else {
                address = addressLine1Str;
            }
        }
        String addressLine2Str = addressLine2.getText();
        if (!addressLine2Str.isEmpty()) {
            if (addressLine2Str.contains(",")) {
                JOptionPane.showMessageDialog(jFrame, "each address line should not contain comma, please use space instead");
                return null;
            } else
                address = address + "\n" + addressLine2Str;
        }
        String addressLine3Str = addressLine3.getText();
        if (!addressLine3Str.isEmpty()) {
            if (addressLine3Str.contains(",")) {
                JOptionPane.showMessageDialog(jFrame, "each address line should not contain comma, please use space instead");
                return null;
            } else
                address = address + "\n" + addressLine3Str;
        }
        contact.setAddress(address);
        contact.setCity(city.getText());
        contact.setState(state.getText());
        contact.setPhone(phone.getText());
        contact.setEmail(email.getText());

        if (birthday.getText().isEmpty())
            contact.setBirthday(null);
        else {
            // avoid invalid date format
            if (birthday.getText().matches("^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$"))
                contact.setBirthday(birthday.getText());
            else {
                JOptionPane.showMessageDialog(jFrame, "invalid birthday");
                return null;
            }
        }

        if (firstMetDate.getText().isEmpty())
            contact.setFirstMetDate(null);
        else {
            // avoid invalid date format
            if (firstMetDate.getText().matches("^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$"))
                contact.setFirstMetDate(firstMetDate.getText());
            else {
                JOptionPane.showMessageDialog(jFrame, "invalid first met date");
                return null;
            }
        }

        return contact;
    }

    /**
     * compose search parameters
     * @return arraylist which stores search parameters
     */
    private ArrayList<String> composeSearchContent() {
        ArrayList<String> searchItemList = new ArrayList<>();

        if (!firstName.getText().isEmpty())
            searchItemList.add(firstName.getText());
        else
            searchItemList.add(null);

        if (!middleName.getText().isEmpty())
            searchItemList.add(middleName.getText());
        else
            searchItemList.add(null);

        if (!lastName.getText().isEmpty())
            searchItemList.add(lastName.getText());
        else
            searchItemList.add(null);

        if (!String.valueOf(gender.getSelectedItem()).isEmpty())
            searchItemList.add(String.valueOf(gender.getSelectedItem()));
        else
            searchItemList.add(null);

        if (birthday.getText().isEmpty())
            searchItemList.add(null);
        else if (!birthday.getText().matches("^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$")) {
            JOptionPane.showMessageDialog(jFrame, "invalid date format");
            return null;
        } else
            searchItemList.add(birthday.getText());

        if (firstMetDate.getText().isEmpty())
            searchItemList.add(null);
        else if (!firstMetDate.getText().matches("^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$")) {
            JOptionPane.showMessageDialog(jFrame, "invalid date format");
            return null;
        } else
            searchItemList.add(firstMetDate.getText());

        if (!email.getText().isEmpty())
            searchItemList.add(email.getText());
        else
            searchItemList.add(null);

        if (!phone.getText().isEmpty())
            searchItemList.add(phone.getText());
        else
            searchItemList.add(null);

        if (!city.getText().isEmpty())
            searchItemList.add(city.getText());
        else
            searchItemList.add(null);

        if (!state.getText().isEmpty())
            searchItemList.add(state.getText());
        else
            searchItemList.add(null);

        if (!addressLine1.getText().isEmpty())
            searchItemList.add(addressLine1.getText());
        else
            searchItemList.add(null);

        if (!addressLine2.getText().isEmpty())
            searchItemList.add(addressLine2.getText());
        else
            searchItemList.add(null);

        if (!addressLine3.getText().isEmpty())
            searchItemList.add(addressLine3.getText());
        else
            searchItemList.add(null);

        return searchItemList;

    }

    /**
     * display search results in JTable
     * @param resultSet ResultSet returned after search operation
     */
    private void displaySearchResult(ResultSet resultSet) {
        // clear the table cache data
        dtm.setRowCount(0);
        contactArrayList = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Vector<String> v = new Vector<>();
                Contact contact = new Contact();

                String firstName = resultSet.getString("first_name");
                v.add(firstName);
                contact.setFirstName(firstName);
                String middleName = resultSet.getString("middle_name");
                v.add(middleName);
                contact.setMiddleName(middleName);
                String lastName = resultSet.getString("last_name");
                v.add(lastName);
                contact.setLastName(lastName);
                String gender = resultSet.getString("gender");
                v.add(gender);
                contact.setGender(gender);
                String street = resultSet.getString("street");
                v.add(street);
                contact.setAddress(street);
                String city = resultSet.getString("city");
                v.add(city);
                contact.setCity(city);
                String state = resultSet.getString("state");
                v.add(state);
                contact.setState(state);
                String phone = resultSet.getString("phone_number");
                v.add(phone);
                contact.setPhone(phone);
                String email = resultSet.getString("email_addr");
                v.add(email);
                contact.setEmail(email);
                String birthday = null;
                if (resultSet.getDate("birthday") != null)
                    birthday = String.valueOf(resultSet.getDate("birthday"));
                v.add(birthday);
                contact.setBirthday(birthday);
                String firstMetDate = null;
                if (resultSet.getDate("first_met_date") != null)
                    firstMetDate = String.valueOf(resultSet.getDate("first_met_date"));
                v.add(firstMetDate);
                contact.setFirstMetDate(firstMetDate);

                contact.setContactId(resultSet.getInt("id"));
                contact.setAddressId(resultSet.getInt("address_id"));

                contactArrayList.add(contact);

                // tell the data model how many columns are added
                dtm.setColumnCount(v.size());
                dtm.addRow(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * display selected contact in JTable to related JTextFields
     * @param contact selected contact
     */
    private void displaySelectedContact(Contact contact) {
        // clear content first
        clearContent();

        if (contact == null)
            return;

        firstName.setText(contact.getFirstName());
        lastName.setText(contact.getLastName());

        if (contact.getMiddleName() != null)
            middleName.setText(contact.getMiddleName());
        if (contact.getGender() != null)
            gender.setSelectedItem(contact.getGender());
        if (contact.getCity() != null)
            city.setText(contact.getCity());
        if (contact.getState() != null)
            state.setText(contact.getState());
        if (contact.getPhone() != null)
            phone.setText(contact.getPhone());
        if (contact.getEmail() != null)
            email.setText(contact.getEmail());
        if (contact.getBirthday() != null)
            birthday.setText(String.valueOf(contact.getBirthday()));
        if (contact.getFirstMetDate() != null)
            firstMetDate.setText(String.valueOf(contact.getFirstMetDate()));

        String street = contact.getAddress();
        if (street != null) {
            String[] streetLines = street.split(",");
            for (int i = 0; i < streetLines.length; i++) {
                if (i == 0 && !streetLines[0].isEmpty())
                    addressLine1.setText(streetLines[0]);
                if (i == 1 && !streetLines[1].isEmpty())
                    addressLine2.setText(streetLines[1]);
                if (i == 2 && !streetLines[2].isEmpty())
                    addressLine3.setText(streetLines[2]);
            }
        }
    }

    /**
     * clear content within all JTextFields
     */
    private void clearContent() {
        firstName.setText("");
        middleName.setText("");
        lastName.setText("");
        gender.setSelectedItem("");
        addressLine1.setText("");
        addressLine2.setText("");
        addressLine3.setText("");
        city.setText("");
        state.setText("");
        phone.setText("");
        email.setText("");
        birthday.setText("");
        firstMetDate.setText("");
    }

    /**
     * limit JTextField allowed character size
     */
    private class JTextFieldLimit extends PlainDocument {
        private int limit;

        JTextFieldLimit(int limit) {
            super();
            this.limit = limit;
        }

        public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
            if (str == null)
                return;

            if ((getLength() + str.length()) <= limit) {
                super.insertString(offset, str, attr);
            }
        }
    }

    public static void main(String[] args) {
        ContactManager contactManager = new ContactManager();
        contactManager.jFrame.setVisible(true);
    }
}
