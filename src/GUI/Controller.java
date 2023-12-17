
package GUI;

        import Data.CadastralObject;
        import Data.GPS;
        import Data.Others.CadastralObjectGenerator;
        import Structure.DynamicHashing.IRecord;
        import Structure.QuadTree.Data;
        import Data.LandParcel;
        import Data.RealEstate;

        import javax.swing.*;
        import javax.swing.event.ListSelectionEvent;
        import javax.swing.event.ListSelectionListener;
        import java.awt.*;
        import java.awt.event.ActionEvent;
        import java.awt.event.ActionListener;
        import java.awt.event.ItemEvent;
        import java.awt.event.ItemListener;
        import java.util.ArrayList;

public class Controller {

    private Model model;
    private View view;

    private Data<? extends CadastralObject> isDataToEdit;

    public Controller(Model parCadaster, View parView) {
        this.model = parCadaster;
        this.view = parView;

        this.view.addInsertButtonListener(new InsertButtonListener());
        this.view.addEditButtonListener(new EditButtonListener());
        this.view.addDeleteButtonListener(new DeleteButtonListener());
        this.view.addFindButtonListener(new FindButtonListener());
        this.view.addTreeButtonListener(new TreeButtonListener());
        this.view.addOtherButtonListener(new OtherButtonListener());
        this.view.addMainComboBoxListener(new MainComboBoxListener());
       // this.view.addOutputListSelectionListener(new OutputListSelectionListener());
        this.view.addCreateTreeButtonListener(new CreateTreeButtonListener());
        this.view.addConfirmButtonListener(new ConfirmButtonListener());
        this.view.addConfirmButtonDownListener(new ConfirmButtonDownListener());
        this.view.addGenerateDataButtonListener(new GenerateButtonListener());
        this.view.addLoadDataButtonListener(new LoadDataListener());
        this.view.addSaveDataButtonListener(new SaveDataListener());
        this.view.addCreateDHTreeButtonListener(new CreateDHListener());
        this.view.addSaveAllFilesButtonListener(new SaveAllDataListener());
        this.view.addLoadAllFilesButtonListener(new LoadAllDataListener());
        view.getLoadDataPanel().setVisible(false);
//        this.view.addCheckboxListener(new CheckBoxListener());
    }

