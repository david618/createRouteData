/**
 * 
 */
package org.jennings.route;

/**
 *
 * @author david
 */
public class Waypoint {
    
    /*
    
    Example: 
    st: 321 (Seconds)
    name: 
    id: 
    lon: 33.4  (wgs84)
    lat: 22.8  (wgs84) 
    dist: 327 (km)
    bearing: 128.3 (deg)
    speed: 221 (km/s)
    et: 1234 (Seconds)
    
    */
    long st; // Time to start based 
    String name;
    int id;
    double lon;
    double lat;
    double distance; // distance to next point  
    double bearing;  // bearing to next point
    double speed;  // constanct speed used when travelling between this point and next    
    long et; // Arrival time at next point  

    public Waypoint(long st, String name, int id, double lon, double lat, double distance, double bearing, double speed, long et) {
        this.st = st;
        this.name = name;
        this.id = id;
        this.lon = lon;
        this.lat = lat;
        this.distance = distance;
        this.bearing = bearing;
        this.speed = speed;
        this.et = et;
    }

    public long getSt() {
        return st;
    }

    public void setSt(long st) {
        this.st = st;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public long getEt() {
        return et;
    }

    public void setEt(long et) {
        this.et = et;
    }

    @Override
    public String toString() {
        return "Waypoint{" + "st=" + st + ", name=" + name + ", id=" + id + ", lon=" + lon + ", lat=" + lat + ", distance=" + distance + ", bearing=" + bearing + ", speed=" + speed + ", et=" + et + '}';
    }
    
    
    
    
    
}
