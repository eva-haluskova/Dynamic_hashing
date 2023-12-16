package GUI;

import Data.GPS;
import Data.LandParcel;
import Data.Others.CadastralObjectGenerator;
import Data.Others.MapCoordinates;
import Data.RealEstate;
import Structure.DynamicHashing.DynamicHashing;
import Structure.DynamicHashing.IRecord;
import Structure.QuadTree.CadastralObjectData;
import Structure.QuadTree.Data;
import Structure.QuadTree.QuadTree;
import Structure.QuadTree.ReadWriterOfTree;
//import Structure.QuadTree.ReadWriterOfTree;

import javax.imageio.event.IIOReadProgressListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

/**
 * Class function is to store data and ensure work with them. Its purpose is act like
 * "Model" in MVC model, so instance of this class is created in controller which
 * collaborate with model and call function from this storage according to changes in view.
 */
public class Model {

    private final int MAX_COUNT_OF_ESTATES = 5;
    private final int MAX_COUNT_OF_PARCELS = 6;
    private MapCoordinates mapEstateTree;
    private MapCoordinates mapParcelTree;
    private CadastralObjectGenerator generator;
    private QuadTree<CadastralObjectData> landParcelQuadTree;
    private QuadTree<CadastralObjectData> realEstateQuadTree;
    private ReadWriterOfTree readWriterOfTree;
    private GPS[] realEstateTreeGPS;
    private GPS[] landParcelTreeGPS;
    private DynamicHashing<LandParcel> landParcelDynamicHashing;
    private DynamicHashing<RealEstate> realEstateDynamicHashing;

    public Model() {
        this.generator = new CadastralObjectGenerator();

        GPS gpsOne = new GPS("N",10,"W",10);
        GPS gpsTwo = new GPS("S",10,"E",10);
        GPS[] gpsFirst = {gpsOne, gpsTwo};
        this.createLandParcelQuadTree(gpsFirst,5);
        this.createRealEstateQuadTree(gpsFirst,5);
        this.inicializeDynamicHashingForLandParcels(2,3,"mainLand.bin", "overfillingLand.bin");
        this.inicializeDynamicHashingForRealEstates(2,3,"mainEstates.bin","overfillingEstates.bin");
        this.generateRealEstates(10,5,gpsFirst);
        System.out.println("idem vkladat druhe dao");
        this.generateLandParcels(10,5,gpsFirst);

    }

    /**
     * Creating dynamic hashing structures
     */
    public void inicializeDynamicHashingForLandParcels(
            int parBlockFactorMainFile,
            int parBlockFactorOverfillingFile,
            String parMainFilePath,
            String parOverfillingFilePath
    ) {
        if (this.landParcelDynamicHashing == null) {
            this.landParcelDynamicHashing = new DynamicHashing<LandParcel>(
                    parBlockFactorMainFile,
                    parBlockFactorOverfillingFile,
                    LandParcel.class,
                    parMainFilePath,
                    parOverfillingFilePath
            );
            System.out.println("Dynamic hashing for land parcel is created!");
        } else {
            System.out.println("Sorry, dynamic hashing for land parcel is already created!");
        }
    }

    public void inicializeDynamicHashingForRealEstates(
            int parBlockFactorMainFile,
            int parBlockFactorOverfillingFile,
            String parMainFilePath,
            String parOverfillingFilePath
    ) {
        if (this.realEstateDynamicHashing == null) {
            this.realEstateDynamicHashing = new DynamicHashing<RealEstate>(
                    parBlockFactorMainFile,
                    parBlockFactorOverfillingFile,
                    RealEstate.class,
                    parMainFilePath,
                    parOverfillingFilePath
            );
            System.out.println("Dynamic hashing for real estate is created!");
        } else {
            System.out.println("Sorry, dynamic hashing for real estate is already created!");
        }
    }

