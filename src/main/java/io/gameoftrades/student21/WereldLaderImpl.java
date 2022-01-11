package io.gameoftrades.student21;

import io.gameoftrades.model.Wereld;
import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.lader.WereldLader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import io.gameoftrades.model.kaart.Kaart;  
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.model.kaart.Terrein;
import io.gameoftrades.model.kaart.TerreinType;
import io.gameoftrades.model.markt.Handel;
import io.gameoftrades.model.markt.HandelType;
import io.gameoftrades.model.markt.Handelswaar;
import io.gameoftrades.model.markt.Markt;
import java.util.ArrayList;
import java.util.List;

public class WereldLaderImpl implements WereldLader {
    
    private Kaart kaart;  
    private List<Stad> steden;
        
    public Kaart getKaart(){
        return kaart;
    }
    
    public List<Stad> getSteden(){
    return steden;
    }


    /**
     *
     * @param resource
     * @return it gets the txt file to load the werreld
     */
    @Override
    public Wereld laad(String resource) {

        try {
            // inputstream reader 

            BufferedReader in = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(resource)));
            String firstLine = in.readLine().trim(); //10, 10

            String[] mapDimensions = firstLine.split(","); // [10, 10]
            int x = Integer.valueOf(mapDimensions[0]); // 10 breedte , 1e getal op kaart bestand
            int y = Integer.valueOf(mapDimensions[1]); // 10 hoogte , 2e getal op kaart bestand

            checkY(resource);
            checkX(resource);

            Kaart kaart = new Kaart(x, y);  // Here we make a Kaart and a Kaart makes a Terrein (empty 2d array)
 
            
            TerreinType t = null;
            Coordinaat c = null;

            ArrayList<String> mapLines = new ArrayList<>();
            for (int i = 0; i < y; i++) {
                String mapLine = in.readLine().trim();
                mapLines.add(mapLine);
            }

            for (int i = 0; i < y; i++) { //Loop to go through strings in the array list of stgrings
                String s = mapLines.get(i);

                for (int j = 0; j < x; j++) { //Loop to go through elements of a string that we extracted from an ArrayList in a previous Loop

                    if (Character.toString(s.charAt(j)).equalsIgnoreCase("Z")) {
                        t = TerreinType.ZEE;
                        c = Coordinaat.op(j, i);

                    } else if (Character.toString(s.charAt(j)).equalsIgnoreCase("R")) {
                        t = TerreinType.BERG;
                        c = Coordinaat.op(j, i);

                    } else if (Character.toString(s.charAt(j)).equalsIgnoreCase("b")) {
                        t = TerreinType.BOS;
                        c = Coordinaat.op(j, i);

                    } else if (Character.toString(s.charAt(j)).equalsIgnoreCase("g")) {
                        t = TerreinType.GRASLAND;
                        c = Coordinaat.op(j, i);

                    } else if (Character.toString(s.charAt(j)).equalsIgnoreCase("s")) {
                        t = TerreinType.STAD;
                        c = Coordinaat.op(j, i);

                    }
                    Terrein ter = new Terrein(kaart, c, t);
                }
            }

            int aantalSteden = Integer.parseInt(in.readLine().trim()); //4
            List<Stad> stedenLijst = new ArrayList<>();

            if (aantalSteden != 0) {

                for (int i = 0; i < aantalSteden; i++) {
                    String firstLineSteden = in.readLine().trim(); //coordinate, coordinate, city name
                    String[] stadCoordinates = firstLineSteden.split(","); // [coordinate, coordinate, city name]
                    int xStad = Integer.valueOf(stadCoordinates[0]); //coordinate x
                    int yStad = Integer.valueOf(stadCoordinates[1]); //coordinate y
                    checkStadCoordinaten(xStad, yStad); //checks user input coordinates
                    xStad = xStad - 1;
                    yStad = yStad - 1;
                    String stadNaam = stadCoordinates[2]; //city name

                    Coordinaat co = Coordinaat.op(xStad, yStad); //make a coordinate
                    Stad s = new Stad(co, stadNaam); //use coordinate and extracted city name to make a stad
                    stedenLijst.add(s);
                }
            }
            