    /**
     * Main menu listeners
     */
    class InsertButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            view.getMainPanel().setVisible(true);
            view.getTreePanel().setVisible(false);
            view.getTypeOfObjectPanel().setVisible(true);
            view.getAddObjectPanel().setVisible(true);
            view.getTypeOfFindedObjectPanel().setVisible(false);
            view.setTypeOfObject("Type of object");
            view.setTypeOfObjectChoose("Real Estate","Land Parcel", null);
            view.getIOPanel().setBorder(BorderFactory.createTitledBorder("Insert data"));
            view.getConfirmButtonDown().setText("Create");
            view.getConfirmButton().setVisible(false);
            manageCoordinateTwoPanel(true);
            view.getNumberOfObjects().setVisible(false);
            view.getSizeOfGenerateObjects().setVisible(false);
            view.getScrollPanePointer().setVisible(false);
            view.getCoordinatesOnePanel().setBorder(BorderFactory.createTitledBorder("Coordinates number one:"));
            view.getCoordinatesTwoPanel().setBorder(BorderFactory.createTitledBorder("Coordinates number one:"));
            view.getCoordinatesOnePanel().setVisible(true);
            view.getCoordinatesTwoPanel().setVisible(true);
            view.getIDNumberFiled().setVisible(false);
            view.getLowerPanel().setPreferredSize(new Dimension(800,150));
            view.getOtherPanel().setVisible(false);
        }
    }

    class EditButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            view.getMainPanel().setVisible(true);
            view.getTreePanel().setVisible(false);
            view.getTypeOfObjectPanel().setVisible(true);
            view.getTypeOfFindedObjectPanel().setVisible(false);
            view.getOutputPanel().setVisible(true);
            view.getAddObjectPanel().setVisible(false);
            view.setTypeOfObject("Type of object");
            view.setTypeOfObjectChoose("Real Estate","Land Parcel", null);
            view.getIOPanel().setBorder(BorderFactory.createTitledBorder("Edit data"));
            view.getConfirmButton().setText("Edit object");
            view.getConfirmButton().setVisible(true);
            manageCoordinateTwoPanel(false);
            view.getNumberOfObjects().setVisible(false);
            view.getScrollPanePointer().setVisible(false);
            view.getSizeOfGenerateObjects().setVisible(false);
            view.getCoordinatesOnePanel().setBorder(BorderFactory.createTitledBorder("Coordinates number one:"));
            view.getCoordinatesTwoPanel().setBorder(BorderFactory.createTitledBorder("Coordinates number one:"));
            view.getCoordinatesOnePanel().setVisible(false);
            view.getCoordinatesTwoPanel().setVisible(false);
            view.getIDNumberFiled().setVisible(true);
            view.getIDNumberFiled().setBorder(BorderFactory.createTitledBorder("Id number"));
            view.getLowerPanel().setPreferredSize(new Dimension(800,300));
            view.getOtherPanel().setVisible(false);


        }
    }

    class DeleteButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            view.getMainPanel().setVisible(true);
            view.getOutputPanel().setVisible(true);
            view.getTreePanel().setVisible(false);
            view.getTypeOfObjectPanel().setVisible(true);
            view.getAddObjectPanel().setVisible(false);
            view.getTypeOfFindedObjectPanel().setVisible(false);
            view.setTypeOfObject("Type of object");
            view.setTypeOfObjectChoose("Real Estate","Land Parcel", null);
            view.getIOPanel().setBorder(BorderFactory.createTitledBorder("Delete data"));
            view.getConfirmButton().setText("Delete object");
            view.getConfirmButton().setVisible(true);
            manageCoordinateTwoPanel(false);
            view.getScrollPanePointer().setVisible(false);
            view.getNumberOfObjects().setVisible(false);
            view.getSizeOfGenerateObjects().setVisible(false);
            view.getCoordinatesOnePanel().setBorder(BorderFactory.createTitledBorder("Coordinates number one:"));
            view.getCoordinatesTwoPanel().setBorder(BorderFactory.createTitledBorder("Coordinates number one:"));
            view.getCoordinatesOnePanel().setVisible(false);
            view.getCoordinatesTwoPanel().setVisible(false);
            view.getIDNumberFiled().setVisible(true);
            view.getIDNumberFiled().setBorder(BorderFactory.createTitledBorder("Id number"));
            view.getLowerPanel().setPreferredSize(new Dimension(800,300));
            view.getOtherPanel().setVisible(false);

        }
    }

    class FindButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            view.getMainPanel().setVisible(true);
            view.getTreePanel().setVisible(false);
            view.getTypeOfObjectPanel().setVisible(true);
            view.getOutputPanel().setVisible(true);
            view.getAddObjectPanel().setVisible(false);
            view.setTypeOfObject("Type of object");
            view.setTypeOfObjectChoose("Real Estate","Land Parcel", null);
            view.getIOPanel().setBorder(BorderFactory.createTitledBorder("Find data"));
            view.getConfirmButton().setText("Find object");
            view.getConfirmButton().setVisible(true);
            //view.getScrollPanePointer().setVisible(true);
            // view.getTypeOfFindedObjectPanel().setVisible(true);
            view.getNumberOfObjects().setVisible(false);
            view.getSizeOfGenerateObjects().setVisible(false);
            view.getCoordinatesOnePanel().setBorder(BorderFactory.createTitledBorder("Coordinates number one:"));
            view.getCoordinatesTwoPanel().setBorder(BorderFactory.createTitledBorder("Coordinates number one:"));
            view.getCoordinatesOnePanel().setVisible(false);
            view.getCoordinatesTwoPanel().setVisible(false);
            view.getIDNumberFiled().setVisible(true);
            view.getIDNumberFiled().setBorder(BorderFactory.createTitledBorder("Id number"));
            view.getLowerPanel().setPreferredSize(new Dimension(800,300));
            view.getOtherPanel().setVisible(false);
        }
    }

    class TreeButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            view.getTreePanel().setVisible(true);
            view.getMainPanel().setVisible(false);
            view.getOutputPanel().setVisible(false);
            view.getLoadDataPanel().setVisible(false);
            view.getIOPanel().setBorder(BorderFactory.createTitledBorder("Create quad tree"));
            view.getOtherPanel().setVisible(false);
        }
    }

    class LoadDataListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            loadData();
        }
    }

    class SaveDataListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            saveData();
        }
    }
    class OtherButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            view.getOtherPanel().setVisible(true);
            view.getMainPanel().setVisible(false);
            view.getTreePanel().setVisible(false);
            view.getIOPanel().setBorder(BorderFactory.createTitledBorder("Create dynamic hashing tree"));
        }
    }

    class GenerateButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            view.getTypeOfObjectPanel().setVisible(true);
            view.setTypeOfObject("Type of object");
            view.getMainPanel().setVisible(true);
            view.getAddObjectPanel().setVisible(false);
            view.getOutputPanel().setVisible(true);
            view.getTreePanel().setVisible(false);
            view.getIOPanel().setBorder(BorderFactory.createTitledBorder("Generate data"));
            view.setTypeOfObjectChoose("Real Estate","Land Parcel",null);
            view.getConfirmButton().setText("Generate");
            view.getNumberOfObjects().setVisible(true);
            view.getTypeOfFindedObjectPanel().setVisible(false);
            manageCoordinateTwoPanel(true);
            view.getScrollPanePointer().setVisible(false);
            view.getNumberOfObjects().setBorder(BorderFactory.createTitledBorder("Count"));
            view.getSizeOfGenerateObjects().setBorder(BorderFactory.createTitledBorder("Size"));
            view.getCoordinatesOnePanel().setBorder(BorderFactory.createTitledBorder("First coordinate of range:"));
            view.getCoordinatesTwoPanel().setBorder(BorderFactory.createTitledBorder("Second coordinate of range:"));
            view.getSizeOfGenerateObjects().setVisible(true);
            view.getCoordinatesOnePanel().setVisible(true);
            view.getCoordinatesTwoPanel().setVisible(true);
            view.getIDNumberFiled().setVisible(false);
            view.getOtherPanel().setVisible(false);
            view.getLowerPanel().setPreferredSize(new Dimension(800,150));
