package org.exoplatform.cluster.tomcat;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: menzli
 * Date: 07/10/13
 * Time: 10:31
 * To change this template use File | Settings | File Templates.
 */
public class ScriptFilesHandler {
    /**

     * createScripts(File destTomcat,File srcTomcat)

     * The method modifies the catalina.bat and catalina.sh files to set the CATALINA_HOME enviornment variables

     *

     * @param destTomcat File object representing the  destination tomcat instance

     * @param srcTomcat File object representing the source tomcat instance

     * @throws java.io.IOException

     */

    public static void createScripts(File destTomcat,File srcTomcat) throws IOException {

        File deststartupScriptBat = new File(destTomcat.getAbsoluteFile()+File.separator+"bin"+File.separator+"catalina.bat");
        File srcstartupScriptBat = new File(srcTomcat.getAbsoluteFile()+File.separator+"bin"+File.separator+"catalina.bat");

        String fileContents = " set CATALINA_HOME="+destTomcat.getAbsolutePath();
        FileWriter startupScriptBatFw = new FileWriter(deststartupScriptBat);
        startupScriptBatFw.write(fileContents+"\n");

        BufferedReader br = new BufferedReader(new FileReader(srcstartupScriptBat));

        String str ="";
        while ((str = br.readLine()) != null) {
            startupScriptBatFw.write(str+"\n");
        }

        startupScriptBatFw.flush();


        File deststartupScriptSh = new File(destTomcat.getAbsoluteFile()+File.separator+"bin"+File.separator+"catalina.sh");
        File srcstartupScriptSh = new File(srcTomcat.getAbsoluteFile()+File.separator+"bin"+File.separator+"catalina.sh");

        fileContents = " export CATALINA_HOME="+destTomcat.getAbsolutePath();
        FileWriter startupScriptShFw = new FileWriter(deststartupScriptSh);
        startupScriptShFw.write(fileContents+"\n");

        br = new BufferedReader(new FileReader(srcstartupScriptSh));

        str ="";
        while ((str = br.readLine()) != null) {
            startupScriptShFw.write(str+"\n");
        }

        startupScriptShFw.flush();


    }
}
