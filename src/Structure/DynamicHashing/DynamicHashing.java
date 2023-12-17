package Structure.DynamicHashing;

import Structure.DynamicHashing.Nodes.ExternalNode;
import Structure.DynamicHashing.Nodes.InternalNode;
import Structure.DynamicHashing.Nodes.Node;

import java.io.*;
import java.util.*;

/**
 * Represents instance of dynamic hashing - efficient way for storage data into file :)
 */
public class DynamicHashing<T extends IRecord> {

    private Node root;
    private int mainFileBlockFactor;
    private int overfillingFileBlockFactor;
    private int firstEmptyBlockMainFile;
    private int firstEmptyBlockOverfillingFile;

    private String rawMainName;
    private String rawOverfillingName;
    private RandomAccessFile rawMain;
    private RandomAccessFile rawOverfillFile;
    private Class<T> type;

    private class Result {
        private IRecord foundRecord;
        private ExternalNode externalNode;

        private Result(IRecord parFoundRecord, ExternalNode parExternalNode) {
            this.foundRecord = parFoundRecord;
            this.externalNode = parExternalNode;
        }

        public IRecord getFoundRecord() {
            return foundRecord;
        }

        public ExternalNode getExternalNode() {
            return externalNode;
        }
    }

    public DynamicHashing(int parMainFileBlockFactor, int parOverfillingFileBlockFactor, Class<T> type,String parNameOfMainFile, String parNameOfOverfillingFile) {

        this.firstEmptyBlockMainFile = -1;
        this.firstEmptyBlockOverfillingFile = -1;

        this.mainFileBlockFactor = parMainFileBlockFactor;
        this.overfillingFileBlockFactor = parOverfillingFileBlockFactor;

        this.rawMainName = parNameOfMainFile + "MainFile.bin";
        this.rawOverfillingName = parNameOfOverfillingFile + "OverfillingFile.bin";

        this.type = type;
        this.inicializeFiles(rawMainName, rawOverfillingName);
    }

    public DynamicHashing(String parNameOfFileToLoad, Class<T> type) {
        this.type = type;
        this.loadTrie(parNameOfFileToLoad);
    }

