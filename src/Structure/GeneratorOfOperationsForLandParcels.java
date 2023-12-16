package Structure;

import Data.CadastralObject;
import Data.GPS;
import Data.LandParcel;
import Data.Others.CadastralObjectGenerator;
import Structure.DynamicHashing.DynamicHashing;
import Structure.DynamicHashing.IRecord;

import java.util.ArrayList;

public class GeneratorOfOperationsForLandParcels extends GeneratorOfOperations<LandParcel> {

    private CadastralObjectGenerator generaterParciel;

    public GeneratorOfOperationsForLandParcels(DynamicHashing<LandParcel> parDynamicHashing) {
        super(parDynamicHashing);
        this.generaterParciel = new CadastralObjectGenerator();
    }

    @Override
    public void insert(int count) {
        int size = 20;
        GPS[] range = new GPS[2];
        range[0] = new GPS("N",100,"W",100);
        range[1] = new GPS("S", 100, "E", 100);

        for (int i = 0; i < count; i++) {
            IRecord record = this.generaterParciel.generateLandParcel(size,range);
            if(this.dynamicHashing.insert(record)) {
                //System.out.println("land parcel " + i + " inserted");
               // this.dynamicHashing.returnSequenceStringOutputWithOverfillingFilesAndBlocks();

//                System.out.print("zoznam zretazencyh: ");
//                ArrayList<Integer> linked = this.dynamicHashing.returnListOfEmptyBlocksMainFile();
//                for (int j = 0; j < linked.size(); j++) {
//                    System.out.print(linked.get(j) + " ");
//                }
//                System.out.println();
//
//                System.out.print("zoznam zretazencyh: ");
//                ArrayList<Integer> linkedpr = this.dynamicHashing.returnListOfEmptyBlocksOverfillingFile();
//                for (int j = 0; j < linkedpr.size(); j++) {
//                    System.out.print(linkedpr.get(j) + " ");
//                }
//                System.out.println();

                this.insertedRecords.add(record);
            }

            //System.out.println("inserted " + i + " item");
        }
        System.out.println("Insert was success!");
    }

    public void insertOne(LandParcel parcel) {
        this.insertedRecords.add(parcel);
    }
}
