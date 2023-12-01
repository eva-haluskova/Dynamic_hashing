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

//    public IRecord findInAll(IRecord parIRecord) {
//
//        ArrayList<IRecord> list = this.returnAllRecords();
//        for (int i = 0; i < list.size(); i++) {
//            if (list.get(i).equals(parIRecord)) {
//                return list.get(i);
//            }
//        }
//        return null;
//    }

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
                    ArrayList<IRecord> dataToInsert = new ArrayList<>();
                    dataToInsert.add(parDataToInsert);
                    while(!isInserted) {
                        dataToInsert.addAll(this.returnDataFromBlock(((ExternalNode) current).getAddress()));

                        // v danom bloku su vymazem zaznami, nakolko idem delit
                        this.deleteAllDataFromBlock(((ExternalNode) current).getAddress());

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

                        Iterator<IRecord> iterator = dataToInsert.iterator();
                        while (iterator.hasNext()) {
                            IRecord record = iterator.next();
                            if (!record.getHash().get(index)) {
                                if (this.insertRecord(record,((ExternalNode)current).getAddress())) {
                                    ((ExternalNode)current).increaseCountOnAddress();
                                    iterator.remove();
                                }
                            } else {
                                if (this.insertRecord(record,newExtNode.getAddress())) {
                                    newExtNode.increaseCountOnAddress();
                                    iterator.remove();
                                }
                            }
                        }


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

    private boolean insertRecord(IRecord parDataToInsert, int parAddressToSeek) {
        Block<T> block = new Block<>(this.blockFactor, type);
        block.fromFileToBlock(parAddressToSeek);
        if(block.insertRecord(parDataToInsert)) {
            block.writeToFile(parAddressToSeek);
            return true;
        }
        return false;
    }

    private boolean insertRecords(ArrayList<IRecord> parDataToInsert, int parAddressToSeek) {
        Block<T> block = new Block<>(this.blockFactor, type);
        block.fromFileToBlock(parAddressToSeek);
        boolean areInserted = true;
        for (int i = 0; i <parDataToInsert.size(); i++) {
            if (!block.insertRecord(parDataToInsert.get(i))) {
                areInserted = false;
            }
        }
        block.writeToFile(parAddressToSeek);
        return areInserted;
    }


    private ArrayList<IRecord> returnDataFromBlock(int parAddressToSeek) {
        Block<T> block = new Block<>(this.blockFactor, type);
        block.fromFileToBlock(parAddressToSeek);
        return block.returnValidRecordsAsArray();
    }

    private void deleteAllDataFromBlock(int parAddressToSeek) {
        Block<T> block = new Block<>(this.blockFactor, type);
        block.fromFileToBlock(parAddressToSeek);
        block.resetCountOfValidRecords();
        block.writeToFile(parAddressToSeek);
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

    public ArrayList<IRecord> returnAllRecords() {
        ArrayList<IRecord> dataToReturn = new ArrayList<>();
        for (int i = 0; i < this.nextEmptyBlock; i++) {
            dataToReturn.addAll(returnDataFromBlock(i));
        }
        return dataToReturn;
    }
     public void returnSequenceStringOutput() {
         for (int i = 0; i < this.nextEmptyBlock; i++) {
             System.out.println("Blok cislo " + i);
             ArrayList<IRecord> dataToReturn = new ArrayList<>();
             dataToReturn.addAll(returnDataFromBlock(i));
             if (!dataToReturn.isEmpty()) {
                 for (int j = 0; j < dataToReturn.size(); j++) {
                     System.out.println(dataToReturn.get(j));
                 }
             } else {
                 System.out.println("Neplatny blok");
             }
         }
     }

}
