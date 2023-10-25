package orm.database.object.type;

import orm.utilities.Treatment;

public class Polygon extends Geometry {
    /* FIELD */
    private Point[] vertices;

    /* CONSTRUCTOR */
    public Polygon(Point[] vertices) {
        super(vertices);
    }

    /* METHODS */
    @Override
    protected void setValue(Object latLngs) {
        if (Treatment.isArray(latLngs, 1, Point.class)) {
            this.vertices = (Point[]) latLngs;
            return;
        }
        throw new IllegalArgumentException("Polygon needs an array of Point");
    }

    @Override
    public String getStringValue() {
        StringBuffer sb = new StringBuffer("POLYGON((");
        for (int i = 0; i < this.vertices.length; i++) {
            sb.append(this.vertices[i].getLongitude() + " " + this.vertices[i].getLatitude());
            if (i < this.vertices.length - 1) {
                sb.append(",");
            }
        }
        sb.append("))");
        return sb.toString();
    }

    public Point[] getVertices() {
        return this.vertices;
    }

    public static Polygon valueOf(String value) {
        value = value.replace("POLYGON((", "");
        value = value.replace("))", "");
        String[] coordinates = value.split(",");
        Point[] vertices = new Point[coordinates.length];
        int index = 0;
        for (String coordinate : coordinates) {
            // Split the coordinate string into longitude and latitude values.
            String[] longitudeLatitude = coordinate.split(" ");
            Double longitude = Double.parseDouble(longitudeLatitude[0]);
            Double latitude = Double.parseDouble(longitudeLatitude[1]);

            // Create a new Point object.
            vertices[index++] = new Point(new Double[] { latitude, longitude });
        }
        return new Polygon(vertices);
    }
}
