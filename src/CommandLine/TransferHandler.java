package CommandLine;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


/**
 * A class to generalize sql to database according to users' instructions and to send
 * the received resultSet back to local for more processes or display.
 *
 * @author Yuanfa(Lyan) Ye
 * @date 8/5/2020
 * @version 8/6/2020
 */
public class TransferHandler {

    Connection connection;

    /**
     * Constructor of this class.
     * @param connection the connection that connects to the database.
     */
    public TransferHandler(Connection connection) {
        this.connection = connection;
    }


    /**
     * To handle the first option which is listing an outline of the account balance for
     * each active user of each month.
     */
    public void handlerOption1() {
        try {
            // a hashmap to keep updating the cumulative account balance for each month of each person
            HashMap mapForCheck = new HashMap<Integer, Double>();

            Statement statement = connection.createStatement();
            String sql = "SELECT u.first_name as \"first name\", " +
                    "u.last_name as \"last name\", " +
                    "strftime('%m', ut.timestamp ) as \"month\", " +
                    "SUM(amount) as \"account balance\", " +
                    "u.id as \"id\" " +
                    "FROM user u JOIN user_transaction ut ON u.id = ut.user_id " +
                    "JOIN transaction_type t ON ut.transaction_type_id = t.id " +
                    "where u.status = 'A' and t.status = 'A'" +
                    "GROUP BY u.name, month";
            ResultSet rs = statement.executeQuery(sql);
            System.out.printf("%-20s %-10s %-10s%n","User Name", "Month", "Account balance(cumulative)");
            while (rs.next()) {
                String name = rs.getString("first name") + " " + rs.getString("last name");
                String month = rs.getString("month");
                double balance = rs.getDouble("account balance");
                int id = rs.getInt("id");
                if (mapForCheck.containsKey(id)) {
                    balance += Double.parseDouble(mapForCheck.get(id).toString());
                }
                //update the cumulative balance for that user
                mapForCheck.put(id, balance);

                outputHandler1(name, month, balance);
            }
            System.out.println();
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }


    /**
     * To display the queried data in a specific format.
     * @param name the name of the user
     * @param month the month
     * @param balance the cumulative balance of that month
     */
    private void outputHandler1(String name, String month, double balance) {
        System.out.printf("%-20s %-10s %-10.2f%n",name, month, balance);
    }


    /**
     * To handle to second option which is listing the account balance across all months for all
     * users in each group.
     */
    public void handlerOption2() {
        try {
            Statement statement = this.connection.createStatement();
            String sql = "SELECT gt.display_name as \"group name\", " +
                    "SUM(ut.amount) as \"balance\" " +
                    "FROM group_type gt JOIN group_membership gm ON gt.id = gm.group_type_id " +
                    "JOIN user u ON gm.user_id = u.id " +
                    "JOIN user_transaction ut ON u.id = ut.user_id " +
                    "JOIN transaction_type tt ON tt.id = ut.transaction_type_id " +
                    "WHERE u.status = 'A' AND tt.status = 'A' AND gt.status = 'A' " +
                    "GROUP BY gt.display_name;";

            ResultSet rs = statement.executeQuery(sql);
            System.out.printf("%-30s %-30s%n", "Group Name", "Balance");
            while (rs.next()) {
                String group_name = rs.getString("group name");
                double balance = rs.getDouble("balance");
                outputHandler2(group_name, balance);
            }
            rs.close();
            statement.close();
            System.out.println();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }


    /**
     * To display the queried data in a specific format.
     * @param name the name of  group
     * @param balance the balance of the group for all users and all transactions.
     */
    private void outputHandler2(String name, double balance) {
        System.out.printf("%-30s %-30.2f%n", name, balance);
    }


    /**
     * To transfer the number of month into word for better visualization.
     * @param month the number of month in string
     * @return the word of the month
     */
    private String monthFormatHandler(String month) {
        String month_in_name;
        switch (Integer.parseInt(month)) {
            case 1:
                month_in_name = "January";
                break;
            case 2:
                month_in_name = "February";
                break;
            case 3:
                month_in_name = "March";
                break;
            case 4:
                month_in_name = "April";
                break;
            case 5:
                month_in_name = "May";
                break;
            case 6:
                month_in_name = "June";
                break;
            case 7:
                month_in_name = "July";
                break;
            case 8:
                month_in_name = "August";
                break;
            case 9:
                month_in_name = "September";
                break;
            case 10:
                month_in_name = "October";
                break;
            case 11:
                month_in_name = "November";
                break;
            case 12:
                month_in_name = "December";
                break;
            default:
                month_in_name = null;
                break;
        }
        return month_in_name;
    }


    /**
     * To handle the third option for users to see that How many transactions of
     * each transaction type have happened per month.
     */
    public void handlerOption3() {
        try {
            Statement statement = this.connection.createStatement();
            String sql = "SELECT tt.display_name as \"display name\", " +
                    "strftime('%m', ut.timestamp ) as \"month\", " +
                    "COUNT(ut.transaction_type_id) as \"transaction count\" " +
                    "FROM transaction_type tt JOIN user_transaction ut " +
                    "ON tt.id = ut.transaction_type_id " +
                    "WHERE tt.status = 'A' " +
                    "GROUP BY tt.display_name, month";

            ResultSet rs = statement.executeQuery(sql);
            System.out.printf("%-40s %-20s %-20s%n","Transaction Type", "Month", "Number of Times");
            while (rs.next()) {
                String type = rs.getString("display name");
                String month_inNum = rs.getString("month");
                String month = monthFormatHandler(month_inNum);
                int count = rs.getInt("transaction count");
                outputHandler3(type, month, count);
            }

            rs.close();
            statement.close();
            System.out.println();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }


    /**
     *  To display the queried data in a specific format.
     * @param type the name of the transaction type
     * @param month the month in word
     * @param count number of times for the type of specific month
     */
    private void outputHandler3(String type, String month, int count) {
        System.out.printf("%-40s %-20s %-20d%n", type, month, count);
    }


    /**
     * A method to create a new temporary column called account_balance to user table from
     * database so that the monthly account balance can be displayed cumulatively.
     *
     * @throws SQLException if anything goes wrong。
     */
    private void addUpBalanceColumn() {
        try {
            // check if the database has account_balance in user table
            String sql = "SELECT account_balance FROM user";
            Statement statement = this.connection.createStatement();
            statement.executeQuery(sql);

            statement.close();

        } catch (SQLException e) {
            try {
                // catch the exception if the column doesn't exist, and then create one.
                String sql = "ALTER TABLE user " +
                        "ADD COLUMN account_balance NOT NULL DEFAULT 0";
                Statement stmt = this.connection.createStatement();
                stmt.executeUpdate(sql);
                stmt.close();
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }


    /**
     * To clean up the temporary column from user table. Can't be used in SQLite.
     * @throws SQLException if sql process goes wrong.
     */
    public void cleanUpTemporaryColumn() throws SQLException {
        String sql = "ALTER TABLE user " +
                "DROP COLUMN account_balance";
        Statement stmt = this.connection.createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
    }


    /**
     * The other method to update the cumulative account balance for each user of each month.
     * Didn't use it, just for other options.
     * @param id the user id
     * @param balance the cumulative account balance of the user
     * @return the new updated cumulative balance
     * @throws SQLException if sql process goes wrong.
     */
    private double updateAndGetNewBalance(int id, double balance) throws SQLException {
        Statement statement = connection.createStatement();
        String sql = "SELECT account_balance FROM user WHERE user.id = " + id;
        ResultSet rs = statement.executeQuery(sql);
        double new_balance = balance;
        if (rs.next()) {
            new_balance += rs.getDouble("account_balance");
        }
        sql = "UPDATE user SET account_balance = " + Double.toString(new_balance)
                + " WHERE user.id = " + id;
        statement.executeUpdate(sql);
        return new_balance;
    }


}