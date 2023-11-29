package Structure.DynamicHashing.Nodes;

/**
 * Represents node of tree. For need to save memory, we need to
 * distinguish between internal and external node, because they
 * need mostly different data.
 */
public abstract class Node {
    protected Node parent;

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parParent) {
        this.parent = parParent;
    }

}
