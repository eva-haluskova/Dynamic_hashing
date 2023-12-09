import Data.LandParcel;
import Structure.DynamicHashing.DynamicHashing;
import Structure.DynamicHashing.IRecord;
import Structure.GeneratorOfOperationsForLandParcels;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        DynamicHashing<LandParcel>  dhp = new DynamicHashing<>(2,8, LandParcel.class,"mainFile.bin","overfillingFile.bin");

        GeneratorOfOperationsForLandParcels generatorOfOperations = new GeneratorOfOperationsForLandParcels(dhp);

        // ---------- generovanie INSERT -------------
        generatorOfOperations.insert(10);

        // ---------- generovanie FIND --------------
        generatorOfOperations.findAllInserted(); // naslo mi kazdy jeden prvnok

        ArrayList<IRecord> inserted = dhp.returnAllRecords();
        generatorOfOperations.checkIfTheyAreSame(inserted); // prvky vybrate sekvence zo stromu su zhodne s vlozenymi

        // ----------- SEKVENCNY VYPIS
        System.out.println("--------- SEKVENCNY VYPIS -------------");
        //dhp.returnSequenceStringOutputWithOverfillingFiles();
        dhp.sequenceStringOutput();
        System.out.println("--------- KONIEC VYPISU-----------------");
        System.out.println("velkost preplnovaku: " + dhp.getSizeOfOverfillingFile());
        System.out.println("velkost hlavenho suboru: " + dhp.getSizeOfMainFile());
        System.out.println("pocet prvkov v strome: " + dhp.size());

        // --------- generovanie DELETE ------------------
        generatorOfOperations.delete();

        // ----------- SEKVENCNY VYPIS
        System.out.println("--------- SEKVENCNY VYPIS -------------");
        //dhp.returnSequenceStringOutputWithOverfillingFiles();
        //dhp.returnSequenceStringOutputWithOverfillingFilesAndBlocks();

        System.out.println("--------- KONIEC VYPISU-----------------");
        System.out.println("velkost preplnovaku: " + dhp.getSizeOfOverfillingFile());
        System.out.println("velkost hlavenho suboru: " + dhp.getSizeOfMainFile());
        System.out.println("pocet prvkov v strome: " + dhp.size());

        System.out.println("Zatiaľ to funguje ^-^");

    }

}


// todo pozri sa ci optimalne posieals veci do metod a tak. lebo mam tusaka ze nie. hlavne by to chcelo spravit objektovo

// TODO musis potestovat pre rozne seedy
// TODO vies ze nemas poriesene vymazavanie adresy od currenta hahahahaha :D - moze vobec nastat? HAAA?
// TODO generovanie novych objektv,,vloz jeden - vloz si to aj do dynamichashing
// TODO a ttiez to generovanie, vloaz iba ak vysledok z insertdynamickhashing je true....ale pi preplnovaku uz asi ani ne...ale ba, keby si chcel vkladat rovnake daticka
// TODO pracu s filom si daj to dynamickeho heshovania
// TODO vlozis do zoznamu vlozenych iba to dato co vratilo true ze je vlozene do trie
// TODO to s tym currentom si potrebujes okseftit
// TODO opravit generator tak aby pir mazanie isli aj prvky prec
// todo delete na boolen
// todo mohla by byt metodka delete data and release blcok

// todo private metody aby boli private
// todo think of if you need in all this methods return value (boolean)

// todo traverz zvlast
// todo v inserte ked vytvaras dorhy node tak si tam envytvaraj pamat...chapes


// todo release v hlavnej pamati - validcount, adresy


