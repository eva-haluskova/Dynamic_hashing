package Data;

import java.io.*;
import java.text.DecimalFormat;

/**
 * Represent some cadastral object - in this case it could be Land Parcel or Real Estate.
 * This abstract class contains common data for all inherit classes, possible methods and abstract methods
 * which is needed to override in child.
 */
public abstract class CadastralObject {

    protected final int COORDINATES_COUNT = 2;
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

    public CadastralObject(
            byte[] parSerializedObject
    ) {
        GPS[] gps = {new GPS(),new GPS()};
        this.gpsCoordinates = gps;
    }

    public abstract TypeOfCadastralObject isInstanceOf();

    public abstract void setDescription(String description);

    public abstract void serializeDetails(DataOutputStream outputStream) throws IOException;

    public abstract void deserializeDetails(DataInputStream inputStream) throws IOException;

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

}
