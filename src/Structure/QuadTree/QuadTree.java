package Structure.QuadTree;

import java.util.*;

/**
 * Class represents univeral QuadTree, into which can be inserted any data.
 * This data needs to be type class Data, which holds needed things for
 * data inserted in quadtree - coordinates and uniqe id - and of course
 * data itself.
 */
public class QuadTree<T> {

    private int maxDepth;
    private int depth;
    private Coordinates rangeOfTree;
    private QuadTreeNode<T> root;

    private Boolean optimalizationOn;
    private double scaleParameter;
    private int numberOfItems;

    public QuadTree(
            double parMinimumX,
            double parMaximumX,
            double parMinimumY,
            double parMaximumY,
            int parMaxDepth
    ){
        this.maxDepth = parMaxDepth;
        this.root = null;
        this.rangeOfTree = new Coordinates(parMinimumX,parMaximumX,parMinimumY,parMaximumY);
        this.depth = 0;
        this.numberOfItems = 0;
        this.optimalizationOn = false;
    }

    public QuadTree(
            Coordinates parCoordinates,
            int parMaxDepth
    ) {
        this.maxDepth = parMaxDepth;
        this.root = null;
        this.rangeOfTree = new Coordinates(parCoordinates);
        this.depth = 0;
        this.numberOfItems = 0;
        this.optimalizationOn = false;
    }

    /**
     * Method which is called when optimalization is on which reset values of tree
     */
    private void reinicializeValueOfTree(Coordinates newRange) {
        this.depth = 0;
        this.numberOfItems = 0;
        this.root = null;
        this.rangeOfTree = newRange;
        this.optimalizationOn = false;
    }

    /**
     * Getters and requests
     */
    public QuadTreeNode<T> getRoot() {
        return root;
    }

    public int getDepth() {
        return this.depth;
    }

    public int getMaxDepth() {
        return this.maxDepth;
    }

    public boolean isEmpty() {
        return this.root == null;
    }

    public int getNumberOfItems() {
        return this.numberOfItems;
    }

    public Coordinates getRangeOfTree() {
        return this.rangeOfTree;
    }

    public void setScaleParameter(double parScale) {
        this.scaleParameter = parScale;
    }

    public boolean isOptimalizate() {
        return this.optimalizationOn;
    }

    public void setOptimalizationOn(boolean parBool){
        this.optimalizationOn = parBool;
    }

    /**
     * Private class used for obtaining data in method FindAppropriateNodeData,
     * for next usage in deleting data and another processing.
     */
    private class Result {

        private QuadTreeNode<T> searchedNode;
        private LinkedList<QuadTreeNode<T>> parents;
        private LinkedList<Integer> indices;

        private Result(
                QuadTreeNode<T> parSearchedNode,
                LinkedList<QuadTreeNode<T>> parParents,
                LinkedList<Integer> parIndices
        ){
            this.searchedNode = parSearchedNode;
            this.parents = parParents;
            this.indices = parIndices;

        }
        public void addParent(QuadTreeNode<T> parNode) {
            this.parents.add(parNode);
        }

        public QuadTreeNode<T> getSearchedNode() {
            return this.searchedNode;
        }

        public LinkedList<QuadTreeNode<T>> getParents() {
            return this.parents;
        }

        public LinkedList<Integer> getIndices() {
            return this.indices;
        }
    }

    /**
     * Method will find appropriate node for next precessing.
     */
    private QuadTreeNode<T> findAppropriateNode(Coordinates parCoordinates, QuadTreeNode<T> parCurrent) {

        QuadTreeNode<T> current = parCurrent;
        boolean nodeIsFind = false;

        while (!nodeIsFind) {
            int quadrant = Coordinates.isFitsToQuadrant(current.getCoordinates(), parCoordinates);

            if (quadrant == -1) {
                nodeIsFind = true;
            } else if (!current.isLeaf()) {
                if (current.hasNthChild(quadrant)) {
                    current = current.accessToNthSon(quadrant);
                } else if (!current.hasNthChild(quadrant)) {
                    nodeIsFind = true;
                }
            } else if (current.isLeaf()) {
                nodeIsFind = true;
            }
        }
        return current;
    }

