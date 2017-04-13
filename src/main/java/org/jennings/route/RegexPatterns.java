package org.jennings.route;


import java.util.regex.Pattern;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author david
 */
public class RegexPatterns {
    
    // Group 3 is unquoted String or null, Groupt 2 is Number or null 
    //public static final Pattern COMMADELIMQUOTEDSTRINGS = Pattern.compile("(([^\"][^,]*)|\"([^\"]*)\"),?");
    
    // Group 2 is number or null; Group 1 is String without quotes; Group 0 is the Quoted String
    public static final Pattern COMMADELIMQUOTEDSTRINGS = Pattern.compile("\"([^\"]+?)\",?|([^,]+),?|,"); 
    
    // Group 2 is number or null; Group 1 is String without quotes; Group 0 is the Quoted String
    public static final Pattern PIPEDELIMQUOTEDSTRINGS = Pattern.compile("\"([^\"]+?)\"\\|?|([^\\|]+)\\|?\\|");
}
