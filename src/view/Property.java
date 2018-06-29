package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.FileType;
import view.FileTree.MyTreeItem;

//属性窗口
public class Property extends Stage {
    private Label name;
    private FileType attribute;
    private Label ItemAttribute;
    private Label path;
    private Label createTime;
    private Label lastModifiedTime;
    private static final int STAGE_WIDTH = 300;
    private static final int STAGE_HEIGHT = 250;

    public Property(MyTreeItem selectedItem) {
        initGraph(selectedItem);
    }

    public void initGraph(MyTreeItem selectedItem) {
        VBox pane = new VBox();
        this.setTitle("Property");
        this.name = new Label("Name: " + selectedItem.getNode().getNodePathName());
        this.name.setLayoutX(100);
        this.attribute = selectedItem.getAttribute();
        if (this.attribute == FileType.FILE) {
            ItemAttribute = new Label("Type: " + "File");
        } else if (this.attribute == FileType.FOLDER) {
            ItemAttribute = new Label("Type: " + "Floder");
        }
        ItemAttribute.setLayoutX(100);

        this.path = new Label("Path: " + selectedItem.getPath());
        this.path.setLayoutX(100);
        this.createTime = new Label("Created: " + selectedItem.getNode().getCreateTime());
        this.lastModifiedTime = new Label("Modified: " + selectedItem.getNode().getLastModifiedTime());


        pane.getChildren().addAll(this.name, ItemAttribute, this.path, this.createTime, this.lastModifiedTime);
        pane.setAlignment(Pos.CENTER_LEFT);
        pane.setPadding(new Insets(25));
        pane.setSpacing(20);
        Scene scene = new Scene(pane, STAGE_WIDTH, STAGE_HEIGHT, Color.WHITE);
        this.setScene(scene);

    }

}
