package Structure.DynamicHashing;

import Structure.DynamicHashing.Nodes.ExternalNode;
import Structure.DynamicHashing.Nodes.InternalNode;
import Structure.DynamicHashing.Nodes.Node;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;

public class DynamicHashing<T extends IRecord> {

    private Node root;
    private int mainFileBlockFactor;
    private int overfillingFileBlockFactor;
    private int firstEmptyBlockMainFile;
    private int firstEmptyBlockOverfillingFile;
    private RandomAccessFile rawMain;
    private RandomAccessFile rawOverfillFile;
    private Class<T> type;

    public DynamicHashing(int parMainFileBlockFactor, int parOverfillingFileBlockFactor, Class<T> type,String parNameOfMainFile, String parNameOfOverfillingFile) {

        this.firstEmptyBlockMainFile = -1;
        this.firstEmptyBlockOverfillingFile = -1;

        this.mainFileBlockFactor = parMainFileBlockFactor;
        this.overfillingFileBlockFactor = parOverfillingFileBlockFactor;

        this.type = type;
        this.inicializeFiles(parNameOfMainFile, parNameOfOverfillingFile);
    }

    /**
     * Work with data - insert, find, delete
     */
    public IRecord find(IRecord parRecord) {

        BitSet traverBitset = parRecord.getHash();
        IRecord record = null;

        boolean foundedNode = false;
        int index = 0;
        Node current = this.root;
        while (!foundedNode) {
            // ak je current interny traverzujem do externeho vrcholu
            if (current.isInstanceOf() == Node.TypeOfNode.INTERNAL) {
                if (!traverBitset.get(index)) {
                    current = ((InternalNode) current).getLeftSon();
                } else {
                    current = ((InternalNode) current).getRightSon();
                }
                index++;
            } else {
                if (((ExternalNode) current).getAddress() != -1) {
                    record = this.findRecordInBlock(parRecord, ((ExternalNode) current).getAddress(), this.rawMain);
                    foundedNode = true;
                } else {
                    return null;
                }
            }
        }
        return record;
    }

