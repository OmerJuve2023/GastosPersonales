package config;

import java.sql.Connection;
import java.sql.DriverManager;


public class Mysql {
    public Connection connection;

    public Mysql() {
    }

    public Connection getConnection() {

        String databaseName="gastos_personales";
        String databaseUser="root";
        String databasePassword="defaultPassword";
        String url="jdbc:mysql://localhost:3306/"+databaseName;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection= DriverManager.getConnection(url,databaseUser,databasePassword);
            System.out.println("connection Succesful");
        }catch (Exception e) {
            e.printStackTrace();
            e.getCause();
            System.out.println("conecctuion dead");
        }
        return connection;
    }
    public static void main(String[] args) {
        Mysql conn=new Mysql();
        conn.getConnection();
    }

}
