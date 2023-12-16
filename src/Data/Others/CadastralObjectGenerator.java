package Data.Others;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import Data.*;
import Data.CadastralObject.TypeOfCadastralObject;
import Structure.QuadTree.Coordinates;

public class CadastralObjectGenerator {

    private Random random;
    private int realEstateId;
    private int landParcelId;

    public CadastralObjectGenerator() {
        this.random = new Random();
        this.realEstateId = 0;
        this.landParcelId = 0;
        this.random.setSeed(1);
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

    public LandParcel generateLandParcel(
            double parSizeOfObject, GPS[] parRangeOfGPS
    ) {
        LandParcel landParcel = new LandParcel(this.getNextLandParcelId(), this.returnGPSOfObject(parSizeOfObject,parRangeOfGPS),"LP_" + this.landParcelId);
        return landParcel;
    }

    public RealEstate generateRealEstate(
            double parSizeOfObject, GPS[] parRangeOfGPS
    ) {
        RealEstate realEstate = new RealEstate(this.getNextRealEstateId(), this.returnGPSOfObject(parSizeOfObject,parRangeOfGPS),"RL_" + this.landParcelId, this.random.nextInt());
        return realEstate;
    }

    public int generateInt() {
        int numb = Math.abs(random.nextInt());
        if (numb > 10000) {
            numb = numb/10000;
        }
        return numb;
    }


    public GPS[] returnGPSOfObject(double parSizeOfObject, GPS[] parRangeOfGPS) {
        MapCoordinates mp = new MapCoordinates(parRangeOfGPS);
        Coordinates coors = mp.getCoordinatesOfRoot();

        double sizeOfXAxes = coors.getUpperX() - coors.getLowerX();
        double sizeOfYAxes = coors.getUpperY() - coors.getLowerY();

        double x1 = random.nextDouble() * (sizeOfXAxes - parSizeOfObject -1);
        double x2 = x1 + parSizeOfObject;
        double y1 = random.nextDouble() * (sizeOfYAxes - parSizeOfObject - 1);
        double y2 = y1 + parSizeOfObject;

        x1 = Math.round(x1 * 100.0) / 100.0;
        x2 = Math.round(x2 * 100.0) / 100.0;
        y1 = Math.round(y1 * 100.0) / 100.0;
        y2 = Math.round(y2 * 100.0) / 100.0;
//        x1 = Math.round(x1);
//        x2 = Math.round(x2);
//        y1 = Math.round(y1);
//        y2 = Math.round(y2);

        Coordinates coor = new Coordinates(x1,x2,y1,y2);

        GPS[] gps = mp.getGPSValue(coor);

        return gps;
    }

    public int getNextLandParcelId() {
        int ret = this.landParcelId;
        this.landParcelId++;
        return ret;
    }

    public int getNextRealEstateId() {
        int ret = this.realEstateId;
        this.realEstateId++;
        return ret;
    }

    public int getRealEstateId() {
        return this.realEstateId;
    }

    public int getLandParcelId() {
        return this.landParcelId;
    }

    public void returnBackNextLandParcelId() {
        this.landParcelId--;
    }

    public void returnBackNextRealEstateId() {
        this.realEstateId--;
    }
}
