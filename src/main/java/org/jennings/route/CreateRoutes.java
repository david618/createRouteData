/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jennings.route;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jennings.geomtools.DistanceBearing;
import org.jennings.geomtools.GeographicCoordinate;
import org.jennings.geomtools.GreatCircle;

/**
 *
 * @author david
 */
public class CreateRoutes {

    GreatCircle gc;
    static final Pattern PATTERN = Pattern.compile("(([^\"][^,]*)|\"([^\"]*)\"),?");

    public CreateRoutes() {
        gc = new GreatCircle();
    }

    public void createPathFile(String filename, Double lon1, Double lat1, Double lon2, Double lat2) {
        FileWriter fw = null;
        BufferedWriter bw = null;

        try {
            fw = new FileWriter(filename);
            bw = new BufferedWriter(fw);

            Long tm = 0L;

            // Random speed and intervale
            double speed = Math.random() * 100 + 200;  // speed from 200 to 300
            double interval = Math.random() + 9.5;  // interval from 9.5 to 10.5 seconds

            double deltaDist = speed * interval / 1000.0;   // speed given in m/s => dist in m  / 1000 == dist in km

            // Find distance from pt1 to pt2
            double lon = lon1;
            double lat = lat1;

            DistanceBearing distBear = gc.getDistanceBearing(lon1, lat1, lon2, lat2);
//            System.out.println(distBear.getDistance());
//            System.out.println(deltaDist);

            String line = tm.toString() + "," + lon1.toString() + "," + lat1.toString() + "," + String.valueOf(speed) + "," + String.valueOf(distBear.getBearing());
            bw.write(line);
            bw.newLine();

            while (deltaDist < distBear.getDistance()) {
                tm += Math.round(interval * 1000);  // convert seconds to milliseconds and round                

                GeographicCoordinate nc = gc.getNewCoordPair(lon, lat, deltaDist, distBear.getBearing());

                line = tm.toString() + "," + String.valueOf(nc.getLon()) + "," + String.valueOf(nc.getLat()) + "," + String.valueOf(speed) + "," + String.valueOf(distBear.getBearing());

                bw.write(line);
                bw.newLine();

                lon = nc.getLon();
                lat = nc.getLat();
                distBear = gc.getDistanceBearing(lon, lat, lon2, lat2);

                // Random speed and intervale
                speed = Math.random() * 100 + 200;  // speed from 200 to 300
                interval = Math.random() + 9.5;  // interval from 9.5 to 10.5 seconds

                deltaDist = speed * interval / 1000.0;   // speed given in m/s => dist in m  / 1000 == dist in km

            }

            tm += Math.round(interval * 1000.0);
            line = tm.toString() + "," + lon2.toString() + "," + lat2.toString() + "," + String.valueOf(speed) + "," + String.valueOf(distBear.getBearing());

            bw.write(line);
            bw.newLine();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
                fw.close();
            } catch (Exception e) {
                // ok to ignore
            }
        }
    }

    public void createPathFile(String filename, Double lon1, Double lat1, Double lon2, Double lat2, Double speed, int interval) {
        FileWriter fw = null;
        BufferedWriter bw = null;

        try {
            fw = new FileWriter(filename);
            bw = new BufferedWriter(fw);

            Long tm = 0L;

            String line = tm.toString() + "," + lon1.toString() + "," + lat1.toString();
            bw.write(line);
            bw.newLine();

            double deltaDist = speed * interval / 1000.0;   // speed given in m/s => dist in m  / 1000 == dist in km

            // Find distance from pt1 to pt2
            double lon = lon1;
            double lat = lat1;

            DistanceBearing distBear = gc.getDistanceBearing(lon1, lat1, lon2, lat2);
//            System.out.println(distBear.getDistance());
//            System.out.println(deltaDist);
            while (deltaDist < distBear.getDistance()) {
                tm += interval;

                GeographicCoordinate nc = gc.getNewCoordPair(lon, lat, deltaDist, distBear.getBearing());

                line = tm.toString() + "," + String.valueOf(nc.getLon()) + "," + String.valueOf(nc.getLat());

                bw.write(line);
                bw.newLine();

                lon = nc.getLon();
                lat = nc.getLat();
                distBear = gc.getDistanceBearing(lon, lat, lon2, lat2);

            }

            tm += interval;
            line = tm.toString() + "," + lon2.toString() + "," + lat2.toString();

            bw.write(line);
            bw.newLine();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
                fw.close();
            } catch (Exception e) {
                // ok to ignore
            }
        }
    }

    public void routes(String code) {
        FileReader fr = null;
        BufferedReader br = null;
        try {
            // Load in Aiports
            fr = new FileReader("airports2.dat");
            br = new BufferedReader(fr);

            HashMap<Integer, Airport> airports = new HashMap<>();

            String line = null;
            while ((line = br.readLine()) != null) {

                line = line.replace("\\\"", "'");  // Replace \" with single quote
                Matcher matcher = RegexPatterns.COMMADELIMQUOTEDSTRINGS.matcher(line);

                ArrayList<String> vals = new ArrayList<>();

                int i = 0;
                while (matcher.find()) {
                    if (matcher.group(2) != null) {
                        String val = matcher.group(2);
                        if (val.equalsIgnoreCase("\\N")) {
                            vals.add(i, null);
                        } else {
                            // This is a number
                            vals.add(i, matcher.group(2));
                        }
                    } else if (matcher.group(1) != null) {
                        // This is a String
                        vals.add(i, matcher.group(1));
                    }
                    i += 1;

                }

                String airportID = vals.get(0);
                String name = vals.get(1);
                String city = vals.get(2);
                String country = vals.get(3);
                String iataOrFaa = vals.get(4);
                String icao = vals.get(5);
                String lat = vals.get(6);
                String lon = vals.get(7);
                String alt = vals.get(8);
                String timezone = vals.get(9);
                String dst = vals.get(10);
                String tz = vals.get(11);

                Airport arpt = new Airport(Integer.parseInt(airportID), name, Double.parseDouble(lat), Double.parseDouble(lon));

                airports.put(Integer.parseInt(airportID), arpt);

            }

            fr = new FileReader("routes.dat");
            br = new BufferedReader(fr);

            int i = 0;
            while ((line = br.readLine()) != null) {

                String fields[] = line.split(",", -1);
                String airline = fields[0];
                String airlineID = fields[1];
                String sourceAirport = fields[2];
                String sourceAirportID = fields[3];
                String destinationAirport = fields[4];
                String destinationAirportID = fields[5];
                String codeShare = fields[6];
                String stops = fields[7];
                String equipment = fields[8];

                if (sourceAirport.equalsIgnoreCase(code) || code == null) {
                    // code == null does all airports
                    i++;

                    int sid = 0;
                    int did = 0;
                    try {
                        sid = Integer.parseInt(sourceAirportID);
                        did = Integer.parseInt(destinationAirportID);
                    } catch (Exception e) {
                        System.out.println("Skipping:" + line);
                    }

                    if (sid > 0 && did > 0) {
                        double lat1 = 0.0;
                        double lon1 = 0.0;
                        double lat2 = 0.0;
                        double lon2 = 0.0;
                        boolean ok = true;
                        if (airports.containsKey(sid)) {
                            lat1 = airports.get(sid).getLat();
                            lon1 = airports.get(sid).getLon();
                        } else {
                            ok = false;
                        }

                        if (airports.containsKey(did)) {
                            lat2 = airports.get(did).getLat();
                            lon2 = airports.get(did).getLon();
                        } else {
                            ok = false;
                        }

                        String filename = "routes" + System.getProperty("file.separator") + sourceAirport + "-" + destinationAirport + ".txt";
                        System.out.println(i + "," + filename + "," + lon1 + "," + lat1 + "," + lon2 + "," + lat2);

                        if (ok) {
                            //createPathFile(filename, lon1, lat1, lon2, lat2, 250.0, 2);
                            createPathFile(filename, lon1, lat1, lon2, lat2);  //random speed 200 to 300 and random interval 0.5 to 1.5 seconds.
                        } else {
                            System.out.println("Skipping2: " + line);
                        }
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                fr.close();
            } catch (Exception e) {
                // ok to ignore
            }
        }

    }

    public static void main(String[] args) {
        CreateRoutes t = new CreateRoutes();
        t.routes("ATL");
    }
}
