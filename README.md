# Linux File System Implementation

A Java-based implementation of a Linux file system, simulating core file system components including inodes, directories, bitmaps, and the file system superblock.

## Overview

This project implements fundamental Linux file system concepts in Java. It provides a simulation of how file systems organize and manage data, including file metadata storage, directory management, and free space tracking.

## Project Components

### Core Classes

- **Superblock.java** - Contains metadata about the file system (total blocks, inodes, block size, etc.)
- **Inode.java** - Represents file metadata (size, permissions, timestamps, block pointers, etc.)
- **Bitmap.java** - Tracks free and allocated blocks/inodes using a bitmap structure
- **Directory.java** - Manages directory operations and contains directory entries
- **DirectoryEntry.java** - Represents individual file/directory entries with name and inode mappings
- **FileSystem.java** - Main file system class handling file operations and management
- **Main.java** - Entry point demonstrating file system usage

## Features

The implementation includes:
- File creation and deletion
- Directory management
- Inode allocation and deallocation
- Block allocation using bitmaps
- File system initialization and formatting
- Directory traversal and file lookup

## Architecture

The file system follows a hierarchical structure similar to Unix/Linux:
- **Superblock**: Stores global file system metadata
- **Inode Table**: Collection of inodes describing files and directories
- **Data Blocks**: Storage for actual file content
- **Bitmap**: Tracks which blocks and inodes are in use

## How It Works

1. The file system is initialized with a superblock containing configuration
2. Inodes are created to represent files and directories
3. Bitmaps track which inodes and blocks are available
4. Directories maintain mappings between filenames and inode numbers
5. File operations manipulate this structure to simulate a real file system

## Getting Started

### Prerequisites
- Java 8 or higher

### Running the Project

```bash
javac *.java
java Main
```

This will compile all Java files and run the Main class, which demonstrates the file system functionality.

## Usage Example

The Main.java file provides examples of:
- Creating files and directories
- Reading file information
- Deleting files
- Traversing directories

## Project Structure

```
linux_file_system/
├── Superblock.java
├── Inode.java
├── Bitmap.java
├── Directory.java
├── DirectoryEntry.java
├── FileSystem.java
└── Main.java
```

## Learning Objectives

This project is useful for understanding:
- File system architecture and design
- Inode-based file systems (ext2/ext3/ext4)
- Block allocation strategies
- Directory management
- File metadata organization

## License

This project is open source and available under the MIT License.

## Author

anubhav100rao

---

**Note**: This is an educational implementation designed to demonstrate file system concepts. For production use, refer to actual Linux file system implementations.