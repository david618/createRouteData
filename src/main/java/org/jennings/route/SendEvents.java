/**
 *
 * Create a set of Routes
 * Create a set of Things on the Routes
 * Send events for the things at a specified rate
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
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author david
 */
public class SendEvents {

    static long numEventsSent;
    static long numIterations;

    final private int STDOUT = 0;
    final private int TCP = 1;
    final private int HTTP = 2;

    final private int TXT = 0;
    final private int JSON = 1;

    final private int DURATIONSSECS = 86400;
    final private int HTTPBATCH = 1000;
    final private int SHOWCNTEVERY = 1000;

    Timer timer;
    int output;
    int fmt;
    int iterations;
    
//    Routes rts;
    ArrayList<Thing> things = new ArrayList<>();

    private OutputStream os = null;

    private final String USER_AGENT = "Mozilla/5.0";

    private HttpClient httpClient;
    private HttpPost httpPost;

    class CheckCount extends TimerTask {

        @Override
        public void run() {

            numIterations += 1;
            
            String postData = ""; // Combine lines and send in groups
            JSONArray jsonArray = new JSONArray();
            JSONObject js = new JSONObject();

            for (Thing t : things) {

                numEventsSent += 1;
                t.setPosition(System.currentTimeMillis());

                String d = ",";

                String line = "";

                switch (fmt) {
                    case TXT:
                        line = t.id + d + t.timestamp + d + t.speed * 1000.0 + d
                                + t.dist + d + t.bearing + d + t.rt.id + d
                                + "\"" + t.location + "\"" + d + t.secsToDep + d
                                + t.gc.getLon() + d + t.gc.getLat();
                        break;
                    case JSON:
                        js = new JSONObject();
                        js.put("id", t.id);
                        js.put("timestamp", t.timestamp);
                        js.put("speed", t.speed * 1000.0);
                        js.put("dist", t.dist);
                        js.put("bearing", t.bearing);
                        js.put("routeid", t.rt.id);
                        js.put("location", t.location);
                        js.put("secsToDep", t.secsToDep);
                        js.put("lon", t.gc.getLon());
                        js.put("lat", t.gc.getLat());
                        line = js.toString();

                }

                switch (output) {
                    case STDOUT:
                        System.out.println(line);
                        break;
                    case TCP:
                        line += "\n";
                        try {
                            os.write(line.getBytes());
                            os.flush();
                            if (numEventsSent % SHOWCNTEVERY == 0) {
                                System.out.println("Total Events Sent: " + numEventsSent);
                            }
                        } catch (Exception e) {
                            //System.out.println("Failed to write to socket");
                        }
                        break;
                    case HTTP:
                        if (fmt == TXT) {
                            postData += line + "\n";
                        } else if (fmt == JSON) {
                            jsonArray.put(js);
                        }

                        if (numEventsSent % HTTPBATCH == 0) {
                            try {
                                if (fmt == TXT) {
                                    postLine(postData);
                                } else if (fmt == JSON) {
                                    postLine(jsonArray.toString());
                                }

                                postData = "";
                                jsonArray = new JSONArray();
                                js = new JSONObject();
                            } catch (Exception e) {
                                System.out.println("Post Failed");
                            }

                        }
                        break;
                    default:
                        System.out.println("Invalid Output");
                }
                
                if (numIterations >= iterations) {
                    timer.cancel();
                }

            }

        }

    }

    private void postLine(String line) throws Exception {

        StringEntity postingString = new StringEntity(line);

        httpPost.setEntity(postingString);

        if (fmt == TXT) {
            httpPost.setHeader("Content-type", "plain/text");
        } else if (fmt == JSON) {
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse resp = httpClient.execute(httpPost);

        }

        httpPost.releaseConnection();
    }

    /*
     where: (default -) 
        - http://urlendpoint (http) 
        - server:port (tcp) - Use '-' to send to standard output 
     what: (default 10:10) 
        - numRoutes:numThings -> Create numRoutes and then create numThings using these routes 
        - numRoutes:numThings:lllon,lllat,urlon,urlat -> Same as before only limited routes to the bounding box
        - filename:numThings -> Load routes from filename and create numThings using these routes
     how: (default 10:txt) 
        - Number of seconds between sending updates
        - Format of things (txt or json)
        - Optional number of iterations (default sends until manually interupted)
     */
    /**
     * Timer contents
     *
     * Compile event(s) and send
     *
     */
    private void send(String where, String what, String how) {

        String parts[];
        String parts2[];

        try {

            parts = how.trim().split(":");
            int rate = Integer.parseInt(parts[0]);
            if (rate < 1) {
                rate = 1;
            }

            fmt = TXT;

            if (parts.length > 1) {
                String fmtRequested = parts[1];
                if (fmtRequested.equalsIgnoreCase("JSON")) {
                    fmt = JSON;
                } else {
                    fmt = TXT;
                }
            }
            
            iterations = -1;
            if (parts.length > 2) {
                Integer iterationsReq = Integer.parseInt(parts[2]);
                if (iterationsReq > 0) {
                    iterations = iterationsReq;
                }
                
            }
            

            numEventsSent = 0;

            Routes rts;
            // Parse where            
            parts = where.trim().split(":");

            if (parts[0].equalsIgnoreCase("-")) {
                output = STDOUT;
            } else if (parts[0].equalsIgnoreCase("http")) {
                output = HTTP;
                httpClient = HttpClientBuilder.create().build();

                httpPost = new HttpPost(where);

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

//            System.out.println("HERE");
//            
//            System.out.println(rts.rts);
            timer = new Timer();
            timer.schedule(new CheckCount(), 0, rate * 1000);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public static void main(String[] args) {

        int numArgs = args.length;

        String where = "-";
        //String what = "routesTwoDay1000.json:20";
        String what = "10:20";
        String how = "10:txt";

//        SendEvents t = new SendEvents();
//        t.send(where, what, how);

        if (numArgs == 0) {
            System.err.println("Usage: SendEvents <where> (<what> <how>)");
            System.err.println();
            System.err.println("where: Dash '-' for stdout (default) |  server:port for tcp | url for http");
            System.err.println();
            System.err.println("what: numRandomRoute:numRandomThings:<bounding box> or filename:numberRandomThings");
            System.err.println("     bounding box format: lowerleftlon,lowerleftlat,upperrightlon,upperrightlat");
            System.err.println("     default: 10:20");
            System.err.println();
            System.err.println("how: seconds:format | seconds:format:num");
            System.err.println("     valid formats: json:txt");
            System.err.println("     num: if specified is number of iterations; otherwise output continues until manually interupted (Ctrl-C)");
            System.err.println("     default: 10:txt (output sample every 10 seconds in text format)");

        } else {

            if (numArgs >= 1) {
                where = args[0];
            }

            if (numArgs >= 2) {
                what = args[1];
            }

            if (numArgs >= 3) {
                how = args[2];
            }

            SendEvents t = new SendEvents();
            t.send(where, what, how);
        }
    }
}
