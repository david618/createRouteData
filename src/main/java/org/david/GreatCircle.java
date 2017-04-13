/*
 * GreatCircle.java
 *
 * Created on January 26, 2007, 12:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.david;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Category;

/**
 *
 * @author jenningd
 */
public class GreatCircle {

    static final Category log = Category.getInstance(GreatCircle.class);

    private final double D2R = Math.PI / 180.0;
    private final double R2D = 180.0 / Math.PI;
    private final double RadiusEarth = 6371000;  //meters

    private final static DecimalFormat df5 = new DecimalFormat("###0.00000");
    private final static DecimalFormat df3 = new DecimalFormat("###0.000");

    static final Pattern PATTERN = Pattern.compile("(([^\"][^,]*)|\"([^\"]*)\"),?");

    /**
     * Creates a new instance of GreatCircle
     */
    public GreatCircle() {

    }

    /**
     *
     * Turns the stack trace into a string.
     *
     * @param t An Exeception Type
     * @return String
     */
    private String getStackTrace(Throwable t) {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    public DistanceBearing getDistanceBearing(double lon1, double lat1, double lon2, double lat2) {

        DistanceBearing distB = new DistanceBearing();

        CoordinateChecker cc = new CoordinateChecker();

        String strErrorMsg = "OK";

        /*
         * Returns the distance from point 1 to point 2 in km 
         *
         */
        double gcDist = 0.0;
        double bearing = 0.0;

        try {

            int intErrorNum = 0;
            if ((intErrorNum = cc.isValidLat(lat1)) < 0) {
                // invalid Lat1                 
                strErrorMsg = cc.getErrorMessage(intErrorNum);
            } else if ((intErrorNum = cc.isValidLat(lat2)) < 0) {
                // invalid lat2                 
                strErrorMsg = cc.getErrorMessage(intErrorNum);
            } else if ((intErrorNum = cc.isValidLon(lon1)) < 0) {
                // invalid lon1
                strErrorMsg = cc.getErrorMessage(intErrorNum);
            } else if ((intErrorNum = cc.isValidLon(lon2)) < 0) {
                // invalid lon2
                strErrorMsg = cc.getErrorMessage(intErrorNum);
            } else {

                // Allow for lon values 180 to 360 (adjust them to -180 to 0)
                if (lon1 > 180.0 && lon1 <= 360) {
                    lon1 = lon1 - 360;
                }
                if (lon2 > 180.0 && lon2 <= 360) {
                    lon2 = lon2 - 360;
                }

                double lon1R = lon1 * D2R;
                double lat1R = lat1 * D2R;
                double lon2R = lon2 * D2R;
                double lat2R = lat2 * D2R;

                if (lat1 == 90 || lat1 == -90) {
                    double l = 90 - lat2;
                    gcDist = RadiusEarth * l * D2R / 1000.0;
                    if (lat1 == -90) {
                        gcDist = Math.PI * RadiusEarth / 1000 - gcDist;
                    }

                } else {

                    boolean useLawCosines = true;

                    if (useLawCosines) {
                        double lambda = Math.abs(lon2R - lon1R);

                        double x1 = Math.cos(lat2R) * Math.sin(lambda);

                        double x2 = Math.cos(lat1R) * Math.sin(lat2R)
                                - Math.sin(lat1R) * Math.cos(lat2R) * Math.cos(lambda);

                        double x3 = Math.sin(lat1R) * Math.sin(lat2R)
                                + Math.cos(lat1R) * Math.cos(lat2R) * Math.cos(lambda);

                        double x4 = Math.sqrt(x1 * x1 + x2 * x2);

                        double sigma = Math.atan2(x4, x3);

                        gcDist = sigma * RadiusEarth / 1000.0;

                        double y1 = Math.sin(lon2R - lon1R) * Math.cos(lat2R);

                        double y2 = Math.cos(lat1R) * Math.sin(lat2R)
                                - Math.sin(lat1R) * Math.cos(lat2R) * Math.cos(lon2R - lon1R);

                        double y3 = Math.atan2(y1, y2);

                        bearing = (y3 * R2D) % 360;

                    } else {
                        // Haversine formula 
                        double dLat = lat2R - lat1R;
                        double dLon = lon2R - lon1R;
                        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                                + Math.cos(lat1R) * Math.cos(lat2R)
                                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

                        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

                        gcDist = RadiusEarth / 1000.0 * c;

                        double y = Math.sin(dLon) * Math.cos(lat2R);
                        double x = Math.cos(lat1R) * Math.sin(lat2R) - Math.sin(lat1R) * Math.cos(lat2R) * Math.cos(dLon);

                        bearing = (Math.atan2(y, x) * R2D) % 360;

                    }

                }
            }
        } catch (Exception e) {
            gcDist = -1000;
            bearing = -1000;
            strErrorMsg = "Unexpected Java Error.  Contact the provider if this error persists.";
        }

        distB.setDistance(new Double(df3.format(gcDist)).doubleValue());

        distB.setBearing1to2(new Double(df5.format(bearing)).doubleValue());
        // return angle on Great Circle is just 180 degrees in the other direction
        distB.setBearing2to1(new Double(df5.format((bearing + 180) % 360)).doubleValue());

        if (gcDist < -1000) {
            strErrorMsg = cc.getErrorMessage(gcDist);
        }

        distB.setMessage(strErrorMsg);

        return distB;
    }

    /*
     * Finds a new coordinate pair given a current point and the bearing from due north.
     * 
     *  Program assumes user inputs a valid CoordPair (-90 < lat < 90 and -180 < lon < 180)
     *  a valid bearing (-180 < bearning < 180) and reasonable distance in km (<50,000 km).
     *
     *  14 May 2007:  This code needs some work.  I'm getting wrong answers in some cases
     *
     */
    public NewCoordinate getNewCoordPair(double lon, double lat, double distance, double bearing) {

        NewCoordinate nc = new NewCoordinate();
        CoordinateChecker cc = new CoordinateChecker();
        String strErrorMsg = "OK";

        double lat1 = lat;
        double lon1 = lon;
        double lat2 = 0.0;
        double lon2 = 0.0;
        int intErrorNum = 0;

        boolean bln360 = false;

        try {

            if (distance * 1000 > RadiusEarth * Math.PI) {
                // Greater than half way around the world politely say no
                strErrorMsg = "Distance specified " + distance + " must be less than half the the circumference of the Earth (" + RadiusEarth * Math.PI + ")";
            } else if (bearing < -180 || bearing > 180) {
                strErrorMsg = "Bearing must be in the range of -180 to 180";

            } else if ((intErrorNum = cc.isValidLat(lat1)) < 0) {
                // invalid Lat1                 
                strErrorMsg = cc.getErrorMessage(intErrorNum);
            } else if ((intErrorNum = cc.isValidLon(lon1)) < 0) {
                // invalid lon1
                strErrorMsg = cc.getErrorMessage(intErrorNum);
            } else {

                // Allow for lon values 180 to 360 (adjust them to -180 to 0)
                double lonDD = lon1;
                if (lonDD > 180.0 && lonDD <= 360) {
                    lonDD = lonDD - 360;
                    lon1 = lonDD;
                    bln360 = true;
                }

                double alpha;
                double l;
                double k;
                double gamma;
                double phi;
                double theta;
                double hdng2;

                double hdng = bearing;

                if (hdng < 0) {
                    hdng = hdng + 360;
                }

                // Round the input            
                BigDecimal bd = new BigDecimal(hdng);
                bd = bd.setScale(6, BigDecimal.ROUND_HALF_UP);
                hdng = bd.doubleValue();

                double dist = distance * 1000;

                if (lat1 == 90 || lat1 == -90) {
                    // hdng doesn't make a lot of since at the poles assume this is just the lon
                    lon2 = hdng;
                    alpha = dist / RadiusEarth;
                    if (lat1 == 90) {
                        lat2 = 90 - alpha * R2D;
                    } else {
                        lat2 = -90 + alpha * R2D;
                    }

                } else if (hdng == 0 || hdng == 360) {
                    // going due north within some rounded number
                    alpha = dist / RadiusEarth;
                    lat2 = lat1 + alpha * R2D;
                    lon2 = lon1;
                } else if (hdng == 180) {
                    // going due south witin some rounded number
                    alpha = dist / RadiusEarth;
                    lat2 = lat1 - alpha * R2D;
                    lon2 = lon1;
                } else if (hdng == 90) {
                    lat2 = lat1;
                    l = 90 - lat1;
                    alpha = dist / RadiusEarth / Math.sin(l * D2R);
                    //phi = Math.asin(Math.sin(alpha)/ Math.sin(l*D2R));                 
                    lon2 = lon1 + alpha * R2D;
                } else if (hdng == 270) {
                    lat2 = lat1;
                    l = 90 - lat1;
                    alpha = dist / RadiusEarth / Math.sin(l * D2R);
                    //phi = Math.asin(Math.sin(alpha)/ Math.sin(l*D2R));                       
                    lon2 = lon1 - alpha * R2D;
                } else if (hdng > 0 && hdng < 180) {
                    l = 90 - lat1;
                    alpha = dist / RadiusEarth;
                    k = Math.acos(Math.cos(alpha) * Math.cos(l * D2R)
                            + Math.sin(alpha) * Math.sin(l * D2R) * Math.cos(hdng * D2R));
                    lat2 = 90 - k * R2D;
                    //phi = Math.asin(Math.sin(hdng*D2R) * Math.sin(alpha)/ Math.sin(k)); 
                    phi = Math.acos((Math.cos(alpha) - Math.cos(k) * Math.cos(l * D2R))
                            / (Math.sin(k) * Math.sin(l * D2R)));
                    lon2 = lon1 + phi * R2D;
                    theta = Math.sin(phi) * Math.sin(l * D2R) / Math.sin(alpha);
                    hdng2 = 180 - theta * R2D;
                } else if (hdng > 180 && hdng < 360) {
                    gamma = 360 - hdng;
                    l = 90 - lat1;
                    alpha = dist / RadiusEarth;
                    k = Math.acos(Math.cos(alpha) * Math.cos(l * D2R)
                            + Math.sin(alpha) * Math.sin(l * D2R) * Math.cos(gamma * D2R));
                    lat2 = 90 - k * R2D;
                    //phi = Math.asin(Math.sin(gamma*D2R) * Math.sin(alpha)/ Math.sin(k));                       
                    phi = Math.acos((Math.cos(alpha) - Math.cos(k) * Math.cos(l * D2R))
                            / (Math.sin(k) * Math.sin(l * D2R)));
                    lon2 = lon1 - phi * R2D;
                    theta = Math.sin(phi) * Math.sin(l * D2R) / Math.sin(alpha);
                    hdng2 = 180 - theta * R2D;
                }

                int decimalPlaces = 6;
                bd = new BigDecimal(lat2);
                bd = bd.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);
                lat2 = bd.doubleValue();

                bd = new BigDecimal(lon2);
                bd = bd.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);
                lon2 = bd.doubleValue();

                if (lat2 > 90) {
                    lat2 = 180 - lat2;
                    lon2 = (lon2 + 180) % 360;
                }

                if (lon2 > 180) {
                    lon2 = lon2 - 360;
                }

                if (lat2 < -90) {
                    lat2 = 180 - lat2;
                    lon2 = (lon2 + 180) % 360;
                }
                if (lon2 < -180) {
                    lon2 = lon2 + 360;
                }

                // adjust the lon back to 360 scale if input was like that
                if (bln360) {
                    if (lon2 < 0) {
                        lon2 = lon2 + 360;
                    }
                }

            }

        } catch (Exception e) {
            lon2 = -1000;
            lat2 = -1000;
            strErrorMsg = "Unexpected Java Error.  Contact the provider if this error persists.";
        }

