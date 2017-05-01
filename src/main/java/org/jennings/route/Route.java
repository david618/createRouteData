/**
    // Series of Waypoints; it is a closed path; the last Waypoint give dist/bearing to the first 
    * Example:
    * 
816,Alice Springs Airport,3319,133.90199279785156,-23.806699752807617,13361.853782406464,168.8274126698336,61068
61648,La Plata Airport,2447,-57.8947,-34.9722,12787.247710042635,60.14191134805573,123564
124405,Adıyaman Airport,5800,38.4688987732,37.7313995361,2911.4261109444706,94.73005499828885,136367
136733,Zhob Airport,2233,69.4636001586914,31.358400344848633,5246.3508694127495,-72.72477455791804,155912
156589,Tripoli International Airport,1157,13.1590003967,32.6635017395,9852.188296138784,-2.4847151672753514,197301
198200,Cape Newenham LRRS Airport,3427,-162.06300354,58.646400451699996,10878.523714379604,-123.86942642142148,247956

    * From 0 to 816 seconds on ground at Alice Springs Airport  
    * From 816 to 61068 in route from Alice Springs Airport to La Plata Airport 
    * From 61068 to 61648 on ground at La Plata Airport
    * And so forth ...
    * From 198200 to 247956 in route from Cape Newenham LRRS Airport to Alice Springs Airport
    
    * System.currentTimeMS/1000 % 247956 = number from 0 to 247955
    
 * Creator: David Jennings
 */
package org.jennings.route;

import java.util.ArrayList;
import java.util.Random;
import org.jennings.geomtools.DistanceBearing;
import org.jennings.geomtools.GeographicCoordinate;
import org.jennings.geomtools.GreatCircle;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author david
 */
public class Route {

    ArrayList<Waypoint> wpts;
    long lastSec;
    int numWpts;
//    int offset;
    int id;

    public Route() {

    }

    public Route(JSONObject json) {
        id = json.getInt("id");
        numWpts = json.getInt("numWpts");
//        offset = json.getInt("offset");
        lastSec = json.getLong("lastSec");

        JSONArray jsonWpts = json.getJSONArray("wpts");

        int i = 0;

        wpts = new ArrayList<>();

        while (i < jsonWpts.length()) {

            JSONObject wpt = jsonWpts.getJSONObject(i);

            String name = wpt.getString("name");
            double lon = wpt.getDouble("lon");
            double lat = wpt.getDouble("lat");
            long st = wpt.getLong("st");
            double distance = wpt.getDouble("distance");
            double bearing = wpt.getDouble("bearing");
            double speed = wpt.getDouble("speed");
            long et = wpt.getLong("et");

            Waypoint wp = new Waypoint(st, name, id, lon, lat, distance, bearing, speed, et);
            wpts.add(wp);

            i++;
        }

    }

    public Route(ArrayList<Waypoint> wpts) {

        this.wpts = wpts;
        numWpts = wpts.size();
        lastSec = wpts.get(numWpts - 1).getEt();
        int maxOffset = 0;
        if (lastSec > Integer.MAX_VALUE) {
            maxOffset = Integer.MAX_VALUE;
        } else {
            maxOffset = (int) lastSec;
        }
        Random rnd = new Random();
//        offset = rnd.nextInt(maxOffset);
    }

    public ArrayList<Waypoint> getWpts() {
        return wpts;
    }

    public void setWpts(ArrayList<Waypoint> wpts) {
        this.wpts = wpts;
    }

    public long getLastSec() {
        return lastSec;
    }

    public void setLastSec(long lastSec) {
        this.lastSec = lastSec;
    }

    public int getNumWpts() {
        return numWpts;
    }

