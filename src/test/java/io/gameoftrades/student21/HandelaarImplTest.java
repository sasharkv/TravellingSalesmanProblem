package io.gameoftrades.student21;

import io.gameoftrades.student21.HandelaarImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import io.gameoftrades.model.Handelaar;
import io.gameoftrades.model.Wereld;
import io.gameoftrades.model.algoritme.HandelsplanAlgoritme;
import io.gameoftrades.model.algoritme.SnelstePadAlgoritme;
import io.gameoftrades.model.algoritme.StedenTourAlgoritme;
import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Pad;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.model.lader.WereldLader;
import io.gameoftrades.model.markt.Handel;
import io.gameoftrades.model.markt.HandelType;
import io.gameoftrades.model.markt.Handelsplan;
import io.gameoftrades.model.markt.Handelswaar;
import io.gameoftrades.model.markt.actie.Actie;
import io.gameoftrades.model.markt.actie.BeweegActie;
import io.gameoftrades.model.markt.actie.HandelsPositie;
import io.gameoftrades.model.markt.actie.KoopActie;
import io.gameoftrades.model.markt.actie.VerkoopActie;

/**
 * Een verzameling eenvoudige tests om te kijken of de handelaar werkt.
 * 
 * Breid deze uit en of maak je eigen oplossing specifieke tests.
 */
public class HandelaarImplTest {

    private Handelaar handelaar;

    @Before
    public void init() {
        handelaar = new HandelaarImpl();
    }

    @Test
    public void zouWereldMoetenLaden() {
        WereldLader lader = handelaar.nieuweWereldLader();
        assertNotNull(lader);

        Wereld wereld = lader.laad("/kaarten/voorbeeld-kaart.txt");
        assertNotNull(wereld);
    }

    @Test
    public void zouEenPadMoetenVinden() {
        Wereld wereld = handelaar.nieuweWereldLader().laad("/kaarten/voorbeeld-kaart.txt");
        assertNotNull(wereld);
        
        Kaart kaart = wereld.getKaart();
        Stad van = wereld.getSteden().get(0);
        Stad naar = wereld.getSteden().get(1);

        SnelstePadAlgoritme algoritme = handelaar.nieuwSnelstePadAlgoritme();
        assertNotNull(algoritme);

        Pad pad = algoritme.bereken(kaart, van.getCoordinaat(), naar.getCoordinaat());

        assertNotNull(pad.getBewegingen());

        // 19 is de tijd voor de meest optimale route, om de bergen heen.
        
        assertEquals(19, pad.getTotaleTijd());

        // Heen

        Coordinaat bestemming = pad.volg(van.getCoordinaat());
        assertEquals(naar.getCoordinaat(), bestemming);

        // En Terug

         Coordinaat bron = pad.omgekeerd().volg(naar.getCoordinaat());
        assertEquals(van.getCoordinaat(), bron);
    }

    @Test
    public void zouEenRouteMoetenVinden() {
        Wereld wereld = handelaar.nieuweWereldLader().laad("/kaarten/voorbeeld-kaart.txt");
        assertNotNull(wereld);

        StedenTourAlgoritme algoritme = handelaar.nieuwStedenTourAlgoritme();
        assertNotNull(algoritme);
        List<Stad> result = algoritme.bereken(wereld.getKaart(), wereld.getSteden());

        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals(4, new HashSet<>(result).size()); // check duplicaten.
    }

    @Test
    public void vindVoorbeeldKaartDeSnelsteRoute() {
        Wereld wereld = handelaar.nieuweWereldLader().laad("/kaarten/voorbeeld-kaart.txt");
        assertNotNull(wereld);

        StedenTourAlgoritme algoritme = handelaar.nieuwStedenTourAlgoritme();
        assertNotNull(algoritme);
        
        List<Stad> result = algoritme.bereken(wereld.getKaart(), wereld.getSteden());
        
//[Stad((7,1),Birmingham), Stad((8,6),Cambridge), Stad((1,7),Derry), Stad((4,1),Aberdeen)
        assertEquals("Birmingham" , result.get(0).getNaam());
        assertEquals("Cambridge" , result.get(1).getNaam());
        assertEquals("Derry" , result.get(2).getNaam());
        assertEquals("Aberdeen" , result.get(3).getNaam());
        
    }
    
    @Test
    public void zouInkoopBestemmingMoetenVinden(){
     Wereld wereld = handelaar.nieuweWereldLader().laad("/kaarten/voorbeeld-kaart.txt");
        assertNotNull(wereld);
        Stad startStad = wereld.getSteden().get(0);
        HandelsplanAlgoritmeImpl algoritme = new HandelsplanAlgoritmeImpl();
        HandelsPositie positie;
        positie = new HandelsPositie(wereld, startStad, 150, 10, 50);
        
        Handel bestemming = algoritme.besteInkoopBestemming(wereld, positie);
        assertEquals("Cambridge" , bestemming.getStad().getNaam());
        
    }
 
//    @Test
//    public void zouVerkoopBestemmingMoetenVinden(){
//        Wereld wereld = handelaar.nieuweWereldLader().laad("/kaarten/voorbeeld-kaart.txt");
//        assertNotNull(wereld);
//
//        HandelsplanAlgoritmeImpl algoritme = new HandelsplanAlgoritmeImpl();
//        HandelsPositie positie;
//        Stad startStad = wereld.getSteden().get(0);
//        HandelType type = HandelType.BIEDT;
//        Handelswaar handelswaar = new Handelswaar("schapen");
//        int prijs = 20;
//        positie = new HandelsPositie(wereld, startStad, 150, 10, 50);
//    
//        Handel h = new Handel(startStad, type, handelswaar,prijs);
//        Actie a = new KoopActie(h);
//        positie= a.voerUit(positie);
//        
//        Stad bestemming = algoritme.besteVerkoopBestemming(wereld, positie);
//        assertEquals("Derry" , bestemming.getNaam());
//        
//    } 
    
    @Test
    public void zouEenHandelsplanMoetenMaken() {
        Wereld wereld = handelaar.nieuweWereldLader().laad("/kaarten/voorbeeld-kaart.txt");
        assertNotNull(wereld);
        
        Stad startStad = wereld.getSteden().get(0);
        
        HandelsplanAlgoritme algoritme = handelaar.nieuwHandelsplanAlgoritme();
        Handelsplan plan = algoritme.bereken(wereld, new HandelsPositie( wereld, startStad, 150, 10, 50));
        plan.toString();
        
        assertNotNull(plan);
        assertNotNull(plan.getActies());
int teller = 0;
        for (Actie a : plan.getActies()) {
            assertNotNull(a);
            assertFalse("BeweegActie gevonden, zet deze om in NavigeerActies", a instanceof BeweegActie);
            teller++;
            System.out.println("actie: " +teller+ a);
        }
    }

}
