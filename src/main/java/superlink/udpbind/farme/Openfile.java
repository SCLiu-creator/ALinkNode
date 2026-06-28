package superlink.udpbind.farme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class Openfile {

    public String name;
    public JButton open(){
        JButton jb2=new JButton("openfile");
        JFileChooser chooser=new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            jb2.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                //show file chooser dialog
                int result =chooser.showOpenDialog(null);
                //if file selected,set it as icon of the label
                if(result==JFileChooser.APPROVE_OPTION)
                {
                    name=chooser.getSelectedFile().getPath();
                    System.out.println(name);
                    File file = new File(name);
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }
        });
        return jb2;
    }
}