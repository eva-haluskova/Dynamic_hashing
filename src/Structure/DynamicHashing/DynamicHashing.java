package Structure.DynamicHashing;

import Structure.DynamicHashing.Nodes.ExternalNode;
import Structure.DynamicHashing.Nodes.Node;

import java.io.IOException;
import java.io.RandomAccessFile;

public class DynamicHashing {

    private Node root;
    private int blockFactor;
    private int nextEmptyBlock;

    public DynamicHashing(int parBlockFactor) {

        this.blockFactor = parBlockFactor;

        try {
            RandomAccessFile file = new RandomAccessFile("file.bin", "rw");
            file.setLength(0);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void insert(IRecord parDataToInsert) {

        if (this.root == null) {
            ExternalNode node = new ExternalNode(null);
            this.root = node;
            //this.root = (ExternalNode) this.root;
            // TODO insert data into this node...resp.naseekovat sa tam kde trebado suboru, zaserializovat data a vlozit ich tam
           // ((ExternalNode) this.root).setAddress(this.nextEmptyBlock * );
        }

    }

}
