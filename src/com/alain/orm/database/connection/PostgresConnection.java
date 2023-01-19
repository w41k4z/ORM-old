package com.alain.orm.database.connection;

import java.sql.SQLException;

public class PostgresConnection extends DatabaseConnection {

    public PostgresConnection(){}

    public PostgresConnection(String url, String user, String password) throws SQLException {
        super(url, user, password);
    }


    @Override
    public DatabaseConnection defaultConnection() throws SQLException {
        return new PostgresConnection("jdbc:postgresql://localhost/star_product", "w41k4z", "w41k4z!");
    }

    @Override
    public String dateFormat(String date) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String sequenceGetter(String getter) {
        return "SELECT nextval('" + getter + "')";
    }

    @Override
    public String functionGetter(String function) {
        return "SELECT * FROM " + function;
    }
    
}