    /**
     * Work with data - insert, find, delete, edit
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
                    record = this.findRecord(parRecord, this.mainFileBlockFactor, ((ExternalNode) current).getAddress(), this.rawMain);
                    foundedNode = true;
                } else {
                    return null;
                }
            }
        }
        return record;
    }

    public Result findRet(IRecord parRecord) {

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
                    record = this.findRecord(parRecord, this.mainFileBlockFactor, ((ExternalNode) current).getAddress(), this.rawMain);
                    foundedNode = true;
                } else {
                    return null;
                }
            }
        }
        return new Result(record,((ExternalNode) current));
    }

    public boolean insert(IRecord parDataToInsert) {

        if (this.root == null) {
            ExternalNode node = new ExternalNode((Node) null);
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

            if (traverBitset.length() == index) {
                if (current.isInstanceOf().equals(Node.TypeOfNode.EXTERNAL)) {
                    if (((ExternalNode) current).getCountOnAddress() >= this.mainFileBlockFactor) {
                        if (this.insertIntoOverfillingFile(((ExternalNode) current), parDataToInsert)) {
                            return true;
                        }
                        return false;
                    }
                }
            }

            // if current is internal - going down to external node
            if (current.isInstanceOf() == Node.TypeOfNode.INTERNAL) {
                if (!traverBitset.get(index)) {
                    current = ((InternalNode) current).getLeftSon();
                } else {
                    current = ((InternalNode) current).getRightSon();
                }
                index++;

            } else  {

                // if current external node doesn't have address to file - allocate block and insert data
                if (((ExternalNode) current).getAddress() == -1) {

                    ((ExternalNode) current).setAddress(this.getAddressOfNextEmptyBlockInMainFile());
                    Block<T> block = new Block<>(this.mainFileBlockFactor, type);

                    if (block.insertRecord(parDataToInsert)) {
                        ((ExternalNode)current).increaseCountOnAddress();
                        this.writeBlockToFile(this.rawMain,((ExternalNode) current).getAddress(),block);
                        return true;
                    }


                // if current external node isn't full - insert data
                } else if (((ExternalNode) current).getCountOnAddress() < this.mainFileBlockFactor) {

                    if (insertRecordIntoBlock(parDataToInsert, this.mainFileBlockFactor, ((ExternalNode) current).getAddress(), this.rawMain)) {
                        ((ExternalNode) current).increaseCountOnAddress();
                        return true;
                    }

                // if current external node is full, it's needed to split nodes util all data are inserted
                } else {
                    boolean isInserted = false;
                    ArrayList<IRecord> dataToInsert = new ArrayList<>();
                    dataToInsert.add(parDataToInsert);
                    while(!isInserted) {

                        // if current is in max depth of tree, data must be inserted into overfilling file
                        if (traverBitset.length() == index) {
                            if (current.isInstanceOf().equals(Node.TypeOfNode.EXTERNAL)) {
                                if (((ExternalNode) current).getCountOnAddress() >= this.mainFileBlockFactor) {
                                    if (this.insertIntoOverfillingFile(((ExternalNode) current), dataToInsert.get(0))) {
                                        return true;
                                    }
                                    return false;
                                }
                            }
                        }

                        // add all data from current node to list which will be inserted later
                        dataToInsert.addAll(this.returnAllDataFromBlock(((ExternalNode) current).getAddress(), this.mainFileBlockFactor, this.rawMain));

                        // remove actual data from current for purpose of splitting data into two nodes
                        this.deleteAllDataFromBlock(((ExternalNode) current).getAddress(), this.mainFileBlockFactor, this.rawMain);
                        ((ExternalNode) current).setCountOnAddress(0);

                        // creating new nodes, set pointers for parent/children
                        InternalNode newIntNode = new InternalNode(current.getParent());
                        ExternalNode newExtNode = new ExternalNode(newIntNode);

                        newIntNode.setLeftSon(current);
                        newIntNode.setRightSon(newExtNode);

                        if (!current.equals(this.root)) {
                            if (((InternalNode) current.getParent()).getLeftSon().equals(current)) {
                                ((InternalNode) current.getParent()).setLeftSon(newIntNode);
                            } else {
                                ((InternalNode) current.getParent()).setRightSon(newIntNode);
                            }
                        } else {
                            this.root = newIntNode;
                        }

                        current.setParent(newIntNode);
                        newExtNode.setAddress(this.getAddressOfNextEmptyBlockInMainFile());

                        // creating/loading blocks
                        Block<T> blockCur;
                        if (((ExternalNode)current).getAddress() != -1) {
                            blockCur = this.readBlockFromFile(this.rawMain, this.mainFileBlockFactor,((ExternalNode)current).getAddress());
                        } else {
                            blockCur = new Block<>(this.mainFileBlockFactor, type);
                        }

                        Block<T> blockNew = new Block<>(this.mainFileBlockFactor, type);

                        // iterate through list of data and trying to insert them into nodes
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

                        // saving and releasing blocks
                        if (!dataToInsert.isEmpty()) {
                            if (newExtNode.getCountOnAddress() == 0) {
                                this.writeBlockToFile(this.rawMain,((ExternalNode)current).getAddress(),blockCur);
                                this.releaseEmptyBlockInMainFile(newExtNode.getAddress(), blockNew);
                                newExtNode.setAddress(-1);
                            }
                            if (((ExternalNode) current).getCountOnAddress() == 0) {
                                this.writeBlockToFile(this.rawMain,newExtNode.getAddress(),blockNew);
                                this.releaseEmptyBlockInMainFile(((ExternalNode) current).getAddress(),blockCur);
                                ((ExternalNode) current).setAddress(-1);
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

    private boolean insertIntoOverfillingFile(ExternalNode parNode, IRecord parRecordToInsert) {
        Block<T> block = this.readBlockFromFile(this.rawMain, this.mainFileBlockFactor, parNode.getAddress());
        if (block.getNextLinkedBlock() == -1) {
            int newAddress = this.getAddressOfNextEmptyBlockInOverfillingFile();
            Block<T> newBlock = new Block<>(this.overfillingFileBlockFactor,type);
            if (newBlock.insertRecord(parRecordToInsert)) {
                parNode.increaseCountOnAddress();
                this.writeBlockToFile(this.rawOverfillFile, newAddress, newBlock);
                block.setNextLinkedBlock(newAddress);
                this.writeBlockToFile(this.rawMain, parNode.getAddress(), block);
                parNode.increaseCountOfLinkedBlocks();
                return true;
            }
        } else {
            Block<T> nextBlock = this.readBlockFromFile(this.rawOverfillFile,this.overfillingFileBlockFactor,block.getNextLinkedBlock());
            int addressOfNextPrevious = block.getNextLinkedBlock();
            while(nextBlock.getNextLinkedBlock() != -1) {
                addressOfNextPrevious = nextBlock.getNextLinkedBlock();
                nextBlock = this.readBlockFromFile(this.rawOverfillFile,this.overfillingFileBlockFactor, nextBlock.getNextLinkedBlock());
            }
            if (nextBlock.getValidCount() == this.overfillingFileBlockFactor) {
                int newAddress = this.getAddressOfNextEmptyBlockInOverfillingFile();
                Block<T> newBlock = new Block<>(this.overfillingFileBlockFactor,type);
                if (newBlock.insertRecord(parRecordToInsert)) {
                    parNode.increaseCountOnAddress();
                    this.writeBlockToFile(this.rawOverfillFile, newAddress, newBlock);
                    nextBlock.setNextLinkedBlock(newAddress);
                    this.writeBlockToFile(this.rawOverfillFile, addressOfNextPrevious, nextBlock);
                    parNode.increaseCountOfLinkedBlocks();
                    return true;
                }
            } else {
                if (nextBlock.insertRecord(parRecordToInsert)) {
                    parNode.increaseCountOnAddress();
                    this.writeBlockToFile(this.rawOverfillFile, addressOfNextPrevious, nextBlock);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean edit(IRecord parRecordToEdit) {
        // chcem editovat nejaky i record, sem si poslem uz irecord, ktory si najdem podla ide ale sem uz pozielam
        // i record so zmenenymi datami.
        Result res = this.findRet(parRecordToEdit);
        if (res != null) {
            Block<T> block = this.readBlockFromFile(this.rawMain, this.mainFileBlockFactor, res.getExternalNode().getAddress());
            if (block.getNextLinkedBlock() == -1) {
                block.editRecord(parRecordToEdit);
                this.writeBlockToFile(this.rawMain, res.getExternalNode().getAddress(), block);
                return true;
            } else {
                if (block.editRecord(parRecordToEdit)) {
                    this.writeBlockToFile(this.rawMain, res.getExternalNode().getAddress(), block);
                    return true;
                }
                int address = block.getNextLinkedBlock();
                while (address != -1) {
                    block = this.readBlockFromFile(this.rawOverfillFile, this.overfillingFileBlockFactor, block.getNextLinkedBlock());
                    if (block.editRecord(parRecordToEdit)) {
                        this.writeBlockToFile(rawOverfillFile, address, block);
                        return true;
                    }
                    address = block.getNextLinkedBlock();
                }
            }
        }
        return false;
    }

    public void delete(IRecord parRecord) {
        BitSet traverBitset = parRecord.getHash();

        if (this.find(parRecord) == null) {
            System.out.println("Data isn't possible to remove, because they don't exist.");
        } else {

            boolean foundedNode = false;
            int index = 0;
            Node current = this.root;
            while (!foundedNode) {
                if (current.isInstanceOf() == Node.TypeOfNode.INTERNAL) {
                    if (!traverBitset.get(index)) {
                        current = ((InternalNode) current).getLeftSon();
                    } else {
                        current = ((InternalNode) current).getRightSon();
                    }
                    index++;
                } else {
                    this.deleteRecord(parRecord,this.mainFileBlockFactor,((ExternalNode) current).getAddress(),this.rawMain);
                    ((ExternalNode) current).decreaseCountOnAddress();

                    // reduction
                    if (this.tryToReduce(((ExternalNode) current))) {
                        this.reduce(((ExternalNode) current));
                    }

                    // merge
                    this.merge((ExternalNode) current);
                    foundedNode = true;
                }
            }
        }
    }

    /**
     * methods belonging to delete - reduction and merging
     */
    private boolean tryToReduce(ExternalNode parNode) {
        return ((parNode.getCountOfLinkedBlocks() - 1) * this.overfillingFileBlockFactor +
                this.mainFileBlockFactor) >= parNode.getCountOnAddress();
    }

