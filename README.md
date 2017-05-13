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

#### Sample Outputs

The following command creates 10 random routes and 20 random things; then outputs the thing events every 1 sec as text to standard output.
<pre>
$ java -cp .\target\createRouteData.jar org.jennings.route.SendEvents - 10:20 1:txt
 </pre>

Sample output.
<pre>
0,1494687448099,0.24956133568990832,8859.259867223129,22.572418149652542,1,"Grand Canyon National Park Airport -> Minsk 1 Airport",-1,-110.153772839688,39.631391698015
1,1494687448099,0.26279191755802794,1656.1253589889275,40.646790475180914,2,"Marcos Juarez Airport -> Chkalovskiy Airport",-1,14.897046686567,50.311932990415
2,1494687448099,0.27463729140029647,6230.435781294918,-18.236633908926777,3,"Cairo West Airport -> Enumclaw Airport",-1,-3.648464450406,67.944484394314
3,1494687448099,0.20863514832374044,5420.153890003976,28.22685258615606,4,"Kisimayu Airport -> Ust-Kamennogorsk Airport",-1,48.256674706211,10.294251627691
4,1494687448099,0.20107286805061808,2660.1101737847675,-73.68687260474411,5,"Gisborne Airport -> Myeik Airport",-1,116.783078330387,-3.266557206666
5,1494687448099,0.25118125727989044,16105.997091480425,-157.00867992009003,6,"Bathpalathang Airport -> Tte. Julio Gallardo Airport",-1,87.440272233578,20.189703859289
</pre>

Fields
- id : Integer : Unique Thing ID
- timestamp: Long : Epoch Time (Number of milliseconds from midnight Jan 1, 1970 UTC/GMT) 
- speed: Double : Speed m/s 
- dist : Double : Distance to next destination (km)
- bearing : Double : Bearing from north (-180 to 180)
- routeid : Integer : Route id 
- location : String : Current departure airport -> destination airport
- secsToDep : Integer : Set to -1 when in route; otherwise number of seconds before departure
- lon : Double : Current longitude, WGS84, SRID 4326
- lat : Double : Current latitude, WGS84, SRID 4326

Similar command to output json
<pre>
java -cp .\target\createRouteData.jar org.jennings.route.SendEvents - 10:20 1:json
 </pre>

Sample output.
<pre>
{"routeid":1,"bearing":-68.21821113511557,"dist":8828.207332060407,"location":"Southwest Bay Airport -> Ormara Airport","lon":140.682229955629,"id":0,"secsToDep":-1,"speed":0.24448057365473222,"lat":-4.379026397537,"timestamp":1494688015380}
{"routeid":2,"bearing":-15.462560121824481,"dist":10437.372395732611,"location":"Platinum Airport -> Atbara Airport","lon":-167.465202507244,"id":1,"secsToDep":-1,"speed":0.21654588252186333,"lat":66.92784539391,"timestamp":1494688015380}
{"routeid":3,"bearing":85.81965014000447,"dist":946.7898508628641,"location":"Kempegowda International Airport -> Butaritari Atoll Airport","lon":164.500669494924,"id":2,"secsToDep":-1,"speed":0.2283970726329144,"lat":5.033036439649,"timestamp":1494688015380}
{"routeid":4,"bearing":-56.286928098342884,"dist":1376.32551754221,"location":"Wels Airport -> Willoughby Lost Nation Municipal Airport","lon":-67.295034417486,"id":3,"secsToDep":-1,"speed":0.23744839714995486,"lat":49.180377329946,"timestamp":1494688015390}
{"routeid":5,"bearing":15.827671436946368,"dist":1348.5843328853261,"location":"Xingyi Airport -> San Carlos","lon":-88.060393449029,"id":4,"secsToDep":-1,"speed":0.2701134740736052,"lat":22.848061719351,"timestamp":1494688015390}
{"routeid":6,"bearing":71.52249936712045,"dist":280.94261797342915,"location":"Peawanuck Airport -> Likoma Island Airport","lon":33.30567778384,"id":5,"secsToDep":-1,"speed":0.21112370389775528,"lat":-9.980338750085,"timestamp":1494688015390}
{"routeid":7,"bearing":-20.729248698677697,"dist":1118.454071767912,"location":"Garden City Regional Airport -> Norman Wells Airport","lon":-114.38574184674,"id":6,"secsToDep":-1,"speed":0.2151741630672122,"lat":57.144612440853,"timestamp":1494688015390}
{"routeid":8,"bearing":119.30619746917826,"dist":1444.6496561325812,"location":"Antwerp International Airport (Deurne) -> Alula Airport","lon":42.929087469543,"id":7,"secsToDep":-1,"speed":0.20168730629386608,"lat":22.600876001255,"timestamp":1494688015390}
{"routeid":9,"bearing":-38.21795102898084,"dist":14848.440023757063,"location":"Ngari Gunsa Airport -> Jose Maria Velasco Ibarra Airport","lon":69.278940723194,"id":8,"secsToDep":-1,"speed":0.252718671435508,"lat":41.875787876457,"timestamp":1494688015390}
</pre>




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
