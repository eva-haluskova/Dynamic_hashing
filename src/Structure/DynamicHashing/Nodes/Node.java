package Structure.DynamicHashing.Nodes;

import Data.CadastralObject;

/**
 * Represents node of tree. For need to save memory, we need to
 * distinguish between internal and external node, because they
 * need mostly different data.
 */
public abstract class Node {

    public enum TypeOfNode {
        INTERNAL,
        EXTERNAL
    }
    protected Node parent;

    public abstract TypeOfNode isInstanceOf();

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parParent) {
        this.parent = parParent;
    }

}