    /**
     * methods for creating trees
     */
    public void createLandParcelQuadTree(GPS[] parCoordinates, int parMaxDepth) {
        if (this.landParcelQuadTree == null) {
            this.landParcelTreeGPS = parCoordinates;
            this.mapParcelTree = new MapCoordinates(parCoordinates);
            this.landParcelQuadTree = new QuadTree<>(this.mapParcelTree.getCoordinatesValue(parCoordinates), parMaxDepth);
            System.out.println("Land Parcel tree is created!");
        } else {
            System.out.println("Sorry, tree is already created!");
        }
    }

    public void createRealEstateQuadTree(GPS[] parCoordinates, int parMaxDepth) {
        if (this.realEstateQuadTree == null) {
            this.realEstateTreeGPS = parCoordinates;
            this.mapEstateTree = new MapCoordinates(parCoordinates);
            this.realEstateQuadTree = new QuadTree<>(this.mapEstateTree.getCoordinatesValue(parCoordinates),parMaxDepth);
            System.out.println("Real Estate tree is created!");
        } else {
            System.out.println("Sorry, tree is already created!");
        }
    }

    /**
     * methods for creating and inserting data
     */
    public boolean insertLandParcel(GPS[] parCoordinates, String parDescription) {
        int newId =  this.generator.getNextLandParcelId();
        CadastralObjectData parcelQT = new CadastralObjectData(newId, parCoordinates);
        Data<CadastralObjectData> dataToInsertIntoQT = new Data(parcelQT,this.mapParcelTree.getCoordinatesValue(parCoordinates),UUID.randomUUID());
        ArrayList<Data<CadastralObjectData>> arrayOfEstates = this.realEstateQuadTree.find(this.mapEstateTree.getCoordinatesValue(parCoordinates));
        if (arrayOfEstates.size() <= MAX_COUNT_OF_ESTATES) {
            int count = 0;
            for (int i = 0; i < arrayOfEstates.size(); i++) {
                ArrayList<Data<CadastralObjectData>> list  = this.landParcelQuadTree.find(arrayOfEstates.get(i).getCoordinates());
                if (list.size() > count) {
                    count = list.size();
                }
            }
            if (count <= MAX_COUNT_OF_PARCELS) {
                this.landParcelQuadTree.insert(dataToInsertIntoQT);
                LandParcel parcelDH = new LandParcel(newId, parCoordinates, parDescription);
                for (int i = 0; i < arrayOfEstates.size(); i++) {
                    parcelDH.addBelongingRealEstate(arrayOfEstates.get(i).getData().getIdentityNumber());
                }
                this.landParcelDynamicHashing.insert(parcelDH);

                // pre vsetky belonging real estates musim updatenovat
                for (int i = 0; i < arrayOfEstates.size(); i++) {
                    RealEstate estateToUpdate = new RealEstate(arrayOfEstates.get(i).getData().getIdentityNumber(), null, "", 3);
                    RealEstate estate = (RealEstate) this.realEstateDynamicHashing.find(estateToUpdate);
                    estate.addBelongingLandParcel(newId);
                    this.realEstateDynamicHashing.edit(estate);
                }
                System.out.println("Data land parcel is created!");
                return true;
            }
            System.out.println("Data cannot be inserted into tree!");
            return false;
        }
        System.out.println("Data cannot be inserted into tree!");
        return false;
    }

