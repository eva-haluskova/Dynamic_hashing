package Structure.DynamicHashing.Nodes;

/**
 * Represents internal node which contain just attributes of pointers of sons.
 */

public class InternalNode extends Node {

    private Node leftSon;
    private Node rightSon;

    public InternalNode(Node parParent) {
        this.parent = parParent;
    }

    public Node getLeftSon() {
        return leftSon;
    }

    public void setLeftSon(Node parLeftSon) {
        this.leftSon = parLeftSon;
    }

    public Node getRightSon() {
        return rightSon;
    }

    public void setRightSon(Node parRightNode) {
        this.rightSon = parRightNode;
    }

    @Override
    public TypeOfNode isInstanceOf() {
        return TypeOfNode.INTERNAL;
    }
}
