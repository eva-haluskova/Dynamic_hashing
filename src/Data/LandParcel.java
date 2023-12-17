package Data;

import Structure.DynamicHashing.IRecord;

import java.io.*;
import java.util.BitSet;
import java.util.Objects;

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
        this.inicializeBelongingRealEstate();
    }

    /**
     * We can also create land parcel from byte array
     */
    public LandParcel(
            byte[] parSerializedObject
    ) {
        super(parSerializedObject);
        this.belongingRealEstates = new int[MAX_COUNT_OF_ESTATES];
        this.fromByteArray(parSerializedObject);
    }

    public LandParcel() {
        super();
        this.belongingRealEstates = new int[MAX_COUNT_OF_ESTATES];
    }

    public LandParcel(int parIdentityNumber) {
        super();
        this.identityNumber = parIdentityNumber;
        this.belongingRealEstates = new int[MAX_COUNT_OF_ESTATES];
    }

    /**
     * Overrode method from parent abstract class
     */
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

        int lengthOfDesc = this.description.length();
        parOutputStream.writeUTF(this.description);
        for (int i = 0; i < MAX_LENGTH_OF_PARCEL_DESCRIPTION - lengthOfDesc; i++) {
            parOutputStream.writeByte(0);
        }

        for (int act: this.belongingRealEstates) {
            parOutputStream.writeInt(act);
        }
    }

    @Override
    public void deserializeDetails(DataInputStream parInputStream) throws IOException {

        this.description = parInputStream.readUTF();
        int zeros = MAX_LENGTH_OF_PARCEL_DESCRIPTION - this.description.length();
        for (int i = 0; i < zeros; i++) {
            parInputStream.readByte();
        }

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
        return "Land Parcel: " + super.toString() + ", " + idesOfRealEstates;
    }

    @Override
    public void makeEverythingNull() {
        super.makeEverythingNull();
        for (int i = 0; i < MAX_COUNT_OF_ESTATES; i++) {
            this.belongingRealEstates[i] = -1;
        }
    }

    /**
     * Overrode methods from implemented interface
     */
    @Override
    public boolean equals(Object parOtherObject) {
        if (parOtherObject == null || getClass() != parOtherObject.getClass() ) {
            return false;
        }

        LandParcel other = (LandParcel) parOtherObject;

        return this.getIdentityNumber() == other.getIdentityNumber();
    }

    @Override
    public int getSize() {
        int size = super.getSize();
        size += MAX_LENGTH_OF_PARCEL_DESCRIPTION * Byte.BYTES + Byte.BYTES * 2; // desc
        size += MAX_COUNT_OF_ESTATES * Integer.BYTES; // array of belonging estates
        return size;
    }

    @Override
    public void createObjectFromBytes(byte[] parData) {
        this.fromByteArray(parData);
    }

    /**
     * Getters and setters of attributes
     */
    public int[] getBelongingRealEstates() {
        int index = -1;
        for (int i = 0; i < MAX_COUNT_OF_ESTATES; i++) {
            if (this.belongingRealEstates[i] == -1) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return this.belongingRealEstates;
        } else {
            int[] ret = new int[index];
            for (int i = 0; i < index; i++) {
                ret[i] = this.belongingRealEstates[i];
            }
            return ret;
        }
    }

    public void addBelongingRealEstate(int parIdentityNumber) {
        for (int i = 0; i < MAX_COUNT_OF_ESTATES; i++) {
            if (this.belongingRealEstates[i] == -1) {
                this.belongingRealEstates[i] = parIdentityNumber;
                break;
            }
        }
    }

    public void deleteBelongingRealEstate(int parIdentityNumber) {
        int indexOfDelete = 0;
        for (int i = 0; i < MAX_COUNT_OF_ESTATES; i++) {
            if (this.belongingRealEstates[i] == parIdentityNumber) {
                indexOfDelete = i;
            }
        }
        for (int i = indexOfDelete; i < MAX_COUNT_OF_ESTATES-1; i++) {
            this.belongingRealEstates[i] = this.belongingRealEstates[i+1];
        }
    }

    public void resetBelongingRealEstate() {
        this.inicializeBelongingRealEstate();
    }

    private void inicializeBelongingRealEstate() {
        for (int i = 0; i < MAX_COUNT_OF_ESTATES; i++) {
            this.belongingRealEstates[i] = -1;
        }
    }

}
