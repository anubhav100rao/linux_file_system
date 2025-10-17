package file_structure;

// Directory.java - Manages directory entries
import java.util.*;

public class Directory {
    private Inode inode;
    private List<DirectoryEntry> entries;

    public Directory(Inode inode) {
        if (inode.getType() != Inode.FileType.DIRECTORY) {
            throw new IllegalArgumentException("Inode must be a directory");
        }
        this.inode = inode;
        this.entries = new ArrayList<>();

        // Add . and .. entries
        entries.add(new DirectoryEntry(inode.getInodeNumber(), ".", (byte) 2));
        entries.add(new DirectoryEntry(inode.getInodeNumber(), "..", (byte) 2));
    }

    public boolean addEntry(DirectoryEntry entry) {
        // Check if entry already exists
        for (DirectoryEntry e : entries) {
            if (e.getName().equals(entry.getName())) {
                return false; // Entry already exists
            }
        }
        entries.add(entry);
        inode.updateModifiedTime();
        return true;
    }

    public boolean removeEntry(String name) {
        // Cannot remove . or ..
        if (name.equals(".") || name.equals("..")) {
            return false;
        }

        boolean removed = entries.removeIf(e -> e.getName().equals(name));
        if (removed) {
            inode.updateModifiedTime();
        }
        return removed;
    }

    public DirectoryEntry findEntry(String name) {
        for (DirectoryEntry entry : entries) {
            if (entry.getName().equals(name)) {
                return entry;
            }
        }
        return null;
    }

    public List<DirectoryEntry> listEntries() {
        return new ArrayList<>(entries);
    }

    public Inode getInode() {
        return inode;
    }
}
