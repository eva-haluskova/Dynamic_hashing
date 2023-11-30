import Data.GPS;
import Data.LandParcel;
import Structure.DynamicHashing.Block;
import Structure.DynamicHashing.DynamicHashing;
import Structure.DynamicHashing.IRecord;
import Structure.GeneratorOfOperationsForLandParcels;

import java.lang.invoke.LambdaConversionException;
import java.util.ArrayList;


public class Main {
    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        GPS gpsOne = new GPS("N",60,"W",30);
        GPS gpsTwo = new GPS("S",30,"E",20);
        GPS[] gpsFirst = {gpsOne, gpsTwo};
        IRecord parcela = new LandParcel(12,gpsFirst,"jednotka objekt");
        IRecord parcela2 = new LandParcel(13,gpsFirst,"dvbojk aobjekt");
        IRecord parcela3 = new LandParcel(14,gpsFirst,"trojka objekt");
        IRecord parcela4 = new LandParcel(15,gpsFirst,"stvorka objekt");
        IRecord parcela5 = new LandParcel(16,gpsFirst,"petka objetk");
        IRecord parcela6= new LandParcel(16,gpsFirst,"salalalalala");

        DynamicHashing<LandParcel>  dhp = new DynamicHashing<LandParcel>(3, LandParcel.class);

        GeneratorOfOperationsForLandParcels generator = new GeneratorOfOperationsForLandParcels(dhp);
        generator.insert(10);
        generator.findAllInserted();

        System.out.println("nekecej akoze vazne???");

//        ArrayList<IRecord> inserted = dhp.returnAllRecords();
//        for (int i = 0; i < inserted.size(); i++) {
//            System.out.println(inserted.get(i).toString());
//        }
//
        System.out.println(dhp.insert(parcela5) ? "toto somc chcela" : "nevlozilsawtf");
        System.out.println(dhp.insert(parcela6)? "vlozene" : "totos om chcela");
        generator.insertOne((LandParcel) parcela5);
        generator.insertOne((LandParcel) parcela6);
        LandParcel fi = (LandParcel) generator.find(parcela5);
        LandParcel fu = (LandParcel) generator.find(parcela6);
        System.out.println(fi.toString());
        System.out.println(fu.toString());


        //generator.checkIfTheyAreSme(inserted);


        System.out.println("Zatiaľ to funguje ^-^");
    }

}

// TODO just idea for uniqe id - time and date to number :)


//        // --------- testing of IRecord interface
//        GPS gpsOne = new GPS("N",60,"W",30);
//        GPS gpsTwo = new GPS("S",30,"E",20);
//        GPS[] gpsFirst = {gpsOne, gpsTwo};
//
//        GPS gpsOneTwo = new GPS("S",24,"E",67);
//        GPS gpsTwoTwo = new GPS("N",13,"E",56);
//        GPS[] gpsSecond = {gpsOneTwo, gpsTwoTwo};
//
//        LandParcel parcel = new LandParcel(2,gpsFirst,"Toto nie");
//        parcel.addBelongingRealEstate(34);
//        parcel.addBelongingRealEstate(2);
//        parcel.addBelongingRealEstate(4);
//
//        RealEstate estate = new RealEstate(4,gpsSecond,"no salala",90);
//        estate.addBelongingLandParcel(90);
//        estate.addBelongingLandParcel(34);
//        estate.addBelongingLandParcel(3);
//        estate.addBelongingLandParcel(44);
//
//        BitSet hash = parcel.getHash();
//        System.out.println("Hash: " + hash);
//        System.out.println(parcel.returnBitSetString());
//
//        BitSet hash2 = estate.getHash();
//        System.out.println("Hash: " + hash2);
//        System.out.println(estate.returnBitSetString());
//
//        System.out.println(parcel.equals(estate) ? "rovnajuu sa": "nerovnaju sa");
//        System.out.println(parcel.equals(parcel) ? "Yess!" : "wtf");
//
//        byte[] parSer = parcel.toByteArray();
//        parcel.fromByteArray(parSer);
//
//        byte[] realSer = estate.toByteArray();
//        estate.fromByteArray(realSer);
//
//        RealEstate anoter = new RealEstate(realSer);
//        LandParcel ano = new LandParcel(parSer);
//
//        System.out.println("size of parcel:" + parcel.getSize());
//        System.out.println("size of real estates:" + estate.getSize());

// ------------testing of creating nodes
//        ExternalNode en = new ExternalNode(null);
//        InternalNode in = new InternalNode(null);
//        en.setAddress(3);
//        in.setLeftSon(en);
