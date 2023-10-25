package orm.database.connection;

import java.sql.SQLException;

import orm.database.object.type.Geometry;

public abstract class PostgresConnection extends DatabaseConnection {

    public PostgresConnection() {
    }

    public PostgresConnection(String url, String user, String password) throws SQLException {
        super(url, user, password);
    }

    @Override
    public String dateFormat(String date) {
        return "'" + date + "'";
    }

    @Override
    public String timeStampFormat(String timestamp) {
        return "TIMESTAMP '" + timestamp + "'";
    }

    @Override
    public String sequenceGetter(String getter) {
        return "SELECT nextval('" + getter + "')";
    }

    @Override
    public String functionGetter(String function) {
        return "SELECT * FROM " + function;
    }

    @Override
    public String geometryParser() {
        return "ST_AsText";
    }

    @Override
    public String geometryAreaMethod() {
        return "ST_Area";
    }

    @Override
    public String geometryIntersectMethod() {
        return "ST_Intersects";
    }

    @Override
    public String geometryFormat(Geometry geometry) {
        return this.geometryParser() + "('" + geometry.getStringValue() + "')";
    }
}
