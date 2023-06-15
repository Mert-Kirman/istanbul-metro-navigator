# Istanbul Metro Navigator

Java program that draws metro lines of Istanbul and their stations on a canvas from the
data given in "coordinates.txt" file, takes two input stations and animates the path if
these two stations are connected.

## How It Works

The program starts by creating two lists which store metro lines with their stations and
transfer stations where one can move onto another metro line. These lists are named as
“metroLines” and “breakPoints” respectively.

The program then takes two more inputs: the name of the station we start from and
the name of the station where we intend to arrive. If either of these two stations does
not exist in the “coordinates.txt” file, the program tells the user that no such station
name exists in the map and exits. If both of these station names are present in the file,
the program then proceeds to find a path between these two input stations using a
recursive function. The recursive algorithm starts from the first input station and tries
to move along the metro line it is a part of and marks the current station to keep track
of the stations already visited. This helps the recursive program to not step into a
station it has already tried out and prevents an infinite loop. The algorithm also adds
the current station to a string to keep track of the stations it has passed starting from
the first input station before moving onto the next station, without any repetitions. If
the algorithm reaches the last station of a metro line it returns a string “stop” meaning
that direction is a dead end and the method starts trying another direction. Finally, if
the recursive algorithm reaches the target station user wants to arrive, it returns the
previously mentioned string where it stored all the station names it passed by starting
from the starting station until the final station.

If the recursive method cannot reach the target station after trying every possible
path, the java program tells the user that these two input stations are not connected
and exits the program. If however a path is found and returned in the string
previously mentioned, the program proceeds to create a canvas, draw all the metro
lines using their own RGB color value and place a white point in the coordinates
where each station is found. If that station’s name begins with a “*” mark in the
“coordinates.txt” input file, its name is written above its corresponding white point.
After this the program turns the white point of the starting station to orange color and
increases its size to indicate current station. It then does the same change for every
station stored in the path string and while doing so it also decreases the size of the
orange points of previous stations thus creating an animation which simulates motion.

### Prerequisites

- An IDE or text editor to run the Java code.

- StdDraw Library

## Running the tests

The program takes a file as input which includes metro line names,
their corresponding RGB color value, the stations of each of these metro lines and
finally the coordinates of these stations. In this case, the program accepts this file
with the name “coordinates.txt”.

An example case is provided in the repository.
