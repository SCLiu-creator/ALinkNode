package superlink.udpbind.fileListen.common;

import superlink.util.Utils;
import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public class NameFileComparator extends AbstractFileComparator implements Serializable {

    private static final long serialVersionUID = 8397947749814525798L;

    public static final Comparator<File> NAME_SYSTEM_COMPARATOR = new NameFileComparator(IOCase.SYSTEM);

    private final IOCase caseSensitivity;

    public NameFileComparator(final IOCase caseSensitivity) {
        this.caseSensitivity = caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity;
    }

    /**
     * Compare the names of two files with the specified case sensitivity.
     *
     * @param file1 The first file to compare
     * @param file2 The second file to compare
     * @return a negative value if the first file's name
     * is less than the second, zero if the names are the
     * same and a positive value if the first files name
     * is greater than the second file.
     */
    @Override
    public int compare(final File file1, final File file2) {
        return caseSensitivity.checkCompareTo(file1.getName(), file2.getName());
    }

    @Override
    public String toString() {
        return super.toString() + "[caseSensitivity=" + caseSensitivity + "]";
    }

    public enum IOCase {
        /**
         * The constant for case sensitive regardless of operating system.
         */
        SENSITIVE ("Sensitive", true),

        /**
         * The constant for case insensitive regardless of operating system.
         */
        INSENSITIVE ("Insensitive", false),

        /**
         * The constant for case sensitivity determined by the current operating system.
         * Windows is case-insensitive when comparing file names, Unix is case-sensitive.
         * <p>
         * <strong>Note:</strong> This only caters for Windows and Unix. Other operating
         * systems (e.g. OSX and OpenVMS) are treated as case sensitive if they use the
         * Unix file separator and case-insensitive if they use the Windows file separator
         * (see {@link java.io.File#separatorChar}).
         * <p>
         * If you serialize this constant on Windows, and deserialize on Unix, or vice
         * versa, then the value of the case-sensitivity flag will change.
         */
        SYSTEM ("System", !isSystemWindows());

        public static boolean isSystemWindows(){
            return Utils.getOs()==1;
        }

        /** Serialization version. */
        private static final long serialVersionUID = -6343169151696340687L;

        /** The sensitivity flag. */
        private final transient boolean sensitive;

        IOCase(final String name, final boolean sensitive) {
            this.sensitive = sensitive;
        }

        public int checkCompareTo(final String str1, final String str2) {
            Objects.requireNonNull(str1, "str1");
            Objects.requireNonNull(str2, "str2");
            return sensitive ? str1.compareTo(str2) : str1.compareToIgnoreCase(str2);
        }

        public boolean checkEquals(final String str1, final String str2) {
            Objects.requireNonNull(str1, "str1");
            Objects.requireNonNull(str2, "str2");
            return sensitive ? str1.equals(str2) : str1.equalsIgnoreCase(str2);
        }

        public boolean checkStartsWith(final String str, final String start) {
            return str.regionMatches(!sensitive, 0, start, 0, start.length());
        }

        public boolean checkEndsWith(final String str, final String end) {
            final int endLen = end.length();
            return str.regionMatches(!sensitive, str.length() - endLen, end, 0, endLen);
        }

        public int checkIndexOf(final String str, final int strStartIndex, final String search) {
            final int endIndex = str.length() - search.length();
            if (endIndex >= strStartIndex) {
                for (int i = strStartIndex; i <= endIndex; i++) {
                    if (checkRegionMatches(str, i, search)) {
                        return i;
                    }
                }
            }
            return -1;
        }

        public boolean checkRegionMatches(final String str, final int strStartIndex, final String search) {
            return str.regionMatches(!sensitive, strStartIndex, search, 0, search.length());
        }

    }

}
