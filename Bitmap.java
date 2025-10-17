package file_structure;

// Bitmap.java - Track free/used blocks and inodes
import java.util.BitSet;

public class Bitmap {
    private BitSet bits;
    private int size;

    public Bitmap(int size) {
        this.size = size;
        this.bits = new BitSet(size);
        // All bits start as 0 (free)
    }

    // Allocate the first free bit
    public int allocate() {
        for (int i = 0; i < size; i++) {
            if (!bits.get(i)) {
                bits.set(i);
                return i;
            }
        }
        return -1; // No free bits
    }

    // Free a specific bit
    public void free(int index) {
        if (index >= 0 && index < size) {
            bits.clear(index);
        }
    }

    // Check if a bit is allocated
    public boolean isAllocated(int index) {
        return index >= 0 && index < size && bits.get(index);
    }

    // Get number of free bits
    public int getFreeCount() {
        return size - bits.cardinality();
    }

    // Reserve a specific bit (for system use)
    public void reserve(int index) {
        if (index >= 0 && index < size) {
            bits.set(index);
        }
    }
}
