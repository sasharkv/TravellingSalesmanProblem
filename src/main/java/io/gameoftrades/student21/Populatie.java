package io.gameoftrades.student21;

import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Stad;
import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * Populatie bestaat van Routes. Normaal 8 Routes, 
 * tenzij het een strijdPopulatie is, dan van 3 Routes.
 * @author Sasha 
 */

public class Populatie {
    private ArrayList<Route> routes = new ArrayList<>(StedenTourAlgoritmeImpl.POPULATIE_GROOTTE);
    
     /**
      * Constructor #1 die alle Routes initializeert.
      * @param populatieGrootte
      * @param stedenTourAlgoritme
      * @param kaart 
      */
    
    public Populatie(int populatieGrootte, StedenTourAlgoritmeImpl stedenTourAlgoritme, Kaart kaart){
    IntStream.range(0, populatieGrootte).forEach(x -> routes.add(new Route(stedenTourAlgoritme.getBeginRoute(), kaart)));   
 // De lijn van code boven (lambda) is hetzelfde als:
 //        for (int i = 0; i < populatieGrootte; i++) {
 //            routes.add(new Route(stedenTourAlgoritme.getBeginRoute(), kaart));         
 //        }
    }
      
    /**
     * Constructor #2
     * @param populatieGrootte
     * @param steden
     * @param kaart 
     */
  
    public Populatie(int populatieGrootte, ArrayList<Stad> steden, Kaart kaart){
    IntStream.range(0, populatieGrootte).forEach(x -> routes.add(new Route(steden, kaart)));    

    }
   
    public ArrayList<Route> getRoutes(){
       return routes; 
    }
    
    /**
     * Comparator methode dat plaats de fittere Routes boven in de array.
     * De goedkoopste Route is bovenaan (heeft index 0).
     */
    public void sorteerRoutesPerFitheid(){
        routes.sort((Route route1, Route route2) -> {
            int flag = 0;
            if (route1.getFitheid() > route2.getFitheid()) flag = -1;
            else if (route1.getFitheid() < route2.getFitheid()) flag = 1;
            return flag;
        });  
    }

}
