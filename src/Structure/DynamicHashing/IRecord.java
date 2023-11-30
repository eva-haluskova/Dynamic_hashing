package Structure.DynamicHashing;

import java.util.BitSet;

/**
 * Interface with methods which are needed for work with "Records" into Blocks,
 * which are part of dynamic hashing.
 */
public interface IRecord {

    boolean equals(Object parAnotherObject);
    BitSet getHash();
    int getSize();
    byte[] toByteArray();
    void fromByteArray(byte[] parData);
    void createObjectFromBytes(byte[] parData);

}
