import Data.GPS;
import Data.LandParcel;
import Data.Others.CadastralObjectGenerator;
import Structure.DynamicHashing.Block;
import Structure.DynamicHashing.DynamicHashing;
import Structure.DynamicHashing.IRecord;
import Structure.GeneratorOfOperationsForLandParcels;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.Objects;


public class Main {
    public static void main(String[] args) throws IllegalAccessException, InstantiationException {

//        GPS gpsOne = new GPS("N",60,"W",30);
//        GPS gpsTwo = new GPS("S",30,"E",20);
//        GPS[] gpsFirst = {gpsOne, gpsTwo};
//
//        CadastralObjectGenerator g = new CadastralObjectGenerator(8);
//        ArrayList<LandParcel> lp = new ArrayList<>();
//        ArrayList<LandParcel> lps = new ArrayList<>();
//
//        for (int i = 0; i < 10; i++) {
//            lp.add(g.generateLandParcel(30,gpsFirst));
//        }
//        for (int i = 0; i < lp.size(); i++) {
//            System.out.println(lp.get(i).getHash());
//        }



        GPS gpsOne = new GPS("N",60,"W",30);
        GPS gpsTwo = new GPS("S",30,"E",20);
        GPS[] gpsFirst = {gpsOne, gpsTwo};


//        IRecord parcela1 = new LandParcel(15,gpsFirst,"stvorka objekt");
//        IRecord parcela2 = new LandParcel(9236,gpsFirst,"petka objekt");
//        IRecord parcela3 = new LandParcel(5526,gpsFirst,"sestrka objekt");
        IRecord parcela4 = new LandParcel(6,gpsFirst,"petka objekt");
        IRecord parcela5 = new LandParcel(52,gpsFirst,"sestrka objekt");
        IRecord parcela6 = new LandParcel(145,gpsFirst,"stvorka objekt");
//        IRecord parcela7 = new LandParcel(9226,gpsFirst,"petka objekt");
//        IRecord parcela8 = new LandParcel(5286,gpsFirst,"sestrka objekt");
//        IRecord parcela9 = new LandParcel(153,gpsFirst,"stvorka objekt");
//        IRecord parcela10 = new LandParcel(149,gpsFirst,"stvorka objekt");





        DynamicHashing<LandParcel>  dhp = new DynamicHashing<>(2,3, LandParcel.class,"mainFile.bin","overfillingFile.bin");

//        System.out.println("find");
//        dhp.insert(parcela1);
//        dhp.insert(parcela2);
//        dhp.insert(parcela3);
//        dhp.insert(parcela4);
//        dhp.insert(parcela5);
//        dhp.insert(parcela6);
//        dhp.insert(parcela7);
//        dhp.insert(parcela8);
//        dhp.insert(parcela9);
//        dhp.insert(parcela10);
//
//        dhp.find(parcela1);
//        dhp.find(parcela2);
//        dhp.find(parcela3);
//        dhp.find(parcela4);
//        dhp.find(parcela5);
//        dhp.find(parcela6);
//        dhp.find(parcela7);
//        dhp.find(parcela8);
//        dhp.find(parcela9);
//        dhp.find(parcela10);

//
//        dhp.returnSequenceStringOutputWithOverfillingFiles();
//
//
//        System.out.println("deteye");
//        dhp.delete(parcela4);
//        dhp.delete(parcela10);
//        dhp.delete(parcela2);
//
//        dhp.returnSequenceStringOutputWithOverfillingFiles();


        GeneratorOfOperationsForLandParcels generatorOfOperations = new GeneratorOfOperationsForLandParcels(dhp);

        // ---------- generovanie INSERT -------------
        generatorOfOperations.insert(10);





//        // ---------- generovanie FIND --------------
        generatorOfOperations.findAllInserted();
//
       ArrayList<IRecord> inserted = dhp.returnAllRecords();
//        //generatorOfOperations.checkIfTheyAreSme(inserted);
        generatorOfOperations.checkIfTheyAreSame(inserted);


//         //------- SEKVENCNY VYPIS ----------
//
        System.out.println("--------- SEKVENCNY VYPIS -------------");
//       // dhp.returnSequenceStringOutput();
//        System.out.println("---------------");
        dhp.returnSequenceStringOutputWithOverfillingFiles();
        System.out.println("--------- KONIEC -----------------");

        generatorOfOperations.delete();

        System.out.println("--------- SEKVENCNY VYPIS -------------");
        dhp.returnSequenceStringOutputWithOverfillingFiles();
        System.out.println("--------- KONIEC -----------------");


//        //dhp.returnSequenceStringOutput();
//
//         //--------- skuska funkconosti porovnavania objektov a vkladanie duplicitnych dat
//        System.out.println(dhp.insert(parcela5) ? "Vlozila sa mi parcela 5" : "nevlozila sa mi parcela 5");
//        System.out.println(dhp.insert(parcela6) ? "vlozila sa mi parcela 6 napriek tomu, ze ma rovnake id ako 5" : "nevlozila sa mi parcela 6, pretoze ma rovnake id ako 5");
//        generatorOfOperations.insertOne((LandParcel) parcela5);
//        generatorOfOperations.insertOne((LandParcel) parcela6);
//        LandParcel fi = (LandParcel) generatorOfOperations.find(parcela5);
//        LandParcel fu = (LandParcel) generatorOfOperations.find(parcela6);
//        System.out.println("Hladala som parcelu 5: " + fi.toString());
//        System.out.println("Hladala som parcelu 6: " + fu.toString());

//        Date currentDate = new Date(); // Current date
//        // Convert Date to int (representing seconds)
//        int seconds = (int) (currentDate.getTime() / 1000);
//        System.out.println(seconds);
//
//        LocalTime currentTime = LocalTime.now();
//
//        // Extract different components of the time
//        int hour = currentTime.getHour();
//        int minute = currentTime.getMinute();
//        int second = currentTime.getSecond();
//
//        // Generate integers using different components of the time
//        int generatedInteger = hour * 10000 + minute * 100 + second;
//
//        int hashCode = Objects.hash(generatedInteger);
//        BitSet bs = BitSet.valueOf(new long[] {hashCode});
//        System.out.println(bs);
//        System.out.println(bs.length());
//
//
//        int seconds2 = (int) (currentDate.getTime() / 1000);
//        System.out.println(seconds2);
//
//        int hashCode2 = Objects.hash(seconds2);
//        BitSet bs2 = BitSet.valueOf(new long[] {hashCode2});
//        System.out.println(bs2);
//        System.out.println(bs2.length());

        System.out.println("Zatiaľ to funguje ^-^");
    }

}

