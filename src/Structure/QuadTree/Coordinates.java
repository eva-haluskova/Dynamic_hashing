package Structure.QuadTree;

import org.jetbrains.annotations.NotNull;

/**
 * Represents class of some objects coordinates. Holds four values -
 * two for x axis, two for y axis. If we want to have only point,
 * not polygon, class contain constructor just for point.
 */

public class Coordinates {

    private double lowerX;
    private double upperX;
    private double lowerY;
    private double upperY;

    private static final double EPSILON = 0.00000001;

    /**
     * Constructor for polygon
     */
    public Coordinates(
            double parLowerX,
            double parUpperX,
            double parLowerY,
            double parUpperY
    ) {
        this.lowerX = parLowerX;
        this.upperX = parUpperX;
        this.lowerY = parLowerY;
        this.upperY = parUpperY;
    }

    /**
     * Constructor for point
     */
    public Coordinates(
            double parX,
            double parY
    ) {
        this.lowerX = parX;
        this.upperX = parX;
        this.lowerY = parY;
        this.upperY = parY;
    }

    /**
     * Copy constructor
     */
    public Coordinates(
            @NotNull Coordinates otherCoordinates
    ) {
        this.lowerX = otherCoordinates.getLowerX();
        this.upperX = otherCoordinates.getUpperX();
        this.lowerY = otherCoordinates.getLowerY();
        this.upperY = otherCoordinates.getUpperY();
    }

    /**
     * getters and setters of specific coordinates
     */
    public double getLowerX() {
        return lowerX;
    }

    public void setLowerX(double lowerX) {
        this.lowerX = lowerX;
    }

    public double getUpperX() {
        return upperX;
    }

    public void setUpperX(double upperX) {
        this.upperX = upperX;
    }

    public double getLowerY() {
        return lowerY;
    }

    public void setLowerY(double lowerY) {
        this.lowerY = lowerY;
    }

    public double getUpperY() {
        return upperY;
    }

    public void setUpperY(double upperY) {
        this.upperY = upperY;
    }

    /**
     * Return true when coordinates belonging to this object are the "same"
     * with coordinates given as a parameter, false otherwise
     */
    public boolean equals(Coordinates parCoordinates) {
        return Math.abs(this.lowerX - parCoordinates.getLowerX()) < EPSILON &&
                Math.abs(this.upperX - parCoordinates.getUpperX()) < EPSILON &&
                Math.abs(this.lowerY - parCoordinates.getLowerY()) < EPSILON &&
                Math.abs(this.upperY - parCoordinates.getUpperY()) < EPSILON;
    }

    /**
     * Return string representation of coordinates
     */
    public String[] returnCoordinatesInString() {
        String[] stringToReturn = {Double.toString(this.getLowerX()),Double.toString(this.getUpperX())
                ,Double.toString(this.getLowerY()),Double.toString(this.getUpperY())};
        return stringToReturn;
    }

    /**
     * Static methods for outer usage
     */

    /**
     * Checks if some cooridnates whole belongs to some other coordinates
     */
    public static boolean isIncludingWholeData(Coordinates parCoordinatesOfArea, Coordinates parCoordinatesOfData) {
        return ((parCoordinatesOfArea.getLowerX() < parCoordinatesOfData.getLowerX() ||
                Math.abs(parCoordinatesOfArea.getLowerX() - parCoordinatesOfData.getLowerX()) < EPSILON) &&

                (parCoordinatesOfArea.getUpperX() > parCoordinatesOfData.getUpperX() ||
                Math.abs(parCoordinatesOfArea.getUpperX() - parCoordinatesOfData.getUpperX()) < EPSILON) &&

                (parCoordinatesOfArea.getLowerY() < parCoordinatesOfData.getLowerY() ||
                Math.abs(parCoordinatesOfArea.getLowerY() - parCoordinatesOfData.getLowerY()) < EPSILON) &&

                (parCoordinatesOfArea.getUpperY() > parCoordinatesOfData.getUpperY() ||
                Math.abs(parCoordinatesOfArea.getUpperY() - parCoordinatesOfData.getUpperY()) < EPSILON));
    }

