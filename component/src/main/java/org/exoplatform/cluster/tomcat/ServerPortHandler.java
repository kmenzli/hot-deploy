package org.exoplatform.cluster.tomcat;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: menzli
 * Date: 07/10/13
 * Time: 10:31
 * To change this template use File | Settings | File Templates.
 */
public class ServerPortHandler {
    /**
     * public static void changePorts(File destTomcatConf,String startupPort,String shutDownport)
     * This method changes the HTTP connector, AJP and shutdown ports of the new instance being created
     *
     * @param destTomcatConf The file object representing the destination tomcat instance's conf directory
     * @param startupPort The HTTP connector port to be used for new instance
     * @param ajpPort  The AJP port to be used for new instance
     * @param shutDownport The shutdown port to be used for new instance
     * @throws java.io.IOException
     */
    public static void changePorts(File destTomcatConf,String startupPort,String ajpPort, String shutDownport) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(destTomcatConf));
        String fileContents = "";
        String str ="";
        while ((str = br.readLine()) != null) {
            fileContents += str;
        }
        int shutDownIndex = fileContents.indexOf("shutdown=\"SHUTDOWN\"");
        String shutDownString = fileContents.substring(shutDownIndex-20,shutDownIndex+20);
        String newShutDownString = "<Server port=\""+shutDownport+"\" shutdown=\"SHUTDOWN\">";

        int startupIndex = fileContents.indexOf("<Connector port=");
        String startupString = fileContents.substring(startupIndex,startupIndex+42);
        String newStartupString = "<Connector port=\""+startupPort+"\" protocol=\"HTTP/1.1\"";

        int ajpIndex = fileContents.indexOf("protocol=\"AJP/1.3\"");
        String ajpString = fileContents.substring(ajpIndex-23,ajpIndex+41);
        String newAjpString = "<Connector port=\""+ajpPort+"\" protocol=\"AJP/1.3\" redirectPort=\"8443\" />";


        fileContents = fileContents.replaceAll(shutDownString, newShutDownString);
        fileContents = fileContents.replaceAll(startupString, newStartupString);
        fileContents = fileContents.replaceAll(ajpString, newAjpString);

        BufferedWriter bw = new BufferedWriter(new FileWriter(destTomcatConf));
        bw.write(fileContents);
        bw.flush();
        bw.close();
    }
}
