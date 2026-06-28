package superlink.filemanage.xmltool.operation;

import java.util.List;

public interface Xml {
    public List view();
    public void add(String path);
    public void del(String path);
}