    /**
     * Method will find appropriate node for next precessing. Difference between this method
     * and method about is in return value. In this method btained data are storage in Result,
     * where are list of parents, indicates of quadrant and of course searched node.
     */
    private Result findAppropriateNodeData(Coordinates parCoordinates, QuadTreeNode<T> parCurrent) {

        LinkedList<QuadTreeNode<T>> parents = new LinkedList<>();
        LinkedList<Integer> indices = new LinkedList<>();

        QuadTreeNode<T> current = parCurrent;
        boolean nodeIsFind = false;

        while (!nodeIsFind) {
            int quadrant = Coordinates.isFitsToQuadrant(current.getCoordinates(), parCoordinates);

            if (quadrant == -1) {
                nodeIsFind = true;
            } else if (!current.isLeaf()) {
                if (current.hasNthChild(quadrant)) {

                    indices.add(quadrant);
                    parents.add(current);
                    current = current.accessToNthSon(quadrant);

                } else if (!current.hasNthChild(quadrant)) {
                    nodeIsFind = true;
                }
            } else if (current.isLeaf()) {
                nodeIsFind = true;
            }
        }
        return new Result(current,parents,indices);
    }

    /**
     * Method ensure inserting data into tree.
     */
    public void insert(Data<T> parData) {
        insert(parData, this.getRoot());
    }

    public void insert(Data<T> parData, QuadTreeNode<T> parStartNode) {
        if (!Coordinates.isIncludingWholeData(this.rangeOfTree,parData.getCoordinates())) {
            System.out.println(parData.getCoordinates().getLowerX() + " " + parData.getCoordinates().getUpperX() + " " + parData.getCoordinates().getLowerY() + " " + parData.getCoordinates().getUpperY());
            System.out.println("data not fints to tree");
            return;
        }

        this.numberOfItems ++;

        if (this.isEmpty()) {
            Coordinates newCoordinates = new Coordinates(this.rangeOfTree);
            QuadTreeNode<T> newNode = new QuadTreeNode<T>(parData, newCoordinates,1);
            this.root = newNode;
            this.depth = 1;
            return;
        }

        ArrayList<Data<T>> dataToInsert = new ArrayList<Data<T>>();
        dataToInsert.add(parData);
        QuadTreeNode<T> searchedNode = this.findAppropriateNode(dataToInsert.get(0).getCoordinates(), parStartNode);

        while (!dataToInsert.isEmpty()) {
            int quadrant = Coordinates.isFitsToQuadrant(searchedNode.getCoordinates(), dataToInsert.get(0).getCoordinates());

            if (quadrant == -1) {
                searchedNode.addData(dataToInsert.remove(0));
            } else if (!searchedNode.isLeaf()) {
                QuadTreeNode<T> newNode = new QuadTreeNode<T>(dataToInsert.remove(0),Coordinates.coordinatesOfNewNode(quadrant, searchedNode.getCoordinates()),searchedNode.getLevel() + 1);
                searchedNode.setChild(quadrant, newNode);
            } else if (searchedNode.isLeaf()) {
                if (searchedNode.getLevel() == this.maxDepth) {
                    searchedNode.addMultipleData(dataToInsert);
                    dataToInsert.clear();
                } else if (searchedNode.hasSameCoordinates(dataToInsert.get(0))) {
                    searchedNode.addData(dataToInsert.remove(0));
                } else {
                    dataToInsert.addAll(searchedNode.getListOfData());
                    searchedNode.removeAllData();

                    Iterator<Data<T>> iterator = dataToInsert.iterator();
                    while (iterator.hasNext()) {
                        Data<T> data = iterator.next();
                        int newQuadrant = Coordinates.isFitsToQuadrant(searchedNode.getCoordinates(),data.getCoordinates());
                        if (newQuadrant == -1) {
                            searchedNode.addData(data);
                            iterator.remove();
                        } else if (!searchedNode.hasNthChild(newQuadrant)) {

                            QuadTreeNode<T> newNode = new QuadTreeNode<T>(data, Coordinates.coordinatesOfNewNode(newQuadrant, searchedNode.getCoordinates()), searchedNode.getLevel() + 1);
                            searchedNode.setChild(newQuadrant, newNode);

                            if (searchedNode.accessToNthSon(newQuadrant).getLevel() > this.depth) {
                                this.depth = searchedNode.accessToNthSon(newQuadrant).getLevel();
                            }
                            iterator.remove();
                        } else if (searchedNode.hasNthChild(newQuadrant) &&
                                searchedNode.accessToNthSon(newQuadrant).hasSameCoordinates(data)) {
                            searchedNode.accessToNthSon(newQuadrant).addData(data);
                            iterator.remove();
                        }
                    }
                    if (!dataToInsert.isEmpty()) {
                        searchedNode = findAppropriateNode(dataToInsert.get(0).getCoordinates(), searchedNode);
                    }
                }
            }
        }

        if (this.optimalizationOn && (this.numberOfItems % 100 == 0)) {
            this.tryToOptimalize();
        }

    }

