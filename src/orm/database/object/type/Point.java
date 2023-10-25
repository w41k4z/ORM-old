package orm.database.object.type;

import orm.utilities.Treatment;

public class Point extends Geometry {
    /* FIELDS */
    private Double latitude;
    private Double longitude;

    /* CONSTRUCTOR */
    /**
     * Default constructor
     * 
     * @param latLngs Array of latitude and longitude
     */
    public Point(Double[] latLngs) {
        super(latLngs);
    }

    /* METHODS */
    @Override
    protected void setValue(Object latLngs) {
        if (Treatment.isArray(latLngs, 1, Double.class)) {
            Double[] latLngsArray = (Double[]) latLngs;
            this.latitude = latLngsArray[0];
            this.longitude = latLngsArray[1];
            return;
        }
        throw new IllegalArgumentException("Point needs an array of latitude and longitude");
    }

    @Override
    public String getStringValue() {
        return "POINT(" + this.longitude + " " + this.latitude + ")";
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public static Point valueOf(String value) {
        value = value.replace("POINT(", "");
        value = value.replace(")", "");
        String[] longitudeLatitude = value.split(" ");
        Double longitude = Double.parseDouble(longitudeLatitude[0]);
        Double latitude = Double.parseDouble(longitudeLatitude[1]);
        return new Point(new Double[] { latitude, longitude });
    }
}