    /**
     * if count of blocks in overfilling file is more that theoretically count of them - reduce them
     */
    private void reduce(ExternalNode parNode) {
        ArrayList<IRecord> dataToInsert = this.returnAllDataFromLinkedBlocks(parNode.getAddress());
        this.removeLinkedBlocks(parNode.getAddress());
        parNode.setCountOfLinkedBlocks(0);
        parNode.setCountOnAddress(0);
        for (int i = 0; i < dataToInsert.size(); i++) {
            this.insert(dataToInsert.get(i));
        }
    }

    /**
     * if count of item into two brother nodes are lower than block factor - merge them into one node
     */
    private void merge(ExternalNode parCurrentNode) {

        boolean isPossibleToMerge = true;

        while(isPossibleToMerge) {

            if (parCurrentNode.equals(this.root)) {
                if (parCurrentNode.getCountOnAddress() == 0) {
                    if (parCurrentNode.getAddress() != -1) {
                        Block<T> cur = this.readBlockFromFile(this.rawMain, this.mainFileBlockFactor, parCurrentNode.getAddress());
                        this.releaseEmptyBlockInMainFile(parCurrentNode.getAddress(), cur);
                        this.root = null;
                    }
                }
                isPossibleToMerge = false;
            } else {
                ExternalNode brotherNode = null;
                if (((InternalNode) parCurrentNode.getParent()).getLeftSon().equals(parCurrentNode)) {
                    if (((InternalNode) parCurrentNode.getParent()).getRightSon().isInstanceOf().equals(Node.TypeOfNode.EXTERNAL)) {
                        brotherNode = ((ExternalNode) ((InternalNode) parCurrentNode.getParent()).getRightSon());
                    }
                } else {
                    if (((InternalNode) parCurrentNode.getParent()).getLeftSon().isInstanceOf().equals(Node.TypeOfNode.EXTERNAL)) {
                        brotherNode = ((ExternalNode) ((InternalNode) parCurrentNode.getParent()).getLeftSon());
                    }
                }

                if (brotherNode != null) {

                    // if this condition isn't true, merge cannot happen
                    if (brotherNode.getCountOnAddress() + parCurrentNode.getCountOnAddress() <= this.mainFileBlockFactor) {

                        if (parCurrentNode.getParent().getParent() == null) {
                            parCurrentNode.setParent(null);
                            this.root = parCurrentNode;
                            isPossibleToMerge = false;
                        } else {
                            InternalNode parent = (InternalNode) parCurrentNode.getParent();
                            InternalNode grantParent = (InternalNode) parent.getParent();
                            if (grantParent.getLeftSon().equals(parent)) {
                                grantParent.setLeftSon(parCurrentNode);
                            } else {
                                grantParent.setRightSon(parCurrentNode);
                            }
                            parCurrentNode.setParent(grantParent);
                        }

                        if (brotherNode.getAddress() != -1) {
                            Block<T> meBlock = this.readBlockFromFile(this.rawMain, this.mainFileBlockFactor, parCurrentNode.getAddress());
                            Block<T> brotherBlock = this.readBlockFromFile(this.rawMain, this.mainFileBlockFactor, brotherNode.getAddress());
                            ArrayList<IRecord> brothersRecords = brotherBlock.returnValidRecordsAsArray();
                            brotherBlock.resetCountOfValidRecords();
                            this.releaseEmptyBlockInMainFile(brotherNode.getAddress(), brotherBlock);
                            for (int i = 0; i < brothersRecords.size(); i++) {
                                meBlock.insertRecord(brothersRecords.get(i));
                            }
                            parCurrentNode.setCountOnAddress(parCurrentNode.getCountOnAddress() + brothersRecords.size());
                            brotherNode.setAddress(-1);
                            this.writeBlockToFile(this.rawMain, parCurrentNode.getAddress(), meBlock);
                        }

                    } else {
                        isPossibleToMerge = false;
                    }
                } else {
                    if (parCurrentNode.getCountOnAddress() == 0) {
                        Block<T> meBlock = this.readBlockFromFile(this.rawMain, this.mainFileBlockFactor, parCurrentNode.getAddress());
                        this.releaseEmptyBlockInMainFile(parCurrentNode.getAddress(),meBlock);
                        parCurrentNode.setAddress(-1);
                    }
                    isPossibleToMerge = false;
                }
            }
        }
    }

