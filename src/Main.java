import Data.LandParcel;
import Data.GPS;
import Structure.DynamicHashing.DynamicHashing;
import Structure.DynamicHashing.IRecord;
import Structure.GeneratorOfOperationsForLandParcels;
import Structure.QuadTree.Data;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        GPS gpsOne = new GPS("N",60,"W",30);
        GPS gpsTwo = new GPS("S",30,"E",20);
        GPS[] gpsFirst = {gpsOne, gpsTwo};

        GPS gpsOne1 = new GPS("S",60,"E",0);
        GPS gpsTwo1 = new GPS("S",20,"E",20);
        GPS[] gpsSecond = {gpsOne1, gpsTwo1};

        IRecord parcela4 = new LandParcel(1,gpsFirst,"petka objekt");
        IRecord parcela5 = new LandParcel(2,gpsFirst,"sestrka objekt");
        IRecord parcela6 = new LandParcel(3,gpsFirst,"stvorka objekt");


        DynamicHashing<LandParcel>  dhp = new DynamicHashing<>(2,3, LandParcel.class,"mainFile.bin","overfillingFile.bin");




        dhp.insert(parcela4);
        dhp.insert(parcela5);
        dhp.insert(parcela6);
        IRecord parcela1 = new LandParcel(4,gpsFirst,"stvorka objekt");
        IRecord parcela2 = new LandParcel(5,gpsFirst,"petka objekt");
        IRecord parcela3 = new LandParcel(6,gpsFirst,"sestrka objekt");
        IRecord parcela7 = new LandParcel(7,gpsFirst,"petka objekt");
        IRecord parcela8 = new LandParcel(8,gpsFirst,"sestrka objekt");
        IRecord parcela9 = new LandParcel(9,gpsFirst,"stvorka objekt");
        IRecord parcela10 = new LandParcel(10,gpsFirst,"stvorka objekt");

        dhp.insert(parcela1);
        dhp.insert(parcela2);
        dhp.insert(parcela3);
        dhp.insert(parcela7);
        dhp.insert(parcela8);
        dhp.insert(parcela9);
        dhp.insert(parcela10);





       // GeneratorOfOperationsForLandParcels generatorOfOperations = new GeneratorOfOperationsForLandParcels(dhp);

        // ---------- generovanie INSERT -------------
       // generatorOfOperations.insert(10);

        // ---------- generovanie FIND --------------
       // generatorOfOperations.findAllInserted(); // naslo mi kazdy jeden prvnok

        ArrayList<IRecord> inserted = dhp.returnAllRecords();
       // generatorOfOperations.checkIfTheyAreSame(inserted); // prvky vybrate sekvence zo stromu su zhodne s vlozenymi

        // ----------- SEKVENCNY VYPIS
        System.out.println("--------- SEKVENCNY VYPIS -------------");
        //dhp.returnSequenceStringOutputWithOverfillingFiles();
        dhp.sequenceStringOutput();
        System.out.println("--------- KONIEC VYPISU-----------------");
        System.out.println("velkost preplnovaku: " + dhp.getSizeOfOverfillingFile());
        System.out.println("velkost hlavenho suboru: " + dhp.getSizeOfMainFile());
        System.out.println("pocet prvkov v strome: " + dhp.size());



        IRecord parcela33 = new LandParcel(6,gpsFirst,"preco da");
        dhp.edit(parcela33);
        IRecord parcela44 = new LandParcel(2,gpsSecond,"NASRAC");
        dhp.edit(parcela44);


        // --------- generovanie DELETE ------------------
       // generatorOfOperations.delete();

        // ----------- SEKVENCNY VYPIS
        System.out.println("--------- SEKVENCNY VYPIS -------------");
        //dhp.returnSequenceStringOutputWithOverfillingFiles();
        //dhp.returnSequenceStringOutputWithOverfillingFilesAndBlocks();
        dhp.sequenceStringOutput();
        System.out.println("--------- KONIEC VYPISU-----------------");
//        System.out.println("velkost preplnovaku: " + dhp.getSizeOfOverfillingFile());
//        System.out.println("velkost hlavenho suboru: " + dhp.getSizeOfMainFile());
//        System.out.println("pocet prvkov v strome: " + dhp.size());

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


//    DynamicHashing<LandParcel>  dhp = new DynamicHashing<>(2,8, LandParcel.class,"mainFile.bin","overfillingFile.bin");
//
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