import Data.GPS;
import Data.LandParcel;
import Structure.DynamicHashing.DynamicHashing;
import Structure.DynamicHashing.IRecord;
import Structure.GeneratorOfOperationsForLandParcels;

import java.util.ArrayList;


public class Main {
    public static void main(String[] args) throws IllegalAccessException, InstantiationException {

        GPS gpsOne = new GPS("N",60,"W",30);
        GPS gpsTwo = new GPS("S",30,"E",20);
        GPS[] gpsFirst = {gpsOne, gpsTwo};

        IRecord parcela4 = new LandParcel(15,gpsFirst,"stvorka objekt");
        IRecord parcela5 = new LandParcel(526,gpsFirst,"petka objekt");
        IRecord parcela6= new LandParcel(526,gpsFirst,"sestrka objekt");

        DynamicHashing<LandParcel>  dhp = new DynamicHashing<>(5, LandParcel.class,"file.bin");

        GeneratorOfOperationsForLandParcels generatorOfOperations = new GeneratorOfOperationsForLandParcels(dhp);

        // ---------- generovanie INSERT -------------
        generatorOfOperations.insert(1000);

        // ---------- generovanie FIND --------------
        generatorOfOperations.findAllInserted();

        ArrayList<IRecord> inserted = dhp.returnAllRecords();
        generatorOfOperations.checkIfTheyAreSme(inserted);

        // ------- SEKVENCNY VYPIS

//        System.out.println("--------- SEKVENCNY VYPIS -------------");
//        dhp.returnContent();
//        System.out.println("--------- KONIEC -----------------");

        //dhp.returnContentOfFile();

        // --------- skuska funkconosti porovnavania objektov a vkladanie duplicitnych dat
        System.out.println(dhp.insert(parcela5) ? "Vlozila sa mi parcela 5" : "nevlozila sa mi parcela 5");
        System.out.println(dhp.insert(parcela6)? "vlozila sa mi parcela 6 napriek tomu, ze ma rovnake id ako 5" : "nevlozila sa mi parcela 6, pretoze ma rovnake id ako 5");
        generatorOfOperations.insertOne((LandParcel) parcela5);
        generatorOfOperations.insertOne((LandParcel) parcela6);
        LandParcel fi = (LandParcel) generatorOfOperations.find(parcela5);
        LandParcel fu = (LandParcel) generatorOfOperations.find(parcela6);
        System.out.println("Hladala som parcelu 5: " + fi.toString());
        System.out.println("Hladala som parcelu 6: " + fu.toString());

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

