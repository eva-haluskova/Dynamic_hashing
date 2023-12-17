package Structure.QuadTree;

import Data.*;
import Data.Others.MapCoordinates;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Class for loading and saving content of tree into csv file.
 */

public class ReadWriterOfTree {

    private QuadTree<CadastralObjectData> treeRealEstate;
    private QuadTree<CadastralObjectData> treeLandParcel;
    private GPS[] parTreeREGPS;
    private GPS[] parTreeLPGPS;

    public ReadWriterOfTree(QuadTree<CadastralObjectData> parTreeRE, QuadTree<CadastralObjectData> parTreeLP,
                            GPS[] parTreeREGPS, GPS[] parTreeLPGPS) {
        this.treeRealEstate = parTreeRE;
        this.treeLandParcel = parTreeLP;
        this.parTreeREGPS = parTreeREGPS;
        this.parTreeLPGPS = parTreeLPGPS;
    }

    public QuadTree<CadastralObjectData> getTreeRealEstate() {
        return this.treeRealEstate;
    }

    public QuadTree<CadastralObjectData> getTreeLandParcel() {
        return this.treeLandParcel;
    }


    public void writeData(String parCsvFilePath) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(parCsvFilePath))) {

            List<Data<CadastralObjectData>> dataEstate = this.treeRealEstate.getAllDataInSubTree(this.treeRealEstate.getRoot());
            List<Data<CadastralObjectData>> dataParcel = this.treeLandParcel.getAllDataInSubTree(this.treeLandParcel.getRoot());

            String[] infoEstate = this.returnTreeDataInStringArray(this.treeRealEstate, this.parTreeREGPS);
            for (int i = 0; i < infoEstate.length; i++) {
                writer.write(infoEstate[i]);
                if (i < infoEstate.length - 1) {
                    writer.write(";");
                }
            }
            writer.write("\n");

            String[] infoLandParcel = this.returnTreeDataInStringArray(this.treeLandParcel,this.parTreeLPGPS);
            for (int i = 0; i < infoLandParcel.length; i++) {
                writer.write(infoLandParcel[i]);
                if (i < infoLandParcel.length - 1) {
                    writer.write(";");
                }
            }
            writer.write("\n");


            for (Data<CadastralObjectData> data : dataEstate) {
                String[] row = this.returnDataToStringArray(data);

                for (int i = 0; i < row.length; i++) {
                    writer.write(row[i]);
                    if (i < row.length - 1) {
                        writer.write(";");
                    }
                }
                writer.write("\n");
            }

            for (Data<CadastralObjectData> data : dataParcel) {
                String[] row = this.returnDataToStringArray(data);

                for (int i = 0; i < row.length; i++) {
                    writer.write(row[i]);
                    if (i < row.length - 1) {
                        writer.write(";");
                    }
                }
                writer.write("\n");
            }
            System.out.println("data saved to quad tree");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readData(String parCsvFilePath) {
        //String[] str = {"primary key","coooralowerX","coorsupprX","coorsloewrY","coorsupperY","(type)","objectNumber","desc"};

        QuadTree<CadastralObjectData> newRealEstateTree;
        QuadTree<CadastralObjectData> newLandParcelTree;


        try (BufferedReader reader = new BufferedReader(new FileReader(parCsvFilePath))) {
            String line;

            String realEstateInformation = reader.readLine();
            String[] infoEstate = realEstateInformation.split(";");
            newRealEstateTree = new QuadTree<>(
                    Double.parseDouble(infoEstate[2]),
                    Double.parseDouble(infoEstate[3]),
                    Double.parseDouble(infoEstate[4]),
                    Double.parseDouble(infoEstate[5]),
                    Integer.parseInt(infoEstate[0]));
            int countOfItemInEstateTree = Integer.parseInt(infoEstate[1]);
            GPS gps1 = new GPS(
                    this.returnLatitude(infoEstate[6]),
                    Double.parseDouble(infoEstate[7]),
                    this.retrunLongitude(infoEstate[8]),
                    Double.parseDouble(infoEstate[9]));

            GPS gps2 = new GPS(
                    this.returnLatitude(infoEstate[10]),
                    Double.parseDouble(infoEstate[11]),
                    this.retrunLongitude(infoEstate[12]),
                    Double.parseDouble(infoEstate[13]));
            GPS[] rootRE = {gps1,gps2};

            String landParcelInformation = reader.readLine();
            String[] infoParcel = landParcelInformation.split(";");
            newLandParcelTree = new QuadTree<>(
                    Double.parseDouble(infoParcel[2]),
                    Double.parseDouble(infoParcel[3]),
                    Double.parseDouble(infoParcel[4]),
                    Double.parseDouble(infoParcel[5]),
                    Integer.parseInt(infoParcel[0]));
            int countOfItemInLandTree = Integer.parseInt(infoParcel[1]);

            GPS gps3 = new GPS(
                    this.returnLatitude(infoParcel[6]),
                    Double.parseDouble(infoParcel[7]),
                    this.retrunLongitude(infoParcel[8]),
                    Double.parseDouble(infoParcel[9]));

            GPS gps4 = new GPS(
                    this.returnLatitude(infoParcel[10]),
                    Double.parseDouble(infoParcel[11]),
                    this.retrunLongitude(infoParcel[12]),
                    Double.parseDouble(infoParcel[13]));
            GPS[] rootLP = {gps3,gps4};


            //String[] str = {"primary key","coooralowerX","coorsupprX","coorsloewrY","coorsupperY","(type)","objectNumber","desc"};
            // ","coooralowerX","coorsupprX","coorsloewrY","coorsupperY","ID", primary key

            MapCoordinates mapEstateTree = new MapCoordinates(rootRE);
            for (int i = 0; i < countOfItemInEstateTree; i++) {
                line = reader.readLine();
                String[] values = line.split(";");
                Coordinates newCoordinates = new Coordinates(
                        Double.parseDouble(values[0]),
                        Double.parseDouble(values[1]),
                        Double.parseDouble(values[2]),
                        Double.parseDouble(values[3]));

                // Java code to illustrate toString() method
                UUID uuid = UUID.fromString(values[5]);
                GPS[] newGps = mapEstateTree.getGPSValue(newCoordinates);
                CadastralObjectData newRealEstate = new CadastralObjectData(Integer.parseInt(values[4]),newGps);
                Data<CadastralObjectData> dataToInsert = new Data(newRealEstate,newCoordinates,uuid);
                newRealEstateTree.insert(dataToInsert);
            }

            MapCoordinates mapLandParcel = new MapCoordinates(rootLP);
            for (int i = 0; i < countOfItemInLandTree; i++) {
                line = reader.readLine();
                String[] values = line.split(";");
                Coordinates newCoordinates = new Coordinates(
                        Double.parseDouble(values[0]),
                        Double.parseDouble(values[1]),
                        Double.parseDouble(values[2]),
                        Double.parseDouble(values[3]));

                // Java code to illustrate toString() method
                UUID uuid = UUID.fromString(values[5]);
                GPS[] newGps = mapLandParcel.getGPSValue(newCoordinates);
                CadastralObjectData newRealEstate = new CadastralObjectData(Integer.parseInt(values[4]),newGps);
                Data<CadastralObjectData> dataToInsert = new Data(newRealEstate,newCoordinates,uuid);
                newLandParcelTree.insert(dataToInsert);
            }

            this.treeLandParcel = newLandParcelTree;
            this.treeRealEstate = newRealEstateTree;
            this.parTreeREGPS = rootRE;
            this.parTreeLPGPS = rootRE;
            System.out.println("data from quad tree loaded");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public String[] returnDataToStringArray(Data<CadastralObjectData> parData){
        String[] coors = parData.getCoordinates().returnCoordinatesInString();
        String object = parData.getData().toListOfString();
        String returnString = String.valueOf(parData.getPrimaryKey());

        String[] concatenatedItems = new String[]{String.join(";", coors), object, returnString};
        return concatenatedItems;
    }

    public String[] returnTreeDataInStringArray(QuadTree<CadastralObjectData> parTree,GPS[] parSur) {

        String[] gps1 = parSur[0].returnAsStringArray();
        String[] gps2 = parSur[1].returnAsStringArray();
        String depth = Integer.toString(parTree.getMaxDepth());
        String[] coors = parTree.getRangeOfTree().returnCoordinatesInString();
        String countOfData = Integer.toString(parTree.getNumberOfItems());

        String[] concatenatedArray = new String[14];

        concatenatedArray[0] = depth;
        concatenatedArray[1] = countOfData;

        System.arraycopy(coors, 0, concatenatedArray, 2, coors.length);
        System.arraycopy(gps1,0,concatenatedArray,2 + coors.length,gps1.length);
        System.arraycopy(gps2, 0, concatenatedArray, 2 + coors.length + gps1.length,gps2.length);

        return concatenatedArray;
    }

    private GPS.Latitude returnLatitude(String parLatitude) {
        if (parLatitude.equals("NORTH")) {
            return GPS.Latitude.NORTH;
        } else {
            return GPS.Latitude.SOUTH;
        }
    }

    private GPS.Longitude retrunLongitude(String parLongitude) {
        if (parLongitude.equals("WEST")) {
            return GPS.Longitude.WEST;
        } else {
            return GPS.Longitude.EAST;
        }
    }

    public QuadTree<CadastralObjectData> returnLandParcelTree() {
        return this.treeLandParcel;
    }

    public QuadTree<CadastralObjectData> returnRealEstateTree() {
        return this.treeRealEstate;
    }

    public GPS[] getParTreeREGPS() {
        return parTreeREGPS;
    }

    public GPS[] getParTreeLPGPS() {
        return parTreeLPGPS;
    }
}
