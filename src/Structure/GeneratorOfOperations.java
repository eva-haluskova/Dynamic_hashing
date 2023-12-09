package Structure;

import Data.LandParcel;
import Structure.DynamicHashing.DynamicHashing;
import Structure.DynamicHashing.IRecord;

import javax.imageio.event.IIOReadProgressListener;
import java.util.ArrayList;

public abstract class GeneratorOfOperations<T extends IRecord> {

    ArrayList<IRecord> insertedRecords = new ArrayList<>();
    DynamicHashing<T> dynamicHashing;

    public GeneratorOfOperations(DynamicHashing<T> parDynamicHashing) {
        this.dynamicHashing = parDynamicHashing;
    }

    public abstract void insert(int count);

    public IRecord find(IRecord parRecord) {
        IRecord foundRecord = this.dynamicHashing.find(parRecord);
        if (foundRecord != null) {
            System.out.println("data finf " + foundRecord);
            return foundRecord;
        }
        return null;
    }

    public ArrayList<IRecord> returnInsertedRecored() {
        return this.insertedRecords;
    }

    public boolean findAllInserted() {

        System.out.println("Vsetky data ktore som inzertovala: ");
        for (int i = 0; i < this.insertedRecords.size(); i++) {
            System.out.println(this.insertedRecords.get(i));
        }

        ArrayList<IRecord> foundArray = new ArrayList<>();
        for (IRecord act : this.insertedRecords) {
            IRecord foundRecord = this.dynamicHashing.find(act);
            if (foundRecord != null) {
                foundArray.add(foundRecord);
            }
        }

        System.out.println("Vsetky data ktore som nasla podla findu v trie: ");
        for (int i = 0; i < foundArray.size(); i++) {
            System.out.println(foundArray.get(i));
        }

        if (foundArray.containsAll(this.insertedRecords) && foundArray.size() == this.insertedRecords.size()) {
            System.out.println("Find of all items was success!!");
            return true;
        } else {
            System.out.println("Find failed! :(");
            return false;
        }
    }

    public void delete() {
        for (int i = 0; i < this.insertedRecords.size(); i++) {
            this.dynamicHashing.delete(this.insertedRecords.get(i));
          //  dynamicHashing.returnSequenceStringOutputWithOverfillingFilesAndBlocks();
//            System.out.print("zoznam zretazencyh: ");
//            ArrayList<Integer> linked = this.dynamicHashing.returnListOfEmptyBlocksMainFile();
//            for (int j = 0; j < linked.size(); j++) {
//                System.out.print(linked.get(j) + " ");
//            }
//            System.out.println();
//
//            System.out.print("zoznam zretazencyh: ");
//            ArrayList<Integer> linkedpr = this.dynamicHashing.returnListOfEmptyBlocksOverfillingFile();
//            for (int j = 0; j < linkedpr.size(); j++) {
//                System.out.print(linkedpr.get(j) + " ");
//            }
//            System.out.println();
        }
        if (this.dynamicHashing.size() == 0) {
            System.out.println("delete was success");
        }
    }

    public boolean checkIfTheyAreSme(ArrayList<IRecord> arrayToCompare) {
        ArrayList<IRecord> foundArray = new ArrayList<>();
        for (IRecord act : this.insertedRecords) {
            IRecord foundRecord = this.dynamicHashing.find(act);
            if (foundRecord != null) {
                foundArray.add(foundRecord);
            }
        }

        System.out.println("Vrati setky data z trie");
        for (int i = 0; i < arrayToCompare.size(); i++) {
            System.out.println(arrayToCompare.get(i));
        };

        if (foundArray.containsAll(arrayToCompare) && foundArray.size() == arrayToCompare.size()) {
            System.out.println("They are same!!");
            return true;
        } else {
            System.out.println("They are not same! :(");
            return false;
        }
    }

    public boolean checkIfTheyAreSame(ArrayList<IRecord> arrayToCompare) {

        System.out.println("Vsetky data ktore som inzertovala: ");
        for (int i = 0; i < this.insertedRecords.size(); i++) {
            System.out.println(this.insertedRecords.get(i));
        }

        System.out.println("return all data form trie");
        for (int i = 0; i < arrayToCompare.size(); i++) {
            System.out.println(arrayToCompare.get(i));
        }




        if (this.insertedRecords.containsAll(arrayToCompare) && this.insertedRecords.size() == arrayToCompare.size()) {
            System.out.println("They are same!!");
            return true;
        } else {
            System.out.println("They are not same! :(");
            return false;
        }
    }

    public int sizeOfTrie() {
        return this.insertedRecords.size();
    }



}
