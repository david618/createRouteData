/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jennings.route;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.jennings.geomtools.GeographicCoordinate;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 *
 * @author david
 */
public class Routes {
    
    ArrayList<Route> rts = new ArrayList<>();
    
    
    public void loadRoutesFile(String filename) {
        try {
                        
            String text = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
            
            JSONArray jsonRts = new JSONArray(text);
            
            int i = 0;
            
            while (i < jsonRts.length()) {
                JSONObject jsonRt = jsonRts.getJSONObject(i);
                Route rt = new Route(jsonRt);
                rts.add(rt);
                
                i++;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public void createRandomRouteFile(String filename, int numRoutes, long durationSecs) {
        try {
            ArrayList<Route> rts = new ArrayList<>();

            RouteBuilder rb = new RouteBuilder();

            JSONArray jsonRts = new JSONArray();

            for (int i = 1; i <= numRoutes; i++) {
                Route rt = rb.createRoute(durationSecs * 2);
                rt.setId(i);
                rts.add(rt);
                jsonRts.put(rt.getRouteJSON());
            }

            FileWriter fw = new FileWriter(filename);

            jsonRts.write(fw);

            fw.close();
            //System.out.println(jsonRts.toString(2));
        } catch (Exception e) {

        }
    }

    @Override
    public String toString() {
        return "Routes{" + "rts=" + rts + '}';
    }

    
    

    public static void main(String[] args) throws Exception {
        Routes t = new Routes();
        //t.createRandomRouteFile("routesThreeDay10.json", 10, 86400*3);
        t.loadRoutesFile("routesOneDay100.json");

//        for (int i = 0; i< 10; i++) {
//            Route rt = t.rts.get(i);        
//            Thing thing = rt.getPosition();        
//            System.out.println(thing);
//            
//        }
        
        Route rt = t.rts.get(4);        
        long n = System.currentTimeMillis();
        
        FileWriter fw = new FileWriter("temp.txt");
        
        for (int i = 0; i <= rt.lastSec*2; i = i + 60) {
            
            Thing thing = rt.getPosition(n + i*1000);        
            fw.write(thing.toString() + "\n");
          
        }
        fw.close();


//        while (true) {
//            Route rt = t.rts.get(6);        
//            Thing thing = rt.getPosition(System.currentTimeMillis() + 1400*1000);        
//            System.out.println(thing);
//            Thread.sleep(1000);            
//        }
        
//        for (Route rt : t.rts) {
//            System.out.println(rt.numWpts);
//            System.out.println(rt);
//        }
        
    }
    
    
}
