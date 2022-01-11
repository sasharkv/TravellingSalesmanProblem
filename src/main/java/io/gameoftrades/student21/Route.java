package io.gameoftrades.student21;

import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Pad;
import io.gameoftrades.model.kaart.Stad;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author Sasha 
 */
public class Route {
    private SnelstePadAlgoritmeImpl snelstePadAlgoritmeInstance = new SnelstePadAlgoritmeImpl(); 
    
    private boolean isFitheidVeranderd = true;
    private double fitheid = 0;
    private ArrayList<Stad> steden = new ArrayList<>();
    private Kaart kaart;
    
    
    /**
     * Constructor #1 dat vult de ArrayList<Stad> steden in met null, 
     * maakt het de lengte van beginRoute van stedenTourAlgoritme
     * @param stedenTourAlgoritme
     * @param kaart  wordt later door Routes gebruikt
     */
    public Route(StedenTourAlgoritmeImpl stedenTourAlgoritme, Kaart kaart){
        stedenTourAlgoritme.getBeginRoute().forEach(x -> steden.add(null));
        this.kaart = kaart;
    }
    
    /**
     * Constructor #2 
     * @param steden
     * @param kaart wordt later door Routes gebruikt
     */
    public Route(ArrayList<Stad> steden, Kaart kaart){
        this.steden.addAll(steden);
        this.kaart = kaart;
        Collections.shuffle(this.steden);
    }
    
    public ArrayList<Stad> getSteden(){
        isFitheidVeranderd = true;
        return steden;
    }
    
    /**
     * Methode die de fitheid van Routes bepaalt
     * @return 
     */
    public double getFitheid(){
        if (isFitheidVeranderd == true){
            fitheid = (1.0/berekenTotaleAfstand())*10000;
            isFitheidVeranderd = false;
        }
        return fitheid;
    }
    
    /**
     * Methode die de SnelstePadAlgoritme van opdracht 2 gebruikt.
     * @return totaleAfstand de totale prijs van een route,
     * i.e. the som van de prijzen van alle Padden binnen deze Route.
     */
    public double berekenTotaleAfstand(){
        int stedenSize = this.steden.size();
        double totaleAfstand = 0;
        for (int i = 0; i < (stedenSize - 1); i++) {
          Pad p = snelstePadAlgoritmeInstance.bereken(this.kaart, steden.get(i).getCoordinaat(), steden.get(i+1).getCoordinaat());
          totaleAfstand += p.getTotaleTijd();
        }
        return (double)totaleAfstand;
    }
    
    @Override
    public String toString(){
        return Arrays.toString(steden.toArray());
    }

}