//            for (Component cp : view.getCoordinatesTwoPanel().getComponents() ) {
//////                    cp.setEnabled(false);
//////                }
        }
    }

    /**
     * Main Panel listeners
     */
    class ConfirmButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (view.getConfirmButton().getText().equals("Find object")) {
                //find();
                view.updateList(findWithData());
            } else if (view.getConfirmButton().getText().equals("Delete object")) {
                delete();
                view.updateList(returnStringOfFile());
            } else if (view.getConfirmButton().getText().equals("Edit object")) {
                changeEditView(true);
                setEditValues();
                view.updateList(returnStringOfFile());

            } else if (view.getConfirmButton().getText().equals("Generate")) {
                generateObjects();
                view.updateList(returnStringOfFile());
            }
        }
    }

    class ConfirmButtonDownListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (view.getConfirmButtonDown().getText().equals("Create")) {
                insert();
            } else if (view.getConfirmButtonDown().getText().equals("Edit")) {
                edit();
                changeEditView(false);
                view.updateList(returnStringOfFile());
            }
        }
    }

    class MainComboBoxListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object item = e.getItem();
                returnAction(item.toString());

            }
        }
    }

    /**
     * Create Tree listeners
     */
    class CreateTreeButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            createQuadTree();
        }
    }

    /**
     * create Dh panel listeners
     */
    class CreateDHListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            createTrie();
        }
    }

    class SaveAllDataListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            save();
        }
    }

    class LoadAllDataListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            load();
        }
    }

