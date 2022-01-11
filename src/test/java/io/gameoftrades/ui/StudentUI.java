package io.gameoftrades.ui;

import io.gameoftrades.model.Handelaar;
import io.gameoftrades.student21.HandelaarImpl;

/**
 * Toont de visuele gebruikersinterface.
 * 
 * Let op: dit werkt alleen als je de WereldLader hebt geimplementeerd (Anders krijg je een NullPointerException).
 */
public class StudentUI {
    public static Handelaar handelaar;
// once i was happy and then i met hhs and it all went downhill from there
	public static void main(String[] args) {
            handelaar = new HandelaarImpl();
		MainGui.toon(handelaar, "/kaarten/voorbeeld-kaart.txt");
	}    
        
        	
}
