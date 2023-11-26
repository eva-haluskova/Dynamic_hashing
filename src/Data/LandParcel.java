package Data;

import java.io.*;

/**
 * This class extends cadastral object abstract class. Contains added data specific for this class.
 */
public class LandParcel extends CadastralObject {

    private final int MAX_COUNT_OF_ESTATES = 5;
    private final int MAX_LENGTH_OF_PARCEL_DESCRIPTION = 11;
    private int[] belongingRealEstates;

    public LandParcel(
            int parIdentityNumber,
            GPS[] parGpsCoordinates,
            String parDescription
    ) {
        super(parIdentityNumber, parGpsCoordinates, parDescription);

        this.belongingRealEstates = new int[MAX_COUNT_OF_ESTATES];
        for (int i = 0; i < MAX_COUNT_OF_ESTATES; i++) {
            this.belongingRealEstates[i] = -1;
        }
    }

    @Override
    public TypeOfCadastralObject isInstanceOf() {
        return TypeOfCadastralObject.LAND_PARCEL;
    }

    @Override
    public void setDescription(String parDescription) {
        if (parDescription != null && parDescription.length() > MAX_LENGTH_OF_PARCEL_DESCRIPTION) {
            this.description = parDescription.substring(0,MAX_LENGTH_OF_PARCEL_DESCRIPTION);
        } else {
            this.description = parDescription;
        }
    }

    @Override
    public void serializeDetails(DataOutputStream parOutputStream) throws IOException {
        for (int act: this.belongingRealEstates) {
            parOutputStream.writeInt(act);
        }
    }

    @Override
    public void deserializeDetails(DataInputStream parInputStream) throws IOException {
        for (int i = 0; i < MAX_COUNT_OF_ESTATES; i++) {
            this.belongingRealEstates[i] = parInputStream.readInt();
        }
    }

    @Override
    public String toString() {
        StringBuilder idesOfRealEstates = new StringBuilder("");
        for (int akt: this.belongingRealEstates) {
            idesOfRealEstates.append(akt);
            idesOfRealEstates.append(" ");
        }
        return "Land Parcel: " + super.toString() + " " + idesOfRealEstates;
    }

    public int[] getBelongingRealEstates() {
        return this.belongingRealEstates;
    }

    public void addBelongingRealEstate(int parIdentityNumber) {
        for (int i = 0; i < MAX_COUNT_OF_ESTATES; i++) {
            if (this.belongingRealEstates[i] == -1) {
                this.belongingRealEstates[i] = parIdentityNumber;
                break;
            }
        }
    }

    public void makeEverythingNull() {
        super.makeEverythingNull();
        for (int i = 0; i < MAX_COUNT_OF_ESTATES; i++) {
            this.belongingRealEstates[i] = -1;
        }
    }

//    Na pracu so suborom sa pouzije RandomAccessFile a na jeho zmensenie
//RandomAccessFile.setLength(long newLength)

}