    public boolean insert(IRecord parDataToInsert) {

        if (this.root == null) {
            ExternalNode node = new ExternalNode(null);
            this.root = node;
            ((ExternalNode) this.root).setAddress(this.getAddressOfNextEmptyBlockInMainFile());

            Block<T> blockNew = new Block<>(this.mainFileBlockFactor, type);
            if  (blockNew.insertRecord(parDataToInsert)) {
                this.writeBlockToFile(this.rawMain, ((ExternalNode) this.root).getAddress(),blockNew);
                ((ExternalNode) this.root).increaseCountOnAddress();
                return true;
            }
        }

        if (this.find(parDataToInsert) != null) {
            System.out.println("data already exists: " + parDataToInsert);
            return false;
        }

        BitSet traverBitset = parDataToInsert.getHash();

        int index = 0;
        Node current = this.root;

        while (true) {
            // ak je current interny traverzujem do externeho vrcholu
            if (current.isInstanceOf() == Node.TypeOfNode.INTERNAL) {
                if (!traverBitset.get(index)) {
                    current = ((InternalNode) current).getLeftSon();
                } else {
                    current = ((InternalNode) current).getRightSon();
                }
                index++;
            } else  {
                // ak v nom neexituje odkaz na adresu - alokujem miesto na blok,vlozim dato.
                if (((ExternalNode) current).getAddress() == -1) {

                    ((ExternalNode) current).setAddress(this.getAddressOfNextEmptyBlockInMainFile());
                    Block<T> block = new Block<>(this.mainFileBlockFactor, type);

                    if (block.insertRecord(parDataToInsert)) {
                        ((ExternalNode)current).increaseCountOnAddress();
                        this.writeBlockToFile(this.rawMain,((ExternalNode) current).getAddress(),block);
                        return true;
                    }


                // ak sa tam zmesti dalsie dato, tak ho tam vlozim
                } else if (((ExternalNode) current).getCountOnAddress() < this.mainFileBlockFactor) {

                    if (insertRecordIntoBlock(parDataToInsert,((ExternalNode) current).getAddress(), this.rawMain)) {
                        ((ExternalNode) current).increaseCountOnAddress();
                        return true;
                    }
                // ak sa nezmsti dalsie dato, musis rozbijat strom kym sa ti to nepodari:
                } else {
                    boolean isInserted = false;
                    ArrayList<IRecord> dataToInsert = new ArrayList<>();
                    dataToInsert.add(parDataToInsert);
                    while(!isInserted) {
                        dataToInsert.addAll(this.returnAllDataFromBlock(((ExternalNode) current).getAddress(), this.rawMain));
                        // v danom bloku su vymazem zaznami, nakolko idem delit
                        this.deleteAllDataFromBlock(((ExternalNode) current).getAddress(), this.rawMain);
                        ((ExternalNode) current).setCountOnAddress(0);

                        // vytvorim si nove nody. Na miesto stareho currena dem dat novy interny ktory ma dvoch synov externych.
                        InternalNode newIntNode = new InternalNode(current.getParent());
                        ExternalNode newExtNode = new ExternalNode(newIntNode);

                        // novemu internemu nastavim synov nove externe
                        newIntNode.setLeftSon(current);
                        newIntNode.setRightSon(newExtNode);

                        // nastavujem nove dieta otcovi curren,a to neplati pre root, ten otca nema :)
                        if (!current.equals(this.root)) {
                            // ak akutlany bol lavy syn, tak jeho otcovi nastavim laveho syna na novy interny node
                            if (((InternalNode) current.getParent()).getLeftSon().equals(current)) {
                                ((InternalNode) current.getParent()).setLeftSon(newIntNode);
                                // inak na praveho
                            } else {
                                ((InternalNode) current.getParent()).setRightSon(newIntNode);
                            }
                        } else {
                            this.root = newIntNode;
                        }

                        current.setParent(newIntNode);
                        newExtNode.setAddress(this.getAddressOfNextEmptyBlockInMainFile());

                        Block<T> blockCur;
                        if (((ExternalNode)current).getAddress() != -1) {
                            blockCur = this.readBlockFromFile(this.rawMain,((ExternalNode)current).getAddress());
                        } else {
                            blockCur = new Block<>(this.mainFileBlockFactor, type);
                        }

                        //Block<T> blockNew = this.readBlockFromFile(this.rawMain, newExtNode.getAddress());
                        Block<T> blockNew = new Block<>(this.mainFileBlockFactor, type);

                        Iterator<IRecord> iterator = dataToInsert.iterator();
                        while (iterator.hasNext()) {
                            IRecord record = iterator.next();
                            if (!record.getHash().get(index)) {
                                if (blockCur.insertRecord(record)) {
                                    ((ExternalNode)current).increaseCountOnAddress();
                                     iterator.remove();
                                }
                            } else {
                                if (blockNew.insertRecord(record)) {
                                    newExtNode.increaseCountOnAddress();
                                    iterator.remove();
                                }
                            }
                        }

                        index++;

                        if (!dataToInsert.isEmpty()) {
                            if (newExtNode.getCountOnAddress() == 0) {

                                this.releaseEmptyBlockInMainFile(newExtNode.getAddress(), blockNew);
                                newExtNode.setAddress(-1);
                                this.writeBlockToFile(this.rawMain,((ExternalNode)current).getAddress(),blockCur);
                            }
                            if (((ExternalNode) current).getCountOnAddress() == 0) {

                                this.releaseEmptyBlockInMainFile(((ExternalNode) current).getAddress(),blockCur);
                                ((ExternalNode) current).setAddress(-1);
                                this.writeBlockToFile(this.rawMain,newExtNode.getAddress(),blockNew);
                                current = newExtNode;
                            }
                        } else {
                            isInserted = true;
                            this.writeBlockToFile(this.rawMain,((ExternalNode)current).getAddress(),blockCur);
                            this.writeBlockToFile(this.rawMain,newExtNode.getAddress(),blockNew);
                        }
                    }
                    return true;
                }
            }
        }
    }

