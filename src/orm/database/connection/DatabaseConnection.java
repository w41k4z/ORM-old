package orm.database.connection;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import orm.database.object.type.Geometry;

public abstract class DatabaseConnection {

    protected java.sql.Connection connection;
    protected String url;
    protected String user;
    protected String password;

    // I- constructors

    //// for null connection
    public DatabaseConnection() {
    }

    public DatabaseConnection(String url, String user, String password) throws SQLException {
        this.setUrl(url);
        this.setUser(user);
        this.setPassword(password);
        this.setConnection(DriverManager.getConnection(this.getUrl(), this.getUser(), this.getPassword()));
        this.setAutoCommit(false);
    }

    // II- setters
    private void setConnection(java.sql.Connection connection) {
        this.connection = connection;
    }

    private void setUrl(String url) {
        this.url = url;
    }

    private void setUser(String user) {
        this.user = user;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    // III- getters
    public java.sql.Connection getConnection() {
        return this.connection;
    }

    public String getUrl() {
        return this.url;
    }

    public String getUser() {
        return this.user;
    }

    private String getPassword() {
        return this.password;
    }

    // IV- methods
    public abstract DatabaseConnection defaultConnection() throws SQLException;

    public abstract String dateFormat(String date);

    public abstract String timeStampFormat(String timestamp);

    public abstract String geometryFormat(Geometry geometry);

    public abstract String sequenceGetter(String getter);

    public abstract String functionGetter(String function);

    public abstract String geometryParser();

    public abstract String geometryAreaMethod();

    public abstract String geometryIntersectMethod();

    public Statement createStatement() throws SQLException {
        return this.getConnection().createStatement();
    }

    public void close() throws SQLException {
        this.getConnection().close();
    }

    //// setting
    public void setAutoCommit(boolean bool) throws SQLException {
        this.getConnection().setAutoCommit(bool);
    }

    //// for transactions
    public void commit() throws SQLException {
        this.getConnection().commit();
    }

    public void rollback() throws SQLException {
        this.getConnection().rollback();
    }
}
