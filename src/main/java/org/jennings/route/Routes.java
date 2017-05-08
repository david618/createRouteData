/**
 * 
 * Routes is a collection of route objects
 * 
 * Support to save and load from file
 * 
 * 
 * 
 * David Jennings
 */
package org.jennings.route;

import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 *
 * @author david
 */
public class Routes {
    
    ArrayList<Route> rts = new ArrayList<>();
    RouteBuilder rb;

    public Routes() {
        rb = new RouteBuilder();
    }
    
    public Routes(double lllon, double lllat, double urlon, double urlat) {
        rb = new RouteBuilder(lllon, lllat, urlon, urlat);
    }
    
      
    
    
    public Route get(int index) {
        
        Route rt = null;
            
        int i = index % rts.size();
        
        try {
            rt = rts.get(i);
            
        } catch (Exception e) {
            
        }
        
        return rt;
        
    }
    
    public void save(String filename) {
        try {

            JSONArray jsonRts = new JSONArray();
            
            for (Route rt: rts) {
                jsonRts.put(rt);
            }

            FileWriter fw = new FileWriter(filename);

            jsonRts.write(fw);
            fw.close();
        } catch (Exception e) {

        }
        
        
    }
    
    public void load(String filename) {
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
    
    public void createRandomRoutes(int numRoutes, long durationSecs) {
        try {
            

            

            for (int i = 1; i <= numRoutes; i++) {
                Route rt = rb.createRoute(durationSecs);
                rt.setId(i);
                rts.add(rt);
            }

            //System.out.println(jsonRts.toString(2));
        } catch (Exception e) {

        }
    }    
    
    public void createRandomRouteFile(String filename, int numRoutes, long durationSecs) {
        try {

            JSONArray jsonRts = new JSONArray();

            for (int i = 1; i <= numRoutes; i++) {
                Route rt = rb.createRoute(durationSecs);
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
        t.createRandomRouteFile("routesOneDayUS10.json", 100, 86400);
//        t.load("routesOneDay100.json");
//        Route rt = t.get(0);
//        System.out.println(rt);

    }
    
    
}
