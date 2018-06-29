package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.FileType;
import model.FileSystem;
import model.Util;
import view.FileDisplay.FileTreeItem;
import view.FileTree.MyTreeItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//窗口管理
public class ViewManager extends BorderPane {

    private PathBar pathBarGraph;
    private FileTree treeGraph;
    private FileDisplay editGraph;
    private VBox editBox;
    public static MyTreeItem rootItem;
    private MyTreeItem currentItem;
    private FileSystem fileSystem = FileSystem.getInstance();
    public static String currentPath = "Root:/";

    public ViewManager() {
        this.initGraph();
        this.setLucency();
    }

    private void initGraph() {
        treeGraph = FileTree.getInstance();
        this.addActionForTreeGraph();
        rootItem = (MyTreeItem) treeGraph.getRoot();
        currentItem = rootItem;
        editGraph = new FileDisplay();
        editBox = new VBox();
        editBox.getChildren().add(editGraph);
        this.addActionForEditGraph();
        pathBarGraph = new PathBar(currentItem);
        this.addActionForPathBar();

        this.setTop(pathBarGraph);
        this.setLeft(treeGraph);
        this.setCenter(editBox);
    }

    private void setLucency() {
        this.editGraph.setStyle("-fx-background:#FFFFFF00;");
        this.treeGraph.setStyle("-fx-background:#FFFFFF00;");
        this.pathBarGraph.setStyle("-fx-background:#FFFFFF00;");
    }

    public VBox getEditBox() {
        return this.editBox;
    }

    private void transformPath(MyTreeItem selectedItem) {
        this.updateEditGraph(selectedItem);
        this.pathBarGraph.updatePath(selectedItem);
    }

