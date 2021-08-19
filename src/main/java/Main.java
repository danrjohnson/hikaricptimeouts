import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    private void createBart(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("grant all on *.* to 'bart'@'%' identified by '51mp50n'");
        statement.close();
        connection.close();
    }

    private void changeBart(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("grant all on *.* to 'bart'@'%' identified by 'gunn'");
        statement.close();
        connection.close();
    }

    public static void main(String[] args) throws SQLException {
        HikariConfig rootConfig = new HikariConfig();
        rootConfig.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/mysql");
        rootConfig.setUsername("root");
        rootConfig.setPassword("thanos");

        HikariDataSource rootDataSource = new HikariDataSource(rootConfig);

        Main main = new Main();
        main.createBart(rootDataSource.getConnection());

        HikariConfig bartConfig = new HikariConfig();
        bartConfig.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/");
        bartConfig.setUsername("bart");
        bartConfig.setPassword("51mp50n");

        // Uncomment out this line to reduce the amount of time spent waiting on Hikari
//        bartConfig.setConnectionTimeout(3000);

        HikariDataSource bartDataSource = new HikariDataSource(bartConfig);

        main.changeBart(rootDataSource.getConnection());


        Connection connection = bartDataSource.getConnection();
        bartDataSource.evictConnection(connection);
        connection = bartDataSource.getConnection();

        Statement statement = connection.createStatement();
        statement.execute("select @@hostname");

        ResultSet resultSet = statement.getResultSet();
        while (resultSet.next()) {
            System.out.println(resultSet.getString("@@hostname"));
        }
    }

}
