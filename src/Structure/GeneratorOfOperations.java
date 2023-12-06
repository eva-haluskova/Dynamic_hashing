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

//        System.out.println("Vsetky data ktore som inzertovala: ");
//        for (int i = 0; i < this.insertedRecords.size(); i++) {
//            System.out.println(this.insertedRecords.get(i));
//        }

        ArrayList<IRecord> foundArray = new ArrayList<>();
        for (IRecord act : this.insertedRecords) {
            IRecord foundRecord = this.dynamicHashing.find(act);
            if (foundRecord != null) {
                foundArray.add(foundRecord);
            }
        }

//        System.out.println("Vsetky data ktore som nasla podla findu v trie: ");
//        for (int i = 0; i < foundArray.size(); i++) {
//            System.out.println(foundArray.get(i));
//        }

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

        if (this.insertedRecords.containsAll(arrayToCompare) && this.insertedRecords.size() == arrayToCompare.size()) {
            System.out.println("They are same!!");
            return true;
        } else {
            System.out.println("They are not same! :(");
            return false;
        }
    }



}
