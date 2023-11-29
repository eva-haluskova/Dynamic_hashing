package Structure.DynamicHashing.Nodes;

/**
 * Represents internal node which contain just attributes of pointers of sons.
 */

public class InternalNode extends Node {

    private Node leftSon;
    private Node rightNode;

    public InternalNode(Node parParent) {
        this.parent = parParent;
    }

    public Node getLeftSon() {
        return leftSon;
    }

    public void setLeftSon(Node parLeftSon) {
        this.leftSon = parLeftSon;
    }

    public Node getRightNode() {
        return rightNode;
    }

    public void setRightNode(Node parRightNode) {
        this.rightNode = parRightNode;
    }
}