// TODO musis potestovat pre rozne seedy
// TODO vies ze nemas poriesene vymazavanie adresy od currenta hahahahaha :D - moze vobec nastat? HAAA?
// TODO generovanie novych objektv,,vloz jeden - vloz si to aj do dynamichashing
// TODO a ttiez to generovanie, vloaz iba ak vysledok z insertdynamickhashing je true....ale pi preplnovaku uz asi ani ne...ale ba, keby si chcel vkladat rovnake daticka
// TODO pracu s filom si daj to dynamickeho heshovania
// TODO vlozis do zoznamu vlozenych iba to dato co vratilo true ze je vlozene do trie
// TODO to s tym currentom si potrebujes okseftit


// piatok vecer:
/*
- prekopat pracu s filom do dynamic hashing
- opekniet dynamic hashing a block
- predstavit si pracu s preplnovakmi
*/

// todo private metody aby boli private
// todo think of if you need in all this methods return value (boolean)

// todo traverz zvlast



//    Block<LandParcel> block = new Block<>(4,LandParcel.class);
//
//        System.out.println(block.getSizeOfHeader());
//                System.out.println(block.getSize());
//                byte[] ss = block.toByteArray();
//                System.out.println(ss.length);
//                block.insertRecord(parcela4);
//                block.insertRecord(parcela5);
//                block.insertRecord(parcela5);
//                block.insertRecord(parcela4);
//                block.insertRecord(parcela6);
//                System.out.println(block.getPreviousFreeBlock());
//                System.out.println(block.getNextFreeBlock());
//                System.out.println(block.getNextLinkedBlock());
//
//
//                ArrayList<IRecord> zoz = block.returnValidRecordsAsArray();
//        for (IRecord sct: zoz
//        ) {
//        System.out.println(sct);
//        }
