package Data;

import java.util.ArrayList;
import java.util.Random;

import Data.CadastralObject.TypeOfCadastralObject;
import Data.MapCoordinates;
import Structure.QuadTree.Coordinates;

public class CadastralObjectGenerator {

    private Random random;
    private int realEstateId;
    private int landParcelId;

    public CadastralObjectGenerator() {
        this.random = new Random();
    }

    public ArrayList<CadastralObject> generateObjects(
            TypeOfCadastralObject parType, int parCount, double parSizeOfObject, GPS[] parRangeOfGPS
    ) {
        ArrayList<CadastralObject> listToReturn = new ArrayList<>();
        if (parType.equals(TypeOfCadastralObject.LAND_PARCEL)) {
            for (int i = 0; i < parCount; i++) {
                listToReturn.add(
                        new LandParcel(this.landParcelId, this.returnGPSOfObject(parSizeOfObject,parRangeOfGPS),"Land_Parcel_" + i)
                );
                this.landParcelId++;
            }
        } else {
            for (int i = 0; i < parCount; i++) {
                listToReturn.add(
                        new RealEstate(this.realEstateId, this.returnGPSOfObject(parSizeOfObject, parRangeOfGPS), "Real_Estate_" + i, i)
                );
                this.realEstateId++;
            }
        }
        return listToReturn;
    }

    private GPS[] returnGPSOfObject(double parSizeOfObject, GPS[] parRangeOfGPS) {
        MapCoordinates mp = new MapCoordinates(parRangeOfGPS);
        Coordinates coors = mp.getCoordinatesOfRoot();

        double sizeOfXAxes = coors.getUpperX() - coors.getLowerX();
        double sizeOfYAxes = coors.getUpperY() - coors.getLowerY();

        double x1 = random.nextDouble() * (sizeOfXAxes - parSizeOfObject - 1);
        double x2 = x1 + parSizeOfObject;
        double y1 = random.nextDouble() * (sizeOfYAxes - parSizeOfObject - 1);
        double y2 = y1 + parSizeOfObject;

        Coordinates coor = new Coordinates(x1,x2,y1,y2);

        GPS[] gps = mp.getGPSValue(coor);

        return gps;
    }
}
