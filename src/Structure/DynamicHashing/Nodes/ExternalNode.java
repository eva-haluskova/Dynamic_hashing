package Structure.DynamicHashing.Nodes;

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
}