    public void setNumWpts(int numWpts) {
        this.numWpts = numWpts;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public JSONObject getRouteJSON() {
        JSONObject json = new JSONObject();

        json.put("id", this.id);
        json.put("numWpts", this.numWpts);
        json.put("lastSec", this.lastSec);
//        json.put("offset", this.offset);

        JSONArray waypts = new JSONArray();

        for (Waypoint wp : this.wpts) {
            JSONObject wpt = new JSONObject();
            wpt.put("name", wp.name);
            wpt.put("id", wp.id);
            wpt.put("lon", wp.lon);
            wpt.put("lat", wp.lat);
            wpt.put("st", wp.st);
            wpt.put("distance", wp.distance);
            wpt.put("bearing", wp.bearing);
            wpt.put("speed", wp.speed);
            wpt.put("et", wp.et);
            waypts.put(wpt);
        }

        json.put("wpts", waypts);

        return json;
    }

    @Override
    public String toString() {
        return "Route{" + "wpts=" + wpts + ", lastSec=" + lastSec + ", numWpts=" + numWpts  + ", id=" + id + '}';
    }

//    public Thing getPosition(Long timestamp) {
//
//        
//        long t = Math.round(System.currentTimeMillis() / 1000.0);
//        
//        if (timestamp != null) {
//            t = Math.round(timestamp / 1000.0);
//        }
//
//        long t2 = (t + this.offset) % this.lastSec;   // some time between 0 and lastSec
//
////        System.out.println(t2);
////        System.out.println(lastSec);
//        
//        int i = 0;
//
//        GreatCircle grc = new GreatCircle();
//        Waypoint wpt = wpts.get(0);
//        GeographicCoordinate gc = new GeographicCoordinate();
//        Thing thing = new Thing();
//        thing.setTimestamp(t);
//
//        while (i <= wpts.size()) {
//            if (i == wpts.size()) {
//                // Wrap to first
//                wpt = wpts.get(0);
//            } else {
//                wpt = wpts.get(i);
//            }
//
//            if (t2 < wpt.st) {
//                // on the ground at this point
//                gc.setLon(wpt.getLon());
//                gc.setLat(wpt.getLat());
//                thing.setGc(gc);
//                thing.setRouteId(id);
//                thing.setSpeed(0.0);
//                thing.setBearing(wpt.bearing);
//                long td = wpt.st - t2;
//                if (td < 0) td = 0;
//                
//                thing.setSecondsToDept(td);                
//                
//                String location = wpt.name + " -> ";                
//                
//                               
//                i++;
//                if (i == wpts.size()) {
//                    // Wrap to first
//                    wpt = wpts.get(0);
//                } else {
//                    wpt = wpts.get(i);
//                }
//                
//                location += wpt.name;
//                thing.setLocation(location);
//                thing.setDist(wpt.distance);      
//                
//
//                break;
//
//            } else if (t2 < wpt.et) {
//                // in route between this wpt and next
//                GeographicCoordinate pt1 = new GeographicCoordinate(wpt.lon, wpt.lat);
//                
//                double dist = wpt.speed * (t2 - wpt.st);
//                
//                DistanceBearing distB = new DistanceBearing(dist, wpt.bearing);
//                gc = grc.getNewCoordPair(pt1, distB);
//                thing.setGc(gc);
//                thing.setRouteId(id);
//                thing.setSpeed(wpt.speed);
//                thing.setBearing(wpt.bearing);
//                String location = wpt.name + " -> ";
//                double remainingDist = wpt.distance - dist;
//                if (remainingDist < 0) remainingDist = 0.0;
//                thing.setDist(remainingDist);
//                thing.setSecondsToDept(-1);
//                
//                i++;
//                if (i == wpts.size()) {
//                    // Wrap to first
//                    wpt = wpts.get(0);
//                } else {
//                    wpt = wpts.get(i);
//                }
//                
//                location += wpt.name;
//                thing.setLocation(location);
//                
//                
//                
//                break;
//                
//                
//            }
//            i++;
//
//        }
//        
//        return thing;
//    }

    public static void main(String[] args) throws Exception {

        Route rt = new Route();
        rt.setId(0);

    }

}
