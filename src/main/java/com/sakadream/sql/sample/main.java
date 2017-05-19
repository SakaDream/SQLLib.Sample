/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sakadream.sql.sample;

import com.sakadream.sql.SQLConfig;
import com.sakadream.sql.SQLConfigJson;
import com.sakadream.sql.SQL;
import com.sakadream.sql.DbType;
import java.sql.ResultSet;
import java.util.Scanner;

/**
 *
 * @author Phan Ba Hai
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        Scanner sc = new Scanner(System.in);
        DbType dbtype;
        String password = "", dbName, host = "";
        Boolean saveJson = false;
        Boolean isEncrypt = false;

        System.out.println("com.sakadream.sql.sample program");
        System.out.println("-------------------------------------------------");

        while (true) {
            System.out.print("Your database type: ");
            String dbTypeAns = sc.nextLine();
            if (dbTypeAns.equalsIgnoreCase("SQL Server")) {
                dbtype = DbType.SQLServer;
                break;
            } else if (dbTypeAns.equalsIgnoreCase("SQLite")) {
                dbtype = DbType.SQLite;
                break;
            } else if (dbTypeAns.equalsIgnoreCase("MySQL")) {
                dbtype = DbType.MySQL;
                break;
            } else {
                System.out.println("DB Type not found! Please try agian");
            }
        }
        
        if (dbtype == DbType.MySQL) {
            System.out.print("Database Host: ");
            host = sc.nextLine();
        }
        System.out.print("Database Name: ");
        dbName = sc.nextLine();
        if (dbtype == DbType.SQLServer | dbtype == DbType.MySQL) {
            System.out.print("Database Password: ");
            password = sc.nextLine();
        } 

        while (true) {
            System.out.print("Write connection string to config.json? (y / n) : ");
            String ans = sc.nextLine();
            if (ans.equalsIgnoreCase("y")) {
                saveJson = true;
                while (true) {
                    System.out.print("Encrypt config.json? (y / n) : ");
                    String ansEncrypt = sc.nextLine();
                    if (ansEncrypt.equalsIgnoreCase("y")) {
                        isEncrypt = true;
                        break;
                    } else if (ansEncrypt.equalsIgnoreCase("n")) {
                        isEncrypt = false;
                        break;
                    }
                }
                break;
            } else if (ans.equalsIgnoreCase("n")) {
                saveJson = false;
                break;
            }
        }

        System.out.println("Creating SQLConfig object...");
        SQLConfig config = new SQLConfig();
        if (dbtype == DbType.SQLServer || dbtype == DbType.SQLite) {
            config.setDBType(dbtype);
            config.setClassNameByDBType();
            config.setDbName(dbName);
            config.setPassword(password);
            config.setGeneratedUrl();
        } else {
            config.setDBType(dbtype);
            config.setClassNameByDBType();
            config.setHost(host);
            config.setPort("3306");
            config.setDbName(dbName);
            config.setUsername("test");
            config.setPassword(password);
            config.setGeneratedUrl();
        }
        if (saveJson) {
            System.out.println("Saving connection string to config.json...");
            SQLConfigJson.save(config, isEncrypt);
        }
        System.out.println("Connecting to database...");
        SQL.createConnection();

        try {
            switch (config.getDBType()) {
                case SQLServer:
                    System.out.println("Dropping table TEST if exists...");
                    try {
                        SQL.updateQuery("DROP TABLE TEST");
                    } catch (Exception e) {}
                    System.out.println("Creating TEST table...");
                    SQL.updateQuery("CREATE TABLE TEST (ID INT IDENTITY(1,1) PRIMARY KEY NOT NULL, "
                            + "NAME NVARCHAR(50) NOT NULL, "
                            + "GENDER INT NOT NULL "
                            + "DEFAULT 0 CHECK(GENDER = 0 OR GENDER = 1), "
                            + "SALARY INT DEFAULT 0)");
                    break;
                case SQLite:
                    System.out.println("Creating TEST table...");
                    SQL.updateQuery("CREATE TABLE 'TEST' "
                            + "('ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                            + "'NAME' TEXT NOT NULL, "
                            + "'GENDER' INTEGER NOT NULL "
                            + "DEFAULT 0 CHECK(GENDER == 0 OR GENDER == 1), "
                            + "'SALARY' REAL DEFAULT 0)");
                    break;
                case MySQL:
                    System.out.println("Dropping table TEST if exists...");
                    try {
                        SQL.updateQuery("DROP TABLE TEST");
                    } catch(Exception e) {}
                    System.out.println("Creating TEST table...");
                    SQL.updateQuery("CREATE TABLE TEST "
                            + "(ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
                            + "NAME VARCHAR(50) NOT NULL, "
                            + "GENDER BIT NOT NULL, "
                            + "SALARY INT DEFAULT 0)");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("-------------------------------------------------");
        String name;
        int gender = 0, salary = 0;
        System.out.println("Please input some information");
        System.out.print("Name: ");
        name = sc.nextLine();
        System.out.print("Gender (1 is male, 0 is female) : ");
        gender = sc.nextInt();
        System.out.print("Salary: ");
        salary = sc.nextInt();

        System.out.println("-------------------------------------------------");
        System.out.println("Inserting record to database...");
        switch (config.getDBType()) {
            case SQLServer:
                SQL.updateQuery("INSERT INTO TEST (NAME, GENDER, SALARY) VALUES (N'" + name + "' , " + gender + " , " + salary + ")");
                break;
            case SQLite:
                SQL.updateQuery("INSERT INTO TEST (NAME, GENDER, SALARY) VALUES ('" + name + "', " + gender + ", " + salary + ")");
                break;
            case MySQL:
                SQL.updateQuery("INSERT INTO TEST (NAME, GENDER, SALARY) VALUES ('" + name + "' , " + gender + " , " + salary + ")");
                break;
        }
        System.out.println("-------------------------------------------------");
        System.out.println("Show all records in database...");
        ResultSet rs = SQL.selectQuery("SELECT * FROM TEST");
        while (rs.next()) {
            System.out.println("ID: " + rs.getInt(1));
            System.out.println("Name: " + rs.getString(2));
            System.out.println("Gender: " + rs.getInt(3));
            System.out.println("Salary: " + rs.getInt(4));
            System.out.println();
        }

        System.out.println("-------------------------------------------------");
        System.out.println("Press any key to exit...");
        sc.nextLine();
    }
}
