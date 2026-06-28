package superlink.udpbind.fileListen.common;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NameFileEntryComparator  implements Comparator<FileEntry> {

    private final NameFileComparator.IOCase caseSensitivity;
    public NameFileEntryComparator(final NameFileComparator.IOCase caseSensitivity) {
        this.caseSensitivity = caseSensitivity == null ? NameFileComparator.IOCase.SENSITIVE : caseSensitivity;
    }

    public FileEntry[] sort(final FileEntry... files) {
        if (files != null) {
            Arrays.sort(files, this);
        }
        return files;
    }

    /**
     * Sort a List of files.
     * <p>
     * This method uses {@link Collections#sort(List, Comparator)}
     * and returns the original list.
     *
     * @param files The files to sort, may be null
     * @return The sorted list
     * @since 2.0
     */
    public List<FileEntry> sort(final List<FileEntry> files) {
        if (files != null) {
            Collections.sort(files, this);
        }
        return files;
    }

    /**
     * String representation of this file comparator.
     *
     * @return String representation of this file comparator
     */
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public int compare(FileEntry file1, FileEntry file2) {
        return caseSensitivity.checkCompareTo(file1.getFile().getName(), file2.getFile().getName());
    }
}
