package Structure.DynamicHashing;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Block<T extends IRecord> {

    private T[] listOfRecords;
    private Class<T> type;
    private int countOfValidRecords;
    private int blockFactor;
    private int sizeOfRecord;
    private Block freeNext;
    private Block freePrevious;

    public Block(int parBlockFactor, Class<T> type) {
        this.listOfRecords = (T[]) new IRecord[parBlockFactor];
        this.blockFactor = parBlockFactor;
        this.type = type;

        this.sizeOfRecord = this.returnSizeOfRecord();
    }

    private int returnSizeOfRecord() {
        // TODO remake
        try {
            T instance = type.newInstance();
            return instance.getSize();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int getSize() {
        int size =  this.listOfRecords.length * this.sizeOfRecord;
        size += this.getSizeOfHeader();
        return size;
    }

    public int getSizeOfHeader() {
        return Integer.BYTES; // zatial
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);

        try {
            hlpOutStream.writeInt(this.countOfValidRecords);

            byte[] concatenatedBytes = hlpByteArrayOutputStream.toByteArray();

            for (int i = 0; i < this.countOfValidRecords; i++) {
                byte[] record = this.listOfRecords[i].toByteArray();
                concatenatedBytes = appendByteArrays(concatenatedBytes, record);
            }

            byte[] byteArray = new byte[this.sizeOfRecord * (this.blockFactor - this.countOfValidRecords)];
            Arrays.fill(byteArray, (byte) 0);

            concatenatedBytes = appendByteArrays(concatenatedBytes,byteArray);
            return concatenatedBytes;
        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion to byte array.");
        }
    }

    public void writeToFile(int parAddress) {
        byte[] blockData = this.toByteArray();
        try {
            RandomAccessFile file = new RandomAccessFile("file.bin", "rw");
            file.seek(this.getSize() * parAddress);

            file.write(blockData);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.fromByteArray(blockData);
    }

    public void fromByteArray(byte[] parData) {
        this.makeBlockNull();

        byte[] header = Arrays.copyOfRange(parData, 0, this.getSizeOfHeader());

        int numberOfReadRecords = 0;

        ByteArrayInputStream hlpByteArrayInputStream = new ByteArrayInputStream(header);
        DataInputStream hlpInStream = new DataInputStream(hlpByteArrayInputStream);
        try {

            numberOfReadRecords = hlpInStream.readInt();
        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array.");
        }

        byte[] records = Arrays.copyOfRange(parData, this.getSizeOfHeader(), parData.length);

        for (int i = 0; i < numberOfReadRecords * this.sizeOfRecord; i += this.sizeOfRecord) {
            byte[] recordBytes = getSubArray(records, i, this.sizeOfRecord);
            this.createRecord(recordBytes);
        }
    }

    private boolean createRecord(byte[] parRecordBytes) {
        try {
            T record = type.newInstance();
            if (record instanceof IRecord) {
                this.insertRecord(record);
                ((IRecord) record).createObjectFromBytes(parRecordBytes);
                return true;
            } else {
                return false;
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }


    public void fromFileToBlock(int parAddress) {
        byte[] blockData = new byte[this.getSize()];
        try {
            RandomAccessFile file = new RandomAccessFile("file.bin", "rw");
            file.seek(this.getSize() * parAddress);

            file.read(blockData);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.fromByteArray(blockData);
    }

    public boolean insertRecord(IRecord parRecord) {
        if (this.countOfValidRecords < this.blockFactor) {
            this.listOfRecords[this.countOfValidRecords] = (T) parRecord;
            this.countOfValidRecords++;
            return true;
        } else {
            return false;
        }
    }

    public IRecord findRecord(IRecord record) {
        for (int i = 0; i < this.countOfValidRecords; i++) {
            if (this.listOfRecords[i].equals(record)) {
                return this.listOfRecords[i];
            }
        }
        return null;
    }

    public IRecord[] returnRecords() {
        return this.listOfRecords;
    }

    public IRecord[] returnValidRecords() {
        IRecord[] validRec = Arrays.copyOfRange(this.listOfRecords,0,this.countOfValidRecords);
        return validRec;
    }

    public ArrayList<IRecord> returnValidRecordsAsArray() {
        ArrayList<IRecord> dataToReturn = new ArrayList<>();
        for (int i = 0; i < this.countOfValidRecords; i++) {
            dataToReturn.add(this.listOfRecords[i]);
        }
        return dataToReturn;
    }

    private byte[] appendByteArrays(byte[] array1, byte[] array2) {
        byte[] result = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

    public byte[] getSubArray(byte[] array, int startIndex, int length) {
        if (startIndex + length > array.length) {
            length = array.length - startIndex;
        }
        return Arrays.copyOfRange(array, startIndex, startIndex + length);
    }

    public T getRecordAt(int index) {
        return this.listOfRecords[index];
    }

    private void makeBlockNull() {
        this.countOfValidRecords = 0;
        for (IRecord act: this.listOfRecords) {
            act = null;
        }
    }

    public void resetCountOfValidRecords() {
        this.countOfValidRecords = 0;
    }
}
