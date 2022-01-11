package io.gameoftrades.student21;

import io.gameoftrades.model.Handelaar;
import io.gameoftrades.model.algoritme.HandelsplanAlgoritme;
import io.gameoftrades.model.algoritme.SnelstePadAlgoritme;
import io.gameoftrades.model.algoritme.StedenTourAlgoritme;
import io.gameoftrades.model.lader.WereldLader;

/**
 * Welkom bij Game of Trades! 
 * 
 * Voordat er begonnen kan worden moet eerst de 'studentNN' package omgenoemd worden
 * zodat iedere groep zijn eigen namespace heeft. Vervang de NN met je groep nummer.
 * Dus als je in groep 3 zit dan wordt de packagenaam 'student03' en ben je in groep
 * 42 dan wordt de package naam 'student42'.
 * 
 * Om te controleren of je het goed hebt gedaan is er de ProjectSanityTest die je kan draaien.
 * 
 */
public class HandelaarImpl implements Handelaar {
    
    private WereldLader wl;
    private SnelstePadAlgoritme spa;
    private StedenTourAlgoritme sta;
    private HandelsplanAlgoritme hpa;
    
    /**
     * Opdracht 1, zie ook de handige test-set in WereldLaderImplTest.
     */
    @Override
    public WereldLader nieuweWereldLader() {
        wl = new WereldLaderImpl();
        return wl;
    } 
    
        public WereldLader getWereldLader(){
        return wl;
    }

    /**
     * Opdracht 2
     */
    @Override
    public SnelstePadAlgoritme nieuwSnelstePadAlgoritme() {    
        spa = new SnelstePadAlgoritmeImpl();
        return spa;
    }
    
    public SnelstePadAlgoritme getSnelstePadAlgoritme(){
        return spa;
    }

    /**
     * Opdracht 3
     */
    @Override
    public StedenTourAlgoritme nieuwStedenTourAlgoritme() {
        sta = new StedenTourAlgoritmeImpl();
        return sta;
    }

    /**
     * Opdracht 4
     */
    @Override
    public HandelsplanAlgoritme nieuwHandelsplanAlgoritme() {
        hpa = new HandelsplanAlgoritmeImpl();
        return hpa;
    }
}