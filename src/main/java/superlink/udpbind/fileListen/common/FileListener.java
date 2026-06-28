package superlink.udpbind.fileListen.common;

import superlink.udpbind.cloude.CloudLocal;
import superlink.udpbind.cloude.FileTrigger;

import java.io.File;
import java.io.IOException;

public class FileListener implements FileAlterationListener{

    private String path;
    private FileTrigger trigger;
    private boolean d=false;

    public FileListener(String path){
        this.path=path;
    }
    public FileListener(FileTrigger trigger){
        this.trigger = trigger;
        this.path= trigger.AbsolutePath;
    }


    @Override
    public void onStart(FileAlterationObserver observer) {
        if (CloudLocal.isInitSynContainer()){
            this.trigger= CloudLocal.getSynContainer().localbin.map.get(this.path);
        }
       // System.out.println("onStart");
    }

    @Override
    public void onDirectoryCreate(File directory) {
        System.out.println("新建：" + directory.getAbsolutePath());
        trigger.addNode(directory.getAbsolutePath());
        d=true;
    }

    @Override
    public void onDirectoryChange(File directory) {
        System.out.println("修改：" + directory.getAbsolutePath());
    }

    @Override
    public void onDirectoryDelete(File directory) {
        System.out.println("删除：" + directory.getAbsolutePath());
        trigger.removeNode(directory.getAbsolutePath());
        d=true;
    }

    @Override
    public void onFileCreate(File file) {
        String compressedPath = file.getAbsolutePath();
        System.out.println("新建：" + compressedPath);
        if (file.canRead()) {
            // TODO 读取或重新加载文件内容
            System.out.println("文件变更，进行处理");
        }
        trigger.addNodeFile(file.getAbsolutePath());
        d=true;
    }

    @Override
    public void onFileChange(File file) {
        String compressedPath = file.getAbsolutePath();
        System.out.println("修改：" + compressedPath);
        trigger.change(file.getAbsolutePath());
    }

    @Override
    public void onFileDelete(File file) {
        System.out.println("删除：" + file.getAbsolutePath());
        trigger.removeNodeFlie(file.getAbsolutePath());
        d=true;
    }

    @Override
    public void onStop(FileAlterationObserver observer) {
        try {
            if (d){
                //todo
                trigger.save();
                d=false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
//            if (CloudeListenCaset.cloudeListenCaset!=null&& CloudeListenCaset.cloudeListenCaset.castThread!=null){
//                CloudeListenCaset.cloudeListenCaset.castThread.interrupt();
//            }

        }catch (Exception e){
            e.getMessage();
        }
        // System.out.println("onStop");
    }





}