    //添加目录树右键菜单
    private void addActionForTreeGraph() {
        this.treeGraph.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                MyTreeItem selectedItem = (MyTreeItem) this.treeGraph.getSelectionModel().getSelectedItem();
                if (selectedItem != this.currentItem) {
                    this.transformPath(selectedItem);
                }
            }

        });
        this.treeGraph.getRootMenu().getAddFolder().setOnAction(e -> {
            this.addFolderToEditGraph();
        });
        this.treeGraph.getRootMenu().getOpen().setOnAction(e -> {
            this.transformPath(rootItem);
        });
        this.treeGraph.getRootMenu().getFormat().setOnAction(e -> {
            this.format();
        });

        this.treeGraph.getFolderMenu().getAddFolder().setOnAction(e -> {
            this.addFolderToEditGraph();
        });
        this.treeGraph.getFolderMenu().getDelete().setOnAction(e -> {
            this.removeItemFromEditGraph();
        });
        this.treeGraph.getFolderMenu().getOpen().setOnAction(e -> {
            MyTreeItem selectedItem = (MyTreeItem) this.treeGraph.getSelectionModel().getSelectedItem();
            this.transformPath(selectedItem);
        });
        this.treeGraph.getFolderMenu().getAttribute().setOnAction(e -> {
            MyTreeItem selectedItem = (MyTreeItem) this.treeGraph.getSelectionModel().getSelectedItem();
            Stage propertyGraph = new Property(selectedItem);
            propertyGraph.show();
        });
        this.treeGraph.getFolderMenu().getRename().setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog(this.getTreeItemName());
            dialog.setTitle("Rename");
            dialog.setHeaderText(null);
            dialog.setContentText("Please enter name:");

            Optional<String> result = dialog.showAndWait();
            String newName = null;
            if (result.isPresent()) {
                newName = result.get();
            }
            if (newName != null && !newName.equals("")) {
                MyTreeItem item = (MyTreeItem) this.treeGraph.getSelectionModel().getSelectedItem();
                if (fileSystem.setFilename(item.getPath(), newName)) {
                    item.setValue(newName);
                    FileTreeItem fileItem = item.getFileItem();
                    if (fileItem != null) {
                        fileItem.updateFilename(newName);
                    }
                    if (item == currentItem) {
                        this.transformPath(item);
                    }
                } else {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("The name is already taken. Please choose a different name.");
                    alert.showAndWait();
                }
            }
        });


        this.treeGraph.getFileMenu().getDelete().setOnAction(e -> {
            this.removeItemFromEditGraph();
        });
        this.treeGraph.getFileMenu().getAttribute().setOnAction(e -> {
            MyTreeItem selectedItem = (MyTreeItem) this.treeGraph.getSelectionModel().getSelectedItem();
            Stage propertyGraph = new Property(selectedItem);
            propertyGraph.show();
        });
        this.treeGraph.getFileMenu().getRename().setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog(this.getTreeItemName());
            dialog.setTitle("Rename");
            dialog.setHeaderText(null);
            dialog.setContentText("Please enter name:");


            Optional<String> result = dialog.showAndWait();
            String newName = null;
            if (result.isPresent()) {
                newName = result.get();
            }
            if (newName != null && !newName.equals("")) {
                MyTreeItem item = (MyTreeItem) this.treeGraph.getSelectionModel().getSelectedItem();
                if (fileSystem.setFilename(item.getPath(), newName)) {
                    item.setValue(newName);
                    FileTreeItem fileItem = item.getFileItem();
                    if (fileItem != null) {
                        fileItem.updateFilename(newName);
                    }
                    if (item == currentItem) {
                        this.transformPath(item);
                    }
                } else {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("The name is already taken. Please choose a different name.");
                    alert.showAndWait();
                }
            }
        });
    }

    //格式化刷新界面
    public void format() {
        for (int i = 0; i < rootItem.getChildList().size(); ++i) {
            System.out.println(i);
            this.treeGraph.removeChildItem(rootItem.getChildList().get(i));
        }
        rootItem.getChildList().clear();
        currentItem = rootItem;
        fileSystem.format();
        this.updateEditGraph(currentItem);

        pathBarGraph.updatePath(currentItem);
    }

    public void recover() {
        Alert information = new Alert(Alert.AlertType.INFORMATION, "Recover Successfully!");
        information.setTitle("Message");
        information.setHeaderText("System Information");
        information.showAndWait();
    }

    public String getTreeItemName() {
        MyTreeItem item = (MyTreeItem) this.treeGraph.getSelectionModel().getSelectedItem();
        return item.getValue();
    }

    //主展示区添加文件夹
    private void addFolderToEditGraph() {
        MyTreeItem selectedItem = (MyTreeItem) this.treeGraph.getSelectionModel().getSelectedItem();
        this.treeGraph.addChildItem(FileType.FOLDER);
        if (selectedItem == this.currentItem) {
            selectedItem = (MyTreeItem) this.treeGraph.getSelectionModel().getSelectedItem();
            this.addItemToEditGraph(selectedItem);
        }
    }

    private void removeItemFromEditGraph() {
        MyTreeItem selectedItem = (MyTreeItem) this.treeGraph.getSelectionModel().getSelectedItem();
        String path = Util.deleRootStr(selectedItem.getPath());
        fileSystem.deleFile(path);
        MyTreeItem parentItem = (MyTreeItem) selectedItem.getParent();
        parentItem.removeChild(selectedItem);
        if (selectedItem == this.currentItem) {
            this.updateEditGraph(parentItem);
            this.pathBarGraph.updatePath(parentItem);
        } else if (selectedItem.getParent() == this.currentItem) {
            this.editGraph.removeFileDirectory(selectedItem);
        }
        this.treeGraph.removeChildItem(selectedItem);
    }

    private void updateEditGraph(MyTreeItem selectedItem) {
        this.currentItem = selectedItem;
        this.editGraph.getChildren().clear();
        List<MyTreeItem> childList = this.currentItem.getChildList();
        if (childList != null && childList.size() > 0) {
            for (MyTreeItem child : childList) {
                this.addItemToEditGraph(child);
            }
        }
    }

    //添加主展示区右键菜单
    private void addActionForEditGraph() {
        this.editGraph.getAddMenu().getAddFile().setOnAction(e -> {
            FileTreeItem item = this.addItemToEditGraph(FileType.FILE);
            MyTreeItem selectedItem = (MyTreeItem) this.treeGraph.getSelectionModel().getSelectedItem();
            selectedItem.getPath();
            this.currentItem.addChild(item.getTreeItem());
            this.currentItem.getChildren().add(item.getTreeItem());
        });
        this.editGraph.getAddMenu().getAddFolder().setOnAction(e -> {
            FileTreeItem item = this.addItemToEditGraph(FileType.FOLDER);
            this.currentItem.addChild(item.getTreeItem());
            this.currentItem.getChildren().add(item.getTreeItem());
        });
    }

    private FileTreeItem addItemToEditGraph(MyTreeItem treeItem) {
        FileTreeItem item = this.editGraph.addFileDirectory(treeItem);
        this.addActionToEditGraphItem(item);
        return item;
    }

    private FileTreeItem addItemToEditGraph(FileType attribute) {
        FileTreeItem item = this.editGraph.addFileDirectory(attribute, currentItem);
        this.addActionToEditGraphItem(item);
        return item;
    }

    //添加地址区选项
    private void addActionForPathBar() {
        Button backBtn = this.pathBarGraph.getBackButton();
        backBtn.setOnMouseClicked(e -> {
            if (this.currentItem != rootItem) {
                MyTreeItem parent = (MyTreeItem) this.currentItem.getParent();
                this.transformPath(parent);
            }
        });

        Button recoverBtn = this.pathBarGraph.getRecover();
        recoverBtn.setOnMouseClicked(e -> {
            fileSystem.recover();
            recover();
        });

        Button backUpBtn = this.pathBarGraph.getBackUp();
        backUpBtn.setOnMouseClicked(e -> {
            fileSystem.backUp();
        });
    }

    //打开文件操作
    private void addActionToEditGraphItem(FileTreeItem item) {
        MyTreeItem treeItem = item.getTreeItem();
        item.getMenu().getDelete().setOnAction(e -> {

            String path = Util.deleRootStr(treeItem.getPath());
            fileSystem.deleFile(path);
            this.currentItem.removeChild(treeItem);
            if (treeItem.getAttribute() == FileType.FOLDER) {
                this.currentItem.getChildren().remove(item.getTreeItem());
            }
            this.editGraph.getChildren().remove(item);
        });

        item.getMenu().getOpen().setOnAction(e -> {
            openMenuDeal(item, treeItem);
        });

        item.getMenu().getAttribute().setOnAction(e -> {
            Stage propertyGraph = new Property(treeItem);
            propertyGraph.show();

        });

        item.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                openMenuDeal(item, treeItem);
            }
        });
    }

    private void openMenuDeal(FileTreeItem item, MyTreeItem treeItem) {
        if (Main.treeItemList.contains(treeItem)) {
            Stage editStage = Main.stageMap.get(treeItem);
            editStage.setIconified(false);
            editStage.requestFocus();
            return;
        }
        if (treeItem.getAttribute() == FileType.FILE) {
            if (Main.treeItemList.size() >= 5) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Too many files are open!\n");
                alert.showAndWait();
                return;
            }
            Editor editObject = new Editor(treeItem, item.getName().getText(), treeItem.getPath());
            String pathName = Util.deleRootStr(treeItem.getPath());
            String strBuffer = fileSystem.openFile(pathName);
            editObject.getContentArea().setText(strBuffer);

            if (treeItem.getNode().getReadType() == 0) {
                editObject.getContentArea().setEditable(false);
            } else {
                editObject.getContentArea().setEditable(true);
            }

            Stage editStage = editObject;
            Main.stageMap.put(treeItem, editStage);
            Main.treeItemList.add(treeItem);
            editStage.show();
            editStage.showingProperty().addListener(e -> {
                if (editStage.isShowing()) {
                } else {
                    Main.stageMap.remove(treeItem);
                }
            });
        } else if (treeItem.getAttribute() == FileType.FOLDER) {
            this.transformPath(treeItem);
        }
    }

    //地址区
    class PathBar extends HBox {
        private Button backUp;
        private Button recover;
        private Button backButton;
        private TextField pathField;
        private MyTreeItem currentItem;

        public PathBar(MyTreeItem currentItem) {
            this.currentItem = currentItem;
            initGraph();
            this.setLucency();
        }

        private void setLucency() {
            this.backUp.setStyle("-fx-background-color:#FFFFFF00;");
            this.recover.setStyle("-fx-background-color:#FFFFFF00;");
            this.backButton.setStyle("-fx-background-color:#FFFFFF00;");
            this.pathField.setStyle("-fx-background:#FFFFFF00;");
        }

        private void initGraph() {
            backUp = new Button("BackUp");
            recover = new Button("Recover");
            backButton = new Button("Back");
            backUp.setOnMouseEntered(e -> {
                backUp.setStyle("-fx-background-color:#E2E8EE;");
            });
            backUp.setOnMouseExited(e -> {
                this.setLucency();
            });
            recover.setOnMouseEntered(e -> {
                recover.setStyle("-fx-background-color:#E2E8EE;");
            });
            recover.setOnMouseExited(e -> {
                this.setLucency();
            });
            backButton.setOnMouseEntered(e -> {
                backButton.setStyle("-fx-background-color:#E2E8EE;");
            });
            backButton.setOnMouseExited(e -> {
                this.setLucency();
            });

            ViewManager.currentPath = getPath();
            pathField = new TextField(ViewManager.currentPath);
            pathField.setMinWidth(550);

            this.getChildren().addAll(backUp, recover, backButton, pathField);

            this.setSpacing(5);
            this.setAlignment(Pos.CENTER_LEFT);
            this.setPadding(new Insets(2));
        }

        private String getPath() {
            List<String> pathList = new ArrayList<String>();
            MyTreeItem temp = currentItem;
            while (temp != treeGraph.getRoot()) {
                pathList.add(temp.getValue());
                temp = (MyTreeItem) temp.getParent();
            }
            StringBuilder path = new StringBuilder("Root:/");
            if (pathList != null && pathList.size() > 0) {
                for (int i = pathList.size() - 1; i >= 0; i--) {
                    path.append(pathList.get(i));
                    if (i != 0) {
                        path.append("/");
                    }
                }
            }
            return path.toString();
        }

        public void updatePath(MyTreeItem currentItem) {
            this.currentItem = currentItem;
            ViewManager.currentPath = getPath();
            this.pathField.setText(ViewManager.currentPath);
        }

        public Button getBackButton() {
            return backButton;
        }

        public Button getRecover() {
            return recover;
        }

        public Button getBackUp() {
            return backUp;
        }
    }

    public PathBar getPathBar() {
        return pathBarGraph;
    }
}
