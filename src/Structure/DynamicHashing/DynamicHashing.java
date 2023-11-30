package Structure.DynamicHashing;

import Data.LandParcel;
import Structure.DynamicHashing.Nodes.ExternalNode;
import Structure.DynamicHashing.Nodes.InternalNode;
import Structure.DynamicHashing.Nodes.Node;
import Structure.QuadTree.Coordinates;
import Structure.QuadTree.Data;
import Structure.QuadTree.QuadTreeNode;

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

    public DynamicHashing(int parBlockFactor, Class<T> type) {

        this.nextEmptyBlock = 0;
        this.blockFactor = parBlockFactor;
        this.type = type;
        try {
            RandomAccessFile file = new RandomAccessFile("file.bin", "rw");
            file.setLength(0);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IRecord find(IRecord parIRecord) {

        BitSet traverBitset = parIRecord.getHash();
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
                record = this.findRecord(parIRecord,((ExternalNode) current).getAddress());
                foundedNode = true;
            }
        }
        return record;
    }

    public IRecord findInAll(IRecord parIRecord) {
        BitSet traverBitset = parIRecord.getHash();
        IRecord record = null;

        ArrayList<IRecord> list = this.returnAllRecords();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(parIRecord)) {
                return list.get(i);
            }
        }
        return null;
    }

    public IRecord findRecord(IRecord parDataToFind, int parAddressToSeek) {
        Block<T> block = new Block<>(this.blockFactor, type);
        block.fromFileToBlock(parAddressToSeek);

        IRecord recordToReturn = block.findRecord(parDataToFind);
        if (recordToReturn != null) {
            return recordToReturn;
        }
        return null;
    }

    public boolean insert(IRecord parDataToInsert) {

        if (this.root == null) {
            ExternalNode node = new ExternalNode(null);
            this.root = node;
            ((ExternalNode) this.root).setAddress(this.nextEmptyBlock);
            this.nextEmptyBlock++;
            if(this.insertRecord(parDataToInsert,((ExternalNode) this.root).getAddress())) {
                ((ExternalNode) this.root).increaseCountOnAddress();
                return true;
            }
        }

        if (this.findInAll(parDataToInsert) != null) {
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

                    if (insertRecord(parDataToInsert,((ExternalNode) current).getAddress())) {
                        ((ExternalNode) current).increaseCountOnAddress();
                        return true;
                    }
                // ak sa tam zmesti dalsie dato, tak ho tam vlozim
                } else if (((ExternalNode) current).getCountOnAddress() < this.blockFactor) {

                    if (insertRecord(parDataToInsert,((ExternalNode) current).getAddress())) {
                        ((ExternalNode) current).increaseCountOnAddress();
                        return true;
                    }
                // ak sa nezmsti dalsie dato, musis rozbijat strom kym sa ti to nepodari:
                } else {
                    boolean isInserted = false;
                    while(!isInserted) {
                        ArrayList<IRecord> dataToInsert = this.returnDataFromBlock(((ExternalNode) current).getAddress());
                        dataToInsert.add(parDataToInsert);
                        // nezabudni na akutalne vkladane dato

                        // vytvorim si nove nody. Na miesto stareho currena dem dat novy interny ktory ma dvoch synov externych.
                        InternalNode newIntNode = new InternalNode(current.getParent());
                        ExternalNode newExtNode = new ExternalNode(newIntNode);
                        ExternalNode newExtNodeTwo = new ExternalNode(newIntNode);

                        // novemu internemu nastavim synov nove externe
                        newIntNode.setLeftSon(newExtNode);
                        newIntNode.setRightSon(newExtNodeTwo);

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

                        newExtNode.setAddress(this.nextEmptyBlock);
                        this.nextEmptyBlock++;
                        newExtNodeTwo.setAddress(this.nextEmptyBlock);
                        this.nextEmptyBlock++;

                        Iterator<IRecord> iterator = dataToInsert.iterator();
                        while (iterator.hasNext()) {
                            IRecord record = iterator.next();
                            // iterator.remove()
                            if (!record.getHash().get(index)) {
                                if (this.insertRecord(record,newExtNode.getAddress())) {
                                    newExtNode.increaseCountOnAddress();
                                    iterator.remove();
                                }
                            } else {
                                if (this.insertRecord(record,newExtNodeTwo.getAddress())) {
                                    newExtNodeTwo.increaseCountOnAddress();
                                    iterator.remove();
                                }
                            }
                        }

                        index++;

                        //  toto by malo byt v e;se if, pretoze ak ti tu vynde 0 znamneta to ze hento iso nebudu nuly
                        // eva uz si to literally osetrila
                        if (!dataToInsert.isEmpty()) {
                            if (newExtNode.getCountOnAddress() == 0) {
                                newExtNode.setAddress(-1);
                                //this.nextEmptyBlock--;
                                current = newExtNodeTwo;
                            } else {
                                // TODO tu musis dorobit metodku taku, teda prekopat to, ze mas iba vytvoreny blok aa do neho vkldas data, nie aj zapiseuje, zapises to az potom naraz
                            }
                            if (newExtNodeTwo.getCountOnAddress() == 0) {
                                newExtNodeTwo.setAddress(-1);
                                this.nextEmptyBlock--;
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

    private boolean insertRecord(IRecord parDataToInsert, int parAddressToSeek) {
        Block<T> block = new Block<>(this.blockFactor, type);
        block.fromFileToBlock(parAddressToSeek);
        if(block.insertRecord(parDataToInsert)) {
            block.writeToFile(parAddressToSeek);
            return true;
        }
        return false;
    }


    private ArrayList<IRecord> returnDataFromBlock(int parAddressToSeek) {
        Block<T> block = new Block<>(this.blockFactor, type);
        block.fromFileToBlock(parAddressToSeek);
        return block.returnValidRecordsAsArray();
    }

    public ArrayList<IRecord> returnAllRecords() {
        ArrayList<IRecord> dataToReturn = new ArrayList<>();
        for (int i = 0; i < this.nextEmptyBlock; i++) {
            dataToReturn.addAll(returnDataFromBlock(i));
        }
        return dataToReturn;
    }

}




//     if (this.root == null) {
//             ExternalNode node = new ExternalNode(null);
//             this.root = node;
//             ((ExternalNode) this.root).setAddress(this.nextEmptyBlock);
//             this.nextEmptyBlock++;
//             return true;
//             }
//
//             Node current = this.root;
//
//
//
//             if (this.root.isInstanceOf() == Node.TypeOfNode.EXTERNAL) {
//             if (((ExternalNode) this.root).getCountOnAddress() < this.blockFactor) {
//        int addressToSeek = ((ExternalNode) this.root).getAddress();
//        Block<T> block = new Block<>(this.blockFactor, type);
//        block.fromFileToBlock(addressToSeek);
//        block.insertRecord(parDataToInsert);
//        ((ExternalNode) this.root).increaseCountOnAddress();
//        return true;
//        }
//        }