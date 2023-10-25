package orm.database.object.type;

import java.sql.SQLException;

import orm.database.connection.DatabaseConnection;
import orm.database.request.SpatialRequest;

public abstract class Geometry {
    /* CONSTRUCTOR */
    public Geometry(Object latLngs) {
        this.setValue(latLngs);
    }

    /* METHODS */
    protected abstract void setValue(Object latLngs);

    public abstract String getStringValue();

    public double getArea(DatabaseConnection connection) throws SQLException {
        return SpatialRequest.getGeometryArea(this, connection);
    }

    public boolean intersects(Geometry geometry, DatabaseConnection connection) throws SQLException {
        return SpatialRequest.intersects(this, geometry, connection);
    }
}
