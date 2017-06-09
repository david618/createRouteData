/**
 *
 * Create a set of CSV files given a Route file, start time, and range
 * Also need to know how many records to put in each file (for example 1,000,000)
 *
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

    public void run(String routeFile, Integer numThg, String outputFolder, Long startTime, Integer stepSec, Integer durSec, Integer samplesPerFile, Integer format) {
        try {
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

            int fileNum = 1;

            // Open First File
            FileWriter fw = new FileWriter(outputFolder + fs + "data" + String.format("%04d", fileNum));
            BufferedWriter bw = new BufferedWriter(fw);

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
                    
                    bw.write(line);
                    bw.newLine();
                    
                    numWritten += 1;
                    
                    if (numWritten % samplesPerFile == 0) {
                        bw.close();
                        fw.close();
                        fileNum += 1;
                        // Open First File
                        fw = new FileWriter(outputFolder + fs + "data" + String.format("%04d", fileNum));
                        bw = new BufferedWriter(fw);                        
                    }
                                        
                }

                t += stepSec * 1000;
            }
            
            bw.close();
            fw.close();
            

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        int numArgs = args.length;
        CreateEventsFile t = new CreateEventsFile();
        
        String routeFile = "routes10000_4day.json";
        Integer numThg = 100000;
        String outputFolder = "/home/david/testfolder";
        Long startTime = System.currentTimeMillis();
        Integer stepSec = 60;
        Integer durSec = 3600;
        Integer samplesPerFile = 1000000;
        Integer format = t.TXT;

        t.run(routeFile, numThg, outputFolder, startTime, stepSec, durSec, samplesPerFile, format);

    }
}