    public void delete(IRecord parRecord) {

    }

    /**
     * Private methods for work with records - processing records for next writing - reading from file
     */
    private boolean insertRecordIntoBlock(IRecord parDataToInsert, int parAddressToSeek, RandomAccessFile parFile) {
        Block<T> block = this.readBlockFromFile(parFile,parAddressToSeek);
        if (block.insertRecord(parDataToInsert)) {
            this.writeBlockToFile(parFile,parAddressToSeek,block);
            return true;
        }
        return false;
    }

    private IRecord findRecordInBlock(IRecord parDataToFind, int parAddressToSeek, RandomAccessFile parFile) {
        Block<T> block = this.readBlockFromFile(parFile,parAddressToSeek);
        IRecord recordToReturn = block.findRecord(parDataToFind);
        if (recordToReturn != null) {
            return recordToReturn;
        }
        return null;
    }

    public void deleteRecordFromBlock(IRecord parDataToDelete, int parAddressToSeek, RandomAccessFile parFile) {
        Block<T> block = this.readBlockFromFile(parFile,parAddressToSeek);
        block.deleteRecord(parDataToDelete);
        this.writeBlockToFile(parFile,parAddressToSeek,block);
    }

    private void deleteAllDataFromBlock(int parAddressToSeek, RandomAccessFile parFile) {
        Block<T> block = this.readBlockFromFile(parFile,parAddressToSeek);
        block.resetCountOfValidRecords();
        this.writeBlockToFile(parFile, parAddressToSeek, block);
    }

    private ArrayList<IRecord> returnAllDataFromBlock(int parAddressToSeek, RandomAccessFile parFile) {
        Block<T> block = this.readBlockFromFile(parFile,parAddressToSeek);
        return block.returnValidRecordsAsArray();
    }



