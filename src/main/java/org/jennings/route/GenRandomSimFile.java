/**
 * Merges multiple Route Files created by CreateRoutes into a simFile
 * 
 * David Jennings
 */
package org.jennings.route;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author david
 */
public class GenRandomSimFile {

    public String tail(File file) {

        RandomAccessFile fileHandler = null;
        try {
            fileHandler = new RandomAccessFile(file, "r");
            long fileLength = fileHandler.length() - 1;
            StringBuilder sb = new StringBuilder();

            for (long filePointer = fileLength; filePointer != -1; filePointer--) {
                fileHandler.seek(filePointer);
                int readByte = fileHandler.readByte();

                if (readByte == 0xA) {
                    if (filePointer == fileLength) {
                        continue;
                    }
                    break;

                } else if (readByte == 0xD) {
                    if (filePointer == fileLength - 1) {
                        continue;
                    }
                    break;
                }

                sb.append((char) readByte);
            }

            String lastLine = sb.reverse().toString();
            return lastLine;
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fileHandler != null) {
                try {
                    fileHandler.close();
                } catch (Exception e) {
                    /* ignore */
                }
            }
        }
    }

    public void gen1() {
        // Load all filename (*.txt)

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss.sss");
        
        
        Long simTime = System.currentTimeMillis();

        String simFilename = "simFile.dat";

        Path simPath = Paths.get("./" + simFilename);

        int duration = 3000;  // number of seconds
        Long duration_ms = duration * 1000L;

        ArrayList filenames = new ArrayList<File>();

        File folder = new File(".");
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            //System.out.println(file.getName());
            if (file.getName().endsWith(".txt")) {
                filenames.add(file);
            }

        }

        

        Random rnd = new Random(System.currentTimeMillis());

        try {

            Files.deleteIfExists(simPath);
            Files.createFile(simPath);

        } catch (Exception e) {
            e.printStackTrace();
        }

        int i = 0;
        // Create sim file (Destroy if already exists)
        while (i < 10000) {
            i++;
            int n = Math.abs(rnd.nextInt() % filenames.size());
            File f = (File) filenames.get(n);

            String tmpFile = "tmpFile.dat";
            Path tmpPath = Paths.get("./" + tmpFile);

            FileReader fr = null;
            BufferedReader br = null;

            FileReader fr_tmp = null;
            BufferedReader br_tmp = null;
                        
            FileWriter fw = null;
            BufferedWriter bw = null;            
            
            try {
                // Copy current simFile to tmpFile
                Files.deleteIfExists(tmpPath);
                Files.copy(simPath, tmpPath);

                fw = new FileWriter(simFilename);
                bw = new BufferedWriter(fw);

                String filename = f.getName();

                System.out.println(n + ": " + f.getName());

                String lastLine = tail(f);
                System.out.println("lastLine: " + lastLine);

                // Milliseconds of last line
                Long lastTime = Long.parseLong(lastLine.split(",")[0]);

                // Generate a random (startTime) from 0 to (lastTime)
                Long startTime = Math.abs(rnd.nextLong() % lastTime);
                Long endTime = startTime + duration_ms;

                // If startTime is close to end then set for last duration ms
                if (endTime > lastTime) {
                    endTime = lastTime;
                }

                fr = new FileReader(f);
                br = new BufferedReader(fr);
                
                fr_tmp = new FileReader(tmpFile);
                br_tmp = new BufferedReader(fr_tmp);
                

                String line = null;
                String lineTmp = null;
                                             
                lineTmp = br_tmp.readLine(); // read first line
                
                while ((line = br.readLine()) != null)  {
                    // Continue until both br and br_tmp have no records
                    String lineParts[] = line.split(",");                    
                    Long time = Long.parseLong(lineParts[0]);
                    
                                        
                    if (time > endTime) {
                        // exit the loop
                        break;
                    }
                    if (time > startTime) {                        
                        Long sTime = time - startTime + simTime;    
                        
                        if (lineTmp == null) {
                            // tmpFile is exhausted
                            String nLine = sTime + "," + i  + "," + sdf.format(new Date(sTime)) + "," + filename.split("\\.")[0] + "," + lineParts[1] + "," + lineParts[2] + "," + lineParts[3] + "," + lineParts[4];
                            //System.out.println(nLine);
                            bw.write(nLine);
                            bw.newLine();                            
                        } else {
                            String linePartsTmp[] = lineTmp.split(",");
                            Long sTimeTmp = Long.parseLong(linePartsTmp[0]);
                            while (sTimeTmp <= sTime ) {
                                //System.out.println(">" + lineTmp);
                                bw.write(lineTmp);
                                bw.newLine();                            
                                lineTmp = br_tmp.readLine();
                                if (lineTmp == null) break;
                                linePartsTmp = lineTmp.split(",");
                                sTimeTmp = Long.parseLong(linePartsTmp[0]);
                            }
                            String nLine = sTime + "," + i + "," + sdf.format(new Date(sTime)) + "," + filename.split("\\.")[0] + "," + lineParts[1] + "," + lineParts[2] + "," + lineParts[3] + "," + lineParts[4];
                            //System.out.println(nLine);
                            bw.write(nLine);
                            bw.newLine();                              
                        }
                        
                        
                    }
                    

                }
                // Add rest from br_tmp
                while ((lineTmp = br_tmp.readLine()) != null ) {
                    
                    bw.write(lineTmp);
                    bw.newLine();
                }
                
                                
                bw.flush();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fr.close();
                } catch (Exception e) {
                    // ok to ignore
                }
                try {
                    br.close();
                } catch (Exception e) {
                    // ok to ignore
                }
                try {
                    fr_tmp.close();
                } catch (Exception e) {
                    // ok to ignore
                }
                try {
                    br_tmp.close();
                } catch (Exception e) {
                    // ok to ignore
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
                
            }

        }

    }

    public static void main(String args[]) {
        GenRandomSimFile t = new GenRandomSimFile();
        t.gen1();
        
//        Long n = System.currentTimeMillis();
//        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss.sss");
//        System.out.println(sdf.format(new Date(n)));
        
    }

}
