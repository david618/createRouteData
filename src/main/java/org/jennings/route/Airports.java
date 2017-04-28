/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jennings.route;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;

/**
 *
 * @author david
 */
public class Airports {
    
    ArrayList<Airport> airports;
    int numAirports;

    public Airports() {
        
        // Load Airports
        
        FileReader fr = null;
        BufferedReader br = null;
        try {
            // Load in Aiports
            fr = new FileReader("airports2.dat");
            br = new BufferedReader(fr);

            airports = new ArrayList<>();

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

                int id = Integer.parseInt(vals.get(0));
                String name = vals.get(1);
                String lat = vals.get(6);
                String lon = vals.get(7);

                Airport arpt = new Airport(id, name, Double.parseDouble(lat), Double.parseDouble(lon));

                airports.add(arpt);
            }
            
            numAirports = airports.size();
            //System.out.println(numAirports);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * Get a Random Airport that is not have the name provided
     * @param name
     * @return
     * @throws Exception 
     */
    public Airport getRndAirport(String name) throws Exception {
        Random rnd = new Random();
        
        int i = rnd.nextInt(numAirports);        
        //System.out.println(i);
        Airport arpt = airports.get(i);
                
        int numTries = 1;
        while (arpt.getName().equalsIgnoreCase(name)) {
            //System.out.println("HERE");
            numTries += 1;
            i = rnd.nextInt(numAirports);        
            arpt = airports.get(i);            
            if (numTries > 10) {
                throw new Exception("Having trouble finding a Random that doesn't match the value requested");
            }
        }
        
        return arpt;
    }
    
    public static void main(String[] args) {
        
        Airports t = new Airports();
        
//        for (Airport arpt: t.airports) {
//            System.out.println(arpt.getName());
//        }
        try {
            String name = "";
            for (int i=0; i<1000; i++) {                
                Airport arpt = t.getRndAirport(name);
                name = arpt.getName();
                System.out.println(name);

            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
    }
    
    
}
