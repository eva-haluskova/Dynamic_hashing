package Structure.DynamicHashing;

import Structure.DynamicHashing.Nodes.Node;

import java.io.IOException;
import java.io.RandomAccessFile;

public class DynamicHashing {

    private Node root;
    private int blockFactor;

    public DynamicHashing(int parBlockFactor) {
        try {
            RandomAccessFile file = new RandomAccessFile("file.bin", "rw");
            // Perform operations with the RandomAccessFile
            file.close(); // Remember to close the file when you're done
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void insert() {

    }

}

//    Na pracu so suborom sa pouzije RandomAccessFile a na jeho zmensenie
//RandomAccessFile.setLength(long newLength)
