package io.gameoftrades.student21;

import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Pad;
import io.gameoftrades.model.kaart.Richting;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Sasha
 */
public class PadImpl implements Pad{
    
    private List<Node> nodePathLocal;
    private ArrayList<Richting> richtingenAL = new ArrayList<>();
    private Richting[] richtingen;  
    private double totalGCost;
    
    public void setNodePathLocal(List<Node> nodePath){
        this.nodePathLocal = nodePath;
    }
    
    public ArrayList<Richting> getRichtingenAL(){
        return richtingenAL;
    }
    
    public void setTotalGCost(double gCost){
        this.totalGCost = gCost;
    }
    
    public void setRichtingenAL(Richting richt){
        this.richtingenAL.add(richt);      
    }
    
    public void arrayListRichtingenToArray(){
        this.richtingen = new Richting[richtingenAL.size()];
        this.richtingen = richtingenAL.toArray(richtingen);
    }   

     /**
     * @return de totale tijd (in bewegingspunten) die dit pad kost.
     */
    @Override
    public int getTotaleTijd() {
        int index = nodePathLocal.size() - 1; //total G cost of a path will be the G cost of the last node
        int totaleTijd = (int) nodePathLocal.get(index).gCost;
        return totaleTijd;      
    }

    /**
     * @return de bewegingen waaruit het pad bestaat.
     */
    @Override
    public Richting[] getBewegingen() {
        return richtingen;
    }

    /**
     * @return het pad in omgekeerde richting.
     */
    @Override
    public Pad omgekeerd() {
        Collections.reverse(nodePathLocal); 
        
        //make AL again
        for (int i = 0; i < nodePathLocal.size()-1; i++){
        setRichtingenAL(Richting.tussen(nodePathLocal.get(i).tileCoord, nodePathLocal.get(i+1).tileCoord));  
        }
        //convert it to a simple array of Richtingen
        arrayListRichtingenToArray();
        
        return this;
    }
        
    
     /**
     * bepaalt op welk coordinaat je uit komt als je het pad volgt.
     * @param start het start coordinaat.
     * @return het eindpunt van het pad.
     */
    @Override
    public Coordinaat volg(Coordinaat crdnt) {   //I don'tknow what to do with a given start coord??
        int index = nodePathLocal.size() - 1; //get to the last node
        Coordinaat eindCoord = nodePathLocal.get(index).tileCoord;
        return eindCoord;        
   }
}