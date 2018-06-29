package model;

public class Util {

    public static int remaindedCapacity(Block[] blocks, int folderIndex) {
        for (int i = 0; i < 8; i++) {
            if (blocks[folderIndex].getNode()[i].getNodeBeginDisk() == 0) return i;
        }
        return -1;
    }

    public static boolean isEmpty(String str) {
        if (str == "" || str == null) {
            return true;
        }
        return false;
    }

    public static String deleRootStr(String pathName) {
        StringBuilder childItemPath = new StringBuilder(pathName);
        int n = childItemPath.indexOf(":");
        childItemPath.delete(0, n + 1);
        return childItemPath.toString();
    }

    public static String getFileName(String pathName) {
        String[] pathArray = pathName.split("/");
        String fileName = new String(pathArray[pathArray.length - 1]);
        return fileName;
    }
}
