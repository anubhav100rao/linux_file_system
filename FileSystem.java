package file_structure;

// FileSystem.java - Main file system implementation
import java.util.*;

public class FileSystem {
    private Superblock superblock;
    private Bitmap inodeBitmap;
    private Bitmap blockBitmap;
    private Map<Integer, Inode> inodeTable;
    private Map<Integer, Directory> directoryCache;
    private Map<Integer, byte[]> blockStorage;

    // Root directory is always inode 2
    private static final int ROOT_INODE = 2;

    public FileSystem(int blockSize, long totalBlocks, long totalInodes) {
        this.superblock = new Superblock(blockSize, totalBlocks, totalInodes);
        this.inodeBitmap = new Bitmap((int) totalInodes);
        this.blockBitmap = new Bitmap((int) totalBlocks);
        this.inodeTable = new HashMap<>();
        this.directoryCache = new HashMap<>();
        this.blockStorage = new HashMap<>();

        // Reserve inode 0 and 1 (system reserved)
        inodeBitmap.reserve(0);
        inodeBitmap.reserve(1);

        // Reserve block 0 (superblock)
        blockBitmap.reserve(0);

        // Create root directory
        createRootDirectory();
    }

    
    private void createRootDirectory() {
        Inode rootInode = new Inode(ROOT_INODE,
                Inode.FileType.DIRECTORY, 0, 0);
        inodeBitmap.reserve(ROOT_INODE);
        inodeTable.put(ROOT_INODE, rootInode);

        Directory rootDir = new Directory(rootInode);
        directoryCache.put(ROOT_INODE, rootDir);

        superblock.allocateInode();
    }

    // Create a new file
    public boolean createFile(String path, int uid, int gid) {
        String[] parts = parsePath(path);
        if (parts.length == 0)
            return false;

        String fileName = parts[parts.length - 1];
        String parentPath = getParentPath(parts);

        // Find parent directory
        Inode parentInode = resolvePath(parentPath);
        if (parentInode == null ||
                parentInode.getType() != Inode.FileType.DIRECTORY) {
            return false;
        }

        Directory parentDir = getDirectory(parentInode.getInodeNumber());

        // Check if file already exists
        if (parentDir.findEntry(fileName) != null) {
            return false;
        }

        // Allocate new inode
        int inodeNum = inodeBitmap.allocate();
        if (inodeNum < 0 || !superblock.allocateInode()) {
            return false;
        }

        // Create new file inode
        Inode fileInode = new Inode(inodeNum,
                Inode.FileType.REGULAR_FILE, uid, gid);
        inodeTable.put(inodeNum, fileInode);

        // Add directory entry
        DirectoryEntry entry = new DirectoryEntry(inodeNum, fileName, (byte) 1);
        parentDir.addEntry(entry);

        return true;
    }

    // Create a new directory
    public boolean createDirectory(String path, int uid, int gid) {
        String[] parts = parsePath(path);
        if (parts.length == 0)
            return false;

        String dirName = parts[parts.length - 1];
        String parentPath = getParentPath(parts);

        // Find parent directory
        Inode parentInode = resolvePath(parentPath);
        if (parentInode == null ||
                parentInode.getType() != Inode.FileType.DIRECTORY) {
            return false;
        }

        Directory parentDir = getDirectory(parentInode.getInodeNumber());

        // Check if directory already exists
        if (parentDir.findEntry(dirName) != null) {
            return false;
        }

        // Allocate new inode
        int inodeNum = inodeBitmap.allocate();
        if (inodeNum < 0 || !superblock.allocateInode()) {
            return false;
        }

        // Create new directory inode
        Inode dirInode = new Inode(inodeNum,
                Inode.FileType.DIRECTORY, uid, gid);
        inodeTable.put(inodeNum, dirInode);

        // Create directory structure
        Directory newDir = new Directory(dirInode);
        directoryCache.put(inodeNum, newDir);

        // Update .. to point to parent
        newDir.removeEntry("..");
        newDir.addEntry(new DirectoryEntry(
                parentInode.getInodeNumber(), "..", (byte) 2));

        // Add directory entry in parent
        DirectoryEntry entry = new DirectoryEntry(inodeNum, dirName, (byte) 2);
        parentDir.addEntry(entry);

        return true;
    }

