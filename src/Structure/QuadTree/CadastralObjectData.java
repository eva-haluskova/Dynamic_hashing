package Structure.QuadTree;

import Data.GPS;
public class CadastralObjectData {

    private int identityNumber;
    GPS[] gpsCoordinates;

    // todo zabezpecit aby sa mi do kvadstormu nevlozili dve datara rovnke id? jup. To budes mat ako to dato uniqe id....vis
    public CadastralObjectData(int parIdentityNumber, GPS[] parGpsCoordinates) {
        this.identityNumber = parIdentityNumber;
        this.gpsCoordinates = parGpsCoordinates;
    }

    public int getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(int identityNumber) {
        this.identityNumber = identityNumber;
    }

    public GPS[] getGpsCoordinates() {
        return gpsCoordinates;
    }

    public void setGpsCoordinates(GPS[] gpsCoordinates) {
        this.gpsCoordinates = gpsCoordinates;
    }

    public void setGpsCoordinates(GPS gpsCoordinates, int index) {
        if (index == 0) {
            this.gpsCoordinates[0] = gpsCoordinates;
        } else if (index == 1) {
            this.gpsCoordinates[1] = gpsCoordinates;
        }
    }
}