    /**
     * Return true if some part of quadrant belong to area, or if some data belongs to area
     */
    public static boolean belongsToArea(Coordinates parCoordinatesOfArea, Coordinates parCoordinatesOfQuadrant) {
        return (
                //1
                ((parCoordinatesOfArea.getLowerX() > parCoordinatesOfQuadrant.getLowerX() ||
                        Math.abs(parCoordinatesOfArea.getLowerX() - parCoordinatesOfQuadrant.getLowerX()) < EPSILON) &&

                        (parCoordinatesOfArea.getLowerX() < parCoordinatesOfQuadrant.getUpperX() ||
                        Math.abs(parCoordinatesOfArea.getLowerX() - parCoordinatesOfQuadrant.getUpperX()) < EPSILON) &&

                        (parCoordinatesOfArea.getLowerY() > parCoordinatesOfQuadrant.getLowerY() ||
                        Math.abs(parCoordinatesOfArea.getLowerY() - parCoordinatesOfQuadrant.getLowerY()) < EPSILON) &&

                        (parCoordinatesOfArea.getLowerY() < parCoordinatesOfQuadrant.getUpperY() ||
                        Math.abs(parCoordinatesOfArea.getLowerY() - parCoordinatesOfQuadrant.getUpperY()) < EPSILON)

                ) ||
                        //2
                        ((parCoordinatesOfArea.getUpperX() > parCoordinatesOfQuadrant.getLowerX() ||
                        Math.abs(parCoordinatesOfArea.getUpperX() - parCoordinatesOfQuadrant.getLowerX()) < EPSILON) &&

                        (parCoordinatesOfArea.getUpperX() < parCoordinatesOfQuadrant.getUpperX() ||
                        Math.abs(parCoordinatesOfArea.getUpperX() - parCoordinatesOfQuadrant.getUpperX()) < EPSILON) &&

                        (parCoordinatesOfArea.getLowerY() > parCoordinatesOfQuadrant.getLowerY() ||
                        Math.abs(parCoordinatesOfArea.getLowerY() - parCoordinatesOfQuadrant.getLowerY()) < EPSILON) &&

                        (parCoordinatesOfArea.getLowerY() < parCoordinatesOfQuadrant.getUpperY() ||
                        Math.abs(parCoordinatesOfArea.getLowerY() - parCoordinatesOfQuadrant.getUpperY()) < EPSILON)
                ) ||
                        //3
                        ((parCoordinatesOfArea.getLowerX() > parCoordinatesOfQuadrant.getLowerX()  ||
                        Math.abs(parCoordinatesOfArea.getLowerX() - parCoordinatesOfQuadrant.getLowerX()) < EPSILON) &&

                        (parCoordinatesOfArea.getLowerX() < parCoordinatesOfQuadrant.getUpperX()  ||
                        Math.abs(parCoordinatesOfArea.getLowerX() - parCoordinatesOfQuadrant.getUpperX()) < EPSILON) &&

                        (parCoordinatesOfArea.getUpperY() > parCoordinatesOfQuadrant.getLowerY()  ||
                        Math.abs(parCoordinatesOfArea.getUpperY() - parCoordinatesOfQuadrant.getLowerY()) < EPSILON) &&

                        (parCoordinatesOfArea.getUpperY() < parCoordinatesOfQuadrant.getUpperY()  ||
                        Math.abs(parCoordinatesOfArea.getUpperY() - parCoordinatesOfQuadrant.getUpperY()) < EPSILON)
                ) ||
                        //4
                        ((parCoordinatesOfArea.getUpperX() > parCoordinatesOfQuadrant.getLowerX()  ||
                        Math.abs(parCoordinatesOfArea.getUpperX() - parCoordinatesOfQuadrant.getLowerX()) < EPSILON) &&

                        (parCoordinatesOfArea.getUpperX() < parCoordinatesOfQuadrant.getUpperX()  ||
                        Math.abs(parCoordinatesOfArea.getUpperX() - parCoordinatesOfQuadrant.getUpperX()) < EPSILON) &&

                        (parCoordinatesOfArea.getUpperY() > parCoordinatesOfQuadrant.getLowerY()  ||
                        Math.abs(parCoordinatesOfArea.getUpperY() - parCoordinatesOfQuadrant.getLowerY()) < EPSILON) &&

                        (parCoordinatesOfArea.getUpperY() < parCoordinatesOfQuadrant.getUpperY()  ||
                        Math.abs(parCoordinatesOfArea.getUpperY() - parCoordinatesOfQuadrant.getUpperY()) < EPSILON)
                ) ||
                        //5
                        ((parCoordinatesOfArea.getLowerY() > parCoordinatesOfQuadrant.getLowerY()  ||
                        Math.abs(parCoordinatesOfArea.getLowerY() - parCoordinatesOfQuadrant.getLowerY()) < EPSILON) &&

                        (parCoordinatesOfArea.getLowerY() < parCoordinatesOfQuadrant.getUpperY() ||
                        Math.abs(parCoordinatesOfArea.getLowerY() - parCoordinatesOfQuadrant.getUpperY()) < EPSILON) &&

                        (parCoordinatesOfArea.getLowerX() < parCoordinatesOfQuadrant.getLowerX() ||
                        Math.abs(parCoordinatesOfArea.getLowerX() - parCoordinatesOfQuadrant.getLowerX()) < EPSILON) &&

                        (parCoordinatesOfArea.getUpperX() > parCoordinatesOfQuadrant.getUpperX() ||
                        Math.abs(parCoordinatesOfArea.getUpperX() - parCoordinatesOfQuadrant.getUpperX()) < EPSILON)
                ) ||
                        //6
                        ((parCoordinatesOfArea.getUpperY() > parCoordinatesOfQuadrant.getLowerY() ||
                        Math.abs(parCoordinatesOfArea.getUpperY() - parCoordinatesOfQuadrant.getLowerY()) < EPSILON) &&

                        (parCoordinatesOfArea.getUpperY() < parCoordinatesOfQuadrant.getUpperY() ||
                        Math.abs(parCoordinatesOfArea.getUpperY() - parCoordinatesOfQuadrant.getUpperY()) < EPSILON) &&

                        (parCoordinatesOfArea.getLowerX() < parCoordinatesOfQuadrant.getLowerX() ||
                        Math.abs(parCoordinatesOfArea.getLowerX() - parCoordinatesOfQuadrant.getLowerX()) < EPSILON) &&

                        (parCoordinatesOfArea.getUpperX() > parCoordinatesOfQuadrant.getUpperX() ||
                        Math.abs(parCoordinatesOfArea.getUpperX() - parCoordinatesOfQuadrant.getUpperX()) < EPSILON)
                ) ||
                        //7
                        ((parCoordinatesOfArea.getLowerX() > parCoordinatesOfQuadrant.getLowerX() ||
                        Math.abs(parCoordinatesOfArea.getLowerX() - parCoordinatesOfQuadrant.getLowerX()) < EPSILON) &&

                        (parCoordinatesOfArea.getLowerX() < parCoordinatesOfQuadrant.getUpperX() ||
                        Math.abs(parCoordinatesOfArea.getLowerX() - parCoordinatesOfQuadrant.getUpperX()) < EPSILON) &&

                        (parCoordinatesOfArea.getLowerY() < parCoordinatesOfQuadrant.getLowerY() ||
                        Math.abs(parCoordinatesOfArea.getLowerY() - parCoordinatesOfQuadrant.getLowerY()) < EPSILON) &&

                        (parCoordinatesOfArea.getUpperY() > parCoordinatesOfQuadrant.getUpperY() ||
                        Math.abs(parCoordinatesOfArea.getUpperY() - parCoordinatesOfQuadrant.getUpperY()) < EPSILON)
                ) ||
                        //8
                        ((parCoordinatesOfArea.getUpperX() > parCoordinatesOfQuadrant.getLowerX()||
                        Math.abs(parCoordinatesOfArea.getUpperX() - parCoordinatesOfQuadrant.getLowerX()) < EPSILON) &&

                        (parCoordinatesOfArea.getUpperX() < parCoordinatesOfQuadrant.getUpperX()||
                        Math.abs(parCoordinatesOfArea.getUpperX() - parCoordinatesOfQuadrant.getUpperX()) < EPSILON) &&

                        (parCoordinatesOfArea.getLowerY() < parCoordinatesOfQuadrant.getLowerY()||
                        Math.abs(parCoordinatesOfArea.getLowerY() - parCoordinatesOfQuadrant.getLowerY()) < EPSILON) &&

                        (parCoordinatesOfArea.getUpperY() > parCoordinatesOfQuadrant.getUpperY() ||
                        Math.abs(parCoordinatesOfArea.getUpperY() - parCoordinatesOfQuadrant.getUpperY()) < EPSILON)
                )
        );
    }

