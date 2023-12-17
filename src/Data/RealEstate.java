package Data;

import Structure.DynamicHashing.IRecord;

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
        this.inicializeLanParcels();
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
        this.serialNumber = -1;
    }

    public RealEstate(int parIdentityNumber) {
        super();
        this.belongingLandParcels = new int[MAX_COUNT_OF_PARCELS];
        this.serialNumber = -1;
        this.identityNumber = parIdentityNumber;
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
                super.toString() + ", " + idesOfRealEstates;
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
        int hashCode = Objects.hash(this.identityNumber);
        //return BitSet.valueOf(new long[] {hashCode});
        BitSet bitSet = new BitSet(32); // Change the number to fit your required length

        // Set bits in the BitSet based on the hash code
        for (int i = 0; i < 32; i++) { // Assuming integer hash code is 32 bits
            boolean isBitSet = ((hashCode >> i) & 1) == 1;
            bitSet.set(i, isBitSet);
        }

        if (bitSet.length() < 32) {
            bitSet.set(bitSet.length(),32,true);
        }
        BitSet n = getSubBitSet(bitSet,0,2);

        if (n.length() < 2) {
            n.set(n.length(),2,true);
        }

        return n;

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
        int index = -1;
        for (int i = 0; i < MAX_COUNT_OF_PARCELS; i++) {
            if (this.belongingLandParcels[i] == -1) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return this.belongingLandParcels;
        } else {
            int[] ret = new int[index];
            for (int i = 0; i < index; i++) {
                ret[i] = this.belongingLandParcels[i];
            }
            return ret;
        }
    }
    public void addBelongingLandParcel(int parIdentityNumber) {
        for (int i = 0; i < MAX_COUNT_OF_PARCELS; i++) {
            if (this.belongingLandParcels[i] == -1) {
                this.belongingLandParcels[i] = parIdentityNumber;
                break;
            }
        }
    }

    public void deleteBelongingLandParcel(int parIdentityNumber) {
        int indexOfDelete = 0;
        for (int i = 0; i < MAX_COUNT_OF_PARCELS; i++) {
            if (this.belongingLandParcels[i] == parIdentityNumber) {
                indexOfDelete = i;
            }
        }
        for (int i = indexOfDelete; i < MAX_COUNT_OF_PARCELS-1; i++) {
            this.belongingLandParcels[i] = this.belongingLandParcels[i+1];
        }
    }

    public void resetBelongingLandParcels() {
        this.inicializeLanParcels();
    }

    private void inicializeLanParcels() {
        for (int i = 0; i < MAX_COUNT_OF_PARCELS; i++) {
            this.belongingLandParcels[i] = -1;
        }
    }
}
