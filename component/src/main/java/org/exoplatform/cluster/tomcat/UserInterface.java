package org.exoplatform.cluster.tomcat;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created with IntelliJ IDEA.
 * User: menzli
 * Date: 07/10/13
 * Time: 10:32
 * To change this template use File | Settings | File Templates.
 */
public class UserInterface {

    static Logger logger;
    static FileHandler fileHandler;
    static SimpleFormatter formatter;
    static {
        logger = Logger.getLogger("UserInterface");
        try {
            formatter = new SimpleFormatter();
            fileHandler = new FileHandler("app.log",true);
            fileHandler.setFormatter(formatter);
        } catch (IOException ex) {
            JOptionPane.showConfirmDialog(null, "File access problem while starting the log manager.", "", JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE);
            logger.log(Level.INFO, "Problem with creating log file " + ex.getMessage());
            System.exit(1);
        }
        logger.addHandler(fileHandler);
    }
    public static void main(String[] args) {

        //The steps involved in creating a new instance are:
        //1) Create an empty directory and copy bin, conf, logs, temp, webapps, lib and work folders from source tomcat installation
        //2) Change the HTTP connector, AJP and shutdown port numbers in new instance's server.xml file present in the conf directory
        //3) Update the catalina.bat and catalina.sh scripts with CATALINA_HOME env. variable.

        boolean inValidSrcLocation = true;
        boolean inValidDestLocation = true;
        boolean inValidTomcatInstances = true;
        boolean inValidPortNumber = true;
        String srcTomcatLocation = "";
        File srcTomcatConf = null;
        File srcTomcat = null;
        File destTomcatRoot = null;
        String noOfInstances = "";
        int instancesNum = 0;
        String destTomcatDirectory = "";

        try{
            while(inValidSrcLocation) {
                srcTomcatLocation = JOptionPane.showInputDialog(null,"Enter the  source tomcat location?","",JOptionPane.QUESTION_MESSAGE);
                logger.log(Level.INFO,"Source tomcat location is : " + srcTomcatLocation);

                if(srcTomcatLocation==null) {
                    System.exit(1);
                } else if("".equals(srcTomcatLocation)) {
                    JOptionPane.showMessageDialog(null, "Invalid soruce location entered. Please try again.", "", JOptionPane.ERROR_MESSAGE);
                } else {
                    srcTomcat = new File(srcTomcatLocation);
                    srcTomcatConf = new File(srcTomcatLocation+File.separator+"conf");
                    if(!srcTomcat.exists() || !srcTomcatConf.exists()){
                        JOptionPane.showMessageDialog(null, "Invalid soruce location entered. Please try again.", "", JOptionPane.ERROR_MESSAGE);
                    } else {
                        inValidSrcLocation=false;
                    }
                }
            }


			/*			if(!System.getenv("CATALINA_HOME").equals(srcTomcatLocation)){
				int response = JOptionPane.showConfirmDialog(null, "CATALINA_HOME: "+System.getenv().get("CATALINA_HOME")+" \nEnetered Location: "+
						srcTomcatLocation +" \nCATALINA_HOME is not same as the location entered. Proceed?", "", JOptionPane.WARNING_MESSAGE);
				if(response!=0){
					System.exit(1);
				}
			}*/

            while(inValidDestLocation) {
                destTomcatDirectory = JOptionPane.showInputDialog(null,"Enter the directory path which will contain files for new instances?","",JOptionPane.QUESTION_MESSAGE);
                logger.log(Level.INFO,"Destination tomcat root installation location is : " + destTomcatDirectory);

                if(destTomcatDirectory==null) {
                    System.exit(1);
                } else if("".equals(destTomcatDirectory)) {
                    JOptionPane.showMessageDialog(null, "Invalid destination location entered. Please try again.", "", JOptionPane.ERROR_MESSAGE);
                } else {
                    destTomcatRoot = new File(destTomcatDirectory);
                    if(!destTomcatRoot.exists()){
                        int response =JOptionPane.showConfirmDialog(null, "Destination Location doesn't exist. Create it?", "", JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE);
                        if(response==0){
                            File f = new File(destTomcatDirectory);
                            f.mkdir();
                        } else {
                            System.exit(1);
                        }
                    }
                    inValidDestLocation = false;
                }
            }


            while(inValidTomcatInstances) {
                noOfInstances = JOptionPane.showInputDialog(null,"Enter the  number of tomcat instances to create?","",JOptionPane.QUESTION_MESSAGE);
                logger.log(Level.INFO,"Number of tomcat instances to create is : " + noOfInstances);

                if(noOfInstances==null) {
                    System.exit(1);
                } else if("".equals(noOfInstances)) {
                    JOptionPane.showMessageDialog(null, "Invalid number of instanced entered. Please try again.", "", JOptionPane.ERROR_MESSAGE);
                } else {
                    try{
                        instancesNum = Integer.parseInt(noOfInstances);
                    } catch(NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Invalid number of instances entered. Please try again.", "", JOptionPane.ERROR_MESSAGE);
                        logger.log(Level.INFO, "Problem with tomcat instances input " + e.getMessage());
                        continue;
                    }
                    inValidTomcatInstances = false;
                }
            }


            List allPortsList = new ArrayList();


            while(inValidPortNumber) {
                for(int i=0;i<instancesNum;i++) {
                    String portscsv = JOptionPane.showInputDialog(null,"Enter the HTTP connector, AJP connector and shutdown ports " +
                            "separated by comma to be used by instance "+(i+1) + " (Ports corresponding to 8080, 8009,8005)?\nDo remember the port numbers entered here.","",JOptionPane.QUESTION_MESSAGE);
                    logger.log(Level.INFO,"Ports for instance " + (i+1) + " are : " + portscsv);

                    if(portscsv==null) {
                        System.exit(1);
                    } else if("".equals(portscsv)) {
                        JOptionPane.showMessageDialog(null, "Invalid ports input. Please try again.", "", JOptionPane.ERROR_MESSAGE);
                        inValidPortNumber = true;
                    } else {
                        try{
                            List ports = new ArrayList();
                            String[] portscsvSplit = portscsv.split(",");

                            if(portscsvSplit.length>3) {
                                throw (new IllegalArgumentException());
                            }
                            int startupPortNum = 0;
                            int ajpPortNum = 0;
                            int shutdownPortNum = 0;

                            for (int j = 0; j < portscsvSplit.length; j++) {
                                startupPortNum = Integer.parseInt(portscsvSplit[0]);
                                ajpPortNum = Integer.parseInt(portscsvSplit[1]);
                                shutdownPortNum = Integer.parseInt(portscsvSplit[2]);
                            }

                            if(startupPortNum < 0 || startupPortNum > 65535 || ajpPortNum < 0 || ajpPortNum > 65535 || shutdownPortNum < 0 || shutdownPortNum > 65535) {
                                throw (new IllegalArgumentException());
                            }

                            ports.add(portscsvSplit[0]);
                            ports.add(portscsvSplit[1]);
                            ports.add(portscsvSplit[2]);
                            allPortsList.add(ports);
                            inValidPortNumber = false;
                        } catch(Exception e){
                            logger.log(Level.INFO, "Problem with ports input " + e.getMessage());
                            JOptionPane.showMessageDialog(null, "Invalid ports input. Please try again.", "", JOptionPane.ERROR_MESSAGE);
                            inValidPortNumber = true;
                        }
                    }
                    if(inValidPortNumber) {
                        i--;
                    }
                }
            }


            JOptionPane.showConfirmDialog(null, "The process will run in background.\nYou will get success message when the processing is complete.", "", JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE);



            int count=1;
            for (Iterator iterator = allPortsList.iterator(); iterator.hasNext();) {
                List portsList = (List) iterator.next();

                logger.log(Level.INFO,"Going to run file operations for instance " + count);

                File destTomcatInstanceDirectory = new File(destTomcatDirectory + File.separator + "Tomcat" + count);
                File destTomcatConf = new File(destTomcatInstanceDirectory.getAbsoluteFile() + File.separator + "conf");
                File destTomcatLogs = new File(destTomcatInstanceDirectory.getAbsoluteFile() + File.separator + "logs");
                File destTomcatTemp = new File(destTomcatInstanceDirectory.getAbsoluteFile() + File.separator + "temp");
                File destTomcatwebapps = new File(destTomcatInstanceDirectory.getAbsoluteFile() + File.separator + "webapps");
                File destTomcatWork = new File(destTomcatInstanceDirectory.getAbsoluteFile() + File.separator + "work");
                File destTomcatBin = new File(destTomcatInstanceDirectory.getAbsoluteFile() + File.separator + "bin");
                File destTomcatLib = new File(destTomcatInstanceDirectory.getAbsoluteFile() + File.separator + "lib");

                destTomcatInstanceDirectory.mkdir();
                logger.log(Level.INFO,"Tomcat instance directory created at " + destTomcatInstanceDirectory.getAbsolutePath());

                destTomcatConf.mkdir();
                logger.log(Level.INFO,"Tomcat instance conf directory created at " + destTomcatConf.getAbsolutePath());

                destTomcatLogs.mkdir();
                logger.log(Level.INFO,"Tomcat instance logs directory created at " + destTomcatLogs.getAbsolutePath());

                destTomcatTemp.mkdir();
                logger.log(Level.INFO,"Tomcat instance temp directory created at " + destTomcatTemp.getAbsolutePath());

                destTomcatwebapps.mkdir();
                logger.log(Level.INFO,"Tomcat instance webapps directory created at " + destTomcatwebapps.getAbsolutePath());

                destTomcatWork.mkdir();
                logger.log(Level.INFO,"Tomcat instance work directory created at " + destTomcatWork.getAbsolutePath());

                destTomcatBin.mkdir();
                logger.log(Level.INFO,"Tomcat instance bin directory created at " + destTomcatBin.getAbsolutePath());

                File srcTomcatLogs = new File(srcTomcatLocation+File.separator+"logs");
                File srcTomcatTemp = new File(srcTomcatLocation+File.separator+"temp");
                File srcTomcatWebApps = new File(srcTomcatLocation+File.separator+"webapps");
                File srcTomcatWork = new File(srcTomcatLocation+File.separator+"work");
                File srcTomcatBin = new File(srcTomcatLocation+File.separator+"bin");
                File srcTomcatLib = new File(srcTomcatLocation+File.separator+"lib");

                try {
                    logger.log(Level.INFO,"");

                    logger.log(Level.INFO,"conf folder is going to be copied from " + srcTomcatConf.getAbsolutePath() + " to " + destTomcatConf.getAbsolutePath());
                    DirectoryCopy.copyFolder(srcTomcatConf, destTomcatConf);

                    logger.log(Level.INFO,"logs folder is going to be copied from " + srcTomcatLogs.getAbsolutePath() + " to " + destTomcatLogs.getAbsolutePath());
                    DirectoryCopy.copyFolder(srcTomcatLogs, destTomcatLogs);

                    logger.log(Level.INFO,"temp folder is going to be copied from " + srcTomcatTemp.getAbsolutePath() + " to " + destTomcatTemp.getAbsolutePath());
                    DirectoryCopy.copyFolder(srcTomcatTemp, destTomcatTemp);

                    logger.log(Level.INFO,"webapps folder is going to be copied from " + srcTomcatWebApps.getAbsolutePath() + " to " + destTomcatwebapps.getAbsolutePath());
                    DirectoryCopy.copyFolder(srcTomcatWebApps, destTomcatwebapps);

                    logger.log(Level.INFO,"work folder is going to be copied from " + srcTomcatWork.getAbsolutePath() + " to " + destTomcatWork.getAbsolutePath());
                    DirectoryCopy.copyFolder(srcTomcatWork, destTomcatWork);

                    logger.log(Level.INFO,"bin folder is going to be copied from " + srcTomcatBin.getAbsolutePath() + " to " + destTomcatBin.getAbsolutePath());
                    DirectoryCopy.copyFolder(srcTomcatBin, destTomcatBin);

                    logger.log(Level.INFO,"lib folder is going to be copied from " + srcTomcatLib.getAbsolutePath() + " to " + destTomcatLib.getAbsolutePath());
                    DirectoryCopy.copyFolder(srcTomcatLib, destTomcatLib);

                    logger.log(Level.INFO,"Changing destination tomcat ports in the server.xml");
                    ServerPortHandler.changePorts(new File(destTomcatInstanceDirectory.getAbsolutePath()+File.separator+"conf"+File.separator+"server.xml"),
                            (String)portsList.get(0),(String)portsList.get(1),(String)portsList.get(2));

                    logger.log(Level.INFO,"Changing destination tomcat scripts");
                    ScriptFilesHandler.createScripts(new File(destTomcatInstanceDirectory.getAbsolutePath()),srcTomcat);

                    count++;

                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Problem copying files", "", JOptionPane.ERROR_MESSAGE);
                    logger.log(Level.INFO,"Instance creation failed with exception " + e.getMessage());
                }
            }

            logger.log(Level.INFO,"All instance creation success");

            JOptionPane.showMessageDialog(null, "Tomcat instance creation successful\n"+noOfInstances + " tomcat instances created at location "+
                    destTomcatDirectory, "", JOptionPane.INFORMATION_MESSAGE);
        } catch(Exception e){
            JOptionPane.showMessageDialog(null, "Unexpected Error.", "", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.INFO,"Instance creation has failed with exception " + e.getMessage());
        }
    }
}
