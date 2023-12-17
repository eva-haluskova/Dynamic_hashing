package Structure.DynamicHashing.Nodes;

import java.io.*;

/**
 * Represents external node which contain attributes for working with data.
 */

public class ExternalNode extends Node {

    private int address;
    private int countOnAddress;
    private int countOfLinkedBlocks;

    public ExternalNode(Node parParent) {
        this.parent = parParent;
        this.address = -1;
        this.countOfLinkedBlocks = 0;
        this.countOnAddress = 0;
    }

    public ExternalNode(byte[] parByteArray) {
        this.fromByteArray(parByteArray);
    }

    public void setParent(Node parParent) {
        this.parent = parParent;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int parAddress) {
        this.address = parAddress;
    }

    public int getCountOnAddress() {
        return countOnAddress;
    }

    public void setCountOnAddress(int parCountOnAddress) {
        this.countOnAddress = parCountOnAddress;
    }

    public void increaseCountOnAddress() {
        this.countOnAddress++;
    }

    public void decreaseCountOnAddress() {
        this.countOnAddress--;
    }

    @Override
    public TypeOfNode isInstanceOf() {
        return TypeOfNode.EXTERNAL;
    }

    public int getCountOfLinkedBlocks() {
        return countOfLinkedBlocks;
    }

    public void setCountOfLinkedBlocks(int parCountOfLinkedBlocks) {
        this.countOfLinkedBlocks = parCountOfLinkedBlocks;
    }

    public void increaseCountOfLinkedBlocks() {
        this.countOfLinkedBlocks++;
    }

    public void decreaseCountOfLinkedBlocks() {
        this.countOfLinkedBlocks++;
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);
        try {
            hlpOutStream.writeInt(this.address);
            hlpOutStream.writeInt(this.countOnAddress);
            hlpOutStream.writeInt(this.countOfLinkedBlocks);
            return hlpByteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion to byte array.");
        }
    }

    public void fromByteArray(byte[] parByteNode) {
        ByteArrayInputStream hlpByteArrayInputStream = new ByteArrayInputStream(parByteNode);
        DataInputStream hlpInStream = new DataInputStream(hlpByteArrayInputStream);

        try {
            this.address = hlpInStream.readInt();
            this.countOnAddress = hlpInStream.readInt();
            this.countOfLinkedBlocks = hlpInStream.readInt();
        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array.");
        }
    }

    public int size() {
        return Integer.BYTES * 3;
    }
}