//    /**
//     * Output panel listener
//     */
//    class OutputListSelectionListener implements ListSelectionListener {
//        @Override
//        public void valueChanged(ListSelectionEvent e) {
//
//            if (!e.getValueIsAdjusting()) {
//                Object selectedValue = view.getListOfOutput().getSelectedValue();
//                if (view.getConfirmButton().getText().equals("Get objects to edit")) {
//                    if ((Data<? extends CadastralObject>) selectedValue != null) {
//                        changeEditView(true);
//                        setEditValues((Data<? extends CadastralObject>) selectedValue);
//                        isDataToEdit = (Data<? extends CadastralObject>) selectedValue;
//                    }
//                } else if (view.getConfirmButton().getText().equals("Get objects to delete")) {
//                    if ((Data<? extends CadastralObject>) selectedValue != null) {
//                        deleteData((Data<? extends CadastralObject>) selectedValue);
//                        view.removeData((Data<? extends CadastralObject>) selectedValue);
//                    }
//                } else if (view.getConfirmButton().getText().equals("Find")) {
//                    if ((Data<? extends CadastralObject>) selectedValue != null) {
//                        view.updatePointerList(showData((Data<? extends CadastralObject>) selectedValue));
//                    }
//                }
//            }
//        }
//    }
//

    /*
      ---- WORK WITH MODEL ----
     */

    /**
     * creating trees
     */
    public void createQuadTree() {
        if (view.getTypeOfTreeOption().equals("Real Estate")) {
            this.model.createRealEstateQuadTree(returnGPSForTreeFromView(), this.view.getMaxDepthOfTree());
        } else {
            this.model.createLandParcelQuadTree(returnGPSForTreeFromView(),this.view.getMaxDepthOfTree());
        }
    }

    public void createTrie() {
        if (view.getTypeOfObjectDHValue().equals("Real Estate")) {
            this.model.inicializeDynamicHashingForRealEstates(view.getMainFileBlockFactor(),view.getOverfillingFileBlockFactor(),"E","E");
        } else {
            this.model.inicializeDynamicHashingForLandParcels(view.getMainFileBlockFactor(),view.getOverfillingFileBlockFactor(),"P","P");
        }
    }

    /**
     * crud operation
     */
    public void insert() {
        if (view.getTypeOfObjectValue().equals("Real Estate")) {
            this.model.insertRealEstate(view.getNumberOfObject(),this.returnGPSFromView(),view.getAddDescription());
        } else {
            this.model.insertLandParcel(this.returnGPSFromView(),view.getAddDescription());
        }
    }

    public String findWithData() {
        if (view.getTypeOfObjectValue().equals("Real Estate")) {
            return this.model.findRealEstatesWithBelongingLandParcelsString(view.getIdNumber());
        } else {
            return this.model.findLandParcelWithBelongingEstatesString(view.getIdNumber());
        }
    }

    public void delete() {
        if (view.getTypeOfObjectValue().equals("Real Estate")) {
            this.model.deleteRealEstate(view.getIdNumber());
        } else {
            this.model.deleteLandParcel(view.getIdNumber());
        }
    }

    public void edit() {
        if (view.getTypeOfObjectValue().equals("Real Estate")) {
            this.model.editRealEstate(view.getIdNumber(),view.getNumberOfObject(),this.returnGPSFromView(),view.getAddDescription());
        } else {
            this.model.editLandParcel(view.getIdNumber(),this.returnGPSFromView(),view.getAddDescription());
        }
    }

    public void generateObjects() {
        if (view.getTypeOfObjectValue().equals("Real Estate")) {
            this.model.generateRealEstates(view.getNumberOfGeneratedObjects(),view.getSizeOfGeneratedObjectsNumber(),returnGPSFromView());
        } else {
            this.model.generateLandParcels(view.getNumberOfGeneratedObjects(),view.getSizeOfGeneratedObjectsNumber(),returnGPSFromView());
        }
    }

    public String returnStringOfFile() {
        if (view.getTypeOfObjectValue().equals("Real Estate")) {
            return this.model.returnSeqenceOutput("R");
        } else {
            return this.model.returnSeqenceOutput("L");
        }
    }

    public void save() {
        this.model.save(view.getNameForSaveDataValue());
    }

    public void load() {
        this.model.load(view.getNameForLoadDataValue());
    }


