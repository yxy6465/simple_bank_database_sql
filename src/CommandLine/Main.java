package CommandLine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


/**
 * A class of commandline tools for users to check the basic information from the bank database.
 *
 * @author Yuanfa(Lyan) Ye
 * @date 8/5/2020
 * @version 8/6/2020
 */
public class Main {

    /**
     * Helper function to print the options out for user to choose.
     */
    public static void printOptions() {
        System.out.println("Please choose the following options to gain the information(ENTER THE NUMBER):");
        System.out.println("1. List an outline of user’s account balances.");
        System.out.println("2. List the year-to-date balance of each group.");
        System.out.println("3. List the transactions details.");
        System.out.println("4. Exit to end the program.");
    }

    /**
     * Main function to connect to the database and the commandLine tool for user to
     * gain the information from the database.
     * @param args arguments
     */
    public static void main(String[] args) {
        Connection connection = null;
        try {
            String url = "jdbc:sqlite:./bank.db";
            connection = DriverManager.getConnection(url);
            System.out.println("Connect to SQLite database successfully.");
            Scanner scan = new Scanner(System.in);

            while (true) {
                printOptions();
                TransferHandler handler = new TransferHandler(connection);

                int option = Integer.parseInt(scan.nextLine());
                if (option == 4) {
                    System.out.println("Thank you for using, have a good day!");
                    break;
                }

                switch (option) {
                    case 1:
                        handler.handlerOption1();
                        break;
                    case 2:
                        handler.handlerOption2();
                        break;
                    case 3:
                        handler.handlerOption3();
                        break;
                    default:
                        System.out.println("Invalid option, please enter again.");
                        break;

                }
            }
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e2) {
                System.err.println(e2.getMessage());
            }
        }
    }
}
