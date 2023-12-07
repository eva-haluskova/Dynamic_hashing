package Structure.DynamicHashing;

import Structure.DynamicHashing.Nodes.ExternalNode;
import Structure.DynamicHashing.Nodes.InternalNode;
import Structure.DynamicHashing.Nodes.Node;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

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
                    record = this.findRecord(parRecord, this.mainFileBlockFactor, ((ExternalNode) current).getAddress(), this.rawMain);
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

            if (traverBitset.length() == index) {
                if (current.isInstanceOf().equals(Node.TypeOfNode.EXTERNAL)) {
                    if (((ExternalNode) current).getCountOnAddress() >= this.mainFileBlockFactor) {
                        //System.out.println("idem vkladat do preplnovaku v bloku " + ((ExternalNode) current).getAddress());
                        if (this.insertIntoOverfillingFile(((ExternalNode) current), parDataToInsert)) {
                            //System.out.println("vlozilo sa tam dato " + parDataToInsert);
                            return true;
                        }
                        return false;
                    }
                }
            }


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
                        //System.out.println("Dato " + parDataToInsert + " sa vlozilo na " + index + " urovni");
                        return true;
                    }


                // ak sa tam zmesti dalsie dato, tak ho tam vlozim
                } else if (((ExternalNode) current).getCountOnAddress() < this.mainFileBlockFactor) {

                    if (insertRecordIntoBlock(parDataToInsert, this.mainFileBlockFactor, ((ExternalNode) current).getAddress(), this.rawMain)) {
                        ((ExternalNode) current).increaseCountOnAddress();
                        //System.out.println("Dato " + parDataToInsert + " sa vlozilo na " + index + " urovni");
                        return true;
                    }
                // ak sa nezmsti dalsie dato, musis rozbijat strom kym sa ti to nepodari:
                } else {
                    boolean isInserted = false;
                    ArrayList<IRecord> dataToInsert = new ArrayList<>();
                    dataToInsert.add(parDataToInsert);
                    while(!isInserted) {

                        // keby to daz nizsie, az vtedy si to rozbijes, teraz mas urcite jedno dato ktore sa ti nezmesilo do currenta pertoze do currena si v
                        // uz v predoslej iteracii naplnila ostatnymi datami
                        if (traverBitset.length() == index) {
                            if (current.isInstanceOf().equals(Node.TypeOfNode.EXTERNAL)) {
                                if (((ExternalNode) current).getCountOnAddress() >= this.mainFileBlockFactor) {
                                    //System.out.println("idem vkladat do preplnovaku v bloku " + ((ExternalNode) current).getAddress());
                                    if (this.insertIntoOverfillingFile(((ExternalNode) current), dataToInsert.get(0))) {
                                        //System.out.println("vlozilo sa tam dato " + dataToInsert.get(0));
                                        return true;
                                    }
                                    return false;
                                }
                            }
                        }

                        dataToInsert.addAll(this.returnAllDataFromBlock(((ExternalNode) current).getAddress(), this.mainFileBlockFactor, this.rawMain));
                        // v danom bloku su vymazem zaznami, nakolko idem delit
                        this.deleteAllDataFromBlock(((ExternalNode) current).getAddress(), this.mainFileBlockFactor, this.rawMain);
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
                            blockCur = this.readBlockFromFile(this.rawMain, this.mainFileBlockFactor,((ExternalNode)current).getAddress());
                        } else {
                            blockCur = new Block<>(this.mainFileBlockFactor, type);
                        }

                        Block<T> blockNew = new Block<>(this.mainFileBlockFactor, type);

                        Iterator<IRecord> iterator = dataToInsert.iterator();
                        while (iterator.hasNext()) {
                            IRecord record = iterator.next();
                            if (!record.getHash().get(index)) {
                                if (blockCur.insertRecord(record)) {
                                    ((ExternalNode)current).increaseCountOnAddress();
                                   // System.out.println("Dato " + record + " sa vlozilo na " + index + " urovni");
                                     iterator.remove();
                                }
                            } else {
                                if (blockNew.insertRecord(record)) {
                                    newExtNode.increaseCountOnAddress();
                                  //  System.out.println("Dato " + record + " sa vlozilo na " + index + " urovni");
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

    public boolean insertIntoOverfillingFile(ExternalNode parNode, IRecord parRecordToInsert) {
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
                //System.out.println("asi som sa tu zasekla");
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

    public void delete(IRecord parRecord) {
        BitSet traverBitset = parRecord.getHash();

        if (this.find(parRecord) == null) {
            System.out.println("Data isn't possible to remove, because they don't exist.");
        } else {

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
                    this.deleteRecord(parRecord,this.mainFileBlockFactor,((ExternalNode) current).getAddress(),this.rawMain);
                    ((ExternalNode) current).decreaseCountOnAddress();

                    // reducation
                    if (this.tryToReduce(((ExternalNode) current))) {
                        this.reduce(((ExternalNode) current));
                    }

                    // merge
                    int countInBrother = -1;
                    if (((InternalNode) current.getParent()).getLeftSon().equals(current)) {
                        if (((ExternalNode)((InternalNode) current.getParent()).getRightSon()).getCountOfLinkedBlocks() == -1) {
                            countInBrother = ((ExternalNode)((InternalNode) current.getParent()).getRightSon()).getCountOnAddress();
                        }
                    } else {
                        if (((ExternalNode)((InternalNode) current.getParent()).getLeftSon()).getCountOfLinkedBlocks() == -1) {
                            countInBrother = ((ExternalNode)((InternalNode) current.getParent()).getLeftSon()).getCountOnAddress();
                        }
                    }

                    int countInMe = -1;
                    if ( ((ExternalNode) current).getCountOfLinkedBlocks() == -1 ) {
                        countInMe = ((ExternalNode) current).getCountOnAddress();
                    }

                    if (countInMe != -1 && countInBrother != -1 &&
                            countInMe + countInBrother < this.mainFileBlockFactor) {

                    }





                    foundedNode = true;
                }
            }
        }
    }

    /*
    striasanie - pri detele zavolam metodu tryToReduce - ktorá vyhodnoti či v danom node - kedze v delete
    traverzujes MAS ten node, a pozrieš sa či countOnAddress/ BlockFactor je mensi ako pocetZretazenych.
    Ak ano, sprvais strasanis. len si vyberes vserky data z daneho bloku - aj so zretazením , pridás si tam
    taku metodku, no a  potom bud - do povodnych blokov pozapisujs data postupne alebo vytvoris nove bloky
    a do nich...len musíš pamätať na tie uvolnene bloky...aby boli uvolnene...ano. takro. Ubolnis si všekty
    tie bloky a potom do novych - ktore ti vrátia adresy tiero staré - pozapisuješ nove data.
     */

    public boolean tryToReduce(ExternalNode parNode) {
        return ((parNode.getCountOfLinkedBlocks() - 1) * this.overfillingFileBlockFactor +
                this.mainFileBlockFactor) >= parNode.getCountOnAddress();
    }

    public void reduce(ExternalNode parNode) {
        ArrayList<IRecord> dataToInsert = this.returnAllDataFromLinkedBlocks(parNode.getAddress());
        this.removeLinkedBlocks(parNode.getAddress());
        parNode.setCountOfLinkedBlocks(0);
        parNode.setCountOnAddress(0);
        for (int i = 0; i < dataToInsert.size(); i++) {
            this.insert(dataToInsert.get(i));
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

    public void deleteRecordFromBlock(IRecord parDataToDelete, int parBlockFactor, int parAddressToSeek, RandomAccessFile parFile) {
        Block<T> block = this.readBlockFromFile(parFile, parBlockFactor, parAddressToSeek);
        block.deleteRecord(parDataToDelete);
        this.writeBlockToFile(parFile,parAddressToSeek,block);
    }

    public void deleteRecord(IRecord parDataToDelete, int parBlockFactor, int parAddressToSeek, RandomAccessFile parFile) {
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
     * method used when reduce...salalal TODO
     */
    public ArrayList<IRecord> returnAllDataFromLinkedBlocks(int parAddressToSeek) {
        ArrayList<IRecord> listOfData = new ArrayList<>();
        listOfData.addAll(returnAllDataFromBlock(parAddressToSeek, this.mainFileBlockFactor, this.rawMain));

        Block<T> block = this.readBlockFromFile(this.rawMain, this.mainFileBlockFactor, parAddressToSeek);
        while (block.getNextLinkedBlock() != -1) {
            listOfData.addAll(returnAllDataFromBlock(block.getNextLinkedBlock(),this.overfillingFileBlockFactor,this.rawOverfillFile));
            block = this.readBlockFromFile(this.rawOverfillFile, this.overfillingFileBlockFactor,block.getNextLinkedBlock());
        }
        return listOfData;
    }

    /**
     * method deletes linked block in overfilling file and remove records in block in main file.
     */
    public void removeLinkedBlocks(int parAddress) {
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

    private ArrayList<IRecord> returnAllDataFromBlock(int parAddressToSeek, int parBlockFactor, RandomAccessFile parFile) {
        Block<T> block = this.readBlockFromFile(parFile, parBlockFactor, parAddressToSeek);
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

    public Block<T> readBlockFromFile(RandomAccessFile parFile, int parBlockFactor, int parAddress) {
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

    // pay attention for atriubt nextLinkedBlock
    public int getAddressOfNextEmptyBlockInOverfillingFile() {
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

    public void releaseEmptyBlockInMainFile(int parAddress, Block<T> parBlock) {
        int addressOfSearched = this.getSizeOfMainFile()/this.getSizeOfMainBlock() - 1;

        if (parAddress == addressOfSearched && parAddress == 0 && parBlock.getNextLinkedBlock() == -1) {
            this.setSizeOfFile(this.rawMain,0);
        } else if (parAddress == addressOfSearched) {
            int addressToShort = addressOfSearched; // adresa kde sa skrati subor - ak je posledny prazdny tak skratim adresu po tiadlo - zatial.
            boolean shorten = false;
            while (!shorten) {
                addressOfSearched--;
                Block<T> previousBlock = this.readBlockFromFile(this.rawMain,this.mainFileBlockFactor, addressOfSearched);
                if (previousBlock.getValidCount() == 0) {
                    Block<T> previousOfPrevious = null;
                    Block<T> nextOfPrevious = null;

                    // load previous linked node if exists
                    if (previousBlock.getPreviousFreeBlock() != -1) { //ak je minus jedna znamena ze je prvy v zretazeni
                        previousOfPrevious = this.readBlockFromFile(this.rawMain, this.mainFileBlockFactor, previousBlock.getPreviousFreeBlock());
                    }
                    // load next linked node if exists
                    if (previousBlock.getNextFreeBlock() != -1) { // ak je minus jedna znamena ze je posledny v zretazeni
                        nextOfPrevious = this.readBlockFromFile(this.rawMain, this.mainFileBlockFactor, previousBlock.getNextFreeBlock());
                    }

                    if (previousOfPrevious == null && nextOfPrevious != null) {
                        this.firstEmptyBlockMainFile = previousBlock.getNextFreeBlock();
                        nextOfPrevious.setPreviousFreeBlock(-1);
                    } else if (nextOfPrevious == null && previousOfPrevious != null) {
                        previousOfPrevious.setNextFreeBlock(-1);
                    } else if (previousOfPrevious == null && nextOfPrevious == null) {
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
            this.setSizeOfFile(this.rawMain,addressToShort);
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

    // pay attention for atrubte zretazenie
    public void releaseEmptyBlockInOverfillingFile(int parAddress, Block<T> parBlock) {
        int addressOfSearched = this.getSizeOfOverfillingFile()/this.getSizeOfOverfillingBlock() - 1;

        if (parAddress == addressOfSearched && parAddress == 0) {
            this.setSizeOfFile(this.rawOverfillFile,0);
        } else if (parAddress == addressOfSearched) {
            int addressToShort = addressOfSearched; // adresa kde sa skrati subor - ak je posledny prazdny tak skratim adresu po tiadlo - zatial.
            boolean shorten = false;
            while (!shorten) {
                addressOfSearched--;
                Block<T> previousBlock = this.readBlockFromFile(this.rawOverfillFile,this.overfillingFileBlockFactor, addressOfSearched);
                if (previousBlock.getValidCount() == 0) {
                    Block<T> previousOfPrevious = null;
                    Block<T> nextOfPrevious = null;

                    // load previous linked node if exists
                    if (previousBlock.getPreviousFreeBlock() != -1) { //ak je minus jedna znamena ze je prvy v zretazeni
                        previousOfPrevious = this.readBlockFromFile(this.rawOverfillFile, this.overfillingFileBlockFactor, previousBlock.getPreviousFreeBlock());
                    }
                    // load next linked node if exists
                    if (previousBlock.getNextFreeBlock() != -1) { // ak je minus jedna znamena ze je posledny v zretazeni
                        nextOfPrevious = this.readBlockFromFile(this.rawOverfillFile, this.overfillingFileBlockFactor, previousBlock.getNextFreeBlock());
                    }

                    if (previousOfPrevious == null && nextOfPrevious != null) {
                        this.firstEmptyBlockOverfillingFile = previousBlock.getNextFreeBlock();
                        nextOfPrevious.setPreviousFreeBlock(-1);
                    } else if (nextOfPrevious == null && previousOfPrevious != null) {
                        previousOfPrevious.setNextFreeBlock(-1);
                    } else if (previousOfPrevious == null && nextOfPrevious == null) {
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
            this.setSizeOfFile(this.rawOverfillFile,addressToShort);
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
     * if count of item into two brother nodes are lower than block factor - merge them into one node
     */
    /*
    na zaver zavoláš metodu na merg - kukneš sa pri delete do seba - či počet zretazenych blokov je 0
    (pozri si či toti máš dobre pošefene)  a či počet zretazenych brata je tiež nula a či dokopy súčet
    sa je menší ako blockfactor. Ak je - zobereš data z jedného a pridás do druhého bez žiadnych okolkov.
    Bereš toho ktorý má menšie adresu :D druhu adresu uvolniš. Robíš cyklicky nahor
     */
    public void merge() {

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

    private void setSizeOfFile(RandomAccessFile parFile,int parAddressForShortInBlocks) {
        try {
            parFile.setLength(new Long(parAddressForShortInBlocks * this.getSizeOfOverfillingBlock()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
//    public ArrayList<IRecord> returnAllRecords(RandomAccessFile parFile, int parFileSize, int parBlockSize) {
//        ArrayList<IRecord> dataToReturn = new ArrayList<>();
//        for (int i = 0; i < parFileSize/parBlockSize; i++) {
//            dataToReturn.addAll(this.returnAllDataFromBlock(i,parFile));
//        }
//        return dataToReturn;
//    }

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
     * print on console containing block sequential
     */
//    public void returnSequenceStringOutput(RandomAccessFile parFile, int parFileSize, int parBlockSize) {
//        for (int i = 0; i < parFileSize/parBlockSize; i++) {
//            System.out.println("Block number " + i);
//            ArrayList<IRecord> dataToReturn = new ArrayList<>();
//            dataToReturn.addAll(returnAllDataFromBlock(i, parFile));
//            if (!dataToReturn.isEmpty()) {
//                for (int j = 0; j < dataToReturn.size(); j++) {
//                    System.out.println(dataToReturn.get(j));
//                }
//            } else {
//                System.out.println("Invalid block");
//            }
//        }
//    }
    public void returnSequenceStringOutput() {
        for (int i = 0; i < this.getSizeOfMainFile()/this.getSizeOfMainBlock(); i++) {
            System.out.println("Block number " + i);
            ArrayList<IRecord> dataToReturn = new ArrayList<>();
            dataToReturn.addAll(returnAllDataFromBlock(i, this.mainFileBlockFactor, this.rawMain));
            if (!dataToReturn.isEmpty()) {
                for (int j = 0; j < dataToReturn.size(); j++) {
                    System.out.println(dataToReturn.get(j));
                }
            } else {
                System.out.println("Invalid block");
            }
        }
    }

    public void returnSequenceStringOutputWithOverfillingFiles() {
        for (int i = 0; i < this.getSizeOfMainFile()/this.getSizeOfMainBlock(); i++) {
            System.out.println("Block number " + i);
            ArrayList<IRecord> dataToReturn = new ArrayList<>();
            dataToReturn.addAll(returnAllDataFromBlock(i, this.mainFileBlockFactor, this.rawMain));
            if (!dataToReturn.isEmpty()) {
                for (int j = 0; j < dataToReturn.size(); j++) {
                    System.out.println(dataToReturn.get(j));
                }
            } else {
                System.out.println("Invalid block");
            }
            Block<T> act = this.readBlockFromFile(this.rawMain, this.mainFileBlockFactor, i);
            if (act.getNextLinkedBlock() != -1) {
                System.out.println("  Preplnovak: ");
                while (act.getNextLinkedBlock() != -1) {
                    System.out.println("   Block na adrese: " + act.getNextLinkedBlock());
                    ArrayList<IRecord> dataToReturnOver = new ArrayList<>();
                    dataToReturnOver.addAll(returnAllDataFromBlock(act.getNextLinkedBlock(), this.overfillingFileBlockFactor, this.rawOverfillFile));
                    if (!dataToReturnOver.isEmpty()) {
                        for (int j = 0; j < dataToReturnOver.size(); j++) {
                            System.out.println("        " + dataToReturnOver.get(j));
                        }
                    }

                    act = this.readBlockFromFile(this.rawOverfillFile, this.overfillingFileBlockFactor, act.getNextLinkedBlock());
                }
            }
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



}
