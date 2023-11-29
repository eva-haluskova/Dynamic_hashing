import Data.GPS;
import Data.LandParcel;
import Data.RealEstate;
import Structure.DynamicHashing.Nodes.*;

import java.util.BitSet;


public class Main {
    public static void main(String[] args) {

        // --------- testing of IRecord interface
        GPS gpsOne = new GPS("N",60,"W",30);
        GPS gpsTwo = new GPS("S",30,"E",20);
        GPS[] gpsFirst = {gpsOne, gpsTwo};

        GPS gpsOneTwo = new GPS("S",24,"E",67);
        GPS gpsTwoTwo = new GPS("N",13,"E",56);
        GPS[] gpsSecond = {gpsOneTwo, gpsTwoTwo};

        LandParcel parcel = new LandParcel(2,gpsFirst,"Toto nie");
        parcel.addBelongingRealEstate(34);
        parcel.addBelongingRealEstate(2);
        parcel.addBelongingRealEstate(4);

        RealEstate estate = new RealEstate(4,gpsSecond,"no salala",90);
        estate.addBelongingLandParcel(90);
        estate.addBelongingLandParcel(34);
        estate.addBelongingLandParcel(3);
        estate.addBelongingLandParcel(44);

        BitSet hash = parcel.getHash();
        System.out.println("Hash: " + hash);
        System.out.println(parcel.returnBitSetString());

        BitSet hash2 = estate.getHash();
        System.out.println("Hash: " + hash2);
        System.out.println(estate.returnBitSetString());

        System.out.println(parcel.areEqual(estate) ? "rovnajuu sa": "nerovnaju sa");
        System.out.println(parcel.areEqual(parcel) ? "Yess!" : "wtf");

        byte[] parSer = parcel.toByteArray();
        parcel.fromByteArray(parSer);

        byte[] realSer = estate.toByteArray();
        estate.fromByteArray(realSer);

        RealEstate anoter = new RealEstate(realSer);
        LandParcel ano = new LandParcel(parSer);

        System.out.println("size of parcel:" + parcel.getSize());
        System.out.println("size of real estates:" + estate.getSize());

        // ------------testing of creating nodes
//        ExternalNode en = new ExternalNode(null);
//        InternalNode in = new InternalNode(null);
//        en.setAddress(3);
//        in.setLeftSon(en);


        System.out.println("Zatiaľ to funguje ^-^");
    }

}