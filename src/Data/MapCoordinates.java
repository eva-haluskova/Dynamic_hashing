package Data;

import Structure.QuadTree.Coordinates;

/**
 * Because we need to put data with GPS coordinates into quadtree, while
 * this implementation of tree is made for coordinates, we need to map them.
 * This class ensure correct conversion form GPS coordinates to tree coordinates
 * and vice versa. Instance of this class is created once for tree - it holds
 * coordinates of root node, so it calculate offset for data coordinates.
 */
public class MapCoordinates {

   private GPS[] gpsOfRoot;
   private Coordinates coordinatesOfRoot;

   public MapCoordinates(GPS[] parGpsOfRoot) {
      if (parGpsOfRoot[0].isBiggerThan(parGpsOfRoot[1]) == -1) {
         this.gpsOfRoot = parGpsOfRoot;
      } else {
         GPS one = parGpsOfRoot[1];
         GPS two = parGpsOfRoot[0];
         GPS[] ofRoot = {one, two};
         this.gpsOfRoot = ofRoot;
      }
      this.coordinatesOfRoot = getCoordinatesValueOfRoot(parGpsOfRoot);
   }


   public Coordinates getCoordinatesValue(GPS[] parGPSValues) {
      GPS parGPSOne = parGPSValues[0];
      GPS parGPSTwo = parGPSValues[1];

      double width, height;
      double posX, posY;

      GPS[] parGps;
      if (parGPSOne.isBiggerThan(parGPSTwo) == -1) {
         parGps = new GPS[]{parGPSOne, parGPSTwo};
      } else {
         parGps = new GPS[]{parGPSTwo, parGPSOne};
      }

      double[] hWBetweenPoints = this.getWidthHeightBetweenTwoGPS(parGPSOne, parGPSTwo);
      double[] hWBetweenRootAndPoint;

      hWBetweenRootAndPoint = this.getWidthHeightBetweenTwoGPS(parGps[0], this.gpsOfRoot[0]);

      height = hWBetweenPoints[1];
      width = hWBetweenPoints[0];

      posX = this.coordinatesOfRoot.getLowerX() + hWBetweenRootAndPoint[0];
      posY = this.coordinatesOfRoot.getLowerY() + hWBetweenRootAndPoint[1];

      return new Coordinates(posX,posX + width,posY,posY + height);
   }

   public Coordinates getCoordinatesValue(GPS parGPSValues) {

      double posX, posY;

      double[] hWBetweenRootAndPoint;

      hWBetweenRootAndPoint = this.getWidthHeightBetweenTwoGPS(parGPSValues, this.gpsOfRoot[0]);

      posX = this.coordinatesOfRoot.getLowerX() + hWBetweenRootAndPoint[0];
      posY = this.coordinatesOfRoot.getLowerY() + hWBetweenRootAndPoint[1];

      return new Coordinates(posX,posX,posY,posY);
   }


   public Coordinates getCoordinatesValueOfRoot(GPS[] parGPSValues) {
      GPS parGPSOne = parGPSValues[0];
      GPS parGPSTwo = parGPSValues[1];

      double width, height;
      double posX, posY;

      GPS[] parGps;
      if (parGPSOne.isBiggerThan(parGPSTwo) == -1) {
         parGps = new GPS[]{parGPSOne, parGPSTwo};
      } else {
         parGps = new GPS[]{parGPSTwo, parGPSOne};
      }
      double[] hWBetweenPoints = this.getWidthHeightBetweenTwoGPS(parGPSOne, parGPSTwo);

      height = hWBetweenPoints[1];
      width = hWBetweenPoints[0];
      posX = 0;
      posY = 0;
      return new Coordinates(posX,posX + width,posY,posY + height);
   }

