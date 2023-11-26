package Data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class extends cadastral object abstract class. Contains added data specific for this class.
 */
public class RealEstate extends CadastralObject {

    private final int MAX_COUNT_OF_PARCELS = 6;
    private final int MAX_LENGTH_OF_ESTATE_DESCRIPTION = 15;
    private int serialNumber;
    private int[] belongingLandParcels;
    public RealEstate(
            int parIdentityNumber,
            GPS[] parGpsCoordinates,
            String parDescription,
            int parSerialNumber
    ) {
        super(parIdentityNumber, parGpsCoordinates, parDescription);

        this.serialNumber = parSerialNumber;

        this.belongingLandParcels = new int[MAX_COUNT_OF_PARCELS];
        for (int i = 0; i < MAX_COUNT_OF_PARCELS; i++) {
            this.belongingLandParcels[i] = -1;
        }
    }

    @Override
    public TypeOfCadastralObject isInstanceOf() {
        return TypeOfCadastralObject.REAL_ESTATE;
    }

    @Override
    public void setDescription(String parDescription) {
        if (parDescription != null && parDescription.length() > MAX_LENGTH_OF_ESTATE_DESCRIPTION) {
            this.description = parDescription.substring(0,MAX_LENGTH_OF_ESTATE_DESCRIPTION);
        } else {
            this.description = parDescription;
        }
    }

    @Override
    public void serializeDetails(DataOutputStream parOutputStream) throws IOException {
        parOutputStream.writeInt(this.serialNumber);

        for (int act: this.belongingLandParcels) {
            parOutputStream.writeInt(act);
        }
    }

    @Override
    public void deserializeDetails(DataInputStream parInputStream) throws IOException {
        this.serialNumber = parInputStream.readInt();

        for (int i = 0; i < MAX_COUNT_OF_PARCELS; i++) {
            this.belongingLandParcels[i] = parInputStream.readInt();
        }
    }

    @Override
    public String toString() {
        StringBuilder idesOfRealEstates = new StringBuilder("");
        for (int akt: this.belongingLandParcels) {
            idesOfRealEstates.append(akt);
            idesOfRealEstates.append(" ");
        }
        return "Real Estate: " +
                "serial numb: " + this.serialNumber + " " +
                super.toString() + " " + idesOfRealEstates;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int parSerialNumber) {
        this.serialNumber = parSerialNumber;
    }

    public int[] getBelongingLandParcels() {
        return this.belongingLandParcels;
    }

    public void addBelongingLandParcel(int parIdentityNumber) {
        for (int i = 0; i < MAX_COUNT_OF_PARCELS; i++) {
            if (this.belongingLandParcels[i] == -1) {
                this.belongingLandParcels[i] = parIdentityNumber;
                break;
            }
        }
    }

    public void makeEverythingNull() {
        super.makeEverythingNull();
        this.serialNumber = 0;
        for (int i = 0; i < MAX_COUNT_OF_PARCELS; i++) {
            this.belongingLandParcels[i] = -1;
        }
    }
}