//    public ArrayList<Data<? extends CadastralObject>> returnAllData() {
//        return this.model.returnAllData();
//    }

//    public ArrayList<LandParcel> returnBelongingParcels(Data<RealEstate> parData) {
//        return this.model.returnAllIncludingParcels(parData);
//    }
//
//    public ArrayList<RealEstate> returnBelongingEstates(Data<LandParcel> parData) {
//        return this.model.returnAllIncludingEstates(parData);
//    }


    public ArrayList<Data<? extends CadastralObject>> generateLandParcels() {
        return null;
        //TODO
        //return this.model.generateLandParcels(view.getNumberOfGeneratedObjects(),view.getSizeOfGeneratedObjectsNumber(),returnGPSFromView());
    }

    public ArrayList<Data<? extends CadastralObject>> generateRealEstates() {
        return null;
        //TODO
       // return this.model.generateRealEstates(view.getNumberOfGeneratedObjects(),view.getSizeOfGeneratedObjectsNumber(),returnGPSFromView());
    }

    public ArrayList<Data<? extends CadastralObject>> generateData() {
        return null;
        //TODO
        //return this.model.generateData(view.getNumberOfGeneratedObjects(),view.getSizeOfGeneratedObjectsNumber(),returnGPSFromView());
    }


    public void deleteData(Data<? extends CadastralObject> dataToDelete) {
        //this.model.delete(dataToDelete);
        // TODO
    }

    public void editeData(Data<? extends CadastralObject> dataToDelete) {
//        if (dataToDelete.getData().isInstanceOf().equals(CadastralObject.TypeOfCadastralObject.LAND_PARCEL)) {
//            this.model.editLandParcelData((Data<LandParcel>)dataToDelete,view.getNumberOfObject(),returnGPSFromView(),view.getAddDescription());
//        } else {
//            this.model.editRealEstateData((Data<RealEstate>)dataToDelete,view.getNumberOfObject(),returnGPSFromView(),view.getAddDescription());
//        }
        // TODO
    }

    public ArrayList<? extends CadastralObject> showData(Data<? extends CadastralObject> data) {

//        if (data.getData().isInstanceOf().equals(CadastralObject.TypeOfCadastralObject.LAND_PARCEL)) {
//            return this.model.returnAllIncludingEstates((Data<LandParcel>)data);
//        } else {
//            return this.model.returnAllIncludingParcels((Data<RealEstate>)data);
//        }
        return null;
        //TODO
    }


    /**
     * Private methods for work with data
     */

    /**
     * extract GPS coordinates of first input of cooridnates
     */
    private GPS returnFirstGpsFromView() {
        GPS.Latitude latOne;
        GPS.Longitude longOne;

        if (view.getLatitudeOneOption().equals("North")) {
            latOne = GPS.Latitude.NORTH;
        } else {
            latOne = GPS.Latitude.SOUTH;
        }

        if (view.getLongitudeOneOption().equals("West")) {
            longOne = GPS.Longitude.WEST;
        } else {
            longOne = GPS.Longitude.EAST;
        }

        GPS gpsOne = new GPS(latOne,view.getLatitudeOnePosition(),longOne,view.getLongitudeOnePosition());
        return gpsOne;
    }

    /**
     * extract GPS array for work with data
     */
    private GPS[] returnGPSFromView() {
        GPS.Latitude latOne;
        GPS.Longitude longOne;
        GPS.Latitude latTwo;
        GPS.Longitude longTwo;

        if (view.getLatitudeOneOption().equals("North")) {
            latOne = GPS.Latitude.NORTH;
        } else {
            latOne = GPS.Latitude.SOUTH;
        }

        if (view.getLongitudeOneOption().equals("West")) {
            longOne = GPS.Longitude.WEST;
        } else {
            longOne = GPS.Longitude.EAST;
        }

        if (view.getLatitudeTwoOption().equals("North")) {
            latTwo = GPS.Latitude.NORTH;
        } else {
            latTwo = GPS.Latitude.SOUTH;
        }

        if (view.getLongitudeTwoOption().equals("West")) {
            longTwo = GPS.Longitude.WEST;
        } else {
            longTwo = GPS.Longitude.EAST;
        }

        GPS gpsOne = new GPS(latOne,view.getLatitudeOnePosition(),longOne,view.getLongitudeOnePosition());
        GPS gpsTwo = new GPS(latTwo,view.getLatitudeTwoPosition(),longTwo,view.getLongitudeTwoPosition());
        GPS[] gps = {gpsOne,gpsTwo};
        return gps;
    }

    /**
     * extract GPS array for range of tree
     */
    private GPS[] returnGPSForTreeFromView() {
        GPS.Latitude latOne;
        GPS.Longitude longOne;
        GPS.Latitude latTwo;
        GPS.Longitude longTwo;
        if (view.getLatitudeOneTreeOption().equals("North")) {
            latOne = GPS.Latitude.NORTH;
        } else {
            latOne = GPS.Latitude.SOUTH;
        }

        if (view.getLongitudeOneTreeOption().equals("West")) {
            longOne = GPS.Longitude.WEST;
        } else {
            longOne = GPS.Longitude.EAST;
        }

        if (view.getLatitudeTwoTreeOption().equals("North")) {
            latTwo = GPS.Latitude.NORTH;
        } else {
            latTwo = GPS.Latitude.SOUTH;
        }

        if (view.getLongitudeTwoTreeOption().equals("West")) {
            longTwo = GPS.Longitude.WEST;
        } else {
            longTwo = GPS.Longitude.EAST;
        }

        GPS gpsOne = new GPS(latOne,view.getLatitudeOneTreePosition(),longOne,view.getLongitudeOneTreePosition());
        GPS gpsTwo = new GPS(latTwo,view.getLatitudeTwoTreePosition(),longTwo,view.getLongitudeTwoTreePosition());
        if (gpsOne.isBiggerThan(gpsTwo) == -1) {
            GPS[] gps = {gpsOne,gpsTwo};
            return gps;
        } else {
            GPS[] gps = {gpsTwo,gpsOne};
            return gps;
        }
    }

    /**
     * save data
     */
    public void saveData() {
        //this.model.saveTrees(view.getTextSaveData());
        // TODO
    }

    public void loadData() {
//        this.model.loadTrees(view.getTextLoadData());
//        // TODO
    }


    /**
     * change view of some items according to value of combobox
     */
    private void returnAction(String parString) {
        switch (parString) {
            case "Real Estate" -> {
                view.getObjectNumber().setText("Serial number");
                view.getObjectNumber().setVisible(true);
                view.getNumberOfObjectFiled().setVisible(true);
                view.updateList(returnStringOfFile());
            }
            case "Land Parcel" -> {
                //view.getObjectNumber().setText("Parcel number");
                view.getObjectNumber().setVisible(false);
                view.getNumberOfObjectFiled().setVisible(false);
                view.updateList(returnStringOfFile());
            }
//            case "Find by point" -> {
////                view.getCoordinatesTwoPanel().setEnabled(false);
////                for (Component cp : view.getCoordinatesTwoPanel().getComponents() ) {
////                    cp.setEnabled(false);
////                }
//                manageCoordinateTwoPanel(false);
//                manageFindObjectPanel(true);
//            }
//            case "Find in area" -> {
//                manageCoordinateTwoPanel(true);
//                manageFindObjectPanel(false);
//            }
        }
    }

    /**
     * managing visibility of panel
     */
    private void manageCoordinateTwoPanel(Boolean setValue) {
        view.getCoordinatesTwoPanel().setEnabled(setValue);
        for (Component cp : view.getCoordinatesTwoPanel().getComponents() ) {
            cp.setEnabled(setValue);
        }
    }

    private void changeEditView(Boolean bol) {
//        view.getMainPanel().setVisible(true);
//        view.getAddObjectPanel().setVisible(true);
//        view.getTypeOfObjectPanel().setVisible(false);
//        view.getOutputPanel().setVisible(false);
//        view.getIOPanel().setBorder(BorderFactory.createTitledBorder("Edit data"));
//        view.getConfirmButtonDown().setText("Edit");
//        view.getConfirmButton().setVisible(false);
//        manageCoordinateTwoPanel(true);
        view.getMainPanel().setVisible(bol);
        view.getAddObjectPanel().setVisible(bol);
        view.getTypeOfObjectPanel().setVisible(!bol);
        view.getOutputPanel().setVisible(!bol);
        view.getIOPanel().setBorder(BorderFactory.createTitledBorder("Edit data"));
        view.getConfirmButtonDown().setText("Edit");
        view.getConfirmButton().setVisible(!bol);
        manageCoordinateTwoPanel(bol);
        view.getCoordinatesOnePanel().setVisible(bol);
        view.getCoordinatesTwoPanel().setVisible(bol);
        if (bol) {
            view.getLowerPanel().setPreferredSize(new Dimension(800, 150));
        } else {
            view.getLowerPanel().setPreferredSize(new Dimension(800, 300));
        }
        view.getIDNumberFiled().setVisible(!bol);
    }

    private void setEditValues() {
        CadastralObject data = null;
        if (view.getTypeOfObjectValue().equals("Real Estate")) {
            data = model.findRealEstatesReal(view.getIdNumber());
        } else {
            data = model.findLandParcelReal(view.getIdNumber());
        }

        view.setLatitudeOnePosition(data.getGpsCoordinates()[0].getLatitudePosition());
        view.setLatitudeTwoPosition(data.getGpsCoordinates()[1].getLatitudePosition());

        view.setLongitudeOnePosition(data.getGpsCoordinates()[0].getLongitudePosition());
        view.setLongitudeTwoPosition(data.getGpsCoordinates()[1].getLongitudePosition());
        if (data.getGpsCoordinates()[0].getLatitude().equals(GPS.Latitude.NORTH)) {
            view.setLatitudeOneOption(0);
        } else {
            view.setLatitudeOneOption(1);
        }

        if (data.getGpsCoordinates()[1].getLatitude().equals(GPS.Latitude.NORTH)) {
            view.setLatitudeTwoOption(0);
        } else {
            view.setLatitudeTwoOption(1);
        }

        if (data.getGpsCoordinates()[0].getLongitude().equals(GPS.Longitude.WEST)) {
            view.setLongitudeOneOption(0);
        } else {
            view.setLongitudeOneOption(1);

        }

        if (data.getGpsCoordinates()[1].getLongitude().equals(GPS.Longitude.WEST)) {
            view.setLongitudeTwoOption(0);
        } else {
            view.setLongitudeTwoOption(1);

        }

        if (data.isInstanceOf().equals(CadastralObject.TypeOfCadastralObject.LAND_PARCEL)) {
            setLandParcelData((LandParcel) data);
        } else {
            setRealEstateData((RealEstate) data);
        }
    }

    private void setLandParcelData(LandParcel data) {
        view.setAddDescription(data.getDescription());
    }

    private void setRealEstateData(RealEstate data) {
        view.setAddDescription(data.getDescription());
        view.setNumberOfObject(data.getSerialNumber());
    }

    public void manageFindObjectPanel(Boolean man) {
        view.getTypeOfFindedObjectPanel().setEnabled(man);
        for (Component cp : view.getTypeOfFindedObjectPanel().getComponents() ) {
            cp.setEnabled(man);
        }
    }
}