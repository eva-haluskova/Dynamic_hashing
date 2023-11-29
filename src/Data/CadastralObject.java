package Data;

import Structure.DynamicHashing.IRecord;

import java.io.*;
import java.text.DecimalFormat;
import java.util.BitSet;

/**
 * Represent some cadastral object - in this case it could be Land Parcel or Real Estate.
 * This abstract class contains common data for all inherit classes, possible methods and abstract methods
 * which is needed to override in child. Class also implements IRecord interface because we treat this
 * classes like records into dynamic hashing and for this purpose we need this classes implement special methods.
 */
public abstract class CadastralObject implements IRecord {

    protected final int COORDINATES_COUNT = 2;
    public enum TypeOfCadastralObject {
        LAND_PARCEL,
        REAL_ESTATE
    }
    protected int identityNumber;
    protected GPS[] gpsCoordinates;
    protected String description;

    public CadastralObject(
            int parIdentityNumber,
            GPS[] parGpsCoordinates,
            String parDescription
    ) {
        this.identityNumber = parIdentityNumber;
        this.gpsCoordinates = parGpsCoordinates;
        this.setDescription(parDescription);
    }

    /**
     * We can also create cadastral object from byte array
     */
    public CadastralObject(
            byte[] parSerializedObject
    ) {
        GPS[] gps = {new GPS(),new GPS()};
        this.gpsCoordinates = gps;
    }

    /**
     * Abstract methods for overriding
     */
    public abstract TypeOfCadastralObject isInstanceOf();

    public abstract void setDescription(String description);

    public abstract void serializeDetails(DataOutputStream outputStream) throws IOException;

    public abstract void deserializeDetails(DataInputStream inputStream) throws IOException;

    /**
     * Getters and setters of attributes
     */
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

    /**
     * Methods overrode from interface
     */
    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);

        try {
            hlpOutStream.writeInt(this.identityNumber);
            hlpOutStream.writeUTF(this.description);

            for (int i = 0; i < COORDINATES_COUNT; i++) {
                hlpOutStream.writeUTF(this.gpsCoordinates[i].stringMapLongitudeName());
                hlpOutStream.writeDouble(this.gpsCoordinates[i].getLongitudePosition());
                hlpOutStream.writeUTF(this.gpsCoordinates[i].stringMapLatitudeName());
                hlpOutStream.writeDouble(this.gpsCoordinates[i].getLatitudePosition());
            }

            this.serializeDetails(hlpOutStream);

            return hlpByteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion to byte array.");
        }
    }

    @Override
    public void fromByteArray(byte[] paArray) {

        // just for make sure it has changes values
        this.makeEverythingNull();
        if (this.description != null) {
            System.out.println(this);
        }

        ByteArrayInputStream hlpByteArrayInputStream = new ByteArrayInputStream(paArray);
        DataInputStream hlpInStream = new DataInputStream(hlpByteArrayInputStream);

        try {

            this.identityNumber = hlpInStream.readInt();
            this.description = hlpInStream.readUTF();

            for (int i = 0; i < COORDINATES_COUNT; i++) {
                this.gpsCoordinates[i].setLongitude(hlpInStream.readUTF());
                this.gpsCoordinates[i].setLongitudePosition(hlpInStream.readDouble());
                this.gpsCoordinates[i].setLatitude(hlpInStream.readUTF());
                this.gpsCoordinates[i].setLatitudePosition(hlpInStream.readDouble());
            }

            this.deserializeDetails(hlpInStream);

            System.out.println(this);

        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array.");
        }
    }

    @Override
    public int getSize() {
        int size = Integer.BYTES; // identityNumber
        size += COORDINATES_COUNT * GPS.getSize(); // gps coors
        size += description.length() * Character.BYTES; // desc

        return size;
    }

    /**
     * Methods which are also override into inherit classes
     */
    public String toString() {

        DecimalFormat df = new DecimalFormat("#.###");
        return  "id: " + this.identityNumber +
                ", desc: " + this.description +
                ", 1. coors: lat: " + this.gpsCoordinates[0].stringMapLatitudeName() +
                ", pos: " + df.format(this.gpsCoordinates[0].getLatitudePosition()) +
                ", long: " + this.gpsCoordinates[0].stringMapLongitudeName() +
                ", pos: " + df.format(this.gpsCoordinates[0].getLongitudePosition()) +
                ", 2. coors: lat: " + this.gpsCoordinates[1].stringMapLatitudeName() +
                ", pos: " + df.format(this.gpsCoordinates[1].getLatitudePosition()) +
                ", long: " + this.gpsCoordinates[1].stringMapLongitudeName() +
                ", pos: " + df.format(this.gpsCoordinates[1].getLongitudePosition());
    }

    /**
     * method just for control if method FromByteArray is correct. Could be also used for
     * make every attribute of class null, or zero...
     */
    protected void makeEverythingNull() {
        this.identityNumber = 0;
        this.description = "";
        for (int i = 0; i < COORDINATES_COUNT; i++) {
            this.gpsCoordinates[i].setLongitude("");
            this.gpsCoordinates[i].setLongitudePosition(0);
            this.gpsCoordinates[i].setLatitude("");
            this.gpsCoordinates[i].setLatitudePosition(0);
        }
    }

    /**
     * Return String representation of getHash method. Returns
     * string of 0 and 1 for the purpose of processing it into trie.
     */
    public String returnBitSetString() {
        BitSet hash = this.getHash();
        StringBuilder sb = new StringBuilder(hash.length());

        for (int i = 0; i < hash.length(); i++) {
            sb.append(hash.get(i) ? '1' : '0');
        }
        return sb.toString();
    }
}
