package org.example;

import java.sql.*;

public class AdminPanelDataBaseConnection {
    public static String url = "jdbc:postgresql://localhost:5432/postgres";
    public static String dbUser = "postgres";
    public static String Password = "Root12345";


    public static boolean SendAdminPassword(long chatId, String password) throws SQLException {
        Connection connection = DriverManager.getConnection(url, dbUser, Password);
        Statement statement = connection.createStatement();

        String query = "select * from admin_panel";

        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            String admin_password = resultSet.getString(2);
            if (admin_password.equals(password)) {
                AdminId(chatId, password);
                return true;
            }
        }

        resultSet.close();
        statement.close();
        connection.close();
        return false;
    }


    public static void AdminId(long chatId, String password) throws SQLException {
        Connection connection = DriverManager.getConnection(url, dbUser, Password);
        Statement statement = connection.createStatement();

        String query = "select * from admin_panel";

        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            String admin_password = resultSet.getString(2);
            if (admin_password.equals(password)) {
                int id = resultSet.getInt(1);
                AdminInformation.AdminId.put(chatId, id);
            }
        }
        resultSet.close();
        statement.close();
        connection.close();
    }


    public static void NewPassword(long chatId, String password) throws SQLException {
        Connection connection = DriverManager.getConnection(url, dbUser, Password);

        Statement statement = connection.createStatement();

        String query = "UPDATE admin_panel SET admin_password = '" + password + "' WHERE admin_id = "+AdminInformation.AdminId.get(chatId)+";";

        statement.execute(query);

        System.out.println(" Ma'lumot bazaga yozildi! ");

    }
}
