# createRouteData
Java code to Create Simulated Aircraft Route Data and tracking of Aircraft on the Routes.  

## Code Overview

### Airport
Java Bean for Airport data

### Airports
Loads airports from https://openflights.org/data.html; Support a bbox constructor to limit aiports to ones in the bbox.

### CreateRoutes
This class opens the airports and routes file from https://openflights.org/data.html.  Creates a file for each route. Each file has sample event data along the route with random speeds from 100 to 300 m/s; samples every 9.5 to 10.5 seconds.  These data files are merged into a Siumation file with GenRandomSimFile.  

### GenRandomSimFile
Merges multiple Route Files created by CreateRoutes into a simFile

### RegExPatterns
Just some RegEx Patters; used to parse the openflight data. There were some challenges in the data because of special characters used and comma seperated quoted strings.

### Route
Route is a set of WayPoints that define the route.  

### RouteArrayList
Experimental.  Considering turning Route into an ArrayList 

### RouteBuilder
Used to create random routes. Has a bbox constructor.

### Routes 
Set of Route objects.  Support saving route list to a file and loading them from a file. 

### Thing
The thing is assiged an id, a route, and an offset into the route.  You could have many things on the same route.

### WayPoint
Point in a route. It includes an id, lon, lat, distance and bearing to next waypoint, speed to next way point, start time and end time for this Segement. 

### SendEvents
Create/Load a set of Things on Routes; then provide positions of those things at a specified rate.  


# Create SimFiles

This was the first output I created from this application.  

## Process
- Started with routes and aiport data from open flights (http://openflights.org/data.html)
- Used my GreatCircle Java code to create points along each route 
  - Code varies speed from 200 to 300 m/s and samples every 0.5 to 1.5 seconds
  - Resulted in ~37,000 routes
- Created another Java app that combines routes into a simFile
  - Start some random time during the flight
  - Merges current flight with previous flights
  - Resulting file simFile_1000.dat has on average 1000 samples a second and simFile_5000.dat has 5000 samples per second
- Played the sim file into GeoEvent to Update a BDS and it worked (I was able to watch flights move)
