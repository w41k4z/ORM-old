package orm.database.request;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import orm.database.connection.DatabaseConnection;
import orm.database.object.type.Geometry;

public class SpatialRequest {

    public static Double getGeometryArea(Geometry geometry, DatabaseConnection connection) throws SQLException {
        String request = "SELECT " + connection.geometryAreaMethod() + "(" + connection.geometryFormat(geometry) + ")";
        boolean CONNECTION_ALREADY_OPENED = true;
        if (connection.getConnection() == null) {
            CONNECTION_ALREADY_OPENED = false;
            connection = connection.defaultConnection();
            connection.setAutoCommit(false);
        }
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(request);
        Double area = null;
        if (result.next()) {
            area = result.getDouble(1);
        }
        if (!CONNECTION_ALREADY_OPENED) {
            connection.close();
        }
        return area;
    }

    public static boolean intersects(Geometry geom1, Geometry geom2, DatabaseConnection connection)
            throws SQLException {
        String request = "SELECT " + connection.geometryIntersectMethod() + "(" + connection.geometryFormat(geom1)
                + ", " + connection.geometryFormat(geom2);
        boolean CONNECTION_ALREADY_OPENED = true;
        if (connection.getConnection() == null) {
            CONNECTION_ALREADY_OPENED = false;
            connection = connection.defaultConnection();
            connection.setAutoCommit(false);
        }
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(request);
        boolean intersects = false;
        if (result.next()) {
            char c = result.getString(1).charAt(0);
            intersects = c == 't' ? true : false;
        }
        if (!CONNECTION_ALREADY_OPENED) {
            connection.close();
        }
        return intersects;
    }
}
