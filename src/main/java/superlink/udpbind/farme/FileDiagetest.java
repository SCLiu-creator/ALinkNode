package superlink.udpbind.farme;
import java.awt.*;

import java.io.File;
public class FileDiagetest {


        public static void main(String[] args) {
            // 创建FileDialog对象
            FileDialog fileDialog = new FileDialog((Frame) null, "Choose a file", FileDialog.LOAD);

            // 显示文件选择对话框
            fileDialog.setVisible(true);

            // 获取用户选择的文件

            String selectedFile = fileDialog.getDirectory() + fileDialog.getFile();

            // 输出用户选择的文件名
            System.out.println("Selected file: " + selectedFile);
        }

}
