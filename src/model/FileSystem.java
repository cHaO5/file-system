package model;

import javafx.scene.control.Alert;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import static model.FileType.FOLDER;

//文件系统的管理
public class FileSystem {
    private FAT fat;
    private ArrayList<OpenFile> openFiles;
    private Block[] blocks = new Block[128];
    private int disksUsedCapacity;
    private int diskFreeCapacity;
    private final int disksTotalCapacity = 128 * 64;

    private static FileSystem fileSystem;

    public FileSystem() {

        fat = FAT.getInstance();
        openFiles = new ArrayList<OpenFile>();
        OpenFile openFile = initOpenFile(2, "/");
        openFiles.add(openFile);
        for (int i = 0; i < 128; i++) {
            blocks[i] = new Block();
        }
        disksUsedCapacity = 3 * 64 - 8;
        diskFreeCapacity = 64 * 128 - 3 * 64 + 8;
    }

    public static FileSystem getInstance() {
        if (fileSystem == null) {
            fileSystem = new FileSystem();
        }
        return fileSystem;
    }

    public FAT getFat() {
        return fat;
    }

    public int getDisksUsedCapacity() {
        return disksUsedCapacity;
    }

    public void setDisksUsedCapacity(int disksUsedCapacity) {
        this.disksUsedCapacity = disksUsedCapacity;
    }

    public int getDiskFreeCapaciyty() {
        return diskFreeCapacity;
    }

    public void setDiskFreeCapaciyty(int diskFreeCapacity) {
        this.diskFreeCapacity = diskFreeCapacity;
    }

    public void setFat(FAT fat) {
        this.fat = fat;
    }

    public ArrayList<OpenFile> getOpenFiles() {
        return openFiles;
    }

    public void setOpenFiles(ArrayList<OpenFile> openFiles) {
        this.openFiles = openFiles;
    }

    public Block[] getBlocks() {
        return blocks;
    }

    public void setBlocks(Block[] blocks) {
        this.blocks = blocks;
    }

    public static void main(String[] args) {
        FileSystem fileSystem = FileSystem.getInstance();
        for (int i = 0; i < fileSystem.fat.getItem().length; i++) {
            System.out.println(fileSystem.fat.getItem()[i]);
        }
    }

    //格式化
    public void format() {
        System.out.println("File System Format!");
        fat.format();
        openFiles = new ArrayList<OpenFile>();
        OpenFile openFile = initOpenFile(2, "/");
        openFiles.add(openFile);
        for (int i = 0; i < 128; i++) {
            blocks[i] = new Block();
        }

        disksUsedCapacity = 3 * 64 - 8;
        diskFreeCapacity = 64 * 128 - 3 * 64 + 8;
    }

