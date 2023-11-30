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
        IRecord foundRecord = this.dynamicHashing.findInAll(parRecord);
        if (foundRecord != null) {
            return foundRecord;
        }
        return null;
    }

    public boolean findAllInserted() {

        for (int i = 0; i < this.insertedRecords.size(); i++) {
            System.out.println(this.insertedRecords.get(i));
        }

        ArrayList<IRecord> foundArray = new ArrayList<>();
        for (IRecord act : this.insertedRecords) {
            //IRecord foundRecord = this.dynamicHashing.find(act);
            IRecord foundRecord = this.dynamicHashing.findInAll(act);
            if (foundRecord != null) {
                foundArray.add(foundRecord);
            }
        }



        System.out.println("nic tu nie je :(");

        for (int i = 0; i < foundArray.size(); i++) {
            System.out.println(foundArray.get(i));
        }

        if (foundArray.containsAll(this.insertedRecords) && foundArray.size() == this.insertedRecords.size()) {
            System.out.println("Find was success!!");
            return true;
        } else {
            System.out.println("Find failed! :(");
            return false;
        }
    }

    public boolean checkIfTheyAreSme(ArrayList<IRecord> arrayToCompare) {
        ArrayList<IRecord> foundArray = new ArrayList<>();
        for (IRecord act : this.insertedRecords) {
            IRecord foundRecord = this.dynamicHashing.findInAll(act);
            if (foundRecord != null) {
                foundArray.add(foundRecord);
            }
        }
        if (foundArray.containsAll(arrayToCompare) && foundArray.size() == arrayToCompare.size()) {
            System.out.println("They are same!!");
            return true;
        } else {
            System.out.println("They are not same! :(");
            return false;
        }
    }



}
