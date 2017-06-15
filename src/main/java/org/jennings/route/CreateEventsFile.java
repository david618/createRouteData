/**
 *
 * Create a set of CSV files given a Route file, start time, and range
 *
 * David Jennings
 */
package org.jennings.route;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import org.json.JSONObject;

/**
 *
 * @author david
 */
public class CreateEventsFile {

    final private int TXT = 0;
    final private int JSON = 1;

    ArrayList<Thing> things = new ArrayList<>();

    public void run(String routeFile, Integer numThg, String outputFolder, String prefix, Long startTime, Integer stepSec, Integer durSec, Integer samplesPerFile, Integer format, Double maxAbsLat) {
        try {
            
            if (maxAbsLat == null) {
                maxAbsLat = 90.0;
            }
            
            // Create the Routes
            Routes rts;
            rts = new Routes();
            rts.load(routeFile);

            // Create Things
            Random rnd = new Random();
            int i = 0;
            while (i < numThg) {
                int rndoffset = Math.abs(rnd.nextInt());
                Thing t = new Thing(i, rts.get(i), rndoffset);
                things.add(t);
                i++;
            }

            String fs = System.getProperty("file.separator");
            String d = ",";

            int fileNum = 0;

            FileWriter fw = null;
            BufferedWriter bw = null;

            // CurrentTime
            Long t = startTime;  // millisecons from epoch

            Long numWritten = 0L;
            
            
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            df.setGroupingUsed(false);
            
            DecimalFormat df5 = new DecimalFormat();
            df5.setMaximumFractionDigits(5);
            df5.setGroupingUsed(false);
            
            
            while (t < startTime + durSec * 1000) {

                for (Thing thg : things) {
                    thg.setPosition(t);

                    String line = "";

                    switch (format) {
                        case TXT:
                            line = thg.id + d 
                                    + thg.timestamp + d 
                                    + df.format(thg.speed * 1000.0) + d
                                    + df.format(thg.dist) + d 
                                    + df.format(thg.bearing) + d
                                    + thg.rt.id + d
                                    + "\"" + thg.location + "\"" + d
                                    + thg.secsToDep + d
                                    + df5.format(thg.gc.getLon()) + d 
                                    + df5.format(thg.gc.getLat());
                            break;
                        case JSON:
                            JSONObject js = new JSONObject();
                            js.put("id", thg.id);
                            js.put("timestamp", thg.timestamp);
                            js.put("speed", df.format(thg.speed * 1000.0));
                            js.put("dist", df.format(thg.dist));
                            js.put("bearing", df.format(thg.bearing));
                            js.put("routeid", thg.rt.id);
                            js.put("location", thg.location);
                            js.put("secsToDep", thg.secsToDep);
                            js.put("lon", df5.format(thg.gc.getLon()));
                            js.put("lat", df5.format(thg.gc.getLat()));
                            line = js.toString();

                    }
                    
                    if (Math.abs(thg.gc.getLat()) > maxAbsLat) {
                        // Skip
                    } else {
            // Open First File
            
                        if (bw == null) {
                            fileNum += 1;
                            fw = new FileWriter(outputFolder + fs + prefix + String.format("%05d", fileNum));
                            bw = new BufferedWriter(fw);
                        }
                        
                        numWritten += 1;
                        bw.write(line);
                        bw.newLine();
                    }
                                                                                
                    if (numWritten % samplesPerFile == 0) {
                        bw.close();
                        fw.close();
                        bw = null;
                        fw = null;
                        
                    }                                        
                }

                t += stepSec * 1000;
            }

            try {
                fw.close();
            } catch (Exception e) {
                // ok to ignore
            }

            try {
                bw.close();
            } catch (Exception e) {
                // ok to ignore
            }
            

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

//        String routeFile = "routes10000_4day.json";
//        Integer numThg = 100000;
//        String outputFolder = "/home/david/testfolder";
//        String prefix = "data";
//        Long startTime = System.currentTimeMillis();
//        Integer stepSec = 60;
//        Integer durSec = 3600;
//        Integer samplesPerFile = 1000000;
//        Integer format = t.TXT;
//
//        t.run(routeFile, numThg, outputFolder, prefix, startTime, stepSec, durSec, samplesPerFile, format, null);
        
        
        int numArgs = args.length;
        CreateEventsFile t = new CreateEventsFile();
        
        if (numArgs < 9 || numArgs > 10) {
            System.err.println("Usage: CreateEventsFile routeFile numThings outputFolder prefix startTime step durationSec samplesPerFile format <latLimit>");
            System.err.println();
            System.err.println("Example: CreateEventsFile routes10000_4day.json 100000 /home/david/testfolder data now 60 3600 1000000 txt");            
        } else {
            
            String routeFile = args[0];
            Integer numThg = Integer.parseInt(args[1]);
            String outputFolder = args[2];
            String prefix = args[3];
            String startTimeStr = args[4];
            
            Long startTime = System.currentTimeMillis();
            if (startTimeStr.equalsIgnoreCase("now")) {
                // ok
            } else {
                startTime = Long.parseLong(startTimeStr);
            }
            
            Integer stepSec = Integer.parseInt(args[5]);
            Integer durSec = Integer.parseInt(args[6]);
            Integer samplesPerFile = Integer.parseInt(args[7]);
                                    
            Integer format = t.TXT;
            if (args[8].equalsIgnoreCase("json")) {
                format = t.JSON;
            } else if (args[8].equalsIgnoreCase("txt")) {
                format = t.TXT;
            } else {
                System.out.println("Unrecognized Format. Defaulting to txt");
            }
            
            Double absMaxLat = null;
            if (numArgs == 10) {
                absMaxLat = Double.parseDouble(args[9]);
            }

            t.run(routeFile, numThg, outputFolder, prefix, startTime, stepSec, durSec, samplesPerFile, format, null);
            
            
        }
        
        

    }
}
