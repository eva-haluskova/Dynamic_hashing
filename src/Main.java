import Data.LandParcel;
import Structure.DynamicHashing.DynamicHashing;
import Structure.DynamicHashing.IRecord;
import Structure.GeneratorOfOperationsForLandParcels;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        DynamicHashing<LandParcel>  dhp = new DynamicHashing<>(2,2, LandParcel.class,"mainFile.bin","overfillingFile.bin");

        GeneratorOfOperationsForLandParcels generatorOfOperations = new GeneratorOfOperationsForLandParcels(dhp);

        // ---------- generovanie INSERT -------------
        generatorOfOperations.insert(10);

        // ---------- generovanie FIND --------------
        generatorOfOperations.findAllInserted(); // naslo mi kazdy jeden prvnok

        ArrayList<IRecord> inserted = dhp.returnAllRecords();
        generatorOfOperations.checkIfTheyAreSame(inserted); // prvky vybrate sekvence zo stromu su zhodne s vlozenymi

        // ----------- SEKVENCNY VYPIS
        System.out.println("--------- SEKVENCNY VYPIS -------------");
        dhp.returnSequenceStringOutputWithOverfillingFiles();
        System.out.println("--------- KONIEC VYPISU-----------------");
        System.out.println("velkost preplnovaku: " + dhp.getSizeOfOverfillingFile());
        System.out.println("velkost hlavenho suboru: " + dhp.getSizeOfMainFile());
        System.out.println("pocet prvkov v strome: " + dhp.size());

        // --------- generovanie DELETE ------------------
        generatorOfOperations.delete();

        // ----------- SEKVENCNY VYPIS
        System.out.println("--------- SEKVENCNY VYPIS -------------");
        dhp.returnSequenceStringOutputWithOverfillingFiles();
        System.out.println("--------- KONIEC VYPISU-----------------");
        System.out.println("velkost preplnovaku: " + dhp.getSizeOfOverfillingFile());
        System.out.println("velkost hlavenho suboru: " + dhp.getSizeOfMainFile());
        System.out.println("pocet prvkov v strome: " + dhp.size());

        System.out.println("Zatiaľ to funguje ^-^");
    }

}
