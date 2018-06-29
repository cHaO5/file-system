package view;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import model.FileType;
import model.FileSystem;
import view.FileTree.MyTreeItem;

//主展示区
public class FileDisplay extends FlowPane {

    private String fileIconSrc = "/image/file-2.png";
    private String emptyFolderIconSrc = "/image/folder-2.png";
    private String folderIconSrc = "/image/folder-2.png";

    private static final int VIEW_WIDTH = 400;
    private static final int VIEW_HEIGHT = 400;

    private static final int GAP_SIZE = 5;

    private static final String SELECTED_COLOR = "#AABBCC55";

    private EditGraphMenu addMenu;
    private static FileSystem fileSystem = FileSystem.getInstance();
    private boolean exist = false;

    public FileDisplay() {
        initGraph();
    }

    public void initGraph() {
        this.addMenu = new EditGraphMenu();
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
            if (!exist && (e.getButton() == MouseButton.SECONDARY || e.isControlDown())) {
                if (this.addMenu.isShowing()) {
                    this.addMenu.hide();
                }
                this.addMenu.show(this, e.getScreenX(), e.getScreenY());
            } else {
                this.addMenu.hide();
            }
        });

        this.setPrefSize(VIEW_WIDTH, VIEW_HEIGHT);
        this.setHgap(GAP_SIZE);
        this.setVgap(GAP_SIZE);
        this.setStyle("-fx-background-color:#FFFFFF; -fx-border-width:0.5; -fx-border-color:#DADADA;");
        this.setPadding(new Insets(5));
    }

    public EditGraphMenu getAddMenu() {
        return this.addMenu;
    }

    public FileTreeItem addFileDirectory(FileType attribute, MyTreeItem parentItem) {
        FileTreeItem item = new FileTreeItem(attribute, parentItem, this);
        this.getChildren().add(item);
        return item;
    }

    public FileTreeItem addFileDirectory(MyTreeItem treeItem) {
        FileTreeItem item = new FileTreeItem(treeItem, this);
        this.getChildren().add(item);
        return item;
    }

    public void removeFileDirectory(MyTreeItem treeItem) {
        ObservableList<Node> itemList = this.getChildren();
        if (itemList != null && itemList.size() > 0) {
            for (Node node : itemList) {
                FileTreeItem item = (FileTreeItem) node;
                if (item.getTreeItem() == treeItem) {
                    this.getChildren().remove(node);
                    break;
                }
            }
        }
    }

    class EditGraphMenu extends ContextMenu {

        private MenuItem addFile;
        private MenuItem addFolder;

        public EditGraphMenu() {
            this.initMenu();
        }

        private void initMenu() {
            addFile = new MenuItem("New File");
            addFolder = new MenuItem("New Folder");
            this.getItems().addAll(addFile, addFolder);
        }

        public MenuItem getAddFile() {
            return addFile;
        }

        public MenuItem getAddFolder() {
            return addFolder;
        }
    }

    //目录树子节点
    class FileTreeItem extends VBox {

        private TextField renameField;
        private Label name;
        private Label icon;
        ImageView emptyIcon;
        ImageView normalIcon;
        private FileType attribute;
        private FileDirectoryMenu menu;
        private FileTreeItem item;
        private MyTreeItem treeItem;
        private MyTreeItem parentItem;
        private Node parent;
        private static final double NAME_WIDTH = 30;

        public FileTreeItem(FileType attribute, MyTreeItem parentItem, Node parent) {
            this.attribute = attribute;
            this.parentItem = parentItem;
            this.parent = parent;
            initItem();
            this.setLucency();
            item = this;
        }

        private void setLucency() {
            this.setStyle("-fx-background:#FFFFFF00;");
            this.renameField.setStyle("-fx-background:#FFFFFF00;");
            this.name.setStyle("-fx-background:#FFFFFF00;");
        }

        public FileTreeItem(MyTreeItem treeItem, Node parent) {
            this.treeItem = treeItem;
            this.attribute = treeItem.getAttribute();
            this.parent = parent;
            initItem();
            item = this;
        }

        public TextField getRenameFiled() {
            return this.renameField;
        }

        public Label getName() {
            return this.name;
        }

        public MyTreeItem getTreeItem() {
            return this.treeItem;
        }

        public FileDirectoryMenu getMenu() {
            return this.menu;
        }

        public void setTreeItem(MyTreeItem treeItem) {
            this.treeItem = treeItem;
        }

        private void initItem() {
            icon = new Label("");
            name = new Label();
            name.setFont(Font.font(12));
            name.setAlignment(Pos.CENTER);
            name.setPrefWidth(90);
            name.setPrefHeight(NAME_WIDTH);
            name.setTextAlignment(TextAlignment.CENTER);
            name.setWrapText(true);
            renameField = new TextField();
            renameField.setAlignment(Pos.CENTER);
            renameField.setEditable(true);
            renameField.setPrefWidth(90);
            //出现重复命名
            renameField.setOnAction(e -> {
                boolean flag = updateFilename();
                if (!flag) {
                    System.out.println("The name is already taken. Please choose a different name.");
                }
            });
            if (treeItem != null) {
                name.setText(treeItem.getValue());
                renameField.setText(treeItem.getValue());
                treeItem.setFileItem(this);
                this.getChildren().addAll(icon, name);
            } else {
                this.treeItem = new MyTreeItem(attribute, parentItem);
                treeItem.setFileItem(this);
                if (attribute == FileType.FILE) {
                    name.setText(this.treeItem.getNode().getNodePathName());
                    renameField.setText(this.treeItem.getNode().getNodePathName());
                    this.getChildren().addAll(icon, renameField);
                } else if (attribute == FileType.FOLDER) {
                    name.setText(this.treeItem.getNode().getNodePathName());
                    renameField.setText(this.treeItem.getNode().getNodePathName());
                    this.getChildren().addAll(icon, renameField);
                }
            }
            if (attribute == FileType.FILE) {
                ImageView fileIcon = new ImageView(new Image(getClass().getResourceAsStream(fileIconSrc)));
                this.icon.setGraphic(fileIcon);
            } else if (attribute == FileType.FOLDER) {
                emptyIcon = new ImageView(new Image(getClass().getResourceAsStream(emptyFolderIconSrc)));
                normalIcon = new ImageView(new Image(getClass().getResourceAsStream(folderIconSrc)));
                if (treeItem.getChildList().isEmpty()) {
                    this.icon.setGraphic(emptyIcon);
                } else {
                    this.icon.setGraphic(normalIcon);
                }
            }

            this.setAlignment(Pos.TOP_CENTER);
            this.setSpacing(5);
            this.setPadding(new Insets(0, 0, 5, 0));
            this.setLucency();
            this.setOnMouseEntered(e -> {
                this.setStyle("-fx-background-color:" + SELECTED_COLOR + ";");
            });
            this.setOnMouseExited(e -> {
                this.setLucency();
            });
            menu = new FileDirectoryMenu();
            this.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
                if (e.getButton() == MouseButton.SECONDARY || e.isControlDown()) {
                    if (menu.isShowing()) {
                        menu.hide();
                    }
                    menu.show(this, e.getScreenX(), e.getScreenY());
                } else {
                    menu.hide();
                }
            });
            menu.showingProperty().addListener(e -> {
                if (menu.isShowing()) {
                    exist = true;
                } else {
                    exist = false;
                }
            });

            this.parent.setOnMouseClicked(e -> {
                if (this.getChildren().contains(renameField)) {
                    if (!renameField.isHover()) {
                        boolean flag = updateFilename();
                        if (!flag) {
                            System.out.println("The name is already taken. Please choose a different name.");
                        }
                    }
                }
            });

        }

        //更新文件名
        private boolean updateFilename() {

            String fileName = null;
            boolean flag = false;
            System.out.println(treeItem.getNode().getNodePathName());
            if (renameField.getText().equals(treeItem.getNode().getNodePathName())) {
                flag = true;
            } else {
                flag = fileSystem.setFilename(treeItem.getPath(), renameField.getText());
                if (!flag) {
                    fileName = treeItem.getNode().getNodePathName();
                } else {
                    fileName = renameField.getText();
                }
            }
            fileName = treeItem.getNode().getNodePathName();
            name.setText(fileName);
            this.getChildren().add(name);
            treeItem.setValue(fileName);
            this.getChildren().remove(renameField);

            return flag;
        }

        public void updateFilename(String fileName) {
            boolean flag = false;
            System.out.println(treeItem.getNode().getNodePathName());
            if (fileName.equals(treeItem.getNode().getNodePathName())) {
                flag = true;
            } else {
                flag = fileSystem.setFilename(treeItem.getPath(), fileName);
                if (!flag) {
                    fileName = treeItem.getNode().getNodePathName();
                }
            }
            fileName = treeItem.getNode().getNodePathName();
            name.setText(fileName);
            treeItem.setValue(fileName);
        }

        public void setEmptyIcon() {
            this.icon.setGraphic(emptyIcon);
        }

        public void setNormalIcon() {
            this.icon.setGraphic(normalIcon);
        }

        //主展示区文件/文件夹右键菜单
        class FileDirectoryMenu extends ContextMenu {

            private MenuItem open;
            private MenuItem delete;
            private MenuItem rename;
            private MenuItem attribute;

            public FileDirectoryMenu() {
                initMenu();
            }

            private void initMenu() {
                open = new MenuItem("Open");
                delete = new MenuItem("Delete");
                rename = new MenuItem("Rename");
                attribute = new MenuItem("Property");
                this.getItems().addAll(open, delete, rename, attribute);
                rename.setOnAction(e -> {
                    item.getChildren().add(item.renameField);
                    item.renameField.setText(name.getText());
                    item.getChildren().remove(item.name);
                });
            }

            public MenuItem getOpen() {
                return open;
            }

            public MenuItem getDelete() {
                return delete;
            }

            public MenuItem getRename() {
                return rename;
            }

            public MenuItem getAttribute() {
                return attribute;
            }

        }

    }
}
