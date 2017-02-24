# createRouteData
Java code to Create Simulated Aircraft Route Data


Created Simulation
- Started with routes and aiport data from open flights (http://openflights.org/data.html)
- Used my GreatCircle Java code to create points along each route 
  - Code varies speed from 200 to 300 m/s and samples every 0.5 to 1.5 seconds
  - Resulted in ~37,000 routes
- Created another Java app that combines routes into a simFile
  - Start some random time during the flight
  - Merges current flight with previous flights
  - Resulting file simFile_1000.dat has on average 1000 samples a second and simFile_5000.dat has 5000 samples per second
- Played the sim file into GeoEvent to Update a BDS and it worked (I was able to watch flights move)
