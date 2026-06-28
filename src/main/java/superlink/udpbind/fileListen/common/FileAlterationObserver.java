package superlink.udpbind.fileListen.common;

import org.dom4j.Element;
import superlink.udpbind.cloude.FileTrigger;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class FileAlterationObserver implements Serializable {

    private static final long serialVersionUID = 1185122225658782848L;
    private final List<FileAlterationListener> listeners = new CopyOnWriteArrayList<>();
    private final FileEntry rootEntry;
    private final FileFilter fileFilter;
    private final Comparator<File> comparator;
    private final Comparator<FileEntry> comparatorFileEntry=new NameFileEntryComparator(NameFileComparator.IOCase.INSENSITIVE);
    FileTrigger fileTrigger;

    public static final File[] EMPTY_FILE_ARRAY = new File[0];

    public FileAlterationObserver(final File directory) {
        this(directory, null);
    }

    public FileAlterationObserver(final File directory, final FileFilter fileFilter) {
        this(new FileEntry(directory), fileFilter);
    }

    protected FileAlterationObserver(final FileEntry rootEntry, final FileFilter fileFilter) {
        if (rootEntry == null) {
            throw new IllegalArgumentException("Root entry is missing");
        }
        if (rootEntry.getFile() == null) {
            throw new IllegalArgumentException("Root directory is missing");
        }
        this.rootEntry = rootEntry;
        this.fileFilter = fileFilter;
        this.comparator = NameFileComparator.NAME_SYSTEM_COMPARATOR;
    }

    protected FileAlterationObserver(final FileTrigger fileTrigger) {
        if (fileTrigger == null) {
            throw new IllegalArgumentException("Root entry is missing");
        }
        if (fileTrigger.document == null) {
            throw new IllegalArgumentException("Root directory is missing");
        }
        Element root = fileTrigger.document.getRootElement();
        String rootpath = root.attribute(0).getValue();
        List<Element> elements = root.elements();
        Element e = elements.get(0);
        String targetpath = e.attribute(0).getValue();
        rootEntry = new FileEntry(new File(rootpath + "/" + targetpath));
//        this.rootEntry = null;
        this.fileFilter = null;
        this.fileTrigger = fileTrigger;
        this.comparator = NameFileComparator.NAME_SYSTEM_COMPARATOR;
    }

    public File getDirectory() {
        return rootEntry.getFile();
    }

    public FileFilter getFileFilter() {
        return fileFilter;
    }

    public void addListener(final FileAlterationListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a file system listener.
     *
     * @param listener The file system listener
     */
    public void removeListener(final FileAlterationListener listener) {
        if (listener != null) {
            while (listeners.remove(listener)) {
                // empty
            }
        }
    }

    public Iterable<FileAlterationListener> getListeners() {
        return listeners;
    }

    public void initialize() throws Exception {
        if (this.fileTrigger != null) {
            initialize1();
        } else {
            initialize0();
        }
    }

    public void initialize0(){
        System.out.println(" observer.initialize()  " + getDirectory());
        rootEntry.refresh(rootEntry.getFile());
        final FileEntry[] children = doListFiles(rootEntry.getFile(), rootEntry);
        rootEntry.setChildren(children);
    }

    public void initialize1() throws Exception {
        System.out.println(" observer.initialize()  " + getDirectory());
        Element root = fileTrigger.document.getRootElement();
        String rootpath = root.attribute(0).getValue();
        String path = "";
        List<Element> elements = root.elements();
        Element e = elements.get(0);
//        String targetpath = e.attribute(0).getValue();
//            rootEntry=new FileEntry(new File(rootpath+"/"+targetpath));
//        parserFileDoc(rootpath + "/" , e, rootEntry);
        rootEntry.reload(rootEntry.getFile());
        List<Element> elementss = e.elements();
        for (Element es : elementss) {
            parserFileDoc(rootEntry.getFile().getAbsolutePath() + '/', es, rootEntry);
        }
//            rootEntry.refresh(rootEntry.getFile());
//            final FileEntry[] children = doListFiles(rootEntry.getFile(), rootEntry);
//            rootEntry.setChildren(children);
    }

    private void parserFileDoc(String datapath, Element element, FileEntry entry) {
        if ("p".equals(element.attribute(0).getName())) {
            String value=element.attribute(0).getValue();
            String path = datapath + value;
            List<Element> elements = element.elements();
            FileEntry cf=entry.newChildInstance(new File(path));
            entry.addChildren(cf);
            for (Element e : elements) {
                parserFileDoc(path + '/', e, cf);
            }
        } else {
            String file = datapath + element.attribute(0).getValue();
            FileEntry cf=entry.newChildInstance(new File(file));
            entry.addChildren(cf);
        }
    }

    /**
     * Checks whether the file and its children have been created, modified or deleted.
     */
    public void checkAndNotify() {
        for (final FileAlterationListener listener : listeners) {
            listener.onStart(this);
        }
        /* fire directory/file events */
        final File rootFile = rootEntry.getFile();
        if (rootFile.exists()) {
            checkAndNotify(rootEntry, rootEntry.getChildren(), listFiles(rootFile));
        } else if (rootEntry.isExists()) {
            checkAndNotify(rootEntry, rootEntry.getChildren(), EMPTY_FILE_ARRAY);
        } else {
            // Didn't exist and still doesn't
//            doDelete(rootEntry);
        }

        /* fire onStop() */
        for (final FileAlterationListener listener : listeners) {
            listener.onStop(this);
        }
    }

    /**
     * Compares two file lists for files which have been created, modified or deleted.
     * @param parent   The parent entry
     * @param previous The original list of files
     * @param files    The current list of files
     */
    private void checkAndNotify(final FileEntry parent, final FileEntry[] previous, final File[] files) {
        int c = 0;
        Arrays.sort(previous,comparatorFileEntry);
        Arrays.sort(files,comparator);
        final FileEntry[] current = files.length > 0 ? new FileEntry[files.length] : FileEntry.EMPTY_ENTRIES;
        for (final FileEntry entry : previous) {
            while (c < files.length && comparator.compare(entry.getFile(), files[c]) > 0) {
                current[c] = createFileEntry(parent, files[c]);
                doCreate(current[c]);
                c++;
            }
            if (c < files.length && comparator.compare(entry.getFile(), files[c]) == 0) {
                doMatch(entry, files[c]);
                checkAndNotify(entry, entry.getChildren(), listFiles(files[c]));
                current[c] = entry;
                c++;
            } else {
                checkAndNotify(entry, entry.getChildren(), EMPTY_FILE_ARRAY);
                doDelete(entry);
            }
        }
        for (; c < files.length; c++) {
            current[c] = createFileEntry(parent, files[c]);
            doCreate(current[c]);
        }
        parent.setChildren(current);
    }

    private FileEntry createFileEntry(final FileEntry parent, final File file) {
        final FileEntry entry = parent.newChildInstance(file);
        entry.refresh(file);
        final FileEntry[] children = doListFiles(file, entry);
        entry.setChildren(children);
        return entry;
    }

    private FileEntry[] doListFiles(final File file, final FileEntry entry) {
        final File[] files = listFiles(file);
        final FileEntry[] children = files.length > 0 ? new FileEntry[files.length] : FileEntry.EMPTY_ENTRIES;
        for (int i = 0; i < files.length; i++) {
            children[i] = createFileEntry(entry, files[i]);
        }
        return children;
    }

    /**
     * Fires directory/file created events to the registered listeners.
     *
     * @param entry The file entry
     */
    private void doCreate(final FileEntry entry) {
        for (final FileAlterationListener listener : listeners) {
            if (entry.isDirectory()) {
                listener.onDirectoryCreate(entry.getFile());
            } else {
                listener.onFileCreate(entry.getFile());
            }
        }
        final FileEntry[] children = entry.getChildren();
        for (final FileEntry aChildren : children) {
            doCreate(aChildren);
        }
    }

    private void doMatch(final FileEntry entry, final File file) {
        if (entry.refresh(file)) {
            for (final FileAlterationListener listener : listeners) {
                if (entry.isDirectory()) {
                    listener.onDirectoryChange(file);
                } else {
                    listener.onFileChange(file);
                }
            }
        }
    }

    /**
     * Fires directory/file delete events to the registered listeners.
     *
     * @param entry The file entry
     */
    private void doDelete(final FileEntry entry) {
        for (final FileAlterationListener listener : listeners) {
            if (entry.isDirectory()) {
                listener.onDirectoryDelete(entry.getFile());
            } else {
                listener.onFileDelete(entry.getFile());
            }
        }
    }

    private File[] listFiles(final File file) {
        File[] children = null;
        if (file.isDirectory()) {
            children = fileFilter == null ? file.listFiles() : file.listFiles(fileFilter);
        }
        if (children == null) {
            children = EMPTY_FILE_ARRAY;
        }
        if (comparator != null && children.length > 1) {
            Arrays.sort(children, comparator);
        }
        return children;
    }

    /**
     * Returns a String representation of this observer.
     *
     * @return a String representation of this observer
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName());
        builder.append("[file='");
        builder.append(getDirectory().getPath());
        builder.append('\'');
        if (fileFilter != null) {
            builder.append(", ");
            builder.append(fileFilter.toString());
        }
        builder.append(", listeners=");
        builder.append(listeners.size());
        builder.append("]");
        return builder.toString();
    }

    public static void main(String[] args) {
        File File=new File("C:\\Users\\liusc\\Desktop\\新建文件夹 (2)");
        File[] Fls=File.listFiles();
        File[] Fls1=File.listFiles();
        System.out.println(Fls);
        System.out.println(Fls1);
    }

}
