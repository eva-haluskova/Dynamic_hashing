import Data.LandParcel;
import Data.GPS;
import GUI.Model;
import Structure.DynamicHashing.DynamicHashing;
import Structure.DynamicHashing.IRecord;
import Structure.GeneratorOfOperationsForLandParcels;
import Structure.QuadTree.Data;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("Zatiaľ to funguje ^-^");
    }

}




// todo oestrit dlzku hashu, aj to z ktoreh strany tan hash beres
// todo pozri sa ci optimalne posieals veci do metod a tak. lebo mam tusaka ze nie. hlavne by to chcelo spravit objektovo
// TODO generovanie novych objektv,,vloz jeden - vloz si to aj do dynamichashing
// TODO a ttiez to generovanie, vloaz iba ak vysledok z insertdynamickhashing je true....ale pi preplnovaku uz asi ani ne...ale ba, keby si chcel vkladat rovnake daticka
// TODO vlozis do zoznamu vlozenych iba to dato co vratilo true ze je vlozene do trie
// TODO opravit generator tak aby pir mazanie isli aj prvky prec
// todo traverz svlast
// todo v inserte ked vytvaras dorhy node tak si tam envytvaraj pamat...chapes
// todo poriadny tester si sprav fakt eva
// todo must have prerobit dlzku hashu v land parcel a real estate



//    GeneratorOfOperationsForLandParcels generatorOfOperations = new GeneratorOfOperationsForLandParcels(dhp);
//
//// ---------- generovanie INSERT -------------
//        generatorOfOperations.insert(10);
//
//                // ---------- generovanie FIND --------------
//                generatorOfOperations.findAllInserted(); // naslo mi kazdy jeden prvnok
//
//                ArrayList<IRecord> inserted = dhp.returnAllRecords();
//        generatorOfOperations.checkIfTheyAreSame(inserted); // prvky vybrate sekvence zo stromu su zhodne s vlozenymi
//
//        // ----------- SEKVENCNY VYPIS
//        System.out.println("--------- SEKVENCNY VYPIS -------------");
//        //dhp.returnSequenceStringOutputWithOverfillingFiles();
//        dhp.sequenceStringOutput();
//        System.out.println("--------- KONIEC VYPISU-----------------");
//        System.out.println("velkost preplnovaku: " + dhp.getSizeOfOverfillingFile());
//        System.out.println("velkost hlavenho suboru: " + dhp.getSizeOfMainFile());
//        System.out.println("pocet prvkov v strome: " + dhp.size());
//
//        // --------- generovanie DELETE ------------------
//        generatorOfOperations.delete();
//
//        // ----------- SEKVENCNY VYPIS
//        System.out.println("--------- SEKVENCNY VYPIS -------------");
//        //dhp.returnSequenceStringOutputWithOverfillingFiles();
//        //dhp.returnSequenceStringOutputWithOverfillingFilesAndBlocks();
//
//        System.out.println("--------- KONIEC VYPISU-----------------");
//        System.out.println("velkost preplnovaku: " + dhp.getSizeOfOverfillingFile());
//        System.out.println("velkost hlavenho suboru: " + dhp.getSizeOfMainFile());
//        System.out.println("pocet prvkov v strome: " + dhp.size());
//
//        System.out.println("Zatiaľ to funguje ^-^");