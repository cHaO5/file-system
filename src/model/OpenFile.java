package model;

//被打开的文件序列
public class OpenFile {

    private String fileName;
    private FileType fileAttribute;
    private int diskNumber;
    private int length;
    private int flag;

    public OpenFile() {
        fileName = new String();
        diskNumber = -1;
        length = 0;
        flag = 0;
    }

    public OpenFile(String fileName, FileType fileAttribute, int diskNumber, int length, int flag) {
        super();
        this.fileName = fileName;
        this.fileAttribute = fileAttribute;
        this.diskNumber = diskNumber;
        this.length = length;
        this.flag = flag;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileType getFileAttribute() {
        return fileAttribute;
    }

    public void setFileAttribute(FileType fileAttribute) {
        this.fileAttribute = fileAttribute;
    }

    public int getDiskNumber() {
        return diskNumber;
    }

    public void setDiskNumber(int diskNumber) {
        this.diskNumber = diskNumber;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
