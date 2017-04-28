/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jennings.route;

import java.util.ArrayList;
import java.util.Random;
import org.jennings.geomtools.DistanceBearing;
import org.jennings.geomtools.GreatCircle;

/**
 *
 * @author david
 */
public class RouteBuilder {
    
    Airports arpts = new Airports();
    GreatCircle gc = new GreatCircle();

    public Route createRoute(long durationSec) {
        
        Route rt = null;
        
        try {
            

            ArrayList<Waypoint> wpts;

            int i = 0;

            wpts = new ArrayList<>();

            Random rnd = new Random();

            Airport firstArpt = arpts.getRndAirport("");

            Airport arpt1 = firstArpt;
            Airport arpt2;

            long t = 0;

            while (t < durationSec) {
                arpt2 = arpts.getRndAirport(arpt1.getName());

                DistanceBearing distB = gc.getDistanceBearing(arpt1.getLon(), arpt1.getLat(), arpt2.getLon(), arpt2.getLat());

                long st = t + rnd.nextInt(600) + 300;  // Delay from 300 to 900 seconds

                double speed = (Math.random() * 100 + 200) / 1000;  // speed from 100 to 300 m/s converted to km/s

                long et = st + Math.round(distB.getDistance() / speed);

                //System.out.println(st + "," + arpt1.getName() + "," + arpt1.getId() + "," + arpt1.getLon() + "," + arpt1.getLat() + "," + distB.getDistance() + "," + distB.getBearing() + "," + et);
                Waypoint wpt = new Waypoint(st, arpt1.getName(), arpt1.getId(), arpt1.getLon(), arpt1.getLat(), distB.getDistance(), distB.getBearing(), speed, et);
                wpts.add(wpt);

                arpt1 = arpt2;

                t = et;
                i++;
            }

            arpt2 = firstArpt;
            if (!arpt2.getName().equalsIgnoreCase(arpt1.getName())) {
                // Only add if the last airport not equal first
                DistanceBearing distB = gc.getDistanceBearing(arpt1.getLon(), arpt1.getLat(), arpt2.getLon(), arpt2.getLat());

                long st = t + rnd.nextInt(600) + 300;  // Delay from 300 to 900 seconds

                double speed = (Math.random() * 100 + 200) / 1000;  // speed from 100 to 300 m/s converted to km/s

                long et = st + Math.round(distB.getDistance() / speed);

                //System.out.println(st + "," + arpt1.getName() + "," + arpt1.getId() + "," + arpt1.getLon() + "," + arpt1.getLat() + "," + distB.getDistance() + "," + distB.getBearing() + "," + et);
                Waypoint wpt = new Waypoint(st, arpt1.getName(), arpt1.getId(), arpt1.getLon(), arpt1.getLat(), distB.getDistance(), distB.getBearing(), speed, et);
                wpts.add(wpt);

            }

            // Add route back to firstArpt
//            System.out.println(wpts.size());
//
//            for (Waypoint wpt : wpts) {
//                System.out.println(wpt.toString());
//            }
            
            rt = new Route(wpts);

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return rt;

    }
    
    
}
