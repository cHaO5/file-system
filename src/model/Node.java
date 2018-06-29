package model;

import java.sql.Timestamp;

import static model.FileType.NONE;

//储存具体文件夹或文件
public class Node {
    private String nodePathName;
    private String nodeType;
    private FileType nodeAttribute;
    private int nodeBeginDisk;
    private int nodeLength;
    private int readType = 1;
    private Timestamp createTime;
    private Timestamp lastModifiedTime;

    public Node() {
        nodePathName = "";
        nodeType = "";
        nodeAttribute = NONE;
        nodeBeginDisk = 0;
        nodeLength = 0;
        readType = 1;
        createTime = new Timestamp(System.currentTimeMillis());
        lastModifiedTime = new Timestamp(System.currentTimeMillis());
    }

    public void initFolderNode(String nodePathName, String nodeType, FileType nodeAttribute,
                               int nodeBeginDisk, int nodeLength) {

        this.nodePathName = nodePathName;
        this.nodeType = new String(nodeType);
        this.nodeAttribute = nodeAttribute;
        this.nodeBeginDisk = nodeBeginDisk;
        this.nodeLength = nodeLength;
        Timestamp temp = new Timestamp(System.currentTimeMillis());
        createTime = temp;
        lastModifiedTime = temp;
    }

    public String getNodePathName() {
        return nodePathName;
    }

    public void setNodePathName(String nodePathName) {
        this.nodePathName = nodePathName;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public FileType getNodeAttribute() {
        return nodeAttribute;
    }

    public void setNodeAttribute(FileType nodeAttribute) {
        this.nodeAttribute = nodeAttribute;
    }

    public int getNodeBeginDisk() {
        return nodeBeginDisk;
    }

    public void setNodeBeginDisk(int nodeBeginDisk) {
        this.nodeBeginDisk = nodeBeginDisk;
    }

    public int getNodeLength() {
        return nodeLength;
    }

    public void setNodeLength(int nodeLength) {
        this.nodeLength = nodeLength;
    }

    public int getReadType() {
        return this.readType;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Timestamp lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }
}