    /**
     * Return list of all data which are in tree witch root is given node
     */
    public ArrayList<Data<T>> getAllDataInSubTree(QuadTreeNode<T> parNode) {

        ArrayList<Data<T>> listToReturn = new ArrayList<>();
        Stack<QuadTreeNode<T>> nodesToProcess = new Stack<>();

        nodesToProcess.push(parNode);

        while (!nodesToProcess.isEmpty()) {

            QuadTreeNode<T> current = nodesToProcess.pop();
            listToReturn.addAll(current.getListOfData());

            if (!current.isLeaf()) {
                for (QuadTreeNode<T> child: current.getChildren()) {
                    if (child != null) {
                        nodesToProcess.push(child);
                    }
                }
            }
        }
        return listToReturn;
    }

    /**
     * Return all data which have given coordinates
     * @param parCoordinates coordinates of data which we want to find
     */
    public ArrayList<Data<T>> find(Coordinates parCoordinates) {
        if (!Coordinates.isIncludingWholeData(this.rangeOfTree,parCoordinates)) {
            return null;
        }

        ArrayList<Data<T>> listToReturn = new ArrayList<>();
        if (this.root != null) {
            Stack<QuadTreeNode<T>> nodesToProcess = new Stack<>();
            nodesToProcess.push(this.root);

            while (!nodesToProcess.isEmpty()) {
                QuadTreeNode<T> current = nodesToProcess.pop();
                for (int i = 0; i < current.getListOfData().size(); i++) {
                    if (Coordinates.belongsToArea(current.getListOfData().get(i).getCoordinates(), parCoordinates) ||
                            Coordinates.isIncludingWholeData(current.getListOfData().get(i).getCoordinates(), parCoordinates)) {
                        listToReturn.add(current.getListOfData().get(i));
                    }

                }
                if (!current.isLeaf()) {
                    for (QuadTreeNode<T> child : current.getChildren()) {
                        if (child != null) {
                            nodesToProcess.push(child);
                        }
                    }
                }
            }
        }
        return listToReturn;
    }

    /**
     * Returns all data in given area
     * @param parCoordinates coordinates of area in which we want to find data
     */
    public ArrayList<Data<T>> findInArea(Coordinates parCoordinates) {
        if (!Coordinates.isIncludingWholeData(this.rangeOfTree,parCoordinates)) {
            return null;
        }

        ArrayList<Data<T>> listToReturn = new ArrayList<>();
        Stack<QuadTreeNode> nodesToProcess = new Stack<>();

        if (this.root != null) {

            nodesToProcess.push(this.root);

            while (!nodesToProcess.isEmpty()) {

                QuadTreeNode<T> current = nodesToProcess.pop();

                if (Coordinates.isIncludingWholeData(parCoordinates, current.getCoordinates())) {
                    listToReturn.addAll(this.getAllDataInSubTree(current));
                } else {
                    if (!current.isLeaf()) {
                        ArrayList<Integer> indicesOfSons = current.getIncludingQuadrants(parCoordinates);
                        for (int index : indicesOfSons) {
                            nodesToProcess.push(current.accessToNthSon(index));
                        }
                    }
                    listToReturn.addAll(current.getAllAppropriateData(parCoordinates));
                }
            }
        }
        return listToReturn;
    }

