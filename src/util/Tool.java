package util;

import model.Disk;

public class Tool {

    public static int remaindedCapacity(Disk[] disks, int folderIndex) {

        for (int i = 0; i < 8; i++) {

            if (disks[folderIndex].getFolderNode()[i].getNodeBeginDisk() == 0) return i;
        }
        return -1;
    }
}
