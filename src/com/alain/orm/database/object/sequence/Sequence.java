package com.alain.orm.database.object.sequence;

import java.sql.ResultSet;
import java.sql.Statement;

import com.alain.orm.database.connection.DatabaseConnection;

public class Sequence {
    
    private String target;

    // I- constructor
    public Sequence(String target) {
        this.setTarget(target);
    }

    // II- setter
    private void setTarget(String target) {
        this.target = target;
    }

    // III- getter
    public String getTarget() {
        return this.target;
    }

    // IV- method
    public int get(DatabaseConnection connection) throws Exception {
        boolean connectionWasMine = false;
        if (connection.getConnection() == null) {
            connectionWasMine = true;
            connection = connection.defaultConnection();
        }

        // the specified request up to the dbms used
        String request = connection.sequenceGetter(this.getTarget());

        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(request);
        result.next();
        int seq = result.getInt(1);
        statement.close();
        if (connectionWasMine)
            connection.close();
        return seq;
    }
}