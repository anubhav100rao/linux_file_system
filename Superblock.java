package file_structure;

// Superblock.java - File System Metadata
public class Superblock {
    private int magicNumber; // File system identifier (0xEF53 for ext2)
    private int blockSize; // Typically 4096 bytes
    private long totalBlocks; // Total blocks in file system
    private long freeBlocks; // Available blocks
    private long totalInodes; // Total inodes
    private long freeInodes; // Available inodes
    private int firstDataBlock; // First block containing data
    private long mountTime; // Last mount timestamp
    private int mountCount; // Number of times mounted
    private FileSystemState state; // CLEAN or DIRTY

    public enum FileSystemState {
        CLEAN, DIRTY
    }

    public Superblock(int blockSize, long totalBlocks, long totalInodes) {
        this.magicNumber = 0xEF53;
        this.blockSize = blockSize;
        this.totalBlocks = totalBlocks;
        this.freeBlocks = totalBlocks - 1; // Reserve first block
        this.totalInodes = totalInodes;
        this.freeInodes = totalInodes - 1; // Reserve root inode
        this.firstDataBlock = 1;
        this.state = FileSystemState.CLEAN;
    }

    public boolean allocateBlock() {
        if (freeBlocks > 0) {
            freeBlocks--;
            return true;
        }
        return false;
    }

    public void freeBlock() {
        if (freeBlocks < totalBlocks) {
            freeBlocks++;
        }
    }

    public boolean allocateInode() {
        if (freeInodes > 0) {
            freeInodes--;
            return true;
        }
        return false;
    }

    public void freeInode() {
        if (freeInodes < totalInodes) {
            freeInodes++;
        }
    }

    // Getters
    public int getBlockSize() {
        return blockSize;
    }

    public long getFreeBlocks() {
        return freeBlocks;
    }

    public long getFreeInodes() {
        return freeInodes;
    }

    public FileSystemState getState() {
        return state;
    }

    public void setState(FileSystemState state) {
        this.state = state;
    }
}