    public boolean insertRealEstate(int parSerialNumber, GPS[] parCoordinates, String parDescription) {
        int newId =  this.generator.getNextRealEstateId();
        CadastralObjectData estateQT = new CadastralObjectData(newId, parCoordinates);
        Data<CadastralObjectData> dataToInsertIntoQT = new Data(estateQT,this.mapEstateTree.getCoordinatesValue(parCoordinates),UUID.randomUUID());
        ArrayList<Data<CadastralObjectData>> arrayOfParcels = this.landParcelQuadTree.find(this.mapParcelTree.getCoordinatesValue(parCoordinates));
        if (arrayOfParcels.size() < MAX_COUNT_OF_PARCELS) {
            int count = 0;
            for (int i = 0; i < arrayOfParcels.size(); i++) {
                ArrayList<Data<CadastralObjectData>> list  = this.realEstateQuadTree.find(arrayOfParcels.get(i).getCoordinates());
                if (list.size() > count) {
                    count = list.size();
                }
            }
            if (count < MAX_COUNT_OF_ESTATES) {
                this.realEstateQuadTree.insert(dataToInsertIntoQT);
                RealEstate estateDH = new RealEstate(newId, parCoordinates, parDescription, parSerialNumber);
                for (int i = 0; i < arrayOfParcels.size(); i++) {
                    estateDH.addBelongingLandParcel(arrayOfParcels.get(i).getData().getIdentityNumber());
                }
                this.realEstateDynamicHashing.insert(estateDH);

                // pre vsetky belonging musim updatenovat
                for (int i = 0; i < arrayOfParcels.size(); i++) {
                    LandParcel parcelToUpdate = new LandParcel(arrayOfParcels.get(i).getData().getIdentityNumber(), null, "");
                    LandParcel parcel = (LandParcel) this.landParcelDynamicHashing.find(parcelToUpdate);
                    parcel.addBelongingRealEstate(newId);
                    this.realEstateDynamicHashing.edit(parcel);
                }
                System.out.println("Data real estate is created!");
                return true;
            }
            System.out.println("Data cannot be inserted into tree!");
            return false;
        }
        System.out.println("Data cannot be inserted into tree!");
        return false;
    }

    /**
     * Finding data according to identity number
     */
    public ArrayList<IRecord> findLandParcelWithBelongingEstates(int parIdentityNumber) {
        ArrayList<IRecord> returnList = new ArrayList<>();
        LandParcel pomPar = new LandParcel(parIdentityNumber);
        LandParcel parcel = (LandParcel) this.landParcelDynamicHashing.find(pomPar);
        returnList.add(parcel);
        int[] belongingRealEstates = parcel.getBelongingRealEstates();
        for (int i = 0; i < belongingRealEstates.length; i++) {
            RealEstate pomRel = new RealEstate(belongingRealEstates[i]);
            RealEstate estate = (RealEstate) this.realEstateDynamicHashing.find(pomRel);
            returnList.add(estate);
        }
        return returnList;
    }

    public String findLandParcelWithBelongingEstatesString(int parIdentityNumber) {
        StringBuilder stringBuilder = new StringBuilder();
        LandParcel pomPar = new LandParcel(parIdentityNumber);
        LandParcel parcel = (LandParcel) this.landParcelDynamicHashing.find(pomPar);
        stringBuilder.append(parcel).append("\n");

        int[] belongingRealEstates = parcel.getBelongingRealEstates();
        for (int i = 0; i < belongingRealEstates.length; i++) {
            RealEstate pomRel = new RealEstate(belongingRealEstates[i]);
            RealEstate estate = (RealEstate) this.realEstateDynamicHashing.find(pomRel);
            stringBuilder.append(estate).append("\n");
        }
        return stringBuilder.toString();
    }

    public LandParcel findLandParcelReal(int parIdentityNumber) {
        ArrayList<IRecord> data = this.findLandParcelWithBelongingEstates(parIdentityNumber);
        System.out.println("Land parcel: " + data.get(0) + " is found!");
        return (LandParcel)data.get(0);
    }


    public ArrayList<IRecord> findRealEstatesWithBelongingLandParcels(int parIdentityNumber) {
        ArrayList<IRecord> returnList = new ArrayList<>();
        RealEstate pomEst = new RealEstate(parIdentityNumber);
        RealEstate estate = (RealEstate) this.realEstateDynamicHashing.find(pomEst);
        returnList.add(estate);
        int[] belongingLandParcels = estate.getBelongingLandParcels();
        for (int i = 0; i < belongingLandParcels.length; i++) {
            LandParcel pomPar = new LandParcel(belongingLandParcels[i]);
            LandParcel parcel = (LandParcel) this.landParcelDynamicHashing.find(pomPar);
            returnList.add(parcel);
        }
        return returnList;
    }

