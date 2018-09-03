package ccxh.top.schedule.util;


import ccxh.top.schedule.ApplicationAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;


public class IOUtil {
    private final static Logger logger=LoggerFactory.getLogger(IOUtil.class);

    public IOUtil() throws UnsupportedEncodingException {
    }

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


    /**
     *
     * @param path  务必带/
     * @return
     * @throws Exception
     */
    public static String readJarFileString(String path) throws Exception {
        InputStream inputStream=IOUtil.class.getResourceAsStream(path);
        StringBuilder sb=new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream), "utf-8"));
        String str=null;
        while ((str=bufferedReader.readLine())!=null){
            sb.append(str).append('\n');
        }
        return sb.toString();
    }


}
