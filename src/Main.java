import java.io.*;
import java.nio.ByteBuffer;

public class Main {
    public static void main(String[] args) {
        String filename = "sikovnaEvickaProgramatorka.ausdva";
        File f = new File(filename);
        byte[] buffer = new byte[100];
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //new FileWriter("abcd").write(bais);
        System.out.println("Hello world!");
        byte[] wt = getFromInt(15);
        System.out.println(wt);
        String back = new String(wt);
        System.out.println(back);

        try {
            var fr = new FileReader("abcd");

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] getFromString(String val) {
        // TODO: 21. 11. 2023 ale treba tu dať nejakú dĺžku aby bol string rovnako dlhy
        return val.getBytes();
    }

    private static byte[] getFromInt(int val) {
        String imd = Integer.toBinaryString(val);
        System.out.println(imd);
        return imd.getBytes();
    }
}