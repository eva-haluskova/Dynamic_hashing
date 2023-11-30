package Data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.Objects;

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

    /**
     * We can also create land parcel from byte array
     */
    public RealEstate(
            byte[] parSerializedObject
    ) {
        super(parSerializedObject);
        this.belongingLandParcels = new int[MAX_COUNT_OF_PARCELS];
        this.fromByteArray(parSerializedObject);
    }

    public RealEstate() {
        super();
        this.belongingLandParcels = new int[MAX_COUNT_OF_PARCELS];
    }

    /**
     * Overrode method from parent abstract class
     */
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

        int lengthOfDesc = this.description.length();
        parOutputStream.writeUTF(this.description);
        for (int i = 0; i < MAX_LENGTH_OF_ESTATE_DESCRIPTION - lengthOfDesc; i++) {
            parOutputStream.writeByte(0);
        }

        parOutputStream.writeInt(this.serialNumber);

        for (int act: this.belongingLandParcels) {
            parOutputStream.writeInt(act);
        }
    }

    @Override
    public void deserializeDetails(DataInputStream parInputStream) throws IOException {

        this.description = parInputStream.readUTF();
        int zeros = MAX_LENGTH_OF_ESTATE_DESCRIPTION - this.description.length();
        for (int i = 0; i < zeros; i++) {
            parInputStream.readByte();
        }

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

    @Override
    public void makeEverythingNull() {
        super.makeEverythingNull();
        this.serialNumber = 0;
        for (int i = 0; i < MAX_COUNT_OF_PARCELS; i++) {
            this.belongingLandParcels[i] = -1;
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

        RealEstate other = (RealEstate) parOtherObject;

        return this.getIdentityNumber() == other.getIdentityNumber();
    }

    @Override
    public int getSize() {
        int size = super.getSize();
        size += MAX_LENGTH_OF_ESTATE_DESCRIPTION * Byte.BYTES + Byte.BYTES * 2; // desc + dva bajty za writeUtf
        size += MAX_COUNT_OF_PARCELS * Integer.BYTES; // belonging parcels
        size +=  Integer.BYTES; // for serial number
        return size;
    }

    @Override
    public void createObjectFromBytes(byte[] parData) {
        this.fromByteArray(parData);
    }

    @Override
    public BitSet getHash() {
        int hashCode = Objects.hash(this.identityNumber,this.description, this.serialNumber);
        return BitSet.valueOf(new long[] {hashCode});
    }

    /**
     * Getters and setters of attributes
     */
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

}