    /**
     * Returns coordinates for new node created in specific node
     */
    public static Coordinates coordinatesOfNewNode(int parQuadrant, Coordinates parCoordinatesOfActual) {

        Coordinates newCoordinates = new Coordinates(0,0,0,0);

        switch (parQuadrant) {
            case 0 -> {
                newCoordinates.setLowerX(parCoordinatesOfActual.getLowerX());
                newCoordinates.setUpperX((parCoordinatesOfActual.getUpperX() + parCoordinatesOfActual.getLowerX()) / 2);
                newCoordinates.setLowerY(parCoordinatesOfActual.getLowerY());
                newCoordinates.setUpperY((parCoordinatesOfActual.getUpperY() + parCoordinatesOfActual.getLowerY()) / 2);
            }
            case 1 -> {
                newCoordinates.setLowerX((parCoordinatesOfActual.getUpperX() + parCoordinatesOfActual.getLowerX()) / 2);
                newCoordinates.setUpperX(parCoordinatesOfActual.getUpperX());
                newCoordinates.setLowerY(parCoordinatesOfActual.getLowerY());
                newCoordinates.setUpperY((parCoordinatesOfActual.getUpperY() + parCoordinatesOfActual.getLowerY()) / 2);
            }
            case 2 -> {
                newCoordinates.setLowerX((parCoordinatesOfActual.getUpperX() + parCoordinatesOfActual.getLowerX()) / 2);
                newCoordinates.setUpperX(parCoordinatesOfActual.getUpperX());
                newCoordinates.setLowerY((parCoordinatesOfActual.getUpperY() + parCoordinatesOfActual.getLowerY()) / 2);
                newCoordinates.setUpperY(parCoordinatesOfActual.getUpperY());
            }
            case 3 -> {
                newCoordinates.setLowerX(parCoordinatesOfActual.getLowerX());
                newCoordinates.setUpperX((parCoordinatesOfActual.getUpperX() + parCoordinatesOfActual.getLowerX()) / 2);
                newCoordinates.setLowerY((parCoordinatesOfActual.getUpperY() + parCoordinatesOfActual.getLowerY()) / 2);
                newCoordinates.setUpperY(parCoordinatesOfActual.getUpperY());
            }
        }
        return newCoordinates;
    }

