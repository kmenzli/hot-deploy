package org.exoplatform.cluster.tomcat;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: menzli
 * Date: 07/10/13
 * Time: 10:31
 * To change this template use File | Settings | File Templates.
 */
public class DirectoryCopy {
    /**
     * copyFolder(File src, File dest)
     * The method uses recursion to invoke itself and hence copy the contents of
     * a folder recursively into another
     *
     * @param src It is the src file/folder which needs to be copied
     * @param dest It is the destination file/folder which will be created
     * @throws java.io.IOException
     */
    public static void copyFolder(File src, File dest) throws IOException {
        if(src.isDirectory()){

            //if directory not exists, create it
            if(!dest.exists()){
                dest.mkdir();
            }

            //list all the directory contents
            String files[] = src.list();

            for (String file : files) {
                //construct the src and dest file structure
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                //recursive copy
                copyFolder(srcFile,destFile);
            }

        }else{
            //if file, then copy it
            //Use bytes stream to support all file types
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;
            //copy the file content in bytes
            while ((length = in.read(buffer)) > 0){
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
        }
    }
}