    public String findRealEstatesWithBelongingLandParcelsString(int parIdentityNumber) {
        StringBuilder stringBuilder = new StringBuilder();
        RealEstate pomEst = new RealEstate(parIdentityNumber);
        RealEstate estate = (RealEstate) this.realEstateDynamicHashing.find(pomEst);
        stringBuilder.append(estate).append("\n");

        int[] belongingLandParcels = estate.getBelongingLandParcels();
        for (int i = 0; i < belongingLandParcels.length; i++) {
            LandParcel pomPar = new LandParcel(belongingLandParcels[i]);
            LandParcel parcel = (LandParcel) this.landParcelDynamicHashing.find(pomPar);
            stringBuilder.append(parcel).append("\n");
        }

        return stringBuilder.toString();
    }

    public RealEstate findRealEstatesReal(int parIdentityNumber) {
        ArrayList<IRecord> data = this.findRealEstatesWithBelongingLandParcels(parIdentityNumber);
        System.out.println("Real estate: " + data.get(0) + " is found!");
        return (RealEstate)data.get(0);
    }

    /**
     * removing data according to their identity number
     */
    public void deleteLandParcel(int parIdentityNumber) {
        LandParcel pomPar = new LandParcel(parIdentityNumber);
        LandParcel parcel = (LandParcel) this.landParcelDynamicHashing.find(pomPar);
        int[] estatesToUpdate = parcel.getBelongingRealEstates();
        for (int i = 0; i < estatesToUpdate.length; i++) {
            RealEstate pomRel = new RealEstate(estatesToUpdate[i]);
            RealEstate estate = (RealEstate) this.realEstateDynamicHashing.find(pomRel);
            estate.deleteBelongingLandParcel(parIdentityNumber);
            this.realEstateDynamicHashing.edit(estate);
        }
        this.landParcelDynamicHashing.delete(parcel);

        // vymazanie data z quadstromu
        ArrayList<Data<CadastralObjectData>> parcelsToDeleteInQuadTree = this.landParcelQuadTree.find(this.mapParcelTree.getCoordinatesValue(parcel.getGpsCoordinates()));
        for (Data<CadastralObjectData> act : parcelsToDeleteInQuadTree) {
            if (act.getData().getIdentityNumber() == parIdentityNumber) {
                this.landParcelQuadTree.delete(act);
            }
        }
        System.out.println("Land parcel: " + parcel + " is deleted!");
    }

    public void deleteRealEstate(int parIdentityNumber) {
        RealEstate pomEst = new RealEstate(parIdentityNumber);
        RealEstate estate = (RealEstate) this.realEstateDynamicHashing.find(pomEst);
        int[] parcelsToUpdate = estate.getBelongingLandParcels();
        for (int i = 0; i < parcelsToUpdate.length; i++) {
            LandParcel pomPar = new LandParcel(parcelsToUpdate[i]);
            LandParcel parcel = (LandParcel) this.landParcelDynamicHashing.find(pomPar);
            parcel.deleteBelongingRealEstate(parIdentityNumber);
            this.landParcelDynamicHashing.edit(parcel);
        }
        this.realEstateDynamicHashing.delete(estate);

        // vymazanie data z quadstromu
        ArrayList<Data<CadastralObjectData>> estatesToDeleteInQuadTree = this.realEstateQuadTree.find(this.mapEstateTree.getCoordinatesValue(estate.getGpsCoordinates()));
        for (Data<CadastralObjectData> act : estatesToDeleteInQuadTree) {
            if (act.getData().getIdentityNumber() == parIdentityNumber) {
                this.realEstateQuadTree.delete(act);
            }
        }
        System.out.println("Real estate: " + estate + " is deleted!");
    }

