package Structure.QuadTree;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class represents QuadTree node, which contains all needed atributes - coordinates of node, list
 * of children and list of data.
 */

public class QuadTreeNode<T> {

    private static final int CHILDREN = 4;

    private Coordinates coordinates;
    private QuadTreeNode<T>[] children;
    private ArrayList<Data<T>> listOfData;

    private int level;

    public QuadTreeNode(Data<T> parData, Coordinates parCoordinates, int parLevel) {
        this.children = new QuadTreeNode[CHILDREN];
        this.listOfData = new ArrayList<>();
        this.listOfData.add(parData);
        this.coordinates = parCoordinates;
        this.level = parLevel;
        this.initChildren();
    }

    /**
     * Basic reqests
     */
    public boolean isLeaf() {
        return this.getCountOfChildren() == 0;
    }

    public boolean isEmpty() {
        return listOfData.isEmpty();
    }

    public boolean hasNthChild(int parIndex) {
        return this.children[parIndex] != null ? true : false;
    }

    public int numberOfEmptySons() {
        return CHILDREN - this.getCountOfChildren();
    }

    /**
     * return true if in data list are the same data with some coordiantes of data as parameter
     */
    public boolean hasSameCoordinates(Data<T> parData) {
        for (Data<T> data: this.listOfData) {
            if (data.getCoordinates().equals(parData.getCoordinates())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Modifiers
     */
    public void addData(Data<T> parData) {
        this.listOfData.add(parData);
    }

    public void addMultipleData(ArrayList<Data<T>> parData) {
        this.listOfData.addAll(parData);
    }

    public Data<T> removeData(int parIndex) {
        return this.listOfData.remove(parIndex);
    }

    // pay attention if data with this id dont exist!!!
    public Data<T> removeData(Data<T> parData) {
        for (int i = 0; i < this.listOfData.size(); i++) {
            if (this.listOfData.get(i).equals(parData)) {
                return this.listOfData.remove(i);
            }
        }
        return null;
    }

    public void removeAllData() {
        this.listOfData.clear();
    }

    /**
     * Getters and setters
     */
    public ArrayList<Data<T>> getListOfData() {
        return new ArrayList<>(this.listOfData);
    }

    public QuadTreeNode<T>[] getChildren(){
        return Arrays.copyOf(this.children, this.children.length);
    }

    public QuadTreeNode<T> accessToNthSon(int parIndex) {
        return this.children[parIndex];
    }

    public void setChild(int parIndex, QuadTreeNode<T> parNewChildren) {
        this.children[parIndex] = parNewChildren;
    }

    public void removeChild(int parIndex) {
        this.children[parIndex] = null;
    }

    public void removeChildren() {
        for (int i = 0; i < CHILDREN; i++) {
            this.children[i] = null;
        }
    }

    public int getLevel() {
        return this.level;
    }

    public int getCountOfChildren() {
        int countOfChildren = 0;
        for (int i = 0; i < CHILDREN; i++) {
            if(this.children[i] != null) {
                countOfChildren++;
            }
        }
        return countOfChildren;
    }

    // use only if you know node has only one child!!
    public int getIndexOfOnlyOneChild() {
        for (int i = 0; i < CHILDREN; i++) {
            if(this.children[i] != null) {
                return i;
            }
        }
        return -1;
    }

    public void setCoordinates(Coordinates parCoordinates) {
        this.coordinates = parCoordinates;
    }

    public Coordinates getCoordinates() {
        return this.coordinates;
    }

    /**
     * This method is designed for finding area in tree. It returns
     * array of indexes which are connected with wanted area.
     */
    public ArrayList<Integer> getIncludingQuadrants(Coordinates parCoordinatesOfArea) {

        ArrayList<Integer> indices = new ArrayList<>();

        for (int i = 0; i < CHILDREN; i++) {
            if (this.children[i] != null){
                if (Coordinates.belongsToArea(parCoordinatesOfArea, this.children[i].getCoordinates())
                || Coordinates.isIncludingWholeData(parCoordinatesOfArea, this.children[i].getCoordinates())) {
                    indices.add(i);
                }
            }
        }
        return indices;
    }

    /**
     * In case there are more that one data with same coordinates in tree
     */
    public ArrayList<Data<T>> getAllAppropriateData(Coordinates parCoordinates) {
        ArrayList<Data<T>> dataToReturn = new ArrayList<>();
        for (Data<T> data : this.listOfData) {
            if (Coordinates.isIncludingWholeData(parCoordinates, data.getCoordinates())) {
                dataToReturn.add(data);
            }
        }
        return dataToReturn;
    }

    /**
     * another private methods
     */
    private void initChildren() {
        for (int i = 0; i < CHILDREN; i++) {
            this.children[i] = null;
        }
    }

}
