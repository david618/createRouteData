# How To Use SendEvents

## Install PreReqs

As root

<pre>
yum -y install epel-release
yum -y install git
yum -y install maven
</pre>

## Clone this repo  
As orginary user

<pre>
git clone https://github.com/david618/createRouteData
</pre>

## Build 

<pre>
cd createRouteData
mvn install
</pre>

Should end with BUILD SUCCESS

## Setup Inputs
Sample Messages that will be sent
<pre>
1981,1494266487422,0.20902777098728215,15036.647327630322,-57.19894641498842,82,"Thornhill Air Base -> Catalina Airport",-1,18.470377468342,-11.910949591314
1982,1494266487422,0.2783268491891012,5666.352916846666,-40.67568624135877,83,"General Abelardo L. Rodríguez International Airport -> Anshan Air Base",-1,-159.610199718585,54.547188232323
1983,1494266487422,0.2870170400534734,7661.121124159629,41.98079052376241,84,"Bafoussam Airport -> Sendai Airport",-1,49.205173784553,37.808264340253
1984,1494266487422,0.29319770616295304,8820.053669231464,107.84723574614834,85,"Learmonth Airport -> Enua Airport",-1,115.443219285222,-22.63242364692
1985,1494266487422,0.26252541725038736,5611.558583245871,58.335872982398975,86,"Tozeur Nefta International Airport -> Chihhang Air Base",-1,64.933674575431,44.726262261316
1986,1494266487422,0.2569982504747237,13970.110443890659,-66.17955418761731,87,"Basco Airport -> Coronel Horácio de Mattos Airport",-1,82.915095898876,30.390789648329
1987,1494266487422,0.28941442074577994,5457.757850768527,152.12729283391,88,"TSTC Waco Airport -> Maria Reiche Neuman Airport",-1,-95.98559788458,29.847741998441
1988,1494266487422,0.21812331116036252,7784.2380392596415,49.55129796439629,89,"Amos Magny Airport -> Bassel Al-Assad International Airport",-1,-68.475546858944,53.166330174696
1989,1494266487422,0.2441319706714344,13386.202438717666,-149.54162688185477,90,"Wiluna Airport -> Deputado Luiz Eduardo Magalhães International Airport",-1,110.533262924046,-39.158020682868
1990,1494266487422,0.29408535978520844,17408.192380280256,-76.10898999421087,91,"Monroe County Airport -> Cunderdin Airport",-1,-90.00645306477,39.741583811959
1991,1494266487422,0.2181381612249903,10951.715895956268,-76.89984745756738,92,"Roi Et Airport -> Bouaké Airport",-1,96.433858879416,17.616316859117
</pre>

Fields
- Field1: id (int)
- Field2: timestamp (long)
- Field3: speed (double) km/s
- Field4: distance (double) km
- Field5: bearing (double) degrees from North
- Field6: Route ID (int)
- Field7: Location (String) Departing Airport -> Destination Airport for this segment of route
- Field8: Time to Deperature (int) -1 means in flight; other currently on around at Destination Airport; number of seconds until it will depart
- Field9: Lon 
- Field10: Lat

For example GeoEvent TCP Input
- Receive Text from a TCP Socket
- Server Port: 5566
- Incoming Data Contains GeoEvent Definition: No
- Create Fixed GeoEvent Definitions: Yes 
- GeoEvent Definition: "flights"
- Create Geometry from Field: yes
- X Geom Field: Field9
- Y Geom Field: Field10

After first run you can change
- Create Fixed GeoEvent Definitions: No
- GeoEvent Definition Name (Existing): "flights"


### Send Messages

<pre>
java -cp target/createRouteData-1.0-SNAPSHOT.jar org.jennings.route.SendEvts localhost:5570 100:2000 10
</pre>



