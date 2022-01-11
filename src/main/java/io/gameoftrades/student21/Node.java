package io.gameoftrades.student21;

import io.gameoftrades.model.kaart.Coordinaat;

/**
 *
 * @author Sasha 
 */
public class Node {
    public Coordinaat tileCoord;
    public Node parent;
    public double fCost, gCost, hCost;
    
    public Node(Coordinaat tileCoord, Node parent, double gCost, double hCost){
        this.tileCoord = tileCoord;
        this.parent =  parent;
        this.gCost = gCost;
        this.hCost = hCost;
        this.fCost = gCost+hCost;
    }

}
