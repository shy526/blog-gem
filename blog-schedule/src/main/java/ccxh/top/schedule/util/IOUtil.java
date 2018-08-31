package ccxh.top.schedule.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


public class IOUtil {
    private final static Logger logger=LoggerFactory.getLogger(IOUtil.class);
    private String  randomPath(String rootPath){
        return null;
    }
    private String  datePath(String rootPath){
        return null;
    }

    /**
     *
     * @param path
     */
    public static boolean deleteDir(String path){
        File dir = new File(path);
        if (!dir.exists()||dir.isFile()){
            return false;
        }
        File[] files = dir.listFiles();
        if (files==null){
            return false;
        }
        for (File file:files){
           if (file.isFile()){
               if (!file.delete()){
                   logger.info("delete {} failure",file.getPath());
               }
           }else {
              return IOUtil.deleteDir(file.getPath());
           }
        }
        return  dir.delete();
    }

}
