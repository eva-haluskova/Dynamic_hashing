package Structure;

import Data.CadastralObject;
import Data.GPS;
import Data.LandParcel;
import Data.Others.CadastralObjectGenerator;
import Structure.DynamicHashing.DynamicHashing;
import Structure.DynamicHashing.IRecord;

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
            this.dynamicHashing.insert(record);
            this.insertedRecords.add(record);
        }
    }

    public void insertOne(LandParcel parcel) {
        this.insertedRecords.add(parcel);
    }
}
