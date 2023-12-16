package Structure.DynamicHashing.Nodes;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

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

    public void toByteArray() {

    }

    public void fromByteArray(byte[] parByteNode) {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);

//        try {
//            hlpOutStream.write();
//        } catch () {
//
//        }
    }
}
