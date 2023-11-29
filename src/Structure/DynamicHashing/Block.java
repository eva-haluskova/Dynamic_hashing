package Structure.DynamicHashing;

public class Block {

    private IRecord[] listOfRecords;
    private int countOfRecords;


    Block(int parBlockFactor) {
        this.listOfRecords = new IRecord[parBlockFactor];
    }

    public int getSize() {
        return 0;
    }

    public byte[] toByteArray() {
        return new byte[4];
    }

    public void fromByteArray(byte[] parData) {

    }
}
