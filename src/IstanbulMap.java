import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Program draws metro lines of Istanbul and their stations on a canvas from the data given in "coordinates.txt" file,
 * takes two input stations and animates the path if these two stations are connected
 */

public class IstanbulMap {
    public static void main(String[] args) {

        // 2D Array that stores metro line, RGB and station information
        ArrayList<ArrayList<String>> metroLines = new ArrayList<>();

        // 2D Array that stores transfer point information
        ArrayList<ArrayList<String>> breakPoints = new ArrayList<>();

        // Try-catch block that takes input from a given file and fills arraylists created above
        try {
            FileInputStream file = new FileInputStream("coordinates.txt");
            Scanner input = new Scanner(file);

            // While block that fills "metroLines" arraylist with each metro line
            int counter = 0;
            while(counter < 10) {
                String[] lineNameRGB = input.nextLine().split(" "); // Array that contains the metro line name and its RGB value
                String[] stationsStr = input.nextLine().split(" "); // Array that contains stations and coordinates
                ArrayList<String> stations = new ArrayList<>();
                stations.add(lineNameRGB[0]); // Add the name of the metro line
                stations.add(lineNameRGB[1]); // Add the RGB color of the metro line
                for (String s : stationsStr) {
                    stations.add(s); // Add the stations and their coordinates
                }
                metroLines.add(stations); // Add "stations" arraylist to "metroLines" arraylist
                counter++;
            }

            // While block that fills "breakpoints" arraylist with each transfer point
            while(input.hasNextLine()) {
                String[] transferPointMetroLine = input.nextLine().strip().split(" ");
                ArrayList<String> breakPoint = new ArrayList<>();
                for(String s:transferPointMetroLine) {
                    breakPoint.add(s);
                }
                breakPoints.add(breakPoint);
            }
            input.close();
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        // From metroLines list only take station names and store them in the form of list
        ArrayList<ArrayList<String>> stations = new ArrayList<>();

        for(int i = 0; i < metroLines.size(); i++) {
            ArrayList<String> tmp = new ArrayList<>(metroLines.get(i));
            stations.add(tmp);
        }

        for(int i = 0; i < stations.size(); i++) {
            stations.get(i).remove(0);
            stations.get(i).remove(0);
            for(int j = 1; j < stations.get(i).size(); j++) {
                stations.get(i).remove(j);
            }
        }

        // Remove the character "*" from station names
        for(int i = 0; i < stations.size(); i++) {
            for(int j = 0; j < stations.get(i).size(); j++) {
                String station = stations.get(i).get(j);
                if(station.startsWith("*")) {
                    station = station.substring(1);
                    stations.get(i).set(j, station);
                }
            }
        }

        System.out.println("Welcome to the Istanbul Metro Navigator !");
        Scanner input = new Scanner(System.in);
        System.out.println("Enter the starting station:");
        String startingStation = input.nextLine(); // Station we are starting from
        System.out.println("Enter the destination station:");
        String finalStation = input.nextLine(); // Destination station
        input.close();

        // If the two input stations are valid, proceed to find the path
        if(stationExists(startingStation, stations) && stationExists(finalStation, stations)) {
            String path = "";
            String temp = ""; // Temporary string we are going to give findPath method as a parameter
            ArrayList<ArrayList<Integer>> startingStationIndexes = findLocation(startingStation, stations);
            for(int i = 0; i < startingStationIndexes.size(); i++) {
                int startingStationIndexI = startingStationIndexes.get(i).get(0); // Outer index of the stations arraylist
                int startingStationIndexJ = startingStationIndexes.get(i).get(1); // Inner index of the stations arraylist

                path = findPath(startingStation, finalStation, stations, breakPoints, temp, startingStationIndexI, startingStationIndexJ, metroLines);
                if(path.length() != 0) {
                    break;
                }
            }
            if(path.length() != 0 && !path.equals("stop")) { // If a valid path is found between two given stations
                String[] pathList = path.split(","); // We are going to store station names in an array
                for(String s:pathList) {
                    System.out.println(s);
                }

                StdDraw.setCanvasSize(1024, 482); // Create canvas
                StdDraw.setXscale(0, 1024);
                StdDraw.setYscale(0, 482);
                StdDraw.enableDoubleBuffering();

                // 2D Array that holds RGB values of each metro line in 3 channels
                int[][] rgbValues = new int[10][3];
                for(int i = 0; i < metroLines.size(); i++) {
                    String[] rgbString = metroLines.get(i).get(1).split(",");
                    rgbValues[i][0] = Integer.parseInt(rgbString[0]);
                    rgbValues[i][1] = Integer.parseInt(rgbString[1]);
                    rgbValues[i][2] = Integer.parseInt(rgbString[2]);
                }

                // 2D Arraylist for keeping coordinates of the stations we have visited before to mark them with orange point on canvas
                ArrayList<ArrayList<Integer>> previousStationCoordinates = new ArrayList<>();

                // For every station being visited, draw the canvas again
                for(String s:pathList) {
                    StdDraw.clear();
                    StdDraw.picture(512, 241, "background.jpg", 1024, 482);

                    // Draw every metro line and their stations on canvas with their corresponding RGB color
                    int metroLineCounter = 0;
                    while(metroLineCounter < metroLines.size()) {
                        for(int i = 3; i < metroLines.get(metroLineCounter).size() - 2; i += 2) {
                            // Set pen color to the RGB value of the current metro line
                            StdDraw.setPenColor(rgbValues[metroLineCounter][0], rgbValues[metroLineCounter][1], rgbValues[metroLineCounter][2]);
                            StdDraw.setPenRadius(0.012);

                            // Choose two adjacent stations and store their coordinates in arrays to draw a line between them
                            String[] firstStationCoordinates = metroLines.get(metroLineCounter).get(i).split(",");
                            String[] secondStationCoordinates = metroLines.get(metroLineCounter).get(i + 2).split(",");
                            int firstStationX = Integer.parseInt(firstStationCoordinates[0]);
                            int firstStationY = Integer.parseInt(firstStationCoordinates[1]);
                            int secondStationX = Integer.parseInt(secondStationCoordinates[0]);
                            int secondStationY = Integer.parseInt(secondStationCoordinates[1]);
                            StdDraw.line(firstStationX, firstStationY, secondStationX, secondStationY); // Draw a part of the current metro line

                            // Mark stations of the current metro line with a white dot
                            StdDraw.setPenColor(StdDraw.WHITE);
                            StdDraw.setPenRadius(0.01);
                            StdDraw.point(firstStationX, firstStationY);
                            StdDraw.point(secondStationX, secondStationY);

                            // Write the names of the stations that start with a "*" above their corresponding white dot
                            StdDraw.setPenColor(StdDraw.BLACK);
                            StdDraw.setFont(new Font("Helvetica", Font.BOLD, 8));
                            if(metroLines.get(metroLineCounter).get(i-1).startsWith("*")) {
                                String stationName = metroLines.get(metroLineCounter).get(i-1).substring(1);
                                StdDraw.text(firstStationX, firstStationY + 5, stationName);
                            }

                            // Write the name of the second station in this for loop only if it starts with "*" and is the last station in the current metro line
                            if(metroLines.get(metroLineCounter).get(i+1).startsWith("*") && i == metroLines.get(metroLineCounter).size() - 3) {
                                String stationName = metroLines.get(metroLineCounter).get(i+1).substring(1);
                                StdDraw.text(secondStationX, secondStationY + 5, stationName);
                            }
                        }
                        metroLineCounter++; // To access the next metro line in metroLines list in the next for loop, increase this index
                    }

                    int metroLineIndex = -1;

                    // Strip "*" from the end of the current station if it exists
                    int stationIndex = -1;
                    if(s.endsWith("*")) {
                        int stationNameLength = s.length();
                        s = s.substring(0,stationNameLength - 1);
                    }

                    // Get the outer and inner indexes of the current station from metroLines list to access the station's coordinates later on
                    for(int i = 0; i < metroLines.size(); i++) {
                        if(metroLines.get(i).contains(s)) {
                            stationIndex = metroLines.get(i).indexOf(s);
                            metroLineIndex = i;
                            break;
                        }
                        if(metroLines.get(i).contains("*" + s)) {
                            stationIndex = metroLines.get(i).indexOf("*" + s);
                            metroLineIndex = i;
                            break;
                        }
                    }

                    // Minimize the previously visited station's orange dot
                    if(!previousStationCoordinates.isEmpty()) {
                        StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
                        StdDraw.setPenRadius(0.01);
                        for(int i = 0; i < previousStationCoordinates.size(); i++) {
                            StdDraw.point(previousStationCoordinates.get(i).get(0), previousStationCoordinates.get(i).get(1));
                        }
                    }

                    // Get the current station's coordinates
                    ArrayList<Integer> currentStationCoordinates = new ArrayList<>();
                    String[] currentStationCoordinatesStr = metroLines.get(metroLineIndex).get(stationIndex + 1).split(",");
                    for(String c:currentStationCoordinatesStr) {
                        currentStationCoordinates.add(Integer.parseInt(c));
                    }

                    // Make the dot of the current station bigger and orange to indicate we are currently at this station
                    int stationCoordinateX = currentStationCoordinates.get(0);
                    int stationCoordinateY = currentStationCoordinates.get(1);
                    StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
                    StdDraw.setPenRadius(0.02);
                    StdDraw.point(stationCoordinateX, stationCoordinateY);

                    // Add this station to the list of previously visited stations to make its dot on canvas smaller
                    previousStationCoordinates.add(currentStationCoordinates);

                    StdDraw.show();
                    StdDraw.pause(300);
                }
            }

            else { // If a valid path is not found between two given stations, it means they are not connected
                System.out.println("These two stations are not connected");
                System.exit(0);
            }
        }

        else {
            // In case station names provided are invalid, exit the program
            System.out.println("The station names provided are not present in this map.");
            System.exit(0);
        }
    }

    /**
     * Recursive method that tries to connect two input stations and returns the station chain in the form of a string
     * @param startingStation The station we start from
     * @param finalStation Target station
     * @param stations 2D Arraylist that includes stations inside sublists with each sublist representing a metro line
     * @param breakPoints 2D Arraylist that includes transfer points and their corresponding metro lines
     * @param temp Empty string where we store each correct station we visit until target station
     * @param startingStationIndexI Outer index for 2D Arraylists that enables us to reach sublists
     * @param startingStationIndexJ Inner index for 2D Arraylists that enables us to reach stations inside sublists
     * @param metroLines 2D Arraylist which contains Metro Line name, RGB info, Stations and their coordinates
     * @return Correct chain of stations from starting station up until target station
     */
    public static String findPath(String startingStation, String finalStation, ArrayList<ArrayList<String>> stations, ArrayList<ArrayList<String>> breakPoints, String temp, int startingStationIndexI, int startingStationIndexJ, ArrayList<ArrayList<String>> metroLines) {
        if(0 <= startingStationIndexJ && startingStationIndexJ < stations.get(startingStationIndexI).size()) {
            // If this station is already visited, return "visited" string in order to not continue this way
            if(stations.get(startingStationIndexI).get(startingStationIndexJ).endsWith("*")) {
                return "visited";
            }
            String currentStationName = findName(startingStationIndexI, startingStationIndexJ, stations);

            // Base case: If we can reach to our target station, add current station name to temp string and return it
            if(currentStationName.equals(finalStation)) {
                temp += currentStationName;
                return temp;
            }

            // Directions to move inside an arraylist
            int[] directions = {-1, 1};

            // If current station is not a transfer station
            if(isBreakPoint(currentStationName, breakPoints) == -1) {
                for(int i:directions) {
                    // Mark current station as visited
                    String visited = stations.get(startingStationIndexI).get(startingStationIndexJ) + "*";
                    stations.get(startingStationIndexI).set(startingStationIndexJ, visited);

                    // Move onto the next station
                    String solution = findPath(startingStation, finalStation, stations, breakPoints, temp + currentStationName + ",", startingStationIndexI, startingStationIndexJ + i, metroLines);
                    if(!solution.equals("stop") && !solution.equals("visited")) { // If there is a valid solution path return this solution path
                        return solution;
                    }
                    else { // If a valid solution is not found
                        if(solution.equals("visited")) { // If we tried to go to a station that we already visited, change direction
                            continue;
                        }
                        if(currentStationName.equals(startingStation)) { // Go to the opposite direction
                            continue;
                        }
                        else { // This direction is a dead end, go back to the starting station of the current metro line
                            return "stop";
                        }
                    }
                }
            }
            // If current station is a transfer station
            else {
                int breakPointIndex = isBreakPoint(currentStationName, breakPoints); // Outer index of the transfer station's sublist
                for(int i = 1; i < breakPoints.get(breakPointIndex).size(); i++) {
                    String metroLine = breakPoints.get(breakPointIndex).get(i); // Get the metro line name that we are going to transfer to

                    int metroLineIndex = findMetroLineIndex(metroLine, metroLines); // From metroLines Arraylist find the index of the metro line that we want to transfer to

                    int stationIndex = 0;
                    for(int j = 0; j < stations.get(metroLineIndex).size(); j++) {
                        if(stations.get(metroLineIndex).get(j).equals(currentStationName)) {
                            stationIndex = j; // Index of the current station that is in the new metro line we want to transfer to
                            break;
                        }
                    }

                    // Mark current transfer station as visited in the new metro line
                    String visited = stations.get(startingStationIndexI).get(startingStationIndexJ) + "*";
                    stations.get(metroLineIndex).set(stationIndex, visited);

                    // If we reach the end of a metro line when started from a breakpoint and can not find a solution we increase the counter so that
                    // when we return to the breakpoint station back we can now move to the opposite direction
                    for(int d:directions) {
                        String solution = findPath(startingStation, finalStation, stations, breakPoints, temp + currentStationName + ",", metroLineIndex, stationIndex + d, metroLines);
                        if(!solution.equals("stop") && !solution.equals("visited")) { // If a valid solution is found
                            return solution;
                        }
                    }
                }
                // If none of the metro lines that our transfer point leads helps us reach our destination, mark this transfer point as visited and report it as a dead end
                String visited = stations.get(startingStationIndexI).get(startingStationIndexJ) + "*";
                stations.get(startingStationIndexI).set(startingStationIndexJ, visited);
                return "stop";
            }
        }
        // If we reach the end or beginning of the stations list and go beyond, return "stop" to indicate this way is not the right one
        return "stop";
    }

    /**
     * Method that returns the name of the station which has the indexes of i and j
     * @param i Outer index
     * @param j Inner index
     * @param lst The list we are searching for to find the name of the station
     * @return Name of the station
     */
    public static String findName(int i, int j, ArrayList<ArrayList<String>> lst) {
        return lst.get(i).get(j);
    }

    /**
     * Method that returns the outer I and inner J indexes of the station name we are searching for
     * @param name Name of the station we are searching the index for
     * @param lst The list we are going to search the station name in
     * @return Indexes of station name
     */
    public static ArrayList<ArrayList<Integer>> findLocation(String name, ArrayList<ArrayList<String>> lst) {

        // Arraylist that contains indexes of all the instances of parameter "name"
        ArrayList<ArrayList<Integer>> stationIndexes = new ArrayList<>();

        for(int i = 0; i < lst.size(); i++) {
            for(int j = 0; j < lst.get(i).size(); j++) {
                String station = lst.get(i).get(j);
                if(station.equals(name)) {
                    ArrayList<Integer> temp = new ArrayList<>(); // A temporary arraylist that contains indexes of a single instance of parameter "name"
                    temp.add(i);
                    temp.add(j);
                    stationIndexes.add(temp);
                    break;
                }
            }
        }
        return stationIndexes;
    }

    /**
     * Returns true if station name exists in the list, otherwise returns false
     * @param name Name of the station
     * @param lst List we are searching the station name in
     * @return true if exists, false otherwise
     */
    public static boolean stationExists(String name, ArrayList<ArrayList<String>> lst) {
        for(int i = 0; i < lst.size(); i++) {
            if(lst.get(i).contains(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method that checks if a station is a transfer point
     * @param name Name of the station
     * @param lst List of break points
     * @return index of the sublist if station is a breakpoint, otherwise return -1
     */
    public static int isBreakPoint(String name, ArrayList<ArrayList<String>> lst) {
        for(int i = 0; i < lst.size(); i++) {
            if(lst.get(i).contains(name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Method that finds the index of the metro line we are looking for from the metroLines or stations array
     * @param name Name of the metro line
     * @param lst metroLines or stations array
     * @return the index of the metro line we are looking for
     */
    public static int findMetroLineIndex(String name, ArrayList<ArrayList<String>> lst) {
        for(int i = 0; i < lst.size(); i++) {
            if(lst.get(i).get(0).equals(name)) {
                return i;
            }
        }
        return -1;
    }
}