package com.jeffin.db;

import com.jeffin.model.Contact;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Author: baojianfeng
 * Date: 2017-10-21
 * Usage: use DBUtil to connect to db server and do related operations
 */
public class DBUtil {
    /**
     * the Connection used to connect to db server
     */
    private Connection conn;
    private static DBUtil instance;

    /**
     * private constructor, prevent access from the outside the class
     */
    private DBUtil() {

    }

    /**
     * public static method to get singleton instance
     * @return singleton DBUtil instance
     */
    public static DBUtil getInstance() {
        DBUtil dbUtil = instance;

        if (null == dbUtil) {
            synchronized (DBUtil.class) {
                dbUtil = instance;
                if (null == dbUtil) {
                    dbUtil = new DBUtil();
                    instance = dbUtil;
                }
            }
        }

        return dbUtil;
    }

    /**
     * connect to local mysql database server
     * @return Connection if connecting to db server successfully
     */
    private synchronized Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Properties properties = new Properties();
            properties.setProperty("user", ""); // please use your own user name
            properties.setProperty("password", ""); // please use your own password
            properties.setProperty("useSSL", "false");
            properties.setProperty("autoReconnect", "true");
            conn = DriverManager.getConnection("jdbc:mysql://localhost/contactmanager", properties); // please use your own url address
        } catch (Exception e) {
            System.out.println("Error in connection: " + e.getMessage());
        }

        return conn;
    }

    /**
     * insert a new contact
     * @param contact new contact instance
     * @return contact after adding contact_id and address_id
     */
    public synchronized Contact executeInsert(Contact contact) {
        if (null == conn)
            getConnection();

        try {
            String sql = "{call insert_new_contact (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            CallableStatement callableStatement = conn.prepareCall(sql);
            callableStatement.setString(1, contact.getFirstName());
            callableStatement.setString(2, contact.getMiddleName());
            callableStatement.setString(3, contact.getLastName());
            callableStatement.setString(4, contact.getGender());
            callableStatement.setDate("birthday", contact.getBirthday());
            callableStatement.setDate("fmd", contact.getFirstMetDate());
            callableStatement.setString(7, contact.getEmail());
            callableStatement.setString(8, contact.getPhone());
            callableStatement.setString(9, contact.getCity());
            callableStatement.setString(10, contact.getState());
            callableStatement.setString(11, contact.getAddress());

            callableStatement.registerOutParameter(12, Types.INTEGER);
            callableStatement.registerOutParameter(13, Types.INTEGER);
            callableStatement.registerOutParameter(14, Types.INTEGER);

            callableStatement.execute();

            contact.setContactId(callableStatement.getInt(13));
            contact.setAddressId(callableStatement.getInt(14));

            callableStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return contact;
    }

    /**
     * update a contact
     * @param contact contact instance
     * @return 1 if the updating is successful, otherwise return 0
     */
    public synchronized int executeUpdate(Contact contact) {
        int result = 0;
        if (conn == null)
            getConnection();

        try {
            String sql = "{call update_contact (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            CallableStatement callableStatement = conn.prepareCall(sql);

            callableStatement.setString(1, contact.getFirstName());
            callableStatement.setString(2, contact.getMiddleName());
            callableStatement.setString(3, contact.getLastName());
            callableStatement.setString(4, contact.getGender());
            callableStatement.setDate("birthday", contact.getBirthday());
            callableStatement.setDate("fmd", contact.getFirstMetDate());
            callableStatement.setString(7, contact.getEmail());
            callableStatement.setString(8, contact.getPhone());
            callableStatement.setString(9, contact.getCity());
            callableStatement.setString(10, contact.getState());
            callableStatement.setString(11, contact.getAddress());
            callableStatement.setInt(12, contact.getContactId());
            callableStatement.setInt(13, contact.getAddressId());

            callableStatement.registerOutParameter(14, Types.INTEGER);

            callableStatement.execute();
            result = callableStatement.getInt(14);
            callableStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * delete a contact
     * @param contact contact instance
     * @return 1 if the delete operation is successful, otherwise return 0
     */
    public synchronized int executeDelete(Contact contact) {
        int result = 0;
        if (conn == null)
            getConnection();

        try {
            String sql = "{call delete_contact (?, ?)}";
            CallableStatement callableStatement = conn.prepareCall(sql);

            callableStatement.setInt(1, contact.getContactId());
            callableStatement.registerOutParameter(2, Types.INTEGER);
            callableStatement.execute();
            result = callableStatement.getInt(2);
            callableStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * search contacts according to specific search parameters
     * @param arrayList store the search parameters
     * @return ResultSet if search is successful
     */
    public synchronized ResultSet executeSearch(ArrayList<String> arrayList) {
        ResultSet resultSet = null;
        if (conn == null)
            getConnection();

        try {
            String sql = "{call search_contact (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            CallableStatement callableStatement = conn.prepareCall(sql);

            for (int i = 1; i <= 13; i++) {
                if (i == 5 || i == 6) {
                    try {
                        if (arrayList.get(i - 1) == null) {
                            callableStatement.setDate(i, null);
                        } else {
                            java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(arrayList.get(i - 1));
                            callableStatement.setDate(i, new java.sql.Date(date.getTime()));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                callableStatement.setString(i, arrayList.get(i - 1));
            }

            callableStatement.execute();
            resultSet = callableStatement.getResultSet();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultSet;
    }

}