    /**
     * In case, we need to split current node into new nodes, this method
     * assign to coordinates of some data, in which quadrant of current
     * node it fits. If it isn't fit to any, so it overlap another quadrant
     * or it lies on edge, it will return -1 what means, data should be inserted
     * into current node.
     */
    public static int isFitsToQuadrant(Coordinates parCoordinatesOfActual, Coordinates parCoordinates) {

        if (
                parCoordinatesOfActual.getLowerX() < parCoordinates.getLowerX() &&
                        (parCoordinatesOfActual.getLowerX() + parCoordinatesOfActual.getUpperX()) / 2
                                > parCoordinates.getUpperX() &&
                        parCoordinatesOfActual.getLowerY() < parCoordinates.getLowerY() &&
                        (parCoordinatesOfActual.getLowerY() + parCoordinatesOfActual.getUpperY()) / 2
                                > parCoordinates.getUpperY()
        ) {
            return 0;
        } else if (
                (parCoordinatesOfActual.getLowerX() + parCoordinatesOfActual.getUpperX()) / 2
                        < parCoordinates.getLowerX() &&
                        parCoordinatesOfActual.getUpperX() > parCoordinates.getUpperX() &&
                        parCoordinatesOfActual.getLowerY() < parCoordinates.getLowerY() &&
                        (parCoordinatesOfActual.getLowerY() + parCoordinatesOfActual.getUpperY()) / 2
                                > parCoordinates.getUpperY()
        ) {
            return 1;
        } else if (
                (parCoordinatesOfActual.getLowerX() + parCoordinatesOfActual.getUpperX()) / 2
                        < parCoordinates.getLowerX() &&
                        parCoordinatesOfActual.getUpperX() > parCoordinates.getUpperX() &&
                        (parCoordinatesOfActual.getLowerY() + parCoordinatesOfActual.getUpperY()) / 2
                                < parCoordinates.getLowerY() &&
                        parCoordinatesOfActual.getUpperY() > parCoordinates.getUpperY()
        ) {
            return 2;
        } else if (
                parCoordinatesOfActual.getLowerX() < parCoordinates.getLowerX() &&
                        (parCoordinatesOfActual.getLowerX() + parCoordinatesOfActual.getUpperX()) / 2
                                > parCoordinates.getUpperX() &&
                        (parCoordinatesOfActual.getLowerY() + parCoordinatesOfActual.getUpperY()) / 2
                                < parCoordinates.getLowerY() &&
                        parCoordinatesOfActual.getUpperY() > parCoordinates.getUpperY()
        ) {
            return 3;
        } else if (
                (parCoordinatesOfActual.getLowerX() < parCoordinates.getLowerX() ||
                        Math.abs(parCoordinatesOfActual.getLowerX() - parCoordinates.getLowerX()) < EPSILON) &&

                        (parCoordinatesOfActual.getUpperX() > parCoordinates.getUpperX() ||
                        Math.abs(parCoordinatesOfActual.getUpperX() - parCoordinates.getUpperX()) < EPSILON) &&

                        (parCoordinatesOfActual.getLowerY() < parCoordinates.getLowerY() ||
                        Math.abs(parCoordinatesOfActual.getLowerY() - parCoordinates.getLowerY()) < EPSILON) &&

                        (parCoordinatesOfActual.getUpperY() > parCoordinates.getUpperY() ||
                        Math.abs(parCoordinatesOfActual.getUpperY() - parCoordinates.getUpperY()) < EPSILON)
        ) {
            return -1;
        } else {
            return -10;
        }

    }

}