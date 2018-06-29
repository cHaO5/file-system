package view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.FileSystem;
import view.FileTree.MyTreeItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Main extends Application {

    private static Stage stage;
    private static FileSystem fileSystem;
    private static ViewManager viewManagerGraph;
    public static List<MyTreeItem> treeItemList = new ArrayList<>();
    public static Map<MyTreeItem, Stage> stageMap = new HashMap<>();

    @Override
    public void start(Stage primaryStage) {
        try {
            stage = primaryStage;
            Group group = new Group();
            fileSystem = FileSystem.getInstance();
            HBox root = new HBox();
            root.setPadding(new Insets(10));
            group.getChildren().add(root);
            Scene scene = new Scene(group, 770, 470);
            viewManagerGraph = new ViewManager();
            root.getChildren().add(viewManagerGraph);

            this.setLucency();

            primaryStage.setTitle("FileSystem");
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setLucency() {
        Main.viewManagerGraph.setStyle("-fx-background:#FFFFFF00;");
    }

    public static Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
