package Data;

import org.jetbrains.annotations.NotNull;

/**
 * Represents GPS coordinates of cadastral objects
 */
public class GPS {

    public enum Latitude {
        NORTH,
        SOUTH
    }

    public enum Longitude {
        EAST,
        WEST
    }

    private static final double EPSILON = 0.00000001;
    private Latitude latitude;
    private Longitude longitude;
    private double latitudePosition;
    private double longitudePosition;

    /**
     * constructor for creating new gps from double values and! Latitude and Longitude enum representation.
     */
    public GPS (
            Latitude parLatitude,
            double parLatitudePosition,
            Longitude parLongitude,
            double parLongitudePosition
    ) {
        this.latitude = parLatitude;
        this.longitude = parLongitude;
        this.latitudePosition = parLatitudePosition;
        this.longitudePosition = parLongitudePosition;
    }

    /**
     * constructor for creating new gps from double values and string representation of latitude and longitude.
     */
    public GPS (
            String parLatitude,
            double parLatitudePosition,
            String parLongitude,
            double parLongitudePosition
    ) {
        this.setLatitude(parLatitude);
        this.setLongitude(parLongitude);
        this.latitudePosition = parLatitudePosition;
        this.longitudePosition = parLongitudePosition;
    }

    /**
     * constructor just for creating new gps, new instance will have default values.
     */
    public GPS() {
        this("N",0,"W",0);
    }

    /**
     * Getters and setters
     */
    public Latitude getLatitude() {
        return latitude;
    }

    public Longitude getLongitude() {
        return longitude;
    }

    public double getLatitudePosition() {
        return latitudePosition;
    }

    public double getLongitudePosition() {
        return longitudePosition;
    }

    public void setLatitude(String parLatitude) {
        if (parLatitude.equals("N") || parLatitude.equals("North") || parLatitude.equals("north")) {
            this.latitude = Latitude.NORTH;
        } else {
            this.latitude = Latitude.SOUTH;
        }
    }

    public void setLongitude(String parLongitude) {
        if (parLongitude.equals("W") || parLongitude.equals("West") || parLongitude.equals("west")) {
            this.longitude = Longitude.WEST;
        } else {
            this.longitude = Longitude.EAST;
        }
    }

    public void setLatitudePosition(double parLatitudePosition) {
        this.latitudePosition = parLatitudePosition;
    }

    public void setLongitudePosition(double parLongitudePosition) {
        this.longitudePosition = parLongitudePosition;
    }

    /**
     *     -1 - param gps is smaller than this , 1  - param gps is bigger than this
     *     what i mean is, that if first coordinates is more northern and western
     */
    public int isBiggerThan(GPS parGps) {

        if (this.getLatitude().equals(parGps.getLatitude()) &&
                this.getLatitude().equals(Latitude.NORTH) &&
                this.getLatitudePosition() > parGps.getLatitudePosition()
                &&
                this.getLongitude().equals(parGps.getLongitude()) &&
                this.getLongitude().equals(Longitude.WEST) &&
                this.getLongitudePosition() > parGps.getLongitudePosition()) {
            return -1;
            // 3.1
        } else if (this.getLatitude().equals(parGps.getLatitude()) &&
                this.getLatitude().equals(Latitude.SOUTH) &&
                this.getLatitudePosition() < parGps.getLatitudePosition()
                &&
                this.getLongitude().equals(parGps.getLongitude()) &&
                this.getLongitude().equals(Longitude.WEST) &&
                this.getLongitudePosition() > parGps.getLongitudePosition()) {
            return -1;
            // 4.1
        } else if (this.getLatitude().equals(parGps.getLatitude()) &&
                this.getLatitude().equals(Latitude.SOUTH) &&
                this.getLatitudePosition() < parGps.getLatitudePosition()
                &&
                this.getLongitude().equals(parGps.getLongitude()) &&
                this.getLongitude().equals(Longitude.EAST) &&
                this.getLongitudePosition() < parGps.getLongitudePosition()) {
            return -1;
            // 1.3
        } else if (this.getLatitude().equals(Latitude.NORTH) &&
                parGps.getLatitude().equals(Latitude.SOUTH)
                &&
                this.getLongitude().equals(parGps.getLongitude()) &&
                this.getLongitude().equals(Longitude.WEST) &&
                this.getLongitudePosition() > parGps.getLongitudePosition()) {
            return -1;
            // 2.2
        } else if (this.getLatitude().equals(Latitude.NORTH) &&
                parGps.getLatitude().equals(Latitude.SOUTH)
                &&
                this.getLongitude().equals(parGps.getLongitude()) &&
                this.getLongitude().equals(Longitude.EAST) &&
                this.getLongitudePosition() < parGps.getLongitudePosition()) {
            return -1;
            // 2.1
        } else if (this.getLatitude().equals(parGps.getLatitude()) &&
                this.getLatitude().equals(Latitude.NORTH) &&
                this.getLatitudePosition() > parGps.getLatitudePosition()
                &&
                this.getLongitude().equals(parGps.getLongitude()) &&
                this.getLongitude().equals(Longitude.EAST) &&
                this.getLongitudePosition() < parGps.getLongitudePosition()) {
            return -1;
            // 3.2
        } else if (this.getLatitude().equals(parGps.getLatitude()) &&
                this.getLatitude().equals(Latitude.SOUTH) &&
                this.getLatitudePosition() < parGps.getLatitudePosition()
                &&
                this.getLongitude().equals(Longitude.WEST) &&
                parGps.getLongitude().equals(Longitude.EAST)){
            return -1;
            // 1.2
        } else if (this.getLatitude().equals(parGps.getLatitude()) &&
                this.getLatitude().equals(Latitude.NORTH) &&
                this.getLatitudePosition() > parGps.getLatitudePosition()
                &&
                this.getLongitude().equals(Longitude.WEST) &&
                parGps.getLongitude().equals(Longitude.EAST)) {
            return -1;
            // 1.4
        } else if (this.getLatitude().equals(Latitude.NORTH) &&
                parGps.getLatitude().equals(Latitude.SOUTH) &&
                this.getLongitude().equals(Longitude.WEST) &&
                parGps.getLongitude().equals(Longitude.EAST)
        ) {
            return -1;
        }

        return 1;
    }

