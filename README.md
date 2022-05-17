# purposes
When you draw spread routes on Garmin Basecamp with few waypoints what you see in Basecamp
may differ from what you see in (my Zumo 595) navigator.
To suply recalculating routes on my Garmin Zumo 595 
I added (every 2000 m or whatever you decide)  new intermediate waypoints on an exported 
*.gpx file.

The new gpx file will have an extention of "_CONWP.gpx"
## Package
You can package a jar with all dependencies with command:

	mvn clean package
	
this will create under dir `/target/` a file `gpxparse-jar-with-dependencies.jar`
wich can be launched (under directorory of project) with command:

	java -jar target/gpxparse.jar
## properties files
this app uses property file `GpxParse.properties` wich contains some important keys but also
key `lastDirGpx` wich is the last file(also dir) opened 	

##Launch

The `gpxparse.jar` file which cannot be launched (under the project directory) with the command:

	java -jar target/gpxparse-jar-with-dependencies.jar
	
because of use `javafx` you have to lauch with 

	java 	--module-path "C:\Program Files\javafx-sdk-17.0.1\lib" 
			--add-modules="javafx.controls,javafx.base,javafx.fxml,javafx.graphics" 
			-jar "target\gpxparse.jar"
			
 or with the cmd `lancia.cmd`

if everything works correctly the following form should appear:


![](./dati/gpxForm.png "La form che compare al lancio")


where:

- **GPX in** is the file to be fed to the program
- **Km min** is the distance I want to make between the WayPoints
- **GPX out** is the file that will be generated
- the "**Lancia BaseCamp**" Check allows you to view, after preparation, the new route, provided that Garmin BaseCamp is correctly installed and registered