    /**
     * editing data according to identity number
     */
    public void editLandParcel(int parIdentityNumber, GPS[] parCoordinates, String parDescription) {
        LandParcel pomPar = new LandParcel(parIdentityNumber);
        LandParcel parcel = (LandParcel) this.landParcelDynamicHashing.find(pomPar);

        if (Arrays.equals(parcel.getGpsCoordinates(), parCoordinates)) {
            // ak editujem iba desc
            parcel.setDescription(parDescription);
            this.landParcelDynamicHashing.edit(parcel);
            System.out.println("Data successfully updated!");
        } else {
            // ak editujem suradnice
            ArrayList<Data<CadastralObjectData>> arrayOfEstatesOnNewPlace = this.realEstateQuadTree.find(this.mapEstateTree.getCoordinatesValue(parCoordinates));
            if (arrayOfEstatesOnNewPlace.size() < MAX_COUNT_OF_PARCELS) {
                int count = 0;
                for (int i = 0; i < arrayOfEstatesOnNewPlace.size(); i++) {
                    ArrayList<Data<CadastralObjectData>> list  = this.landParcelQuadTree.find(arrayOfEstatesOnNewPlace.get(i).getCoordinates());
                    if (list.size() > count) {
                        count = list.size();
                    }
                }
                if (count <= MAX_COUNT_OF_PARCELS) {

                    // resetovanie prisluchajucich estatov v editovanej parceli
                    parcel.resetBelongingRealEstate();
                    for (int i = 0; i < arrayOfEstatesOnNewPlace.size(); i++) {
                        parcel.addBelongingRealEstate(arrayOfEstatesOnNewPlace.get(i).getData().getIdentityNumber());
                    }
                    // vymazanie smernika zo starych estatov
                    int[] estatesToUpdate = parcel.getBelongingRealEstates();
                    for (int i = 0; i < estatesToUpdate.length; i++) {
                        RealEstate pomRel = new RealEstate(estatesToUpdate[i]);
                        RealEstate estate = (RealEstate) this.realEstateDynamicHashing.find(pomRel);
                        estate.deleteBelongingLandParcel(parIdentityNumber);
                        this.realEstateDynamicHashing.edit(estate);
                    }
                    // pridanie smernikov do novych estatov
                    for (int i = 0; i < arrayOfEstatesOnNewPlace.size(); i++) {
                        RealEstate estateToUpdate = new RealEstate(arrayOfEstatesOnNewPlace.get(i).getData().getIdentityNumber(), null, "", 3);
                        RealEstate estate = (RealEstate) this.realEstateDynamicHashing.find(estateToUpdate);
                        estate.addBelongingLandParcel(parIdentityNumber);
                        this.realEstateDynamicHashing.edit(estate);
                    }

                    // editovanie samotnej parceli
                    parcel.setGpsCoordinates(parCoordinates);
                    parcel.setDescription(parDescription);
                    this.landParcelDynamicHashing.edit(parcel);

                    // editovanie v strome
                    ArrayList<Data<CadastralObjectData>> parcelsToDeleteInQuadTree = this.landParcelQuadTree.find(this.mapParcelTree.getCoordinatesValue(parcel.getGpsCoordinates()));
                    for (Data<CadastralObjectData> act : parcelsToDeleteInQuadTree) {
                        if (act.getData().getIdentityNumber() == parIdentityNumber) {
                            this.landParcelQuadTree.edit(act, this.mapParcelTree.getCoordinatesValue(parCoordinates));
                        }
                    }

                    System.out.println("Land parcel successfully updated");
                } else {
                    System.out.println("Data cannot be edited!");
                }
            } else {
                System.out.println("Data cannot be edited!");
            }
        }
    }

