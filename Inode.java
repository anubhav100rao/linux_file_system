package file_structure;

import java.util.*;

public class Inode {
    private int inodeNumber;
    private FileType type;
    private int permissions; // Unix permissions (e.g., 0755)
    private int uid; // Owner user ID
    private int gid; // Owner group ID
    private long size; // File size in bytes
    private int linkCount; // Number of hard links

    // Timestamps
    private long createdTime;
    private long modifiedTime;
    private long accessedTime;

    // Block pointers
    private int[] directBlocks; // 12 direct pointers
    private int singleIndirect; // Points to block of pointers
    private int doubleIndirect; // Points to block of blocks of pointers
    private int tripleIndirect; // Three levels of indirection

    public enum FileType {
        REGULAR_FILE,
        DIRECTORY,
        SYMBOLIC_LINK,
        BLOCK_DEVICE,
        CHARACTER_DEVICE,
        NAMED_PIPE,
        SOCKET
    }

    public Inode(int inodeNumber, FileType type, int uid, int gid) {
        this.inodeNumber = inodeNumber;
        this.type = type;
        this.uid = uid;
        this.gid = gid;
        this.permissions = (type == FileType.DIRECTORY) ? 0755 : 0644; // perform commands like chmod here
        this.size = 0;
        this.linkCount = 1;

        long currentTime = System.currentTimeMillis();
        this.createdTime = currentTime;
        this.modifiedTime = currentTime;
        this.accessedTime = currentTime;

        this.directBlocks = new int[12];
        Arrays.fill(directBlocks, -1); // -1 means not allocated
        this.singleIndirect = -1;
        this.doubleIndirect = -1;
        this.tripleIndirect = -1;
    }

    // Add a block pointer
    public boolean addBlock(int blockNumber) {
        for (int i = 0; i < directBlocks.length; i++) {
            if (directBlocks[i] == -1) {
                directBlocks[i] = blockNumber;
                return true;
            }
        }
        // Would need to implement indirect blocks here
        return false;
    }

    // Get block number for a given logical block index
    public int getBlockNumber(int logicalBlock) {
        if (logicalBlock < 12) {
            return directBlocks[logicalBlock];
        }
        // Would need to implement indirect block lookup
        return -1;
    }

    public void updateAccessTime() {
        this.accessedTime = System.currentTimeMillis();
    }

    public void updateModifiedTime() {
        this.modifiedTime = System.currentTimeMillis();
    }

    // Getters and setters
    public int getInodeNumber() {
        return inodeNumber;
    }

    public FileType getType() {
        return type;
    }

    public int getPermissions() {
        return permissions;
    }

    public void setPermissions(int permissions) {
        this.permissions = permissions;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getLinkCount() {
        return linkCount;
    }

    public void incrementLinkCount() {
        linkCount++;
    }

    public void decrementLinkCount() {
        linkCount--;
    }

    public int getUid() {
        return uid;
    }

    public int getGid() {
        return gid;
    }

    public long getModifiedTime() {
        return modifiedTime;
    }
}