    /**
     * Method delete appropriate data from tree
     */
    public void delete(Data<T> parData) {

        Result result = this.findAppropriateNodeData(parData.getCoordinates(),this.root);

        QuadTreeNode<T> nodeWithDataToDelete = result.getSearchedNode();
        LinkedList<QuadTreeNode<T>> parents = result.getParents();
        LinkedList<Integer> indices = result.getIndices();
        this.numberOfItems --;
        nodeWithDataToDelete.removeData(parData);

        boolean correctlyRemoved = false;

        while (!correctlyRemoved) {

            if (!nodeWithDataToDelete.isLeaf()) {
                correctlyRemoved = true;
            } else if (nodeWithDataToDelete.isLeaf() && !nodeWithDataToDelete.isEmpty()) {
                correctlyRemoved = true;
            } else if (nodeWithDataToDelete.isLeaf() && nodeWithDataToDelete.isEmpty()) {
                // vymazanie nodeWithDataToDelete
                if (nodeWithDataToDelete.equals(this.root)) {
                    correctlyRemoved = true;
                    this.root = null;
                } else {

                    QuadTreeNode<T> actualParent = parents.getLast();
                    // ak je tento node leaf a je prazdy mazem ho. Dole idem dalej zistovat ci ma brata.
                    actualParent.removeChild(indices.removeLast());

                    // ak ma brata chcem ho vymazat. ale pozor!! jedine ak je brat leaf!!! :)
                    if (actualParent.getCountOfChildren() == 1) {
                        QuadTreeNode<T> brother = actualParent.accessToNthSon(actualParent.getIndexOfOnlyOneChild());
                        if (brother.isLeaf()) {
                            ArrayList<Data<T>> dataToMigrate = brother.getListOfData();
                            brother.removeAllData();
                            actualParent.removeChild(actualParent.getIndexOfOnlyOneChild());
                            actualParent.addMultipleData(dataToMigrate);
                        }
                    }
                    nodeWithDataToDelete = parents.removeLast();
                }
            }
        }
    }

    /**
     * If data in tree has changed key atribute - what ar coordinates, this
     * data needs to be deleted and inserted again according to new coordinates.
     */
    public void edit(Data<T> parData, Coordinates newCoordinates) {
        this.delete(parData);
        parData.setCoordinates(newCoordinates);
        this.insert(parData);
    }

    /**
     * This method provide possibility of changing maximum depth of tree.
     */
    public void changeDepth(int parNewMaxDepth) {
        int oldMaxDepth = this.maxDepth;
        this.maxDepth = parNewMaxDepth;

        if (this.maxDepth > oldMaxDepth) {
            if (oldMaxDepth == this.depth) {
                ArrayList<QuadTreeNode<T>> nodesToProcess = this.findAllNodesInLevel(oldMaxDepth);
                for (int i = 0; i < nodesToProcess.size(); i++) {
                    QuadTreeNode<T> node = nodesToProcess.get(i);
                    ArrayList<Data<T>> dataToInsert = node.getListOfData();
                    node.removeAllData();
                    for (int j = 0; j < dataToInsert.size(); j++) {
                        this.insert(dataToInsert.get(j),node);
                    }
                }
            }
        } else if (this.maxDepth < oldMaxDepth) {
            if (this.maxDepth < this.depth) {
                ArrayList<QuadTreeNode<T>> nodesToProcess = this.findAllNodesInLevel(this.maxDepth);
                this.depth = 0;
                for (int i = 0; i < nodesToProcess.size(); i++) {
                    QuadTreeNode<T> node = nodesToProcess.get(i);
                    ArrayList<Data<T>> dataToInsert = this.getAllDataInSubTree(node);
                    node.removeAllData();
                    node.removeChildren();
                    node.addMultipleData(dataToInsert);
                    if (node.getLevel() > this.depth) {
                        this.depth = node.getLevel();
                    }
                }
            }
        }
    }

