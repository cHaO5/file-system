package model;

public class FAT {
    public  static FAT fat;
    private int[] item;

    public FAT() {

        item = new int[128];
        item[0] = -1;
        item[1] = -1;
        item[2] = -1;
        for(int i = 3; i < 128; i++) {

            item[i] = 0;
        }
    }


    public static FAT getInstance() {

        if(fat == null) {
            fat = new FAT();
        }

        return fat;
    }

    public void format() {
        System.out.println("FAT format!");
        item = new int[128];
        item[0] = -1;
        item[1] = -1;
        item[2] = -1;
        for(int i = 3; i < 128; i++) {

            item[i] = 0;
        }
    }


    public int getFreeDisk() {

        for(int i = 3; i < 128; i++) {

            if(item[i] == 0) {
                return i;
            }
        }
        return 0;
    }

    public int nextDiskIndex(int diskIndex) throws ArrayIndexOutOfBoundsException{

        if(diskIndex < 3 || diskIndex > 127) {
            throw new ArrayIndexOutOfBoundsException(diskIndex);
        }
        return item[diskIndex];
    }


    public int getFileLength(int beginDiskNum) {

        if(item[beginDiskNum] == 0) {

            System.out.println("Not Found!");
            return -1;
        }
        int len = 0;
        while(beginDiskNum != -1) {

            len++;
            beginDiskNum = item[beginDiskNum];
        }

        return len;
    }


    public int getFreeDiskNum() {

        int n = 0;
        for(int i = 3; i < 128; i++) {

            if(item[i] == 0) n++;
        }

        return n;
    }

    public int[] getItem() {
        return item;
    }
}
