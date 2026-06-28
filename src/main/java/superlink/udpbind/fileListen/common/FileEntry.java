package superlink.udpbind.fileListen.common;

import java.io.File;
import java.io.Serializable;

public class FileEntry implements Serializable {

    private static final long serialVersionUID = -2505664948818681153L;

    static final FileEntry[] EMPTY_ENTRIES = new FileEntry[0];

    private final FileEntry parent;
    private FileEntry[] children;
    private final File file;
    private String name;
    private boolean exists;
    private boolean directory;
    private long lastModified;
    private long length;
    public int refed=0;

    /**
     * Construct a new monitor for a specified {@link File}.
     *
     * @param file The file being monitored
     */
    public FileEntry(final File file) {
        this(null, file);
    }

    /**
     * Construct a new monitor for a specified {@link File}.
     *
     * @param parent The parent
     * @param file The file being monitored
     */
    public FileEntry(final FileEntry parent, final File file) {
        if (file == null) {
            throw new IllegalArgumentException("File is missing");
        }
        this.file = file;
        this.parent = parent;
        this.name = file.getName();
    }

    /**
     * Refresh the attributes from the {@link File}, indicating
     * whether the file has changed.
     * <p>
     * This implementation refreshes the <code>name</code>, <code>exists</code>,
     * <code>directory</code>, <code>lastModified</code> and <code>length</code>
     * properties.
     * <p>
     * The <code>exists</code>, <code>directory</code>, <code>lastModified</code>
     * and <code>length</code> properties are compared for changes
     *
     * @param file the file instance to compare to
     * @return {@code true} if the file has changed, otherwise {@code false}
     */
    public boolean refresh(final File file) {

        // cache original values
        final boolean origExists       = exists;
        final long    origLastModified = lastModified;
        final boolean origDirectory    = directory;
        final long    origLength       = length;

        // refresh the values
        name         = file.getName();
        exists       = file.exists();
        directory    = exists && file.isDirectory();
        lastModified = exists ? file.lastModified() : 0;
        length       = exists && !directory ? file.length() : 0;

        refed++;
        // Return if there are changes
        return exists != origExists ||
                lastModified != origLastModified ||
                directory != origDirectory ||
                length != origLength;
    }

    public boolean reload(final File file) {

        // cache original values
        final boolean origExists       = exists;
        final long    origLastModified = lastModified;
        final boolean origDirectory    = directory;
        final long    origLength       = length;

        // refresh the values
        name         = file.getName();
        exists       = file.exists();
        directory    = exists && file.isDirectory();
        lastModified = exists ? file.lastModified() : 0;
        length       = exists && !directory ? file.length() : 0;
        children = EMPTY_ENTRIES;

        refed++;
        // Return if there are changes
        return exists != origExists ||
                lastModified != origLastModified ||
                directory != origDirectory ||
                length != origLength;
    }

    /**
     * Create a new child instance.
     * <p>
     * Custom implementations should override this method to return
     * a new instance of the appropriate type.
     *
     * @param file The child file
     * @return a new child instance
     */
    public FileEntry newChildInstance(final File file) {
        return new FileEntry(this, file);
    }

    /**
     * Return the parent entry.
     *
     * @return the parent entry
     */
    public FileEntry getParent() {
        return parent;
    }

    /**
     * Return the level
     *
     * @return the level
     */
    public int getLevel() {
        return parent == null ? 0 : parent.getLevel() + 1;
    }

    /**
     * Return the directory's files.
     *
     * @return This directory's files or an empty
     * array if the file is not a directory or the
     * directory is empty
     */
    public FileEntry[] getChildren() {
        return children != null ? children : EMPTY_ENTRIES;
    }

    public void setChildren(final FileEntry... children) {
        this.children = children;
    }

    public void addChildren(final FileEntry... children) {
        FileEntry[] children0=getChildren();
        // 创建一个新的数组，长度是两个输入数组长度之和
        FileEntry[] mergedArray = new FileEntry[children0.length + children.length];
        // 复制第一个数组到新数组
        System.arraycopy(children0, 0, mergedArray, 0, children0.length);
        // 复制第二个数组到新数组
        System.arraycopy(children, 0, mergedArray, children0.length, children.length);
        for (FileEntry entry:children){
            entry.reload(entry.getFile());
        }
        this.children = mergedArray;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(final long lastModified) {
        this.lastModified = lastModified;
    }

    public long getLength() {
        return length;
    }

    public void setLength(final long length) {
        this.length = length;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(final boolean exists) {
        this.exists = exists;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(final boolean directory) {
        this.directory = directory;
    }

    public String toString(){
        return file.getAbsolutePath();
    }
}
