/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jennings.route;

import org.jennings.geomtools.GeographicCoordinate;

/**
 *
 * @author david
 */
public class Thing {
    
    GeographicCoordinate gc;
    int id;
    int routeId;
    double speed;
    double bearing; 
    double dist;
    String location; 
    long timestamp; 
    long secondsToDept;

    public long getSecondsToDept() {
        return secondsToDept;
    }

    public void setSecondsToDept(long secondsToDept) {
        this.secondsToDept = secondsToDept;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }
    

    public GeographicCoordinate getGc() {
        return gc;
    }

    public void setGc(GeographicCoordinate gc) {
        this.gc = gc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Thing{" + "gc=" + gc + ", id=" + id + ", routeId=" + routeId + ", speed=" + speed + ", bearing=" + bearing + ", dist=" + dist + ", location=" + location + ", timestamp=" + timestamp + ", secondsToDept=" + secondsToDept + '}';
    }


    
    
}