    public void editRealEstate(int parIdentityNumber, int parSerialNumber, GPS[] parCoordinates, String parDescription) {
        RealEstate pomEst = new RealEstate(parIdentityNumber);
        RealEstate estate = (RealEstate) this.realEstateDynamicHashing.find(pomEst);

        if (Arrays.equals(estate.getGpsCoordinates(), parCoordinates)) {
            // ak editujem iba desc
            estate.setDescription(parDescription);
            estate.setSerialNumber(parSerialNumber);
            this.realEstateDynamicHashing.edit(estate);
            System.out.println("Data successfully updated!");
        } else {
            // ak editujem suradnice
            ArrayList<Data<CadastralObjectData>> arrayOfParcelsOnNewPlace = this.landParcelQuadTree.find(this.mapParcelTree.getCoordinatesValue(parCoordinates));
            if (arrayOfParcelsOnNewPlace.size() < MAX_COUNT_OF_ESTATES) {
                int count = 0;
                for (int i = 0; i < arrayOfParcelsOnNewPlace.size(); i++) {
                    ArrayList<Data<CadastralObjectData>> list  = this.realEstateQuadTree.find(arrayOfParcelsOnNewPlace.get(i).getCoordinates());
                    if (list.size() > count) {
                        count = list.size();
                    }
                }
                if (count < MAX_COUNT_OF_ESTATES) {
                    // resetovanie prisluchajucich estatov v editovanej parceli
                    estate.resetBelongingLandParcels();
                    for (int i = 0; i < arrayOfParcelsOnNewPlace.size(); i++) {
                        estate.addBelongingLandParcel(arrayOfParcelsOnNewPlace.get(i).getData().getIdentityNumber());
                    }
                    // vymazanie smernika zo starych estatov
                    int[] parcelsToUpdate = estate.getBelongingLandParcels();
                    for (int i = 0; i < parcelsToUpdate.length; i++) {
                        LandParcel pomPar = new LandParcel(parcelsToUpdate[i]);
                        LandParcel parcel = (LandParcel) this.landParcelDynamicHashing.find(pomPar);
                        parcel.deleteBelongingRealEstate(parIdentityNumber);
                        this.landParcelDynamicHashing.edit(estate);
                    }
                    // pridanie smernikov do novych estatov
                    for (int i = 0; i < arrayOfParcelsOnNewPlace.size(); i++) {
                        LandParcel parcelToUpdate = new LandParcel(arrayOfParcelsOnNewPlace.get(i).getData().getIdentityNumber(), null, "");
                        LandParcel parcel = (LandParcel) this.landParcelDynamicHashing.find(parcelToUpdate);
                        parcel.addBelongingRealEstate(parIdentityNumber);
                        this.landParcelDynamicHashing.edit(parcel);
                    }

                    // editovanie samotnej parceli
                    estate.setGpsCoordinates(parCoordinates);
                    estate.setDescription(parDescription);
                    estate.setSerialNumber(parSerialNumber);
                    this.realEstateDynamicHashing.edit(estate);

                    // editovanie v strome
                    ArrayList<Data<CadastralObjectData>> estatesToDeleteInQuadTree = this.realEstateQuadTree.find(this.mapEstateTree.getCoordinatesValue(estate.getGpsCoordinates()));
                    for (Data<CadastralObjectData> act : estatesToDeleteInQuadTree) {
                        if (act.getData().getIdentityNumber() == parIdentityNumber) {
                            this.realEstateQuadTree.edit(act, this.mapEstateTree.getCoordinatesValue(parCoordinates));
                        }
                    }
                    System.out.println("Real estate successfully updated");
                } else {
                    System.out.println("Data cannot be edited!");
                }
            } else {
                System.out.println("Data cannot be edited!");
            }
        }
    }

    /**
     * generating data
     */
    public void generateLandParcels(int parNumber, int parSize,GPS[] parGps) {
        for (int i = 0; i < parNumber; i++) {
            this.insertLandParcel(this.generator.returnGPSOfObject(parSize,parGps),"LP_" + this.generator.getLandParcelId());
        }
    }

    public void generateRealEstates(int parNumber, int parSize,GPS[] parGps) {
        for (int i = 0; i < parNumber; i++) {
            int numb = generator.getRealEstateId();
            this.insertRealEstate(numb, this.generator.returnGPSOfObject(parSize,parGps),"RE_" + numb);
        }
    }

    public String returnSeqenceOutput(String parName) {
        if (parName.equals("L")) {
            //System.out.println(this.landParcelDynamicHashing.seqenceStringOfTrie());
            return this.landParcelDynamicHashing.seqenceStringOfTrie();
        } else {
            //System.out.println(this.realEstateDynamicHashing.seqenceStringOfTrie());
            return this.realEstateDynamicHashing.seqenceStringOfTrie();
        }
    }


    private void saveDynamicHashing() {

    }

    private void saveQuadTrees() {

    }

    public void save() {

    }


}
