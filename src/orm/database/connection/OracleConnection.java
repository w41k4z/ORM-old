package orm.database.connection;

import java.sql.SQLException;

public abstract class OracleConnection extends DatabaseConnection {
    
    public OracleConnection(){}

    public OracleConnection(String url, String user, String password) throws SQLException {
        super(url, user, password);
    }

    @Override
    public String dateFormat(String date) {
        return "TO_DATE(" + date + ", 'YYYY-MM-DD HH:MI:SS')";
    }

    @Override
    public String timeStampFormat(String timestamp) {
        return null;
    }

    @Override
    public String sequenceGetter(String getter) {
        return "SELECT " + getter + ".nextval FROM DUAL";
    }

    @Override
    public String functionGetter(String function) {
        return "SELECT * FROM TAB(" + function + ")";
    }

}
