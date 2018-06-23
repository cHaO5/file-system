package model;

import com.sun.tools.corba.se.idl.constExpr.Times;

import java.sql.Timestamp;

public class FolderNode {
    private String nodePathName;
    private String nodeType;
    private int nodeAttritute;
    private int nodeBeginDisk;
    private int nodeLength;
    private int readType = 1;
    private Timestamp createTime;
    private Timestamp lastModifiedTime;

    public FolderNode() {

        nodeAttritute = 0;   //锟斤拷示锟矫登硷拷锟斤拷未锟斤拷使锟斤拷
        nodeBeginDisk = 0;   //锟斤拷示锟矫登硷拷锟斤拷未锟斤拷使锟斤拷
    }

    public FolderNode(int attribute, String nodePathName, int  nodeBeginDisk) {

        this.nodeAttritute = attribute;
        this.nodePathName = nodePathName;
        this.nodeBeginDisk = nodeBeginDisk;
        this.nodeLength = 1;
    }

    public void initFolderNode(String nodePathName, String nodeType, int  nodeAttritute,
                               int  nodeBeginDisk, int  nodeLength) {

        this.nodePathName = nodePathName;
        this.nodeType = new String(nodeType);
        this.nodeAttritute = nodeAttritute;
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

    public int getNodeAttritute() {
        return nodeAttritute;
    }

    public void setNodeAttritute(int nodeAttritute) {
        this.nodeAttritute = nodeAttritute;
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

    //设置文件为只读
    public void setOnlyReadType(){
        this.readType = 0;
    }

    //设置文件为可读可写
    public void setCanWriteReadType(){
        this.readType = 1;
    }

    public int getReadType(){
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
