import Data.GPS;
import Data.LandParcel;
import Data.RealEstate;

import java.io.*;
import java.nio.ByteBuffer;

public class Main {
    public static void main(String[] args) {
//        String filename = "sikovnaEvickaProgramatorka.ausdva";
//        File f = new File(filename);
//        byte[] buffer = new byte[100];
//        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        //new FileWriter("abcd").write(bais);
//        System.out.println("Hello world!");
//        byte[] wt = getFromInt(15);
//        System.out.println(wt);
//        String back = new String(wt);
//        System.out.println(back);
//
//        try {
//            var fr = new FileReader("abcd");
//
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }

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

        byte[] parSer = parcel.ToByteArray();
        parcel.FromByteArray(parSer);

        byte[] realSer = estate.ToByteArray();
        estate.FromByteArray(realSer);

        System.out.println("Zatiaľ to funguje ^-^");
    }

//    private static byte[] getFromString(String val) {
//        // TODO: 21. 11. 2023 ale treba tu dať nejakú dĺžku aby bol string rovnako dlhy
//        return val.getBytes();
//    }
//
//    private static byte[] getFromInt(int val) {
//        String imd = Integer.toBinaryString(val);
//        System.out.println(imd);
//        return imd.getBytes();
//    }
}