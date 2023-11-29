package Structure.DynamicHashing.Nodes;

/**
 * Represents external node which contain attributes for working with data.
 */

public class ExternalNode extends Node {

    private int address;
    private int countOnAddress;

    public ExternalNode(Node parParent) {
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
}