    /**
     * Private methods for work with records - processing records for next writing - reading from file
     */
    private boolean insertRecordIntoBlock(IRecord parDataToInsert, int parBlockFactor, int parAddressToSeek, RandomAccessFile parFile) {
        Block<T> block = this.readBlockFromFile(parFile,parBlockFactor,parAddressToSeek);
        if (block.insertRecord(parDataToInsert)) {
            this.writeBlockToFile(parFile,parAddressToSeek,block);
            return true;
        }
        return false;
    }

    private IRecord findRecordInBlock(IRecord parDataToFind,int parBlockFactor, int parAddressToSeek, RandomAccessFile parFile) {
        Block<T> block = this.readBlockFromFile(parFile, parBlockFactor, parAddressToSeek);
        IRecord recordToReturn = block.findRecord(parDataToFind);
        if (recordToReturn != null) {
            return recordToReturn;
        }
        return null;
    }

    private IRecord findRecord(IRecord parDataToFind,int parBlockFactor, int parAddressToSeek, RandomAccessFile parFile) {
        Block<T> block = this.readBlockFromFile(parFile, parBlockFactor, parAddressToSeek);
        if (block.getNextLinkedBlock() == -1) {
            return findRecordInBlock(parDataToFind, this.mainFileBlockFactor, parAddressToSeek, this.rawMain);
        } else {
            IRecord recordToRetur = block.findRecord(parDataToFind);
            if (recordToRetur != null) {
                return recordToRetur;
            }

            do {
                block = this.readBlockFromFile(this.rawOverfillFile, this.overfillingFileBlockFactor, block.getNextLinkedBlock());
                IRecord recordToReturn = block.findRecord(parDataToFind);
                if (recordToReturn != null) {
                    return recordToReturn;
                }
            } while (block.getNextLinkedBlock() != -1);

            return null;
        }
    }

    private void deleteRecordFromBlock(IRecord parDataToDelete, int parBlockFactor, int parAddressToSeek, RandomAccessFile parFile) {
        Block<T> block = this.readBlockFromFile(parFile, parBlockFactor, parAddressToSeek);
        block.deleteRecord(parDataToDelete);
        this.writeBlockToFile(parFile,parAddressToSeek,block);
    }

    private void deleteRecord(IRecord parDataToDelete, int parBlockFactor, int parAddressToSeek, RandomAccessFile parFile) {
        Block<T> block = this.readBlockFromFile(parFile, parBlockFactor, parAddressToSeek);
        if (block.getNextLinkedBlock() == -1) {
            deleteRecordFromBlock(parDataToDelete,parBlockFactor,parAddressToSeek,parFile);
        } else {
            if (block.deleteRecordRet(parDataToDelete)) {
                this.writeBlockToFile(this.rawMain,parAddressToSeek,block);
                return;
            }
            do {
                int addr = block.getNextLinkedBlock();
                block = this.readBlockFromFile(this.rawOverfillFile, this.overfillingFileBlockFactor, block.getNextLinkedBlock());
                if (block.deleteRecordRet(parDataToDelete)) {
                    this.writeBlockToFile(this.rawOverfillFile,addr,block);
                    return;
                }
            } while (block.getNextLinkedBlock() != -1);
        }
    }

    private void deleteAllDataFromBlock(int parAddressToSeek, int parBlockFactor, RandomAccessFile parFile) {
        Block<T> block = this.readBlockFromFile(parFile, parBlockFactor, parAddressToSeek);
        block.resetCountOfValidRecords();
        this.writeBlockToFile(parFile, parAddressToSeek, block);
    }

    /**
     * method used when reduce
     */
    private ArrayList<IRecord> returnAllDataFromLinkedBlocks(int parAddressToSeek) {
        ArrayList<IRecord> listOfData = new ArrayList<>();
        listOfData.addAll(returnAllDataFromBlock(parAddressToSeek, this.mainFileBlockFactor, this.rawMain));

        Block<T> block = this.readBlockFromFile(this.rawMain, this.mainFileBlockFactor, parAddressToSeek);
        while (block.getNextLinkedBlock() != -1) {
            listOfData.addAll(returnAllDataFromBlock(block.getNextLinkedBlock(),this.overfillingFileBlockFactor,this.rawOverfillFile));
            block = this.readBlockFromFile(this.rawOverfillFile, this.overfillingFileBlockFactor,block.getNextLinkedBlock());
        }
        return listOfData;
    }

    private ArrayList<IRecord> returnAllDataFromBlock(int parAddressToSeek, int parBlockFactor, RandomAccessFile parFile) {
        Block<T> block = this.readBlockFromFile(parFile, parBlockFactor, parAddressToSeek);
        return block.returnValidRecordsAsArray();
    }

    /**
     * method deletes linked block in overfilling file and remove records in block in main file.
     */
    private void removeLinkedBlocks(int parAddress) {
        Block<T> block = this.readBlockFromFile(this.rawMain,this.mainFileBlockFactor,parAddress);
        block.resetCountOfValidRecords();
        int address = block.getNextLinkedBlock();
        block.setNextLinkedBlock(-1);
        this.writeBlockToFile(this.rawMain, parAddress, block);

        while (address != -1) {
            block = this.readBlockFromFile(this.rawOverfillFile, this.overfillingFileBlockFactor, address);
            int aktadr = address;
            address = block.getNextLinkedBlock();
            this.releaseEmptyBlockInOverfillingFile(aktadr,block); // tu sa aj nastavuje hodnota zretazenia a aj VC
        }
    }

