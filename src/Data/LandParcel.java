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
        for (int i = 0; i < MAX_COUNT_OF_ESTATES; i++) {
            this.belongingRealEstates[i] = -1;
        }
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
        return "Land Parcel: " + super.toString() + " " + idesOfRealEstates;
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

    @Override
    public BitSet getHash() {
        //int hashCode = Objects.hash(this.identityNumber);
        //return BitSet.valueOf(new long[] {hashCode});
        int hashCode = Objects.hash(this.identityNumber);

        // Create a BitSet with a fixed length (e.g., 64 bits in this example)
        BitSet bitSet = new BitSet(32); // Change the number to fit your required length

        // Set bits in the BitSet based on the hash code
        for (int i = 0; i < 32; i++) { // Assuming integer hash code is 32 bits
            boolean isBitSet = ((hashCode >> i) & 1) == 1;
            bitSet.set(i, isBitSet);
        }


        if (bitSet.length() < 32) {
            bitSet.set(bitSet.length(),32,true);
        }
      //  System.out.println(bitSet);
       // System.out.println(bitSet.length());
       // System.out.println("----------");
        BitSet n = getSubBitSet(bitSet,0,8);

        if (n.length() < 8) {
            n.set(n.length(),8,true);
        }

        //System.out.println(n);
        //System.out.println(n.length());
        return n;
    }


    public static BitSet getSubBitSet(BitSet originalBitSet, int fromIndex, int toIndex) {
        BitSet subBitSet = new BitSet(toIndex - fromIndex);

        for (int i = fromIndex; i < toIndex; i++) {
            if (originalBitSet.get(i)) {
                subBitSet.set(i - fromIndex);
            }
        }

        return subBitSet;
    }

    /**
     * Getters and setters of attributes
     */
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

}
