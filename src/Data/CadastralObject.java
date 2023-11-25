package Data;

public abstract class CadastralObject {

    private static final int COORDINATES_COUNT = 2;
    public enum TypeOfCadastralObject {
        LAND_PARCEL,
        REAL_ESTATE
    }
    protected int identityNumber;
    protected String description;
    protected GPS[] gpsCoordinates;

    public CadastralObject(
            int parIdentityNumber,
            GPS[] parGpsCoordinates,
            String parDescription
    ) {
        this.identityNumber = parIdentityNumber;
        this.gpsCoordinates = parGpsCoordinates;
        this.setDescription(parDescription);
    }
    public abstract TypeOfCadastralObject isInstanceOf();

    public abstract void setDescription(String description);

    //public abstract byte[] serialize();

    //public getHash()

    public static boolean areEqual(CadastralObject firstCadastralObject, CadastralObject secondCadastralObject) {
        if (
            (firstCadastralObject.isInstanceOf().equals(TypeOfCadastralObject.LAND_PARCEL) &&
            secondCadastralObject.isInstanceOf().equals(TypeOfCadastralObject.LAND_PARCEL)) &&
            firstCadastralObject.getIdentityNumber() == secondCadastralObject.getIdentityNumber()
        ){
            return true;
        } else if ((firstCadastralObject.isInstanceOf().equals(TypeOfCadastralObject.REAL_ESTATE) &&
                secondCadastralObject.isInstanceOf().equals(TypeOfCadastralObject.REAL_ESTATE)) &&
                firstCadastralObject.getIdentityNumber() == secondCadastralObject.getIdentityNumber()
        ) {
            return true;
        }
        return false;
    }

    public int getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(int parIdentityNumber) {
        this.identityNumber = parIdentityNumber;
    }

    public String getDescription() {
        return description;
    }

    public GPS[] getGpsCoordinates() {
        return gpsCoordinates;
    }

    public void setGpsCoordinates(GPS[] parGpsCoordinates) {
        this.gpsCoordinates = parGpsCoordinates;
    }
}