    /**
     * work with file - writing, reading, closing
     */
    private void writeBlockToFile(RandomAccessFile parFile, int parAddress, Block<T> parBlock) {
        byte[] blockData = parBlock.toByteArray();
        try {
            parFile.seek(parBlock.getSize() * parAddress);
            parFile.write(blockData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Block<T> readBlockFromFile(RandomAccessFile parFile, int parBlockFactor, int parAddress) {
        Block<T> block = new Block<>(parBlockFactor, type);
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

    public void finishWorkWithTrie() {
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
    private int getAddressOfNextEmptyBlockInMainFile() {
        if (this.firstEmptyBlockMainFile == -1) {
            return this.getSizeOfMainFile()/this.getSizeOfMainBlock();
        } else {
            int addressToReturn = this.firstEmptyBlockMainFile;
            Block<T> nextEmpty = this.readBlockFromFile(this.rawMain, this.mainFileBlockFactor,this.firstEmptyBlockMainFile);
            if (this.firstEmptyBlockMainFile != -1 && nextEmpty.getNextFreeBlock() == -1) {
                this.firstEmptyBlockMainFile = -1;
                return addressToReturn;
            } else {
                this.firstEmptyBlockMainFile = nextEmpty.getNextFreeBlock();
                nextEmpty.setNextFreeBlock(-1);
                Block<T> nextNextEmpty = this.readBlockFromFile(this.rawMain,this.mainFileBlockFactor,this.firstEmptyBlockMainFile);
                nextNextEmpty.setPreviousFreeBlock(-1);
                this.writeBlockToFile(this.rawMain,this.firstEmptyBlockMainFile,nextNextEmpty);
                nextEmpty.setNextFreeBlock(-1);
                this.writeBlockToFile(this.rawMain,addressToReturn,nextEmpty);
                return addressToReturn;
            }
        }

    }

    private int getAddressOfNextEmptyBlockInOverfillingFile() {
        if (this.firstEmptyBlockOverfillingFile == -1) {
            return this.getSizeOfOverfillingFile()/this.getSizeOfOverfillingBlock();
        } else {
            int addressToReturn = this.firstEmptyBlockOverfillingFile;
            Block<T> nextEmpty = this.readBlockFromFile(this.rawOverfillFile, this.overfillingFileBlockFactor,this.firstEmptyBlockOverfillingFile);
            if (this.firstEmptyBlockOverfillingFile != -1 && nextEmpty.getNextFreeBlock() == -1) {
                this.firstEmptyBlockOverfillingFile = -1;
                return addressToReturn;
            } else {
                this.firstEmptyBlockOverfillingFile = nextEmpty.getNextFreeBlock();
                nextEmpty.setNextFreeBlock(-1);
                Block<T> nextNextEmpty = this.readBlockFromFile(this.rawOverfillFile,this.overfillingFileBlockFactor,this.firstEmptyBlockOverfillingFile);
                nextNextEmpty.setPreviousFreeBlock(-1);
                this.writeBlockToFile(this.rawOverfillFile,this.firstEmptyBlockOverfillingFile,nextNextEmpty);
                nextEmpty.setNextFreeBlock(-1);
                this.writeBlockToFile(this.rawOverfillFile,addressToReturn,nextEmpty);
                return addressToReturn;
            }
        }
    }

    private void releaseEmptyBlockInMainFile(int parAddress, Block<T> parBlock) {
        int addressOfSearched = this.getSizeOfMainFile()/this.getSizeOfMainBlock() - 1;

        if (parAddress == addressOfSearched && parAddress == 0 && parBlock.getNextLinkedBlock() == -1) {
            this.setSizeOfMainFile(this.rawMain,0);
        } else if (parAddress == addressOfSearched) {
            int addressToShort = addressOfSearched;
            boolean shorten = false;
            while (!shorten) {
                addressOfSearched--;
                Block<T> previousBlock = this.readBlockFromFile(this.rawMain,this.mainFileBlockFactor, addressOfSearched);
                if (previousBlock.getValidCount() == 0) {
                    Block<T> previousOfPrevious = null;
                    Block<T> nextOfPrevious = null;

                    // load previous linked node if exists
                    if (previousBlock.getPreviousFreeBlock() != -1) {
                        previousOfPrevious = this.readBlockFromFile(this.rawMain, this.mainFileBlockFactor, previousBlock.getPreviousFreeBlock());
                    }
                    // load next linked node if exists
                    if (previousBlock.getNextFreeBlock() != -1) {
                        nextOfPrevious = this.readBlockFromFile(this.rawMain, this.mainFileBlockFactor, previousBlock.getNextFreeBlock());
                    }

                    if (previousOfPrevious == null && nextOfPrevious != null) {
                        this.firstEmptyBlockMainFile = previousBlock.getNextFreeBlock();
                        nextOfPrevious.setPreviousFreeBlock(-1);
                    } else if (nextOfPrevious == null && previousOfPrevious != null) {
                        previousOfPrevious.setNextFreeBlock(-1);
                    } else if (previousOfPrevious == null) {
                        this.firstEmptyBlockMainFile = -1;
                    } else {
                        previousOfPrevious.setNextFreeBlock(previousBlock.getNextFreeBlock());
                        nextOfPrevious.setPreviousFreeBlock(previousBlock.getPreviousFreeBlock());
                    }

                    if (previousOfPrevious != null) {
                        this.writeBlockToFile(this.rawMain,previousBlock.getPreviousFreeBlock(),previousOfPrevious);
                    }
                    if (nextOfPrevious != null) {
                        this.writeBlockToFile(this.rawMain,previousBlock.getNextFreeBlock(),nextOfPrevious);
                    }
                    if (addressOfSearched == 0) {
                        shorten = true;
                    }
                    addressToShort--;

                } else {
                    shorten = true;
                }
            }
            this.setSizeOfMainFile(this.rawMain,addressToShort);
        } else if (this.firstEmptyBlockMainFile == -1) {
            this.firstEmptyBlockMainFile = parAddress;
            parBlock.resetCountOfValidRecords();
            parBlock.setNextLinkedBlock(-1);
            this.writeBlockToFile(this.rawMain, parAddress, parBlock);
        } else {
            int address = this.firstEmptyBlockMainFile;
            this.firstEmptyBlockMainFile = parAddress;
            parBlock.resetCountOfValidRecords();
            parBlock.setNextLinkedBlock(-1);
            parBlock.setNextFreeBlock(address);
            Block<T> nextEmpty = this.readBlockFromFile(this.rawMain,this.mainFileBlockFactor,address);
            nextEmpty.setPreviousFreeBlock(parAddress);
            this.writeBlockToFile(this.rawMain, address, nextEmpty);
            this.writeBlockToFile(this.rawMain,parAddress,parBlock);
        }
    }

    private void releaseEmptyBlockInOverfillingFile(int parAddress, Block<T> parBlock) {
        int addressOfSearched = this.getSizeOfOverfillingFile()/this.getSizeOfOverfillingBlock() - 1;

        if (parAddress == addressOfSearched && parAddress == 0) {
            this.setSizeOfOverfillingFile(this.rawOverfillFile,0);
        } else if (parAddress == addressOfSearched) {
            int addressToShort = addressOfSearched;
            boolean shorten = false;
            while (!shorten) {
                addressOfSearched--;
                Block<T> previousBlock = this.readBlockFromFile(this.rawOverfillFile,this.overfillingFileBlockFactor, addressOfSearched);
                if (previousBlock.getValidCount() == 0) {
                    Block<T> previousOfPrevious = null;
                    Block<T> nextOfPrevious = null;

                    // load previous linked node if exists
                    if (previousBlock.getPreviousFreeBlock() != -1) {
                        previousOfPrevious = this.readBlockFromFile(this.rawOverfillFile, this.overfillingFileBlockFactor, previousBlock.getPreviousFreeBlock());
                    }
                    // load next linked node if exists
                    if (previousBlock.getNextFreeBlock() != -1) {
                        nextOfPrevious = this.readBlockFromFile(this.rawOverfillFile, this.overfillingFileBlockFactor, previousBlock.getNextFreeBlock());
                    }

                    if (previousOfPrevious == null && nextOfPrevious != null) {
                        this.firstEmptyBlockOverfillingFile = previousBlock.getNextFreeBlock();
                        nextOfPrevious.setPreviousFreeBlock(-1);
                    } else if (nextOfPrevious == null && previousOfPrevious != null) {
                        previousOfPrevious.setNextFreeBlock(-1);
                    } else if (previousOfPrevious == null) {
                        this.firstEmptyBlockOverfillingFile = -1;
                    } else {
                        previousOfPrevious.setNextFreeBlock(previousBlock.getNextFreeBlock());
                        nextOfPrevious.setPreviousFreeBlock(previousBlock.getPreviousFreeBlock());
                    }

                    if (previousOfPrevious != null) {
                        this.writeBlockToFile(this.rawOverfillFile,previousBlock.getPreviousFreeBlock(),previousOfPrevious);
                    }
                    if (nextOfPrevious != null) {
                        this.writeBlockToFile(this.rawOverfillFile,previousBlock.getNextFreeBlock(),nextOfPrevious);
                    }
                    if (addressOfSearched == 0) {
                        shorten = true;
                    }
                        addressToShort--;

                } else {
                    shorten = true;
                }
            }
            this.setSizeOfOverfillingFile(this.rawOverfillFile,addressToShort);
        } else {
            if (this.firstEmptyBlockOverfillingFile == -1) {
                this.firstEmptyBlockOverfillingFile = parAddress;
                parBlock.resetCountOfValidRecords();
                parBlock.setNextLinkedBlock(-1);
                this.writeBlockToFile(this.rawOverfillFile, parAddress, parBlock);
            } else {
                int address = this.firstEmptyBlockOverfillingFile;
                this.firstEmptyBlockOverfillingFile = parAddress;
                parBlock.resetCountOfValidRecords();
                parBlock.setNextLinkedBlock(-1);
                parBlock.setNextFreeBlock(address);
                Block<T> nextEmpty = this.readBlockFromFile(this.rawOverfillFile, this.overfillingFileBlockFactor, address);
                nextEmpty.setPreviousFreeBlock(parAddress);
                this.writeBlockToFile(this.rawOverfillFile, address, nextEmpty);
                this.writeBlockToFile(this.rawOverfillFile, parAddress, parBlock);
            }
        }
    }

    /**
     * Sizes
     */
    public int getSizeOfMainFile() {
        try {
            return Math.round(this.rawMain.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getSizeOfOverfillingFile() {
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

    private void setSizeOfMainFile(RandomAccessFile parFile, int parAddressForShortInBlocks) {
        try {
            parFile.setLength(new Long(parAddressForShortInBlocks * this.getSizeOfMainBlock()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setSizeOfOverfillingFile(RandomAccessFile parFile, int parAddressForShortInBlocks) {
        try {
            parFile.setLength(new Long(parAddressForShortInBlocks * this.getSizeOfOverfillingBlock()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Return number of items inserted into tree
     */
    public int size() {
        if (this.root == null) {
            return 0;
        }

        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);

        int size = 0;

        while (!queue.isEmpty()) {
            int levelSize = queue.size();

            for (int i = 0; i < levelSize; i++) {
                Node current = queue.poll();

                if (current.isInstanceOf().equals(Node.TypeOfNode.EXTERNAL)) {
                    size += ((ExternalNode) current).getCountOnAddress();
                }

                if (current.isInstanceOf().equals(Node.TypeOfNode.INTERNAL)) {

                    if (((InternalNode) current).getLeftSon() != null) {
                        queue.offer(((InternalNode) current).getLeftSon());
                    }
                    if (((InternalNode) current).getRightSon() != null) {
                        queue.offer(((InternalNode) current).getRightSon());
                    }
                }
            }
        }
        return size;
    }

    /**
     * Returning data from the whole trie
     */
    public ArrayList<IRecord> returnAllRecords() {
        ArrayList<IRecord> dataToReturn = new ArrayList<>();
        for (int i = 0; i < this.getSizeOfMainFile()/this.getSizeOfMainBlock(); i++) {
            dataToReturn.addAll(this.returnAllDataFromBlock(i,this.mainFileBlockFactor,this.rawMain));

            Block<T> act = this.readBlockFromFile(this.rawMain, this.mainFileBlockFactor, i);
            if (act.getNextLinkedBlock() != -1) {
                while (act.getNextLinkedBlock() != -1) {
                    dataToReturn.addAll(returnAllDataFromBlock(act.getNextLinkedBlock(), this.overfillingFileBlockFactor, this.rawOverfillFile));
                    act = this.readBlockFromFile(this.rawOverfillFile, this.overfillingFileBlockFactor, act.getNextLinkedBlock());
                }
            }
        }
        return dataToReturn;
    }

    /**
     * Sequential string output
     */
    public void sequenceStringOutput() {
        System.out.println("------------------Sequential string output--------------------");
        for (int i = 0; i < this.getSizeOfMainFile()/this.getSizeOfMainBlock(); i++) {
            System.out.println("Block number " + i);
            ArrayList<IRecord> dataToReturn = new ArrayList<>();
            Block<T> block = this.readBlockFromFile(this.rawMain,this.mainFileBlockFactor,i);
            dataToReturn.addAll(block.returnValidRecordsAsArray());

            System.out.println("countOfValidRecords: " + block.getValidCount());
            System.out.println("nextFreeBlock: " + block.getNextFreeBlock());
            System.out.println("previousFreeBlock: " + block.getPreviousFreeBlock());
            System.out.println("nextLinkedBlock: " + block.getNextLinkedBlock());

            if (!dataToReturn.isEmpty()) {
                for (int j = 0; j < dataToReturn.size(); j++) {
                    System.out.println(dataToReturn.get(j));
                }
            } else {
                System.out.println("Invalid block");
            }

            System.out.println("----------------------");
            if (block.getNextLinkedBlock() != -1) {
                System.out.println("  Overfilling blocks: ");
                while (block.getNextLinkedBlock() != -1) {

                    System.out.println("----------------------");
                    System.out.println("   Block on address: " + block.getNextLinkedBlock());
                    ArrayList<IRecord> dataToReturnOver = new ArrayList<>();
                    dataToReturnOver.addAll(returnAllDataFromBlock(block.getNextLinkedBlock(), this.overfillingFileBlockFactor, this.rawOverfillFile));
                    block = this.readBlockFromFile(this.rawOverfillFile, this.overfillingFileBlockFactor, block.getNextLinkedBlock());

                    System.out.println("    countOfValidRecords: " + block.getValidCount());
                    System.out.println("    nextFreeBlock: " + block.getNextFreeBlock());
                    System.out.println("    previousFreeBlock: " + block.getPreviousFreeBlock());
                    System.out.println("    nextLinkedBlock: " + block.getNextLinkedBlock());

                    if (!dataToReturnOver.isEmpty()) {
                        for (int j = 0; j < dataToReturnOver.size(); j++) {
                            System.out.println("        " + dataToReturnOver.get(j));
                        }
                    }

                }
                System.out.println("----------------------");
            }
        }
    }

    public String seqenceStringOfTrie() {
        StringBuilder output = new StringBuilder("------------------Sequential string output--------------------\n");
        for (int i = 0; i < this.getSizeOfMainFile() / this.getSizeOfMainBlock(); i++) {
            output.append("Block number ").append(i).append("\n");
            ArrayList<IRecord> dataToReturn = new ArrayList<>();
            Block<T> block = this.readBlockFromFile(this.rawMain, this.mainFileBlockFactor, i);
            dataToReturn.addAll(block.returnValidRecordsAsArray());

            output.append("countOfValidRecords: ").append(block.getValidCount()).append("\n");
            output.append("nextFreeBlock: ").append(block.getNextFreeBlock()).append("\n");
            output.append("previousFreeBlock: ").append(block.getPreviousFreeBlock()).append("\n");
            output.append("nextLinkedBlock: ").append(block.getNextLinkedBlock()).append("\n");

            if (!dataToReturn.isEmpty()) {
                for (int j = 0; j < dataToReturn.size(); j++) {
                    output.append(dataToReturn.get(j)).append("\n");
                }
            } else {
                output.append("Invalid block\n");
            }

            output.append("----------------------\n");
            if (block.getNextLinkedBlock() != -1) {
                output.append("  Overfilling blocks: \n");
                while (block.getNextLinkedBlock() != -1) {
                    output.append("----------------------\n");
                    output.append("----- Block on address: ").append(block.getNextLinkedBlock()).append("\n");
                    ArrayList<IRecord> dataToReturnOver = new ArrayList<>();
                    dataToReturnOver.addAll(returnAllDataFromBlock(block.getNextLinkedBlock(), this.overfillingFileBlockFactor, this.rawOverfillFile));
                    block = this.readBlockFromFile(this.rawOverfillFile, this.overfillingFileBlockFactor, block.getNextLinkedBlock());

                    output.append("----- countOfValidRecords: ").append(block.getValidCount()).append("\n");
                    output.append("----- nextFreeBlock: ").append(block.getNextFreeBlock()).append("\n");
                    output.append("----- previousFreeBlock: ").append(block.getPreviousFreeBlock()).append("\n");
                    output.append("----- nextLinkedBlock: ").append(block.getNextLinkedBlock()).append("\n");

                    if (!dataToReturnOver.isEmpty()) {
                        for (int j = 0; j < dataToReturnOver.size(); j++) {
                            output.append("--------- ").append(dataToReturnOver.get(j)).append("\n");
                        }
                    }
                }
                output.append("----------------------\n");
            }
        }
        return output.toString();
    }


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

    private void reinicializeFiles(String parNameOfMainFile, String parNameOfOverfillingFile) {
        try {
            this.rawMain = new RandomAccessFile(parNameOfMainFile, "rw");
            this.rawOverfillFile = new RandomAccessFile(parNameOfOverfillingFile, "rw");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveTrie(String parPathFile) {
        this.finishWorkWithTrie();

        // pre order
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(parPathFile + "DH.txt"))) {
            writer.write(Integer.toString(this.mainFileBlockFactor));
            writer.write(";");
            writer.write(Integer.toString(this.overfillingFileBlockFactor));
            writer.write("\n");
            writer.write(Integer.toString(this.firstEmptyBlockMainFile));
            writer.write(";");
            writer.write(Integer.toString(this.firstEmptyBlockOverfillingFile));
            writer.write("\n");

            File tempfileMain = new File(this.rawMainName);
            File datafileMain = new File(parPathFile + this.rawMainName);
            tempfileMain.renameTo(datafileMain);
            writer.write(parPathFile + this.rawMainName);

            //writer.write(this.rawMainName);

            System.out.println("Ukladam hlavny subor s nazvom: " + this.rawMainName);
            writer.write(";");

            File tempfileOverifilling = new File(this.rawOverfillingName);
            File datafileOverfilling = new File(parPathFile + this.rawOverfillingName);
            tempfileOverifilling.renameTo(datafileOverfilling);
            writer.write(parPathFile + this.rawOverfillingName);

            //writer.write(this.rawOverfillingName);

            System.out.println("Ukladam preplnovak s nazvom: " + this.rawOverfillingName);
            writer.write("\n");


            if (root == null) {
                writer.close();
                return;
            }

            Stack<String> paths = new Stack<>();
            paths.push("");

            Stack<Node> stack = new Stack<>();
            stack.push(root);

            while (!stack.isEmpty()) {
                Node current = stack.pop();
                String path = paths.pop();

                // If it's an internal node, push its children and update the path
                if (current instanceof InternalNode) {
                    stack.push(((InternalNode) current).getRightSon());
                    paths.push(path + "1");

                    stack.push(((InternalNode) current).getLeftSon());
                    paths.push(path + "0");
                } else { // It's an external node

                    writer.write(path);
                    writer.write(";");
                    writer.write(Integer.toString(((ExternalNode) current).getAddress()));
                    writer.write(";");
                    writer.write(Integer.toString(((ExternalNode) current).getCountOnAddress()));
                    writer.write(";");
                    writer.write(Integer.toString(((ExternalNode) current).getCountOfLinkedBlocks()));
                    writer.write("\n");

                    System.out.println("This node has data: ");
                    System.out.println("Path: " + path);
                    System.out.println(((ExternalNode) current).getAddress());
                    System.out.println(((ExternalNode) current).getCountOnAddress());
                    System.out.println(((ExternalNode) current).getCountOfLinkedBlocks());
                }
            }
            System.out.println("Successfully saved dynamic hasning!");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadTrie(String parPathFile) {
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(parPathFile + "DH.txt"))) {
            line = reader.readLine();
            String[] blockFactors = line.split(";");
            this.mainFileBlockFactor = Integer.parseInt(blockFactors[0]);
            this.overfillingFileBlockFactor = Integer.parseInt(blockFactors[1]);
            line = reader.readLine();
            String[] emptyBlocks = line.split(";");
            this.firstEmptyBlockMainFile = Integer.parseInt(emptyBlocks[0]);
            this.firstEmptyBlockOverfillingFile = Integer.parseInt(emptyBlocks[1]);
            line = reader.readLine();
            String[] namesOfFiles = line.split(";");
            this.rawMainName = namesOfFiles[0];
            this.rawOverfillingName = namesOfFiles[1];
            this.reinicializeFiles(this.rawMainName,this.rawOverfillingName);

            this.root = new InternalNode(null);

            while ((line = reader.readLine()) != null) {
                // nacitavam uzly

                String[] info = line.split(";");
                String path = info[0];

                Node current = this.root;


                // Traverse the tree according to the path
                int index = 1;

                for (char c : path.toCharArray()) {
                    // idem do lava
                    if (c == '0') {
                        if (((InternalNode)current).getLeftSon() == null) {
                            if (index == path.length()) {
                                ((InternalNode) current).setLeftSon(new ExternalNode(current));
                            } else {
                                ((InternalNode) current).setLeftSon(new InternalNode(current));
                            }
                        }
                        current = ((InternalNode) current).getLeftSon();
                        // idem doprava
                    } else if (c == '1') {

                        if (((InternalNode)current).getRightSon() == null) {
                            if (index == path.length()) {
                                ((InternalNode) current).setRightSon(new ExternalNode(current));
                            } else {
                                ((InternalNode) current).setRightSon(new InternalNode(current));
                            }
                        }
                        current = ((InternalNode) current).getRightSon();
                    }
                    index++;
                }
                ((ExternalNode)current).setAddress(Integer.parseInt(info[1]));
                ((ExternalNode)current).setCountOnAddress(Integer.parseInt(info[2]));
                ((ExternalNode)current).setCountOfLinkedBlocks(Integer.parseInt(info[3]));

            }
            System.out.println("Successfully loaded dynamicHashing");
        } catch (IOException e) {
            e.printStackTrace();

        }
    }



}