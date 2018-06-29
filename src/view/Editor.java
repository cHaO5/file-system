package view;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.FileSystem;
import model.Util;
import view.FileTree.MyTreeItem;

//文件编辑窗口
public class Editor extends Stage {

    private FileMenuBar menuBar;
    private TextArea contentArea;
    private String fileName;
    private String pathName;
    private MyTreeItem treeItem;

    private static final int STAGE_WIDTH = 500;
    private static final int STAGE_HEIGHT = 400;

    public Editor() {
        this(null, "Untitled");
    }

    public Editor(MyTreeItem treeItem, String filaname) {
        this.fileName = filaname;
        System.out.println(filaname);
        this.treeItem = treeItem;
        this.initGraph();
    }

    public Editor(MyTreeItem treeItem, String filename, String pathName) {
        this(treeItem, filename);
        this.pathName = pathName;
    }

    private void closeStage() {
        this.close();
    }

    private void initGraph() {
        VBox root = new VBox();
        this.contentArea = new TextArea();
        this.contentArea.setWrapText(true);
        this.contentArea.setEditable(true);
        this.contentArea.prefWidthProperty().bind(this.widthProperty());
        this.contentArea.prefHeightProperty().bind(this.heightProperty());

        this.menuBar = new FileMenuBar();
        this.menuBar.prefWidthProperty().bind(this.widthProperty());
        this.menuBar.setStyle("-fx-background-color:#EFEFEF; -fx-border-width:1; -fx-border-color:#D0D0D0;");

        root.getChildren().addAll(menuBar, contentArea);
        this.setTitle(fileName + " - Editor");
        Scene scene = new Scene(root, STAGE_WIDTH, STAGE_HEIGHT, Color.WHITE);
        this.setScene(scene);
    }

    //选项菜单栏
    class FileMenuBar extends ToolBar {
        private Double size = 12.0;
        private FileSystem fileSystem;

        private Button saveButton;
        private Button saveExitButton;
        private Button exitButton;

        public FileMenuBar() {
            this.initMenuBar();
        }

        private void initMenuBar() {
            fileSystem = FileSystem.getInstance();
            this.saveButton = new Button("Save");
            this.saveExitButton = new Button("Save&Exit");
            this.exitButton = new Button("Exit");
            System.out.println("show button!");

            this.getItems().addAll(saveButton, saveExitButton, exitButton);
            contentArea.setFont(Font.font(size));

            addSaveButtonEvent();
            addSaveAndExitButtonEvent();
            addExitButtonEvent();

        }

        private void addSaveButtonEvent() {
            this.saveButton.setOnAction(e -> {
                String buffer = contentArea.getText();
                String path = Util.deleRootStr(pathName);
                fileSystem.storeIntoFile(path, buffer);
            });
        }

        private void addSaveAndExitButtonEvent() {

            this.saveExitButton.setOnAction(e -> {

                String buffer = contentArea.getText();
                String path = Util.deleRootStr(pathName);
                fileSystem.storeIntoFile(path, buffer);
                closeStage();
            });
        }

        private void addExitButtonEvent() {
            this.exitButton.setOnAction(e -> {
                closeStage();
            });
        }
    }


    public TextArea getContentArea() {
        return this.contentArea;
    }
}