    // Write data to a file
    public boolean writeFile(String path, byte[] data) {
        Inode inode = resolvePath(path);
        if (inode == null ||
                inode.getType() != Inode.FileType.REGULAR_FILE) {
            return false;
        }

        int blockSize = superblock.getBlockSize();
        int blocksNeeded = (data.length + blockSize - 1) / blockSize;

        // Allocate blocks
        List<Integer> blocks = new ArrayList<>();
        for (int i = 0; i < blocksNeeded; i++) {
            int blockNum = blockBitmap.allocate();
            if (blockNum < 0 || !superblock.allocateBlock()) {
                // Rollback
                for (int b : blocks) {
                    blockBitmap.free(b);
                    superblock.freeBlock();
                }
                return false;
            }
            blocks.add(blockNum);
            inode.addBlock(blockNum);
        }

        // Write data to blocks
        for (int i = 0; i < blocksNeeded; i++) {
            int offset = i * blockSize;
            int length = Math.min(blockSize, data.length - offset);
            byte[] blockData = new byte[blockSize];
            System.arraycopy(data, offset, blockData, 0, length);
            blockStorage.put(blocks.get(i), blockData);
        }

        inode.setSize(data.length);
        inode.updateModifiedTime();
        return true;
    }

    // Read data from a file
    public byte[] readFile(String path) {
        Inode inode = resolvePath(path);
        if (inode == null ||
                inode.getType() != Inode.FileType.REGULAR_FILE) {
            return null;
        }

        int blockSize = superblock.getBlockSize();
        long fileSize = inode.getSize();
        byte[] data = new byte[(int) fileSize];

        int blocksToRead = (int) ((fileSize + blockSize - 1) / blockSize);
        for (int i = 0; i < blocksToRead; i++) {
            int blockNum = inode.getBlockNumber(i);
            if (blockNum < 0)
                continue;

            byte[] blockData = blockStorage.get(blockNum);
            if (blockData != null) {
                int offset = i * blockSize;
                int length = (int) Math.min(blockSize, fileSize - offset);
                System.arraycopy(blockData, 0, data, offset, length);
            }
        }

        inode.updateAccessTime();
        return data;
    }

    // List directory contents
    public List<String> listDirectory(String path) {
        Inode inode = resolvePath(path);
        if (inode == null ||
                inode.getType() != Inode.FileType.DIRECTORY) {
            return Collections.emptyList();
        }

        Directory dir = getDirectory(inode.getInodeNumber());
        List<String> files = new ArrayList<>();
        for (DirectoryEntry entry : dir.listEntries()) {
            if (!entry.getName().equals(".") &&
                    !entry.getName().equals("..")) {
                files.add(entry.getName());
            }
        }
        return files;
    }

    // Delete a file
    public boolean deleteFile(String path) {
        String[] parts = parsePath(path);
        if (parts.length == 0)
            return false;

        String fileName = parts[parts.length - 1];
        String parentPath = getParentPath(parts);

        Inode fileInode = resolvePath(path);
        if (fileInode == null ||
                fileInode.getType() != Inode.FileType.REGULAR_FILE) {
            return false;
        }

        Inode parentInode = resolvePath(parentPath);
        Directory parentDir = getDirectory(parentInode.getInodeNumber());

        // Free all blocks
        for (int i = 0; i < 12; i++) {
            int blockNum = fileInode.getBlockNumber(i);
            if (blockNum >= 0) {
                blockBitmap.free(blockNum);
                blockStorage.remove(blockNum);
                superblock.freeBlock();
            }
        }

        // Free inode
        inodeBitmap.free(fileInode.getInodeNumber());
        inodeTable.remove(fileInode.getInodeNumber());
        superblock.freeInode();

        // Remove directory entry
        parentDir.removeEntry(fileName);

        return true;
    }

    // Helper: Resolve path to inode
    private Inode resolvePath(String path) {
        if (path.equals("/")) {
            return inodeTable.get(ROOT_INODE);
        }

        String[] parts = parsePath(path);
        Inode current = inodeTable.get(ROOT_INODE);

        for (String part : parts) {
            if (current.getType() != Inode.FileType.DIRECTORY) {
                return null;
            }

            Directory dir = getDirectory(current.getInodeNumber());
            DirectoryEntry entry = dir.findEntry(part);
            if (entry == null) {
                return null;
            }

            current = inodeTable.get(entry.getInodeNumber());
            if (current == null) {
                return null;
            }
        }

        return current;
    }

    private String[] parsePath(String path) {
        if (path.equals("/"))
            return new String[0];
        path = path.replaceAll("^/+", "").replaceAll("/+$", "");
        return path.isEmpty() ? new String[0] : path.split("/");
    }

    private String getParentPath(String[] parts) {
        if (parts.length <= 1)
            return "/";
        return "/" + String.join("/",
                Arrays.copyOfRange(parts, 0, parts.length - 1));
    }

    private Directory getDirectory(int inodeNum) {
        return directoryCache.computeIfAbsent(inodeNum,
                k -> new Directory(inodeTable.get(k)));
    }

    // Get file system statistics
    public String getStats() {
        return String.format(
                "File System Stats:" +
                        "  Block Size: %d bytes" +
                        "  Free Blocks: %d" +
                        "  Free Inodes: %d" +
                        "  Total Files: %d" + "  State: %s",
                superblock.getBlockSize(),
                superblock.getFreeBlocks(),
                superblock.getFreeInodes(),
                inodeTable.size(),
                superblock.getState());
    }
}