   public GPS[] getGPSValue(Coordinates parCoordinates) {
      GPS.Latitude firstLatitude;
      GPS.Latitude secondLatitude;

      GPS.Longitude firstLongitude;
      GPS.Longitude secondLongitude;

      double firstLatitudePosition;
      double secondLatitudePosition;

      double firstLongitudePosition;
      double secondLongitudePosition;

      // first GPS
      double distanceBetweenRootLowerXAndParLowerX = parCoordinates.getLowerX() - this.coordinatesOfRoot.getLowerX();
      double distanceBetweenRootLowerYAndParLowerY = parCoordinates.getLowerY() - this.coordinatesOfRoot.getLowerY();
      // longitude
      if (this.gpsOfRoot[0].getLongitude().equals(GPS.Longitude.WEST) &&
         this.gpsOfRoot[0].getLongitudePosition() > distanceBetweenRootLowerXAndParLowerX) {
         firstLongitude = GPS.Longitude.WEST;
         firstLongitudePosition = Math.abs(this.gpsOfRoot[0].getLongitudePosition() - distanceBetweenRootLowerXAndParLowerX);
      } else if (this.gpsOfRoot[0].getLongitude().equals(GPS.Longitude.WEST) &&
         this.gpsOfRoot[0].getLongitudePosition() < distanceBetweenRootLowerXAndParLowerX) {
         firstLongitude = GPS.Longitude.EAST;
         firstLongitudePosition = Math.abs(this.gpsOfRoot[0].getLongitudePosition() - distanceBetweenRootLowerXAndParLowerX);
      } else {
         firstLongitude = GPS.Longitude.EAST;
         firstLongitudePosition = Math.abs(this.gpsOfRoot[0].getLongitudePosition() + distanceBetweenRootLowerXAndParLowerX);
      }
      // latidue
      if (this.gpsOfRoot[0].getLatitude().equals(GPS.Latitude.NORTH) &&
      this.gpsOfRoot[0].getLatitudePosition() > distanceBetweenRootLowerYAndParLowerY) {
            firstLatitude = GPS.Latitude.NORTH;
            firstLatitudePosition = Math.abs(this.gpsOfRoot[0].getLatitudePosition() - distanceBetweenRootLowerYAndParLowerY);
      } else if (this.gpsOfRoot[0].getLatitude().equals(GPS.Latitude.NORTH) &&
              this.gpsOfRoot[0].getLatitudePosition() < distanceBetweenRootLowerYAndParLowerY) {
         firstLatitude = GPS.Latitude.SOUTH;
         firstLatitudePosition = Math.abs(this.gpsOfRoot[0].getLatitudePosition() - distanceBetweenRootLowerYAndParLowerY);
      } else {
         firstLatitude = GPS.Latitude.SOUTH;
         firstLatitudePosition = Math.abs(this.gpsOfRoot[0].getLatitudePosition() + distanceBetweenRootLowerYAndParLowerY);
      }

      // osetrenie druhej suradnice

      double distanceBetweenRootUpperXAndParUpperX = parCoordinates.getUpperX() - this.coordinatesOfRoot.getLowerX();
      double distanceBetweenRootUpperYAndParUpperY = parCoordinates.getUpperY() - this.coordinatesOfRoot.getLowerY();

      if (this.gpsOfRoot[0].getLongitude().equals(GPS.Longitude.WEST) &&
              this.gpsOfRoot[0].getLongitudePosition() > distanceBetweenRootUpperXAndParUpperX) {
         secondLongitude = GPS.Longitude.WEST;
         secondLongitudePosition = Math.abs(this.gpsOfRoot[0].getLongitudePosition() - distanceBetweenRootUpperXAndParUpperX);
      } else if (this.gpsOfRoot[0].getLongitude().equals(GPS.Longitude.WEST) &&
              this.gpsOfRoot[0].getLongitudePosition() < distanceBetweenRootUpperXAndParUpperX) {
         secondLongitude = GPS.Longitude.EAST;
         secondLongitudePosition = Math.abs(this.gpsOfRoot[0].getLongitudePosition() - distanceBetweenRootUpperXAndParUpperX);
      } else {
         secondLongitude = GPS.Longitude.EAST;
         secondLongitudePosition = Math.abs(this.gpsOfRoot[0].getLongitudePosition() + distanceBetweenRootUpperXAndParUpperX);
      }
      // latidue
      if (this.gpsOfRoot[0].getLatitude().equals(GPS.Latitude.NORTH) &&
              this.gpsOfRoot[0].getLatitudePosition() > distanceBetweenRootUpperYAndParUpperY) {
         secondLatitude = GPS.Latitude.NORTH;
         secondLatitudePosition = Math.abs(this.gpsOfRoot[0].getLatitudePosition() - distanceBetweenRootUpperYAndParUpperY);
      } else if (this.gpsOfRoot[0].getLatitude().equals(GPS.Latitude.NORTH) &&
              this.gpsOfRoot[0].getLatitudePosition() < distanceBetweenRootUpperYAndParUpperY) {
         secondLatitude = GPS.Latitude.SOUTH;
         secondLatitudePosition = Math.abs(this.gpsOfRoot[0].getLatitudePosition() - distanceBetweenRootUpperYAndParUpperY);
      } else {
         secondLatitude = GPS.Latitude.SOUTH;
         secondLatitudePosition = Math.abs(this.gpsOfRoot[0].getLatitudePosition() + distanceBetweenRootUpperYAndParUpperY);
      }

      GPS one = new GPS(firstLatitude,firstLatitudePosition,firstLongitude,firstLongitudePosition);
      GPS two = new GPS(secondLatitude,secondLatitudePosition,secondLongitude,secondLongitudePosition);
      GPS[] gps = {one,two};
      return gps;
   }


   /**
    * return width and height of difference between two GPS coordinates
    * @return int[0] - width [x]
    *         int[1] - height [y]
    *
    */
   public double[] getWidthHeightBetweenTwoGPS(GPS parGPSOne, GPS parGPSTwo) {
      double[] dimension = new double[2];

      if (parGPSOne.getLatitude().equals(parGPSTwo.getLatitude())) {
         dimension[1] = Math.abs(parGPSOne.getLatitudePosition() - parGPSTwo.getLatitudePosition());

      } else if (!parGPSOne.getLatitude().equals(parGPSTwo.getLatitude())) {
         dimension[1] = Math.abs(parGPSOne.getLatitudePosition() + parGPSTwo.getLatitudePosition());
      }

      if (parGPSOne.getLongitude().equals(parGPSTwo.getLongitude())) {
         dimension[0] = Math.abs(parGPSOne.getLongitudePosition() - parGPSTwo.getLongitudePosition());
      } else if (!parGPSOne.getLongitude().equals(parGPSTwo.getLongitude())) {
        dimension[0] = Math.abs(parGPSOne.getLongitudePosition() + parGPSTwo.getLongitudePosition());
      }

      return dimension;
   }

   public Coordinates getCoordinatesOfRoot() {
      return this.coordinatesOfRoot;
   }

   public GPS[] getGpsOfRoot() {
      return this.gpsOfRoot;
   }
}
