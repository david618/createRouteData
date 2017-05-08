/**
 *
 *
 *
 *
 * David Jennings
 */
package org.jennings.route;

import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author david
 */
public class SendEvents {

    final private int STDOUT = 0;
    final private int TCP = 1;
    final private int HTTP = 2;
    
    final private int DURATIONSSECS = 86400;

    Timer timer;
    int output;
//    Routes rts;
    ArrayList<Thing> things = new ArrayList<>();
    
    
    private OutputStream os = null;

    class CheckCount extends TimerTask {

        @Override
        public void run() {
            
            
            
            for (Thing t: things) {
                t.setPosition(System.currentTimeMillis());
                
                String d = ",";
                
                String line = t.id + d + t.timestamp + d + t.speed + d +
                        t.dist + d + t.bearing + d + t.rt.id + d +
                        "\"" + t.location + "\"" + d + t.secsToDep + d +
                        t.gc.getLon() + d + t.gc.getLat();
                
                switch (output) {
                    case STDOUT:
                        System.out.println(line);                
                        break;
                    case TCP:
                        line += "\n";
                        try {
                            os.write(line.getBytes());
                            os.flush();
                        } catch (Exception e) {
                            //System.out.println("Failed to write to socket");
                        }
                        break;
                    case HTTP:
                        
                        break;
                    default:
                        System.out.println("Invalid Output");
                }
                
                
            }
            System.out.println();
            
        }

    }

    /*
     where: (default -) 
        - http://urlendpoint (http) 
        - server:port (tcp) - Use '-' to send to standard output 
     what: (default 10:10) 
        - numRoutes:numThings -> Create numRoutes and then create numThings using these routes 
        - numRoutes:numThings:lllon,lllat,urlon,urlat -> Same as before only limited routes to the bounding box
        - filename:numThings -> Load routes from filename and create numThings using these routes
     rate: (default 1) 
        - Number of seconds between sending updates; min 1; max (600?) batch: (default 1000) 
        - Number of events to send in batch / parallel 
        - For example if number is 10000 and batch is 1000 then start 10 threads and each thread send 1000 
        - Need to set Max Threads to prevent system errors (100?)
     */    
    
    /**
     * Timer contents
     *
     * Compile event(s) and send
     *
     */
    private void send(String where, String what, String rate) {

        String parts[];
        String parts2[];
        
        try {           
            
            Routes rts;
            // Parse where            
            parts = where.trim().split(":");
            
            if (parts[0].equalsIgnoreCase("-")) {
                output = STDOUT;
            } else if (parts[0].equalsIgnoreCase("http")) {
                output = HTTP;
            } else {
                output = TCP;
                int port = 5565;
                String server = parts[0];
                
                port = Integer.parseInt(parts[1]);
                
                
                
                Socket skt = new Socket(server, port);
                this.os = skt.getOutputStream();
            }
            
            
            parts = what.trim().split(":");
            int numThg = 10;
            
            // Parse what
            try {
                int numRt = Integer.parseInt(parts[0]);
                numThg = Integer.parseInt(parts[1]);
                
                if (parts.length == 2) {
                    rts = new Routes();
                    rts.createRandomRoutes(numRt, DURATIONSSECS);
                } else {
                    // bbox provided
                    parts2 = parts[1].split(",");
                    double lllon = Double.parseDouble(parts2[0]);
                    double lllat = Double.parseDouble(parts2[1]);
                    double urlon = Double.parseDouble(parts2[2]);
                    double urlat = Double.parseDouble(parts2[3]);
                    
                    rts = new Routes(lllon, lllat, urlon, urlat);
                    rts.createRandomRoutes(numRt, DURATIONSSECS);                    
                    
                }
                
            } catch (NumberFormatException e) {
                // The what is not not a number assume file
                rts = new Routes();
                rts.load(parts[0]);
                
                if (parts.length == 1) {
                    // Default to 10
                    numThg = 10;
                } else {
                    numThg = Integer.parseInt(parts[1]);
                }
                
            }
            
            // Create Things
            Random rnd = new Random();
            int i = 0;
            while (i < numThg) {
                
                int rndoffset = Math.abs(rnd.nextInt());
                
                Thing t = new Thing(i, rts.get(i), rndoffset);
                things.add(t);
                i++;
                
            }
            
            
                        
            // Parse rate            
            Integer r = Integer.parseInt(rate);
            
//            System.out.println("HERE");
//            
//            System.out.println(rts.rts);
            
            timer = new Timer();
            timer.schedule(new CheckCount(), 0, r * 1000);

            
        } catch (Exception e) {
            e.printStackTrace();
            
        }
        
    }


    public static void main(String[] args) {

        int numArgs = args.length;

        String where = "-";
        String what = "10:20";
        String rate = "1";

        if (numArgs >= 1) {
            where = args[0];
        }

        if (numArgs >= 2) {
            what = args[1];
        }

        if (numArgs >= 3) {
            rate = args[2];
        }

        SendEvents t = new SendEvents();
        t.send(where, what, rate);
        
    }
}