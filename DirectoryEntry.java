package file_structure;

// DirectoryEntry.java - Maps filenames to inodes
public class DirectoryEntry {
    private int inodeNumber;
    private short recordLength;
    // recordLength: total size in bytes of this directory entry on-disk.
    // Many filesystems require directory entries to be aligned to 4-byte
    // boundaries; this value includes the fixed-size fields plus the
    // filename padded to the alignment boundary.
    private byte nameLength;
    // nameLength: the length of the filename in bytes (not including any
    // padding). Stored as a single byte here because filenames are typically
    // limited in directory entries (e.g., 255 bytes max in many filesystems).
    private byte fileType;
    // fileType: a small value indicating the kind of the inode this entry
    // refers to (e.g., regular file, directory, symbolic link). Using a byte
    // mirrors on-disk directory entry layouts that pack the file type compactly.
    private String name;
    // name: the actual filename (human-friendly String). When written to disk
    // this is the nameLength bytes followed by padding up to recordLength.

    public DirectoryEntry(int inodeNumber, String name, byte fileType) {
        this.inodeNumber = inodeNumber;
        this.name = name;
        this.nameLength = (byte) name.length();
        this.fileType = fileType;
        // Record length must be multiple of 4 for alignment
        this.recordLength = (short) (8 + ((nameLength + 3) & ~3));
    }

    public int getInodeNumber() {
        return inodeNumber;
    }

    public String getName() {
        return name;
    }

    public byte getFileType() {
        return fileType;
    }

    public short getRecordLength() {
        return recordLength;
    }

    @Override
    public String toString() {
        return String.format("DirEntry{name='%s', inode=%d, type=%d}",
                name, inodeNumber, fileType);
    }
}