        nc.setLat(new Double(df5.format(lat2)).doubleValue());
        nc.setLon(new Double(df5.format(lon2)).doubleValue());
        nc.setMessage(strErrorMsg);

        return nc;
    }

    private void runRandomWorldTest() {
        // Begin Test random world
        // Test the code with random coordinates around the world
        Random rnd = new Random();

        double lat1;
        double lon1;
        double lat2;
        double lon2;
        String strLine;

        try {
            FileOutputStream fos = new FileOutputStream("C:\\temp\\GreatCircleRunRandomWorldTest.txt");

            OutputStreamWriter osw = new OutputStreamWriter(fos);

            int i = 0;

            while (i < 30000) {
                lat1 = (1000.0 * rnd.nextGaussian()) % 90;
                lon1 = (1000.0 * rnd.nextGaussian()) % 180;
                lat2 = (1000.0 * rnd.nextGaussian()) % 90;
                lon2 = (1000.0 * rnd.nextGaussian()) % 180;

                DistanceBearing distB = getDistanceBearing(lon1, lat1, lon2, lat2);
                double dist = distB.getDistance();
                double head = distB.getBearing1to2();

                NewCoordinate nc = getNewCoordPair(lon1, lat1, dist, head);
                double newLon = nc.getLon();
                double newLat = nc.getLat();

                strLine = String.valueOf(lon1) + ":";
                strLine += String.valueOf(lat1) + ":";
                strLine += String.valueOf(lon2) + ":";
                strLine += String.valueOf(lat2) + ":";
                strLine += String.valueOf(dist) + ":";
                strLine += String.valueOf(head) + ":";
                strLine += String.valueOf(newLon) + ":";
                strLine += String.valueOf(newLat);

                osw.write(strLine + "\n");

                i++;
            }

            osw.close();
            fos.close();

        } catch (Exception e) {
            log.error(getStackTrace(e));
        }
        // End Test random world

    }

    private void runRandomDistBearTest() {
        // Begin Test Random Distances and Bearings
        // Test the code with random coordinates around the world
        Random rnd = new Random();

        double lat1;
        double lon1;
        double lat2;
        double lon2;
        String strLine;

        try {
            FileOutputStream fos = new FileOutputStream("C:\\temp\\GreatCircleRunRandomDistBearTest.txt");

            OutputStreamWriter osw = new OutputStreamWriter(fos);

            int i = 0;

            while (i < 30000) {
                lat1 = (1000.0 * rnd.nextGaussian()) % 90;
                lon1 = (1000.0 * rnd.nextGaussian()) % 180;

                double dist = Math.abs((40000.0 * rnd.nextGaussian()) % 20000.0);
                double head = (1000.0 * rnd.nextGaussian()) % 180.0;

                NewCoordinate nc = getNewCoordPair(lon1, lat1, dist, head);

                lat2 = nc.getLat();
                lon2 = nc.getLon();

                DistanceBearing distB = getDistanceBearing(lon1, lat1, lon2, lat2);
                double dist2 = distB.getDistance();
                double head2 = distB.getBearing1to2();

                strLine = String.valueOf(lon1) + ":";
                strLine += String.valueOf(lat1) + ":";
                strLine += String.valueOf(dist) + ":";
                strLine += String.valueOf(head) + ":";
                strLine += String.valueOf(lon2) + ":";
                strLine += String.valueOf(lat2) + ":";
                strLine += String.valueOf(dist2) + ":";
                strLine += String.valueOf(head2);

                osw.write(strLine + "\n");

                i++;
            }

            osw.close();
            fos.close();

        } catch (Exception e) {
            log.error(getStackTrace(e));
        }
        // End Test random distances and bearings        
    }

    private void runRandomTroubleSpotTest() {
        // Test the code with random coordinates around latitude 90, -90, and lon 180
        // Begin Test trouble spots
        Random rnd = new Random();

        double lat1;
        double lon1;
        double lat2;
        double lon2;
        String strLine;

        try {
            FileOutputStream fos = new FileOutputStream("C:\\temp\\GreatCircleRunRandomTroubleSpotTest.txt");

            OutputStreamWriter osw = new OutputStreamWriter(fos);

            double dist = 0.0;
            double head = 0.0;

            // 1000 points staring near 90N
            int i = 0;
            while (i < 1000) {
                lat1 = 89.0 + rnd.nextInt(1000) / 1000.0;
                lon1 = (1000.0 * rnd.nextGaussian()) % 180;
                lat2 = (1000.0 * rnd.nextGaussian()) % 90;
                lon2 = (1000.0 * rnd.nextGaussian()) % 180;

                DistanceBearing distB = getDistanceBearing(lon1, lat1, lon2, lat2);
                dist = distB.getDistance();
                head = distB.getBearing1to2();

                NewCoordinate nc = getNewCoordPair(lon1, lat1, dist, head);

                double newLon = nc.getLon();
                double newLat = nc.getLat();

                strLine = String.valueOf(lon1) + ":";
                strLine += String.valueOf(lat1) + ":";
                strLine += String.valueOf(lon2) + ":";
                strLine += String.valueOf(lat2) + ":";
                strLine += String.valueOf(dist) + ":";
                strLine += String.valueOf(head) + ":";
                strLine += String.valueOf(newLon) + ":";
                strLine += String.valueOf(newLat);

                osw.write(strLine + "\n");

                i++;
            }
            // 1000 points staring near 90S
            i = 0;
            while (i < 1000) {
                lat1 = -89.0 - rnd.nextInt(1000) / 1000.0;
                lon1 = (1000.0 * rnd.nextGaussian()) % 180;
                lat2 = (1000.0 * rnd.nextGaussian()) % 90;
                lon2 = (1000.0 * rnd.nextGaussian()) % 180;

                DistanceBearing distB = getDistanceBearing(lon1, lat1, lon2, lat2);
                dist = distB.getDistance();
                head = distB.getBearing1to2();

                NewCoordinate nc = getNewCoordPair(lon1, lat1, dist, head);
                double newLon = nc.getLon();
                double newLat = nc.getLat();

                strLine = String.valueOf(lon1) + ":";
                strLine += String.valueOf(lat1) + ":";
                strLine += String.valueOf(lon2) + ":";
                strLine += String.valueOf(lat2) + ":";
                strLine += String.valueOf(dist) + ":";
                strLine += String.valueOf(head) + ":";
                strLine += String.valueOf(newLon) + ":";
                strLine += String.valueOf(newLat);

                osw.write(strLine + "\n");

                i++;
            }

            // 1000 points ending near 90N
            i = 0;
            while (i < 1000) {
                lat1 = (1000.0 * rnd.nextGaussian()) % 90;
                lon1 = (1000.0 * rnd.nextGaussian()) % 180;
                lat2 = 89.0 + rnd.nextInt(1000) / 1000.0;
                lon2 = (1000.0 * rnd.nextGaussian()) % 180;

                DistanceBearing distB = getDistanceBearing(lon1, lat1, lon2, lat2);
                dist = distB.getDistance();
                head = distB.getBearing1to2();

                NewCoordinate nc = getNewCoordPair(lon1, lat1, dist, head);
                double newLon = nc.getLon();
                double newLat = nc.getLat();

                strLine = String.valueOf(lon1) + ":";
                strLine += String.valueOf(lat1) + ":";
                strLine += String.valueOf(lon2) + ":";
                strLine += String.valueOf(lat2) + ":";
                strLine += String.valueOf(dist) + ":";
                strLine += String.valueOf(head) + ":";
                strLine += String.valueOf(newLon) + ":";
                strLine += String.valueOf(newLat);

                osw.write(strLine + "\n");

                i++;
            }

            // 1000 points ending near 90S
            i = 0;
            while (i < 1000) {
                lat1 = (1000.0 * rnd.nextGaussian()) % 90;
                lon1 = (1000.0 * rnd.nextGaussian()) % 180;
                lat2 = -89.0 - rnd.nextInt(1000) / 1000.0;
                lon2 = (1000.0 * rnd.nextGaussian()) % 180;

                DistanceBearing distB = getDistanceBearing(lon1, lat1, lon2, lat2);
                dist = distB.getDistance();
                head = distB.getBearing1to2();

                NewCoordinate nc = getNewCoordPair(lon1, lat1, dist, head);
                double newLon = nc.getLon();
                double newLat = nc.getLat();

                strLine = String.valueOf(lon1) + ":";
                strLine += String.valueOf(lat1) + ":";
                strLine += String.valueOf(lon2) + ":";
                strLine += String.valueOf(lat2) + ":";
                strLine += String.valueOf(dist) + ":";
                strLine += String.valueOf(head) + ":";
                strLine += String.valueOf(newLon) + ":";
                strLine += String.valueOf(newLat);

                osw.write(strLine + "\n");

                i++;
            }

            // 1000 points crossing near 180 crossing to near -180
            i = 0;
            while (i < 1000) {
                lat1 = (1000.0 * rnd.nextGaussian()) % 90;
                lon1 = 170.0 + 10 * rnd.nextInt(1000) / 1000.0;
                lat2 = (1000.0 * rnd.nextGaussian()) % 90;
                lon2 = -170.0 - 10 * rnd.nextInt(1000) / 1000.0;

                DistanceBearing distB = getDistanceBearing(lon1, lat1, lon2, lat2);
                dist = distB.getDistance();
                head = distB.getBearing1to2();

                NewCoordinate nc = getNewCoordPair(lon1, lat1, dist, head);
                double newLon = nc.getLon();
                double newLat = nc.getLat();

                strLine = String.valueOf(lon1) + ":";
                strLine += String.valueOf(lat1) + ":";
                strLine += String.valueOf(lon2) + ":";
                strLine += String.valueOf(lat2) + ":";
                strLine += String.valueOf(dist) + ":";
                strLine += String.valueOf(head) + ":";
                strLine += String.valueOf(newLon) + ":";
                strLine += String.valueOf(newLat);

                osw.write(strLine + "\n");

                i++;
            }
            // 1000 points crossing near 180 crossing to near -180
            i = 0;
            while (i < 1000) {
                lat1 = (1000.0 * rnd.nextGaussian()) % 90;
                lon1 = -170.0 - 10 * rnd.nextInt(1000) / 1000.0;
                lat2 = (1000.0 * rnd.nextGaussian()) % 90;
                lon2 = 170.0 + 10 * rnd.nextInt(1000) / 1000.0;

                DistanceBearing distB = getDistanceBearing(lon1, lat1, lon2, lat2);
                dist = distB.getDistance();
                head = distB.getBearing1to2();

                NewCoordinate nc = getNewCoordPair(lon1, lat1, dist, head);
                double newLon = nc.getLon();
                double newLat = nc.getLat();

                strLine = String.valueOf(lon1) + ":";
                strLine += String.valueOf(lat1) + ":";
                strLine += String.valueOf(lon2) + ":";
                strLine += String.valueOf(lat2) + ":";
                strLine += String.valueOf(dist) + ":";
                strLine += String.valueOf(head) + ":";
                strLine += String.valueOf(newLon) + ":";
                strLine += String.valueOf(newLat);

                osw.write(strLine + "\n");

                i++;
            }

            osw.close();
            fos.close();

        } catch (Exception e) {
            log.error(getStackTrace(e));
        }
        // End of Test Trouble spots

    }

    private void runSinglePointTest() {
        // Test the code with one point

        double lon1 = 137.6389;
        double lat1 = -73.8183;
        double lon2 = -39.8347;
        double lat2 = -45.0278;

        System.out.println("Point 1 = (" + lon1 + "," + lat1 + ")");
        System.out.println("Point 2 = (" + lon2 + "," + lat2 + ")");

        // Find the distance and bearing
        DistanceBearing distB = getDistanceBearing(lon1, lat1, lon2, lat2);
        double dist = distB.getDistance();
        double head = distB.getBearing1to2();

        System.out.println("distance = " + dist);
        System.out.println("heading = " + head);

        //dist = 1000000;
        //head = 128343.234;
        // Given the first point and distance and bearing find 2nd point
        NewCoordinate nc = getNewCoordPair(lon1, lat1, dist, head);
        double newLon = nc.getLon();
        double newLat = nc.getLat();

        System.out.println("New Point = (" + newLon + "," + newLat + ")");

    }

    // Break points up by number of segments
    // Break points up by distance (e.g. point every 100m)
    // Break up points vy speed and time (e.g.  Point every 10 seconds given a specified speed)  Average speed of Commerical Jet is 900 km/hour or 250 meters/s
    public String getPoints(Double lon1, Double lat1, Double lon2, Double lat2, Double speed, int interval) {

        String pts = "";

        pts += lon1.toString() + "," + lat1.toString();

        try {
            double deltaDist = speed * interval / 1000.0;   // speed given in m/s => dist in m  / 1000 == dist in km

            // Find distance from pt1 to pt2
            double lon = lon1;
            double lat = lat1;

            long tm = 0;

            DistanceBearing distBear = getDistanceBearing(lon1, lat1, lon2, lat2);
            System.out.println(distBear.getDistance());
            System.out.println(deltaDist);
            while (deltaDist < distBear.getDistance()) {

                NewCoordinate nc = getNewCoordPair(lon, lat, deltaDist, distBear.getBearing1to2());

                pts += "," + String.valueOf(nc.getLon()) + "," + String.valueOf(nc.getLat());
                lon = nc.getLon();
                lat = nc.getLat();
                distBear = getDistanceBearing(lon, lat, lon2, lat2);
                tm += interval;

            }

            pts += "," + lon2.toString() + "," + lat2.toString();

            System.out.println(tm);

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return pts;
    }
    
    public void createPathFile(String filename, Double lon1, Double lat1, Double lon2, Double lat2) {
        FileWriter fw = null;
        BufferedWriter bw = null;

        try {
            fw = new FileWriter(filename);
            bw = new BufferedWriter(fw);

            Long tm = 0L;

            // Random speed and intervale
            double speed = Math.random()*100 + 200;  // speed from 200 to 300
            double interval = Math.random() + 9.5;  // interval from 9.5 to 10.5 seconds
            
            double deltaDist = speed * interval / 1000.0;   // speed given in m/s => dist in m  / 1000 == dist in km

            // Find distance from pt1 to pt2
            double lon = lon1;
            double lat = lat1;

            DistanceBearing distBear = getDistanceBearing(lon1, lat1, lon2, lat2);
//            System.out.println(distBear.getDistance());
//            System.out.println(deltaDist);

            String line = tm.toString() + "," + lon1.toString() + "," + lat1.toString() + "," + String.valueOf(speed) + "," + String.valueOf(distBear.getBearing1to2());
            bw.write(line);
            bw.newLine();

            while (deltaDist < distBear.getDistance()) {
                tm += Math.round(interval * 1000);  // convert seconds to milliseconds and round                
                
                NewCoordinate nc = getNewCoordPair(lon, lat, deltaDist, distBear.getBearing1to2());

                line = tm.toString() + "," + String.valueOf(nc.getLon()) + "," + String.valueOf(nc.getLat()) + "," + String.valueOf(speed) + "," + String.valueOf(distBear.getBearing1to2());

                bw.write(line);
                bw.newLine();

                lon = nc.getLon();
                lat = nc.getLat();
                distBear = getDistanceBearing(lon, lat, lon2, lat2);

                // Random speed and intervale
                speed = Math.random()*100 + 200;  // speed from 200 to 300
                interval = Math.random() + 9.5;  // interval from 9.5 to 10.5 seconds

                deltaDist = speed * interval / 1000.0;   // speed given in m/s => dist in m  / 1000 == dist in km
                
                
                
            }

            tm += Math.round(interval * 1000.0);
            line = tm.toString() + "," + lon2.toString() + "," + lat2.toString() + "," + String.valueOf(speed) + "," + String.valueOf(distBear.getBearing1to2());

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

            DistanceBearing distBear = getDistanceBearing(lon1, lat1, lon2, lat2);
//            System.out.println(distBear.getDistance());
//            System.out.println(deltaDist);
            while (deltaDist < distBear.getDistance()) {
                tm += interval;

                NewCoordinate nc = getNewCoordPair(lon, lat, deltaDist, distBear.getBearing1to2());

                line = tm.toString() + "," + String.valueOf(nc.getLon()) + "," + String.valueOf(nc.getLat());

                bw.write(line);
                bw.newLine();

                lon = nc.getLon();
                lat = nc.getLat();
                distBear = getDistanceBearing(lon, lat, lon2, lat2);

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
            fr = new FileReader("airports.dat");
            br = new BufferedReader(fr);

            HashMap<Integer, Airport> airports = new HashMap<>();

            String line = null;
            while ((line = br.readLine()) != null) {

                ArrayList<String> vals = new ArrayList<String>();
                Matcher matcher = PATTERN.matcher(line);
                int i = 0;
                while (matcher.find()) {
                    if (matcher.group(2) != null) {
                        //System.out.print(matcher.group(2) + "|");
                        vals.add(i, matcher.group(2));
                    } else if (matcher.group(3) != null) {
                        //System.out.print(matcher.group(3) + "|");
                        vals.add(i, matcher.group(3));
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

                Airport arpt = new Airport(name, Double.parseDouble(lat), Double.parseDouble(lon));

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
        GreatCircle gc = new GreatCircle();
        //String pts = gc.getPoints(-90.370028, 38.748697, -112.011583, 33.434278, 250.0, 60);

        //System.out.println(pts);
        gc.routes("STL");

//        String line = "7S,\\N,RSH,7098,ANI,5967,,0,";
//        
//        String fields[] = line.split(",",-1);
//        
//        System.out.println(fields[8]);
//        gc.runSinglePointTest();
//        gc.runRandomWorldTest();
//        gc.runRandomTroubleSpotTest();
//        gc.runRandomDistBearTest();
    }

}
