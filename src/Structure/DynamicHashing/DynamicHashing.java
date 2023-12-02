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
    private int blockFactor;
    private int nextEmptyBlock;
    private Class<T> type;

    private RandomAccessFile rawMain;

    public DynamicHashing(int parBlockFactor, Class<T> type,String parNameOfFile) {

        this.nextEmptyBlock = 0;
        this.blockFactor = parBlockFactor;
        this.type = type;
        try {
            this.rawMain = new RandomAccessFile("file.bin", "rw");
            this.rawMain.setLength(0);
            //this.rawMain.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                record = this.findRecord(parRecord,((ExternalNode) current).getAddress());
                foundedNode = true;
            }
        }
        return record;
    }

    public boolean insert(IRecord parDataToInsert) {

        if (this.root == null) {
            ExternalNode node = new ExternalNode(null);
            this.root = node;
            ((ExternalNode) this.root).setAddress(this.nextEmptyBlock);
            this.nextEmptyBlock++;
            if(this.insertRecord(parDataToInsert,((ExternalNode) this.root).getAddress(), this.rawMain)) {
                ((ExternalNode) this.root).increaseCountOnAddress();
                return true;
            }
        }

        if (this.find(parDataToInsert) != null) {
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
                        ((ExternalNode) current).setAddress(this.nextEmptyBlock);
                        this.nextEmptyBlock++;
                    if (insertRecord(parDataToInsert,((ExternalNode) current).getAddress(),this.rawMain)) {
                        ((ExternalNode) current).increaseCountOnAddress();
                        return true;
                    }
                // ak sa tam zmesti dalsie dato, tak ho tam vlozim
                } else if (((ExternalNode) current).getCountOnAddress() < this.blockFactor) {

                    if (insertRecord(parDataToInsert,((ExternalNode) current).getAddress(), this.rawMain)) {
                        ((ExternalNode) current).increaseCountOnAddress();
                        return true;
                    }
                // ak sa nezmsti dalsie dato, musis rozbijat strom kym sa ti to nepodari:
                } else {
                    boolean isInserted = false;
                    ArrayList<IRecord> dataToInsert = new ArrayList<>();
                    dataToInsert.add(parDataToInsert);
                    while(!isInserted) {
                        dataToInsert.addAll(this.returnDataFromBlock(((ExternalNode) current).getAddress(),this.rawMain));

                        // v danom bloku su vymazem zaznami, nakolko idem delit
                        this.deleteAllDataFromBlock(((ExternalNode) current).getAddress(),this.rawMain);

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
                        newExtNode.setAddress(this.nextEmptyBlock);
                        this.nextEmptyBlock++;

                        Block<T> blockCur = this.readBlockFromFile(this.rawMain,((ExternalNode)current).getAddress());
                        Block<T> blockNew = this.readBlockFromFile(this.rawMain, newExtNode.getAddress());

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

                        this.writeBlockToFile(this.rawMain,((ExternalNode)current).getAddress(),blockCur);
                        this.writeBlockToFile(this.rawMain,newExtNode.getAddress(),blockNew);

                        index++;

                        if (!dataToInsert.isEmpty()) {
                            if (newExtNode.getCountOnAddress() == 0) {

                                this.nextEmptyBlock--;
                                newExtNode.setAddress(-1);
                            }
                            if (((ExternalNode) current).getCountOnAddress() == 0) {

                                ((ExternalNode) current).setAddress(-1);
                                current = newExtNode;
                            }
                        } else {
                            isInserted = true;
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
    private boolean insertRecord(IRecord parDataToInsert, int parAddressToSeek, RandomAccessFile parFile) {
        Block<T> block = this.readBlockFromFile(parFile,parAddressToSeek);
        if (block.insertRecord(parDataToInsert)) {
            this.writeBlockToFile(parFile,parAddressToSeek,block);
            return true;
        }
        return false;
    }

    private ArrayList<IRecord> returnDataFromBlock(int parAddressToSeek, RandomAccessFile parFile) {
        Block<T> block = this.readBlockFromFile(parFile,parAddressToSeek);
        return block.returnValidRecordsAsArray();
    }

    private void deleteAllDataFromBlock(int parAddressToSeek, RandomAccessFile parFile) {
        Block<T> block = this.readBlockFromFile(parFile,parAddressToSeek);
        block.resetCountOfValidRecords();
        this.writeBlockToFile(parFile, parAddressToSeek, block);
    }

    private IRecord findRecord(IRecord parDataToFind, int parAddressToSeek) {
        Block<T> block = this.readBlockFromFile(this.rawMain,parAddressToSeek);
        IRecord recordToReturn = block.findRecord(parDataToFind);
        if (recordToReturn != null) {
            return recordToReturn;
        }
        return null;
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
        Block<T> block = new Block<>(this.blockFactor, type);
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

    public void finishWorkWithFile(RandomAccessFile parFile) {
        try {
            parFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returning data from the whole trie
     */
    public ArrayList<IRecord> returnAllRecords() {
        ArrayList<IRecord> dataToReturn = new ArrayList<>();
        for (int i = 0; i < this.nextEmptyBlock; i++) {
            dataToReturn.addAll(this.returnDataFromBlock(i,this.rawMain));
        }
        return dataToReturn;
    }

    /**
     * print on console containing block sequential
     */
    public void returnSequenceStringOutput() {
        for (int i = 0; i < this.nextEmptyBlock; i++) {
            System.out.println("Block number " + i);
            ArrayList<IRecord> dataToReturn = new ArrayList<>();
            dataToReturn.addAll(returnDataFromBlock(i,this.rawMain));
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