    /**
     * Method for calculate health of quad tree. It's calculate from two "subhealths"
     * 1. health of depth - if depth of tree is appropriate to number of items in it
     * 2. health of nullpointers - if tree has optimal number of nullpointers
     * (closer explanation in documentation)
     */
    public double health() {
        double weightDepth = 1;
        double weightNullptr = 2;
        double healthOfDepth = this.getHealthOfDepth();
        double healthOfNullptr = this.getHealthOfNullPtr();
        double health = (weightDepth * healthOfDepth + weightNullptr * healthOfNullptr)/ (weightDepth + weightNullptr);
        if (health > 1) {
            health = 100;
        } else {
            health = Math.round(health * 100);
        }
        System.out.println("health: " + health);
        return health;
    }

    /**
     * Submethods for calculating health of tree
     */
    private double optimalDepth() {
        double logOne = Math.log(Math.sqrt(this.numberOfItems));
        double logTwo = Math.log(2);
        return (logOne/logTwo) + 1; // ;)
    }

    private double optimalNumberOfNull() {
        if ((this.numberOfItems % 3) == 0) {
            return 1;
        } else if ((this.numberOfItems % 3) == 1) {
            return 0;
        } else if ((this.numberOfItems % 3) == 2) {
            return 2;
        }
        return -1;
    }

    private int getNumberOfNullPointer() {
        int count = 0;

        Stack<QuadTreeNode<T>> nodesToProcess = new Stack<>();
        nodesToProcess.push(this.root);

        while (!nodesToProcess.isEmpty()) {

            QuadTreeNode<T> current = nodesToProcess.pop();
            if (!current.isLeaf()) {
                count += current.numberOfEmptySons();
            }
            if (!current.isLeaf()) {
                for (QuadTreeNode<T> child: current.getChildren()) {
                    if (child != null) {
                        nodesToProcess.push(child);
                    }
                }
            }
        }
        return count;
    }

    private double getHealthOfDepth() {
        double pomLogOne = Math.log(this.optimalDepth());
        double pomLogTwo = Math.log(this.depth);
        double healthOfDepth = (pomLogOne / pomLogTwo); // * 100

        return healthOfDepth;
    }

    private double getHealthOfNullPtr() {
        double pomLogThree = Math.log(2 - this.optimalNumberOfNull() + getNumberOfNullPointer());
        double pomLogFour = Math.log(2);
        double healthOfNullptr = (pomLogFour / pomLogThree);
        return healthOfNullptr;
    }

