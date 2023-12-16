package GUI;

import java.awt.*;

public class GuiRunner {

    public static void main(String[] args) {
        View testView = new View();
        Model testModel = new Model();
        Controller controller = new Controller(testModel,testView);
        testView.setVisible(true);
        testView.setContentPane(testView.getRootPanel());

        testView.setResizable(false);
        testView.setSize(600,600);
        testView.setPreferredSize(new Dimension(800, 450));
        testView.setTitle("Cadaster");
        testView.setLocationRelativeTo(null);
        testView.setDefaultCloseOperation(testView.EXIT_ON_CLOSE);
        testView.pack();
    }
}