    //恢复系统
    public void recover() {
        StringBuilder jsonStr = new StringBuilder();
        try {
            String path = System.getProperty("java.class.path");
            int firstIndex = path.lastIndexOf(System.getProperty("path.separator")) + 1;
            int lastIndex = path.lastIndexOf(File.separator) + 1;
            path = path.substring(firstIndex, lastIndex);
            BufferedReader reader = new BufferedReader(new FileReader(new File(path + File.separator + "recover.json")));
            String temp = "";
            while ((temp = reader.readLine()) != null) jsonStr.append(temp);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            JSONObject json = new JSONObject(jsonStr.toString());
            this.diskFreeCapacity = json.getInt("diskFreeCapacity");
            this.disksUsedCapacity = json.getInt("disksUsedCapacity");
            JSONArray jsonFAT = json.getJSONArray("FAT");
            for (int i = 2; i < 128; ++i) {
                fat.setItem(i, jsonFAT.getInt(i));
                if (jsonFAT.getInt(i) == -1) {
                    for (int folderNodeNum = 0; folderNodeNum < 8; ++folderNodeNum) {
                        JSONArray jsonNodes = json.getJSONArray("disk-" + i + "-folderNode-" + folderNodeNum);
                        JSONObject jsonNode = jsonNodes.getJSONObject(0);
                        blocks[i].getNode()[folderNodeNum].setNodePathName((String) jsonNode.get("nodePathName"));
                        blocks[i].getNode()[folderNodeNum].setNodeType((String) jsonNode.get("nodeType"));

                        if (jsonNode.get("nodeAttribute").equals("NONE")) {
                            blocks[i].getNode()[folderNodeNum].setNodeAttribute(FileType.NONE);
                        } else if (jsonNode.get("nodeAttribute").equals("FILE")) {
                            blocks[i].getNode()[folderNodeNum].setNodeAttribute(FileType.FILE);
                        } else if (jsonNode.get("nodeAttribute").equals("FOLDER")) {
                            blocks[i].getNode()[folderNodeNum].setNodeAttribute(FileType.FOLDER);
                        } else if (jsonNode.get("nodeAttribute").equals("SYSTEM")) {
                            blocks[i].getNode()[folderNodeNum].setNodeAttribute(FileType.SYSTEM);
                        }

                        blocks[i].getNode()[folderNodeNum].setNodeBeginDisk((int) jsonNode.get("nodeBeginDisk"));
                        blocks[i].getNode()[folderNodeNum].setNodeLength((int) jsonNode.get("nodeLength"));
                        blocks[i].getNode()[folderNodeNum].setCreateTime(Timestamp.valueOf((String) jsonNode.get("createTime")));
                        blocks[i].getNode()[folderNodeNum].setLastModifiedTime(Timestamp.valueOf((String) jsonNode.get("lastModifiedTime")));
                    }
                    JSONObject diskContent = json.getJSONObject("disk-" + i);
                    blocks[i].setContent(diskContent.getString("content"));
                } else if (jsonFAT.getInt(i) == 0) {
                    blocks[i] = new Block();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("Recover successfully!");
    }


    //系统备份
    public void backUp() {
        JSONObject json = new JSONObject();

        try {
            json.put("disksUsedCapacity", disksUsedCapacity);
            json.put("diskFreeCapacity", diskFreeCapacity);
            JSONArray jsonFAT = new JSONArray();
            int[] tempFat = fat.getItem();
            for (int i = 2; i < 128; ++i) {
                jsonFAT.put(tempFat[i]);
                if (tempFat[i] == -1) {
                    for (int folderNodeNum = 0; folderNodeNum < 8; ++folderNodeNum) {
                        JSONArray jsonFileNodes = new JSONArray();
                        JSONObject folderNode = new JSONObject();
                        folderNode.put("nodePathName", blocks[i].getNode()[folderNodeNum].getNodePathName());
                        folderNode.put("nodeType", blocks[i].getNode()[folderNodeNum].getNodeType());
                        folderNode.put("nodeAttribute", blocks[i].getNode()[folderNodeNum].getNodeAttribute());
                        folderNode.put("nodeBeginDisk", blocks[i].getNode()[folderNodeNum].getNodeBeginDisk());
                        folderNode.put("nodeLength", blocks[i].getNode()[folderNodeNum].getNodeLength());
                        folderNode.put("createTime", blocks[i].getNode()[folderNodeNum].getCreateTime());
                        folderNode.put("lastModifiedTime", blocks[i].getNode()[folderNodeNum].getLastModifiedTime());
                        jsonFileNodes.put(folderNode);
                        json.put("disk-" + i + "-folderNode-" + folderNodeNum, jsonFileNodes);
                    }

                    JSONObject jsonContent = new JSONObject();
                    jsonContent.put("content", blocks[i].getContent());
                    json.put("disk-" + i, jsonContent);
                }
            }
            json.put("FAT", jsonFAT);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonStr = json.toString();
        try {
            String path = System.getProperty("java.class.path");
            int firstIndex = path.lastIndexOf(System.getProperty("path.separator")) + 1;
            int lastIndex = path.lastIndexOf(File.separator) + 1;
            path = path.substring(firstIndex, lastIndex);
//            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(new File("recover.json"))));
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(new File(path + File.separator + "recover.json"))));
            if (writer != null) System.out.println("NO");
            writer.write(jsonStr);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("BackUp!");
        Alert information = new Alert(Alert.AlertType.INFORMATION, "Backup Successfully! You can check recover.json .");
        information.setTitle("Message");
        information.setHeaderText("System Information");
        information.showAndWait();
    }

    //新建文件
    public Node createFile(String pathName) {
        int folderIndex = pathSearch(pathName);

        if (folderIndex == -1) {
            System.out.println("createFile Failed");
            return null;
        }
        if (folderIndex == 0) {
            System.out.println("createFile Failed");
            return null;
        }
        String[] pathArray = pathName.split("/");
        String fileName = new String(pathArray[pathArray.length - 1]);
        int n = Util.remaindedCapacity(blocks, folderIndex);
        if (n == -1) {

            System.out.println("Node is full!");
            return null;
        }
        int totalFreeDisk = fat.getFreeDiskNum();
        if (totalFreeDisk == 0) {

            System.out.println("No more space");
            return null;
        }
        int freeDisk = fat.getFreeDisk();
        fat.getItem()[freeDisk] = -1;
        FileType nodeAttribute = FileType.NONE;
        if (pathName.endsWith(".txt")) {

            nodeAttribute = FileType.FILE;
        } else {

            nodeAttribute = FOLDER;
        }

        blocks[folderIndex].getNode()[n].initFolderNode(fileName, "  ", nodeAttribute, freeDisk, 1);
        if (nodeAttribute == FileType.FILE) {
            System.out.println("Create file");
            updateDisksCapacity(-8);
        } else {
            System.out.println("Create folder");
            updateDisksCapacity(-8);
        }
        return blocks[folderIndex].getNode()[n];
    }

    //打开文件
    public String openFile(String pathName) {
        int folderIndex = pathSearch(pathName);

        if (folderIndex != 0 && folderIndex != 2) {
            System.out.println("Open successfully!");
            return null;
        }
        if (openFiles.size() > 50) {
            System.out.println("Open too much files");
            return null;
        }

        int beginDiskIndex = getBeginDiskNum(pathName);
        if (beginDiskIndex == 0) {
            System.out.println("Error");
            return null;
        }
        if (pathName.endsWith(".txt")) {

            if (!judgeOpenFile(pathName)) {

                OpenFile openFile = initOpenFile(beginDiskIndex, pathName);
                openFiles.add(openFile);
            }
        }

        if (pathName.endsWith(".txt")) {
            StringBuilder sbBuilder = new StringBuilder();
            sbBuilder.append(getFileContent(beginDiskIndex));
            return sbBuilder.toString();
        } else {
            return null;
        }

    }

    //删除文件
    public void deleFile(String pathName) {

        int beginDisk = getBeginDiskNum(pathName);
        if (beginDisk == -1 || beginDisk == 0) {
            return;
        }

        if (!deleFolderNode(pathName)) {
            System.out.println("File not found!");
            return;
        }
        if (!freeDiskCapacity(beginDisk)) {
            System.out.println("Out of space");
            return;
        }
        System.out.println("Delete");
        return;
    }

    //储存文件内容
    public void storeIntoFile(String pathName, String content) {
        System.out.println(content + "storeintofile");
        int len = storeIntoDisk(pathName, content);
        Node node = getFolderNode(pathName);
        if (node == null) {
            return;
        }
        node.setNodeLength(len);
        return;
    }

    //设置文件名
    public boolean setFilename(String pathName, String newName) {

        boolean flag = checkRename(pathName, newName);
        if (flag) return false;
        StringBuilder path = new StringBuilder(pathName);
        path.append(".");
        int diskNum = pathSearch(path.toString());
        String fileName = Util.getFileName(pathName);
        int n = 0;
        for (int i = 0; i < 8; i++) {

            if (fileName.equals(blocks[diskNum].getNode()[i].getNodePathName())) {
                n = i;
            }
        }
        blocks[diskNum].getNode()[n].setNodePathName(newName);
        return true;
    }

    //检查重名
    public boolean checkRename(String pathName, String newName) {
        StringBuilder path = new StringBuilder(pathName);
        path.append(".");
        int diskNum = pathSearch(path.toString());
        for (int i = 0; i < 8; i++) {
            if (newName.equals(blocks[diskNum].getNode()[i].getNodePathName())) {
                return true;
            }
        }

        return false;
    }

    //--------------------------------------------------------------------------------------//

    private int getBeginDiskNum(String pathName) {

        if ("/".equals(pathName)) {
            return 2;
        }
        int diskNum = pathSearch(pathName);
        if (diskNum != 0) {
            return 0;
        }
        int length = pathName.split("/").length;
        String fileName = pathName.split("/")[length - 1];
        pathName += ".";
        diskNum = pathSearch(pathName);
        int i = 0;
        for (; i < 8; i++) {
            if (fileName.equals(blocks[diskNum].getNode()[i].getNodePathName())) {
                return blocks[diskNum].getNode()[i].getNodeBeginDisk();
            }
        }

        return 0;
    }

    //比对路径，只有当每个文件结点都相同时才能确定是同一文件
    private int pathSearch(String pathName) {

        if ("/".equals(pathName)) {
            return 2;
        }
        String[] pathArray = pathName.split("/");
        String fileName = new String(pathArray[pathArray.length - 1]);
        if (Util.isEmpty(pathName)) {

            return -1;
        }
        int folderIndex = 2;
        boolean flag = false;
        int type = 1;
        FileType fileType = FileType.FOLDER;
        for (int i = 1; i < pathArray.length; i++) {
            if (i == pathArray.length - 1) {

                if (fileName.endsWith(".txt")) {
                    type = 0;
                    fileType = FileType.FILE;
                }
            } else {
                type = 1;
                fileType = FileType.FOLDER;
            }
            Node[] nodes = Arrays.copyOf(blocks[folderIndex].getNode(), blocks[folderIndex].getNode().length);
            int j = 0;
            for (; j < 8; j++) {

                int k = nodes[j].getNodeBeginDisk();
                if (k != 0) {
                    if (nodes[j].getNodeAttribute() == fileType) {
                        if (pathArray[i].equals(nodes[j].getNodePathName())) {

                            if (i == pathArray.length - 1) {

                                flag = true;
                                break;
                            }
                            folderIndex = k;
                            break;
                        }
                    }
                }
            }
            if (j >= 8 && i < pathArray.length - 1) {
                return -1;
            }
        }

        if (!flag) {
            return folderIndex;
        } else {
            return 0;
        }
    }

    //获取文件内容
    private String getFileContent(int diskNum) {

        StringBuilder sBuffer = new StringBuilder();
        String str = null;
        while (diskNum != -1) {

            sBuffer.append(blocks[diskNum].getContent());
            try {
                diskNum = fat.nextDiskIndex(diskNum);
            } catch (Exception e) {
                System.exit(0);
            }
        }
        return sBuffer.toString();
    }

    private boolean judgeOpenFile(String pathName) {

        int beginDiskIndex = getBeginDiskNum(pathName);
        String[] strArray = pathName.split("/");
        String fileName = strArray[strArray.length - 1];
        Iterator<OpenFile> it = openFiles.iterator();
        while (it.hasNext()) {

            OpenFile openFile = it.next();
            if (openFile.getDiskNumber() == beginDiskIndex &&
                    openFile.getFileName().equals(fileName)) {
                return true;
            }
        }
        return false;

    }

    private OpenFile initOpenFile(int diskNum, String pathName) {
        String[] strArray = pathName.split("/");
        String fileName = " ";
        if (strArray.length > 1)
            fileName = strArray[strArray.length - 1];
        FileType nodeAttribute = FileType.NONE;
        if (pathName.endsWith(".txt")) {

            nodeAttribute = FileType.FOLDER;
        } else {
            nodeAttribute = FileType.FILE;
        }
        int beginDiskNum = diskNum;
        int length = fat.getFileLength(beginDiskNum);
        int flag = 1;
        OpenFile openFile = new OpenFile(fileName, nodeAttribute, beginDiskNum, length, flag);

        return openFile;

    }

    //将内容写入Block
    private int storeIntoDisk(String pathName, String content) {

        System.out.println(content + "storeintodisk");

        int beginDiskNum = getBeginDiskNum(pathName);
        if (beginDiskNum == 0 || beginDiskNum == -1) {

            System.out.println("Error path");
            return 0;
        }
        String originalContent = getFileContent(beginDiskNum);
        int length = (int) (Math.ceil(((double) content.length() / 64)));
        int size = fat.getFreeDiskNum();
        if (length > size) {
            System.out.println("Too long");
            return 0;
        }
        boolean flag = freeDiskCapacity(beginDiskNum);
        if (!flag) {
            System.out.println("No space");
            return 0;
        }
        int i = 0;
        int nextDiskNum = 0;
        for (; i < length - 1; i++) {

            blocks[beginDiskNum].setContent(content.substring(64 * i, 64 * (i + 1)));
            fat.getItem()[beginDiskNum] = -1;
            nextDiskNum = fat.getFreeDisk();
            fat.getItem()[beginDiskNum] = nextDiskNum;
            beginDiskNum = nextDiskNum;
        }
        blocks[beginDiskNum].setContent(content.substring(64 * i, content.length()));
        fat.getItem()[beginDiskNum] = -1;
        int usedCapacity = originalContent.length() - content.length();
        updateDisksCapacity(usedCapacity);

        Node node = getFolderNode(pathName);
        //node.setLastModifiedTime(new Timestamp(System.currentTimeMillis()));

        return length;
    }

    private boolean freeDiskCapacity(int beginDiskNum) {
        if (beginDiskNum < 3 || beginDiskNum > 127) {
            System.out.println("No space");
            return false;
        }
        int nextDisk = 0;
        do {
            nextDisk = fat.getItem()[beginDiskNum];
            fat.getItem()[beginDiskNum] = 0;
            blocks[beginDiskNum].setContent("");
            beginDiskNum = nextDisk;

        } while (nextDisk != -1);

        return true;
    }

    private Node getFolderNode(String pathName) {
        int diskNum = -1;
        diskNum = pathSearch(pathName);
        if (diskNum == -1 || diskNum == 0) {
            return null;
        }
        StringBuilder buff = new StringBuilder(pathName);
        buff.append(".");
        diskNum = pathSearch(buff.toString());
        int length = pathName.split("/").length;
        String fileName = pathName.split("/")[length - 1];
        int i = 0;
        for (; i < 8; i++) {
            if (fileName.equals(blocks[diskNum].getNode()[i].getNodePathName())) {
                return blocks[diskNum].getNode()[i];
            }
        }

        return null;
    }

    private boolean deleFolderNode(String pathName) {

        int diskNum = 0;
        diskNum = pathSearch(pathName);
        if (diskNum == -1 || diskNum == 0) {
            return false;
        }
        StringBuilder path = new StringBuilder(pathName);
        path.append(".");
        diskNum = pathSearch(path.toString());
        int length = pathName.split("/").length;
        String fileName = pathName.split("/")[length - 1];
        int i = 0;
        for (; i < 8; i++) {
            if (fileName.equals(blocks[diskNum].getNode()[i].getNodePathName())) {
                updateDisksCapacity(blocks[diskNum].getNode()[i].getNodeLength());
                blocks[diskNum].getNode()[i] = new Node();
                return true;
            }
        }
        return false;
    }


    private boolean updateDisksCapacity(int usedCapacity) {
        int used = this.getDisksUsedCapacity();
        int free = this.getDiskFreeCapaciyty();
        if ((free + usedCapacity) < 0) {
            return false;
        }
        used -= usedCapacity;
        free += usedCapacity;
        this.setDisksUsedCapacity(used);
        this.setDiskFreeCapaciyty(free);
        return true;
    }


}