    private ArrayList<QuadTreeNode<T>> findAllNodesInLevel(int parLevel) {
        QuadTreeNode<T> root = this.root;
        ArrayList<QuadTreeNode<T>> nodesToReturn = new ArrayList<>();

        if (root == null) {
            return null;
        }

        Queue<QuadTreeNode<T>> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            // Number of nodes at the current level
            int nodesAtCurrentLevel = queue.size();

            for (int i = 0; i < nodesAtCurrentLevel; i++) {
                QuadTreeNode<T> currentNode = queue.poll();
                if (currentNode.getLevel() == parLevel) {
                    nodesToReturn.add(currentNode);
                }

                QuadTreeNode<T>[] children = currentNode.getChildren();
                if (children != null) {
                    for (int j = 0; j < 4; j++) {
                        if (children[j] != null) {
                            queue.add(children[j]);
                        }
                    }
                }
            }
        }
        return nodesToReturn;
    }

    /**
     * optimalizacion number two
     */
    public void tryToOptimalizeTwoObjects() {
        // porovnat vsetky objekty a najst tie najvzialenejsie
        // urcit si nove hranice stormu
        // presypat strom

        ArrayList<Data<T>> dataToInsert = this.getAllDataInSubTree(this.root);
        System.out.println(dataToInsert.size());
        int indexOfFirstData = 0;
        int indexOfSecondData = 0;
        double maxDistance = 0;

        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
        int indexMinX, indexMaxX, indexMinY, indexMaxY = 0;

        for (int i = 0; i < dataToInsert.size(); i++) {
            for (int j = 0; j < dataToInsert.size(); j++) {
                double surX = dataToInsert.get(i).getCoordinates().getLowerX() -
                        dataToInsert.get(j).getCoordinates().getLowerX();
                double surY = dataToInsert.get(i).getCoordinates().getLowerY() -
                        dataToInsert.get(j).getCoordinates().getLowerY();
                double sqaredC = Math.sqrt(Math.pow(surX,2) +
                        Math.pow(surY,2));
                if (sqaredC > maxDistance) {
                    maxDistance = sqaredC;
                    indexOfFirstData = i;
                    indexOfSecondData = j;
                }
            }

            if (dataToInsert.get(i).getCoordinates().getLowerX() < minX) {
                minX = dataToInsert.get(i).getCoordinates().getLowerX();
                indexMinX = i; // porbabbly will not used
            }
            if (dataToInsert.get(i).getCoordinates().getUpperX() > maxX) {
                maxX = dataToInsert.get(i).getCoordinates().getUpperX();
                indexMaxX = i;
            }
            if (dataToInsert.get(i).getCoordinates().getLowerY() < minY) {
                minY = dataToInsert.get(i).getCoordinates().getLowerY();
                indexMinY = i;
            }
            if (dataToInsert.get(i).getCoordinates().getUpperY() > maxY) {
                maxY = dataToInsert.get(i).getCoordinates().getUpperY();
                indexMaxY = i;
            }
        }
        System.out.println(dataToInsert.get(indexOfFirstData).getCoordinates().returnCoordinatesInString());
        System.out.println(dataToInsert.get(indexOfSecondData).getCoordinates().returnCoordinatesInString());

        double centerX = (dataToInsert.get(indexOfFirstData).getCoordinates().getLowerX() + dataToInsert.get(indexOfSecondData).getCoordinates().getUpperX()) / 2;
        double centerY = (dataToInsert.get(indexOfFirstData).getCoordinates().getLowerY() + dataToInsert.get(indexOfSecondData).getCoordinates().getUpperY()) / 2;

        double lowerX, lowerY, upperX, upperY, newXRange, newYRange;

        if (Math.abs(centerX - maxX) < Math.abs(centerX - minX)) {
            newXRange = Math.abs(centerX - minX);
        } else  {
            newXRange = Math.abs(centerX - maxX);
        }

        if (Math.abs(centerY - maxY) < Math.abs(centerY - minY)) {
            newYRange = Math.abs(centerY - minY);
        } else  {
            newYRange = Math.abs(centerY - maxY);
        }

        lowerX = centerX - newXRange - 1;
        upperX = centerX + newXRange + 1;
        lowerY = centerY - newYRange - 1;
        upperY = centerY + newYRange + 1;

        Coordinates newRange = new Coordinates(lowerX, upperX, lowerY,upperX);
        this.reinicializeValueOfTree(newRange);

        System.out.println(dataToInsert.size());
        for (int i = 0; i < dataToInsert.size(); i++) {
            this.insert(dataToInsert.get(i));
        }

    }



    public void tryToOptimalize() {
        // look on 10 percent of width and height and figure out on which line is the less colision of objects
        // count how should be width/height according to far away item
        // change tree boundaries
        // delete end insert whole data
        // find if health is better
        // end :)

        this.health();
        System.out.println("----Optimalize----");
        double width = this.rangeOfTree.getUpperX() - this.rangeOfTree.getLowerX();
        double height = this.rangeOfTree.getUpperY() - this.rangeOfTree.getLowerY();
        int tenPercentageWidth = (int) Math.round(width/10);
        int tenPercentageHeight = (int) Math.round(height/10);
        int indexOfNewLineWidth = returnIndexOfTheLeastNumberOfColisionForWidth(tenPercentageWidth);
        int indexOfNewLineHeight = returnIndexOfTheLeastNumberOfColisionForHeight(tenPercentageHeight);

        double newXcentre = (this.rangeOfTree.getLowerX() + this.rangeOfTree.getUpperX()) / 2 + indexOfNewLineWidth;
        double newYcentre = (this.rangeOfTree.getLowerY() + this.rangeOfTree.getUpperY()) / 2 + indexOfNewLineHeight;

        double newWidth = this.calculateWidth(newXcentre);
        double newHeight = this.calculateHeight(newYcentre);

       Coordinates newCoors = new Coordinates(0,2*newWidth,0,2*newHeight);

        ArrayList<Data<T>> dataToInsert = this.getAllDataInSubTree(this.root);
        this.reinicializeValueOfTree(newCoors);
        for (int i = 0; i < dataToInsert.size(); i++) {
            this.insert(dataToInsert.get(i));
        }
        this.health();
    }

    private int returnIndexOfTheLeastNumberOfColisionForWidth(int parRange) {
        int minColision = Integer.MAX_VALUE;
        int indexOfArea = 0;
        double centreX = (this.rangeOfTree.getLowerX() + this.rangeOfTree.getUpperX())/2;

        // width
        for (int i = -parRange; i < parRange; i++) {
            Coordinates coors = new Coordinates(centreX + i, centreX+i+1,this.rangeOfTree.getLowerY(),this.rangeOfTree.getUpperY());
            ArrayList<Data<T>> data = this.findInArea(coors);
            if (data.size() < minColision) {
                minColision = data.size();
                indexOfArea = i;
            }
        }
        return indexOfArea;
    }

    private int returnIndexOfTheLeastNumberOfColisionForHeight(int parRange) {
        int minColision = Integer.MAX_VALUE;
        int indexOfArea = 0;
        double centreY = (this.rangeOfTree.getLowerY() + this.rangeOfTree.getUpperY())/2;

        // height
        for (int i = -parRange; i < parRange; i++) {
            Coordinates coors = new Coordinates(this.rangeOfTree.getLowerX(),this.rangeOfTree.getUpperX(),centreY + i, centreY+i+1);
            ArrayList<Data<T>> data = this.findInArea(coors);
            if (data.size() < minColision) {
                minColision = data.size();
                indexOfArea = i;
            }
        }
        return indexOfArea;
    }

    private double calculateWidth(double newCentreX) {
        ArrayList<Data<T>> data = this.getAllDataInSubTree(this.root);
        double maxDistance = Double.MIN_VALUE;
        for (int i = 0; i < data.size(); i++) {
            if (Math.abs(newCentreX - data.get(i).getCoordinates().getLowerX()) > maxDistance) {
                maxDistance = Math.abs(newCentreX - data.get(i).getCoordinates().getLowerX());
            }
            if (Math.abs(data.get(i).getCoordinates().getUpperX() - newCentreX) > maxDistance) {
                maxDistance = Math.abs(data.get(i).getCoordinates().getUpperX() - newCentreX);
            }
        }
        return maxDistance;
    }

    private double calculateHeight(double newCentreY) {
        ArrayList<Data<T>> data = this.getAllDataInSubTree(this.root);
        double maxDistance = Double.MIN_VALUE;
        for (int i = 0; i < data.size(); i++) {
            if (Math.abs(newCentreY - data.get(i).getCoordinates().getLowerY()) > maxDistance) {
                maxDistance = Math.abs(newCentreY - data.get(i).getCoordinates().getLowerY());
            }
            if (Math.abs(data.get(i).getCoordinates().getUpperY() - newCentreY) > maxDistance) {
                maxDistance = Math.abs(data.get(i).getCoordinates().getUpperY() - newCentreY);
            }
        }
        return maxDistance;
    }

}