    /**
     * work with file - writing, reading, closing
     */
    public void writeBlockToFile(RandomAccessFile parFile, int parAddress, Block<T> parBlock) {
        byte[] blockData = parBlock.toByteArray();
        try {
            parFile.seek(parBlock.getSize() * parAddress);
            parFile.write(blockData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Block<T> readBlockFromFile(RandomAccessFile parFile, int parAddress) {
        Block<T> block = new Block<>(this.mainFileBlockFactor, type);
        byte[] blockData = new byte[block.getSize()];

        try {
            parFile.seek(block.getSize() * parAddress);
            parFile.read(blockData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        block.fromByteArray(blockData);
        return block;
    }

    public void finishWorkWithFile() {
        try {
            this.rawMain.close();
            this.rawOverfillFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Management of free blocks
     */
    public int getAddressOfNextEmptyBlockInMainFile() {
        if (this.firstEmptyBlockMainFile == -1) {
            return this.getSizeOfMainFile()/this.getSizeOfMainBlock();
        } else {
            int addressToReturn = this.firstEmptyBlockMainFile;
            Block<T> nextEmpty = this.readBlockFromFile(this.rawMain,this.firstEmptyBlockMainFile);
            if (this.firstEmptyBlockMainFile != -1 && nextEmpty.getNextFreeBlock() == -1) {
                this.firstEmptyBlockMainFile = -1;
                return addressToReturn;
            } else {
                this.firstEmptyBlockMainFile = nextEmpty.getNextFreeBlock();
                nextEmpty.setNextFreeBlock(-1);
                Block<T> nextNextEmpty = this.readBlockFromFile(this.rawMain,this.firstEmptyBlockMainFile);
                nextNextEmpty.setPreviousFreeBlock(-1);
                this.writeBlockToFile(this.rawMain,this.firstEmptyBlockMainFile,nextNextEmpty);
                nextEmpty.setNextFreeBlock(-1);
                this.writeBlockToFile(this.rawMain,addressToReturn,nextEmpty);
                return addressToReturn;
            }
        }

    }

    public int getAddressOfNextEmptyBlockInOverfillingFile() {
        return -1;
    }

    public void releaseEmptyBlockInMainFile(int parAddress, Block<T> parBlock) {
        if (this.firstEmptyBlockMainFile == -1) {
            this.firstEmptyBlockMainFile = parAddress;
            this.writeBlockToFile(this.rawMain, parAddress, parBlock);
        } else {
            int address = this.firstEmptyBlockMainFile;
            this.firstEmptyBlockMainFile = parAddress;
            parBlock.setNextFreeBlock(address);
            Block<T> nextEmpty = this.readBlockFromFile(this.rawMain,address);
            nextEmpty.setPreviousFreeBlock(parAddress);
            this.writeBlockToFile(this.rawMain, address, nextEmpty);
            this.writeBlockToFile(this.rawMain,parAddress,parBlock);
        }
    }

    public void releaseEmptyBlockInOverfillingFile() {

    }

    /**
     * some useful private methods
     */
    private void inicializeFiles(String parNameOfMainFile, String parNameOfOverfillingFile) {
        try {
            this.rawMain = new RandomAccessFile(parNameOfMainFile, "rw");
            this.rawMain.setLength(0);

            this.rawOverfillFile = new RandomAccessFile(parNameOfOverfillingFile, "rw");
            this.rawOverfillFile.setLength(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getSizeOfMainFile() {
        try {
            return Math.round(this.rawMain.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private int getSizeOfOverfillingFile() {
        try {
            return Math.round(this.rawOverfillFile.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private int getSizeOfMainBlock() {
        Block<T> b = new Block<>(this.mainFileBlockFactor,type);
        return b.getSize();
    }

    private int getSizeOfOverfillingBlock() {
        Block<T> b = new Block<>(this.overfillingFileBlockFactor,type);
        return b.getSize();
    }

    /**
     * Returning data from the whole trie
     */
    public ArrayList<IRecord> returnAllRecords(RandomAccessFile parFile, int parFileSize, int parBlockSize) {
        ArrayList<IRecord> dataToReturn = new ArrayList<>();
        for (int i = 0; i < parFileSize/parBlockSize; i++) {
            dataToReturn.addAll(this.returnAllDataFromBlock(i,parFile));
        }
        return dataToReturn;
    }

    public ArrayList<IRecord> returnAllRecords() {
        ArrayList<IRecord> dataToReturn = new ArrayList<>();
        for (int i = 0; i < this.getSizeOfMainFile()/this.getSizeOfMainBlock(); i++) {
            dataToReturn.addAll(this.returnAllDataFromBlock(i,this.rawMain));
        }
        return dataToReturn;
    }

    /**
     * print on console containing block sequential
     */
    public void returnSequenceStringOutput(RandomAccessFile parFile, int parFileSize, int parBlockSize) {
        for (int i = 0; i < parFileSize/parBlockSize; i++) {
            System.out.println("Block number " + i);
            ArrayList<IRecord> dataToReturn = new ArrayList<>();
            dataToReturn.addAll(returnAllDataFromBlock(i, parFile));
            if (!dataToReturn.isEmpty()) {
                for (int j = 0; j < dataToReturn.size(); j++) {
                    System.out.println(dataToReturn.get(j));
                }
            } else {
                System.out.println("Invalid block");
            }
        }
    }
    public void returnSequenceStringOutput() {
        for (int i = 0; i < this.getSizeOfMainFile()/this.getSizeOfMainBlock(); i++) {
            System.out.println("Block number " + i);
            ArrayList<IRecord> dataToReturn = new ArrayList<>();
            dataToReturn.addAll(returnAllDataFromBlock(i, this.rawMain));
            if (!dataToReturn.isEmpty()) {
                for (int j = 0; j < dataToReturn.size(); j++) {
                    System.out.println(dataToReturn.get(j));
                }
            } else {
                System.out.println("Invalid block");
            }
        }
    }

}
