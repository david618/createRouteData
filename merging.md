# Merging Routes into a Simulation File

Created 37,181 routes

- If I start every route and produce 1 sample/s. I'd have a rate of 37,181 messages/second.
- If I start 2x I could get 74,372, 3x I could get 111,553 messages/second.


Start with routes in various start positions not always at beginning, but somewhere in the route.

1 min 
- 40,000/s = 240,000
- 80,000/s = 480,000
- 120,000/s = 720,000

10 min 
- 2.4
- 4.8
- 7.2 million

Input: 37,181 files with run times from 0 to n seconds.

- Open file (find max time)
- Run one, two, or three times; choose random starting time the file run for x minutes (Must be at least x minutes before end of file)
- Merge into master run file sorting on time 

Generate with varing speeds from 200 to 300; and intervals from 0.5 to 1.5 seconds.

Picked 10,000 routes
- Start each route at a random time on the route at least duration milliseconds before end
- Merge the data with master adding master start time