    /**
     * Return true when one coordinates are the same
     * with another given coordinates, false otherwise
     */
    public static boolean equals(GPS parCoordinatesOne, GPS parCoordinatesTwo) {
        return Math.abs(parCoordinatesOne.getLatitudePosition() - parCoordinatesTwo.getLatitudePosition()) < EPSILON &&
                Math.abs(parCoordinatesOne.getLongitudePosition() - parCoordinatesTwo.getLongitudePosition()) < EPSILON &&
                parCoordinatesOne.getLatitude().equals(parCoordinatesTwo.getLatitude()) &&
                parCoordinatesOne.getLongitude().equals(parCoordinatesTwo.getLongitude());
    }

    /**
     * Return true when one array of coordinates are the same
     * with another given array of coordinates, false otherwise
     */
    public static boolean equals(GPS[] parCoordinatesOne, GPS[] parCoordinatesTwo) {
        boolean areSame = false;
        for (int i = 0; i < parCoordinatesOne.length; i++) {
            if (Math.abs(parCoordinatesOne[i].getLatitudePosition() - parCoordinatesTwo[i].getLatitudePosition()) < EPSILON &&
                    Math.abs(parCoordinatesOne[i].getLongitudePosition() - parCoordinatesTwo[i].getLongitudePosition()) < EPSILON &&
                    parCoordinatesOne[i].getLatitude().equals(parCoordinatesTwo[i].getLatitude()) &&
                    parCoordinatesOne[i].getLongitude().equals(parCoordinatesTwo[i].getLongitude())) {
                areSame = true;
            } else {
                areSame = false;
            }
        }
        return areSame;
    }


    /**
     * Some methods which returns string representations of some class property
     */
    public String[] returnAsStringArray() {
        // latitude, longitude, latipos, longpos
        String[] s = {this.latitude.toString(),Double.toString(this.latitudePosition), this.longitude.toString()
                , Double.toString(this.longitudePosition)};
        return s;
    }

    public String stringMapLongitudeName() {
        if (this.longitude.equals(Longitude.WEST)) {
            return "W";
        } else {
            return "E";
        }
    }

    public String stringMapLatitudeName() {
        if (this.latitude.equals(Latitude.NORTH)) {
            return "N";
        } else {
            return "S";
        }
    }

    /**
     * returns size of object
     */
    public static int getSize() {
        int size =  Character.BYTES * 2; // longitue and latitutude
        size += Double.BYTES * 2; // positions

        return size;
    }

}