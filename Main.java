package file_structure;

// Main.java - Demo and test program
public class Main {
    public static void main(String[] args) {
        // Create file system: 4KB blocks, 1000 blocks, 500 inodes
        FileSystem fs = new FileSystem(4096, 1000, 500);

        System.out.println("=== Linux File System Demo ===\n");
        System.out.println(fs.getStats());
        System.out.println();

        // Create directories
        System.out.println("Creating directories...");
        fs.createDirectory("/home", 1000, 1000);
        fs.createDirectory("/home/user", 1000, 1000);
        fs.createDirectory("/var", 0, 0);
        fs.createDirectory("/var/log", 0, 0);
        System.out.println("✓ Directories created");
        System.out.println();

        // Create files
        System.out.println("Creating files...");
        fs.createFile("/home/user/file1.txt", 1000, 1000);
        fs.createFile("/home/user/file2.txt", 1000, 1000);
        fs.createFile("/var/log/system.log", 0, 0);
        System.out.println("✓ Files created");
        System.out.println();

        // Write to file
        System.out.println("Writing to file...");
        String content = "Hello, Linux File System!\nThis is a test file.";
        fs.writeFile("/home/user/file1.txt", content.getBytes());
        System.out.println("✓ Data written to /home/user/file1.txt");
        System.out.println();

        // Read from file
        System.out.println("Reading from file...");
        byte[] data = fs.readFile("/home/user/file1.txt");
        System.out.println("Content: " + new String(data));
        System.out.println();

        // List directory contents
        System.out.println("Listing /home/user:");
        for (String file : fs.listDirectory("/home/user")) {
            System.out.println("  - " + file);
        }
        System.out.println();

        System.out.println("Listing /var/log:");
        for (String file : fs.listDirectory("/var/log")) {
            System.out.println("  - " + file);
        }
        System.out.println();

        // Delete file
        System.out.println("Deleting file...");
        fs.deleteFile("/home/user/file2.txt");
        System.out.println("✓ /home/user/file2.txt deleted");
        System.out.println();

        System.out.println("Listing /home/user after deletion:");
        for (String file : fs.listDirectory("/home/user")) {
            System.out.println("  - " + file);
        }
        System.out.println();

        // Final stats
        System.out.println(fs.getStats());
    }
}

/*
 * Expected Output:
 * === Linux File System Demo ===
 * 
 * File System Stats:
 * Block Size: 4096 bytes
 * Free Blocks: 999
 * Free Inodes: 494
 * Total Files: 6
 * State: CLEAN
 * 
 * Creating directories...
 * ✓ Directories created
 * 
 * Creating files...
 * ✓ Files created
 * 
 * Writing to file...
 * ✓ Data written to /home/user/file1.txt
 * 
 * Reading from file...
 * Content: Hello, Linux File System!
 * This is a test file.
 * 
 * Listing /home/user:
 * - file1.txt
 * - file2.txt
 * 
 * Listing /var/log:
 * - system.log
 * 
 * Deleting file...
 * ✓ /home/user/file2.txt deleted
 * 
 * Listing /home/user after deletion:
 * - file1.txt
 * 
 * File System Stats:
 * Block Size: 4096 bytes
 * Free Blocks: 998
 * Free Inodes: 495
 * Total Files: 5
 * State: CLEAN
 */
