import Data.GPS;
import Data.LandParcel;
import Structure.DynamicHashing.Block;
import Structure.DynamicHashing.IRecord;


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

        Block<LandParcel> block = new Block<>(10, LandParcel.class);
        block.insertRecord(parcela);
        block.insertRecord(parcela2);
        block.insertRecord(parcela3);
        block.insertRecord(parcela4);
        block.insertRecord(parcela5);

        byte[] blockSer = block.toByteArray();
        System.out.println("teoretical size of block: " + block.getSize());
        System.out.println("Practial :( size of block: " + blockSer.length);
        //System.out.println(block.getRecordAt(2).toString());


        Block<LandParcel> blockOfP = new Block<>(10, LandParcel.class);
        blockOfP.fromByteArray(blockSer);

        System.out.println(blockOfP.getRecordAt(3).toString());

        block.fromByteArray(blockSer);





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
//        System.out.println(parcel.areEqual(estate) ? "rovnajuu sa": "nerovnaju sa");
//        System.out.println(parcel.areEqual(parcel) ? "Yess!" : "wtf");
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