            steden = stedenLijst;

            int aantalHandels = Integer.parseInt(in.readLine().trim()); // in voorbeeld 5
            List<Handel> handels = new ArrayList<>();

            if (aantalHandels
                    != 0) {

                for (int i = 0; i < aantalHandels; i++) {
                    String firstLineMarkten = in.readLine().trim(); // stadsnaam, handeltype (enum), handelWaar, prijs (int)

                    String[] marktVanStad = firstLineMarkten.split(","); // array of stadsnaam, handeltype (enum), handelWaar, prijs (int)
                    String stadWaarMarktIs = marktVanStad[0];
                    // zoek welke stad in arraylist overeenkomt met naam stad. 
                    Stad handelStad = null;
                    for (int k = 0; k < stedenLijst.size(); k++) {
                        if (stedenLijst.get(k).getNaam().equals(stadWaarMarktIs)) {
                            handelStad = stedenLijst.get(k);
                        }
                    }
                    HandelType handelsType = null;

                    if (marktVanStad[1].equalsIgnoreCase("BIEDT")) {
                        handelsType = HandelType.BIEDT;
                    } else if (marktVanStad[1].equalsIgnoreCase("VRAAGT")) {
                        handelsType = HandelType.VRAAGT;
                    }

                    String stringHandel = marktVanStad[2];
                    Handelswaar handelsWaar = new Handelswaar(stringHandel);

                    int prijs = Integer.valueOf(marktVanStad[3]);

                    Handel handel = new Handel(handelStad, handelsType, handelsWaar, prijs);

                    handels.add(handel);

                }
            }
            Markt markt = new Markt(handels);
            Wereld wereldOpdrachtEen = new Wereld(kaart, stedenLijst, markt);
            this.kaart = kaart;

            return wereldOpdrachtEen;

        } catch (IOException ex) {
            throw new RuntimeException("unable to load " + resource);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Not a legal argument");
        } catch (ArrayIndexOutOfBoundsException e) {
            // throw new RuntimeException("unable to load " + resource);
            throw new ArrayIndexOutOfBoundsException("array empty");
        }

    }

    /**
     * Method to check if actual Y is the same as the Y given in the text
     * resource
     *
     * @param resource
     * @throws IOException
     * @throws IllegalArgumentException if Y klopt niet
     */
    private void checkY(String resource) throws IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(resource)));
        String firstLine = in.readLine().trim(); //10, 10

        String[] mapDimensions = firstLine.split(","); // [10, 10]
        int y = Integer.valueOf(mapDimensions[1]); // 10 hoogte , 2e getal op kaart bestand

        int yCounter = 0;
        while (!(in.readLine().trim().matches(".*\\d.*"))) { //if it doesn't contain a number, increase a counter +1
            yCounter++;
        }
        if (y != yCounter) {
            throw new IllegalArgumentException("Y klopt niet");
        }
    }

    /**
     * Method to check if actual X is the same as the X given in the text
     * resource
     *
     * @param resource
     * @throws IOException
     * @throws IllegalArgumentException if X klopt niet
     */
    private void checkX(String resource) throws IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(resource)));
        String firstLine = in.readLine().trim(); //10, 10

        String[] mapDimensions = firstLine.split(","); // [10, 10]
        int x = Integer.valueOf(mapDimensions[0]); // 10 breedte, a given X

        String mapLine = in.readLine().trim();
        int realX = mapLine.length(); //the actual breedte of the first line of a map

        if (x != 0 && x != realX) {
            throw new IllegalArgumentException("X klopt niet");
        }

    }

    /**
     * Checks if the coordinates given in the map are not less than or equal to
     * 0
     *
     * @param xStad
     * @param yStad throws IllegalArgumentException if x or y are less than or
     * equal to 0
     */
    private void checkStadCoordinaten(int xStad, int yStad) {
        if (xStad <= 0) {
            throw new IllegalArgumentException("X coordinate is <= 0");
        } else if (yStad <= 0) {
            throw new IllegalArgumentException("Y coordinate is <= 0");
        }

    }

}