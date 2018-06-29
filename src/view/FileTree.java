package view;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Node;
import model.FileType;
import model.FileSystem;
import model.Util;
import view.FileDisplay.FileTreeItem;

import java.util.ArrayList;
import java.util.List;

//文件目录树
public class FileTree extends TreeView<String> {

    private static MyTreeItem selectedItem;
    private static String emptyFolderImageSrc = "/image/folder.png";
    private static String folderImageSrc = "/image/folder.png";
    private static String fileImageSrc = "/image/file.png";
    private static String rootImageSrc = "/image/folder.png";
    private static final int VIEW_WIDTH = 200;
    private static final int VIEW_HEIGHT = 200;
    private static FileTree instance;

    private MyContextMenu rootMenu;
    private MyContextMenu folderMenu;
    private MyContextMenu fileMenu;

    private static FileSystem fileSystem = FileSystem.getInstance();

    private FileTree() {
        this.initTreeGraph();
    }

    private void initTreeGraph() {
        MyTreeItem rootItem = new MyTreeItem(FileType.SYSTEM, null);
        this.setRoot(rootItem);
        rootItem.setExpanded(true);

        rootMenu = new MyContextMenu(FileType.SYSTEM);
        folderMenu = new MyContextMenu(FileType.FOLDER);
        fileMenu = new MyContextMenu(FileType.FILE);

        this.setPrefSize(VIEW_WIDTH, VIEW_HEIGHT);
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.getSelectionModel().selectedItemProperty().addListener(e -> {
            selectedItem = (MyTreeItem) this.getSelectionModel().getSelectedItem();
            FileType attribute = selectedItem.getAttribute();
            if (attribute == FileType.FOLDER) {
                this.setContextMenu(folderMenu);
            } else if (attribute == FileType.SYSTEM) {
                this.setContextMenu(rootMenu);
            } else if (attribute == FileType.FILE) {
                this.setContextMenu(fileMenu);
            }
        });

        this.getSelectionModel().select(rootItem);
    }

    public static FileTree getInstance() {
        if (instance == null) {
            instance = new FileTree();
        }
        return instance;
    }


    public MyContextMenu getRootMenu() {
        return this.rootMenu;
    }

    public MyContextMenu getFolderMenu() {
        return this.folderMenu;
    }

    public MyContextMenu getFileMenu() {
        return fileMenu;
    }

    //添加子节点
    public void addChildItem(FileType attribute) {
        selectedItem = (MyTreeItem) this.getSelectionModel().getSelectedItem();
        MyTreeItem childItem = new MyTreeItem(attribute, selectedItem);
        selectedItem.addChild(childItem);
        if (attribute == FileType.FOLDER || attribute == FileType.SYSTEM) {
            selectedItem.getChildren().add(childItem);
            selectedItem.setExpanded(true);
            this.getSelectionModel().select(childItem);
        }
    }

    //删除子节点
    public void removeChildItem(MyTreeItem selectedItem) {
        MyTreeItem parentItem = (MyTreeItem) selectedItem.getParent();
        if (parentItem == null) System.out.println("parentItem is null!");
        parentItem.getChildren().remove(selectedItem);
        this.getSelectionModel().select(parentItem);
    }

    public static Node initChildItem(FileType attribute, String path) {
        String pathName = Util.deleRootStr(path);
        if ("/".equals(pathName))
            return null;
        Node childNode = fileSystem.createFile(pathName);
        return childNode;
    }

    //目录树
    static class MyTreeItem extends TreeItem<String> {

        private FileType attribute;
        private Node node;
        private List<MyTreeItem> childList;
        private MyTreeItem parentItem;
        private ImageView emptyIcon;
        private ImageView normalIcon;
        private String path;
        private FileTreeItem fileItem;

        public MyTreeItem(FileType attribute, MyTreeItem parentItem) {
            node = new Node();
            this.attribute = attribute;
            this.parentItem = parentItem;
            this.childList = new ArrayList<MyTreeItem>();
            if (attribute == FileType.FOLDER) {
                emptyIcon = new ImageView(new Image(getClass().getResourceAsStream(emptyFolderImageSrc)));
                this.setGraphic(emptyIcon);
                this.setValue("untitled folder");
                normalIcon = new ImageView(new Image(getClass().getResourceAsStream(folderImageSrc)));
            } else if (attribute == FileType.FILE) {
                ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(fileImageSrc)));
                this.setGraphic(icon);
                this.setValue("untitled.txt");
            } else {
                ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(rootImageSrc)));
                this.setGraphic(icon);
                this.setValue("SystemFile");
            }
            this.path = getItemPath();
            String fileName = getFileName(attribute);
            this.setValue(fileName);
            this.path = getItemPath();
            this.valueProperty().addListener(e -> {
                this.path = getItemPath();
            });
            node = initChildItem(attribute, this.path);

        }

        public void setFileItem(FileTreeItem item) {
            this.fileItem = item;
        }

        public FileTreeItem getFileItem() {
            return fileItem;
        }

        public FileType getAttribute() {
            return this.attribute;
        }

        public List<MyTreeItem> getChildList() {
            return this.childList;
        }

        public void addChild(MyTreeItem child) {
            this.childList.add(child);
            if (this.attribute == FileType.FOLDER) {
                this.setGraphic(normalIcon);
                if (this.fileItem != null) {
                    this.fileItem.setNormalIcon();
                }
            }
        }

        public void removeChild(MyTreeItem child) {
            this.childList.remove(child);
            if (this.attribute == FileType.FOLDER && this.childList.isEmpty()) {
                this.setGraphic(emptyIcon);
                if (this.fileItem != null) {
                    this.fileItem.setEmptyIcon();
                }
            }
        }

        public MyTreeItem getParentItem() {
            return this.parentItem;
        }

        public Node getNode() {
            return node;
        }

        public void setNode(Node node) {
            this.node = node;
        }

        private String getItemPath() {
            List<String> pathList = new ArrayList<String>();
            MyTreeItem temp = this;
            while (temp.getParentItem() != null) {
                pathList.add(temp.getValue());
                temp = (MyTreeItem) temp.getParentItem();
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

        public String getPath() {
            return this.path;
        }

        public String getFileName(FileType attribute) {
            String pathName = this.getPath();
            boolean flag = true;
            String fileName = "untitled";
            if (attribute == FileType.FILE) {
                fileName = "untitled.txt";
                for (int i = 1; i < 8; i++) {
                    flag = fileSystem.checkRename(pathName, fileName);
                    if (!flag) {
                        break;
                    }
                    fileName = "untitled(" + i + ").txt";
                }
            } else if (attribute == FileType.FOLDER) {
                fileName = "untitled folder";
                for (int i = 1; i < 8; i++) {
                    flag = fileSystem.checkRename(pathName, fileName);
                    if (!flag) {
                        break;
                    }
                    fileName = "untitled folder(" + i + ")";
                }
            } else if (attribute == FileType.SYSTEM) {
                fileName = "ROOT";
            }
            System.out.println("New " + fileName);
            return fileName;
        }

    }

    //目录树右键菜单
    class MyContextMenu extends ContextMenu {
        private MenuItem open = new MenuItem("Open");
        private MenuItem delete = new MenuItem("Delete");
        private MenuItem rename = new MenuItem("Rename");
        private MenuItem addFolder = new MenuItem("New Folder");
        private MenuItem attribute = new MenuItem("Property");
        private MenuItem format = new MenuItem("Format");

        public MyContextMenu(FileType attribute) {
            if (attribute == FileType.FOLDER) {
                this.createFolderMenu();
            } else if (attribute == FileType.SYSTEM) {
                this.createRootMenu();
            } else if (attribute == FileType.FILE) {
                this.createFileMenu();
            }
        }

        public void createRootMenu() {
            open = new MenuItem("Open");
            addFolder = new MenuItem("New Folder");
            format = new MenuItem("Format");
            this.getItems().addAll(open, addFolder, format);
        }

        public void createFileMenu() {
            delete = new MenuItem("Delete");
            rename = new MenuItem("Rename");
            attribute = new MenuItem("Property");
            this.getItems().addAll(delete, rename, attribute);
        }

        public void createFolderMenu() {
            open = new MenuItem("Open");
            delete = new MenuItem("Delete");
            rename = new MenuItem("Rename");
            addFolder = new MenuItem("New Folder");
            attribute = new MenuItem("Property");
            this.getItems().addAll(open, delete, rename, addFolder, attribute);
        }

        public MenuItem getAddFolder() {
            return addFolder;
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

        public MenuItem getFormat() {
            return format;
        }
    }

}