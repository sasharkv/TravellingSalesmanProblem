package io.gameoftrades.student21;

import io.gameoftrades.debug.Debuggable;
import io.gameoftrades.debug.Debugger;
import io.gameoftrades.debug.DummyDebugger;
import io.gameoftrades.model.algoritme.StedenTourAlgoritme;
import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Stad;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Genetisch Algoritme
 *
 * @author Sasha
 */
public class StedenTourAlgoritmeImpl implements StedenTourAlgoritme, Debuggable {

    private Debugger debug = new DummyDebugger();

    @Override
    public void setDebugger(Debugger debugger) {
        this.debug = debugger;
    }

    //waarschijnlijkheid van mutatie, range is tussen 0 en 1
    //Route = chromosoom, Stad = gene in deze chromosoom
    public static final double MUTATIE_PERCENTAGE = 0.25;
    public static final int STRIJD_SELECTIE_GROOTTE = 3; 
    public static final int POPULATIE_GROOTTE = 8;
    //een fitste Route word niet gemuteerd
    public static final int AANTAL_ELITE_ROUTES = 1; 
    //wij beginnen met generatie 0 en eindigen met generatie 29
    public static final int AANTAL_GENERATIES = 30; 
    private ArrayList<Stad> beginRoute = new ArrayList<>();

    /**
     * bepaalt de beste volgorde van reizen tussen de steden zodat dit het
     * minste tijd kost en iedere stad slechts 1 keer wordt aangedaan (opdracht
     * 3).
     *
     * @param kaart de kaart waarop de steden liggen.
     * @param list de steden die aangedaan moeten worden.
     * @return een lijst op volgorde waarin de steden aangedaan moeten worden.
     */
    
    @Override
    public List<Stad> bereken(Kaart kaart, List<Stad> list) {
        //Stap 1. We krijgen een Kaart en een List<Stad>. 
        //De list wordt opgeslagen als een class variable.
        this.beginRoute.addAll(list);

        //Stap 2. We maken begin Populatie aan, die bestaat uit 8 Routes. 
        //Elke Route is gemakt door shuffling van de beginRoute van stap 1.
        //We sorteren deze 8 Routes per fitheid. De fitste Route (i.e. de goedkoopste)
        //is geplaatst naar het begin van de array. (Hij krijgt index 0).
        Populatie populatie = new Populatie(POPULATIE_GROOTTE, beginRoute, kaart);
        populatie.sorteerRoutesPerFitheid();
        int generationNummer = 0;
        printHeading(generationNummer++);
        printPopulatie(populatie);

        //Stap 3. We roepen evolve aan (dus we kruisen en muteren) de Populatie die we net hebben gecreerd.
        //De uitslag van deze evolutie is generatie 1. Wij sorteren het weer per fitheid.
        //Wij herhalen deze proces tot we 30 generaties in totaal hebben.
        while (generationNummer < AANTAL_GENERATIES) {
            printHeading(generationNummer++);
            populatie = evolve(populatie, kaart);
            populatie.sorteerRoutesPerFitheid();
            printPopulatie(populatie);
        }

        System.out.println("Beste Route: " + populatie.getRoutes().get(0));
        System.out.println(" met een afstand van: " + String.format("%.2f", (double) populatie.getRoutes().get(0).berekenTotaleAfstand()));

        //Stap 4. De fitste Route van de gehele evolutis is Route met index 0 
        //in de laatste generatie. We geven het terug.
        List<Stad> uitslag = (List) populatie.getRoutes().get(0).getSteden();
        debug.debugSteden(kaart, list);
        return uitslag;
    }

    /**
     * Methode om de uitslagen uit te printen
     * @param populatie
     */
    public void printPopulatie(Populatie popukatie) {
        popukatie.getRoutes().forEach(x -> {
            System.out.println(Arrays.toString(x.getSteden().toArray()) + " |  "
                    + String.format("%.4f", x.getFitheid()) + "   |  " + String.format("%.2f", x.berekenTotaleAfstand()));
        });
        System.out.println("");
    }

    /**
     * Methode om de headers uit te printen
     * @param generatieNummer
     */
    public void printHeading(int generatieNummer) {
        System.out.println("Generatie # " + generatieNummer);
        int stedenNamenLengte = 0;
        for (int x = 0; x < beginRoute.size(); x++) {
            stedenNamenLengte += beginRoute.get(x).getNaam().length();
        }
        int arrayLength = stedenNamenLengte + beginRoute.size() * 2;
        if ((arrayLength % 2) == 0) {
            System.out.println(" ");
        }
        for (int x = 0; x < stedenNamenLengte + beginRoute.size() * 2; x++) {
            System.out.print("-");
        }
        System.out.println("");
    }

    public ArrayList<Stad> getBeginRoute() {
        return beginRoute;
    }

    public Populatie evolve(Populatie populatie, Kaart kaart) {
        return muteerPopulatie(kruisPopulatie(populatie, kaart));
    }

    /**
     * Methode om populaties samen te kruisen. Instantieert een nieuwe populatie
     * met hetzelfde grootte als de oude. Roept kruisRoute() aan
     *
     * @param populatie is een oude populatie
     * @return kruisingPopulatie is de uitslag van het kruisen
     */
    Populatie kruisPopulatie(Populatie populatie, Kaart kaart) {
        Populatie kruisingPopulatie
                = new Populatie(populatie.getRoutes().size(), this, kaart);
        //"this" param is de instantie van dit algoritme
        IntStream.range(0, AANTAL_ELITE_ROUTES).forEach(x
                -> kruisingPopulatie.getRoutes().set(x, populatie.getRoutes()
                        .get(x))); //de fitste chromosoom is toegevoegd zonder verandering
        IntStream.range(AANTAL_ELITE_ROUTES, kruisingPopulatie.getRoutes()
                .size()).forEach(x -> {
                    Route route1 = kiesStrijdPopulatie(populatie, kaart).getRoutes().get(0);                                                               //pick up 2 routes that are the fittest in their own set of 3 random routes selected for the tournament
                    Route route2 = kiesStrijdPopulatie(populatie, kaart).getRoutes().get(0);
                    //nieuwe routes set hier
                    kruisingPopulatie.getRoutes().set(x, kruisRoute(route1, route2, kaart)); 
                });
        return kruisingPopulatie;
    }

    /**
     * Methode om twee Routes te kruisen. 
     *
     * @param route1
     * @param route2
     * @param kaart 
     * @return een nieuwe Route die gemaakt is door het kruisen 
     * 
     * Voorbeeld:
     * 
     * route1: [Aberdeen, Birmingham, Cambridge, Derry] 
     * route2: [Derry, Cambridge, Birmingham, Aberdeen] 
     * 
     * tussentijds Route: [Aberdeen, Birmingham, null, null] 
     * 
     * finale kruisingRoute: [Aberdeen, Birmingham, Derry, Cambridge]
     */
    Route kruisRoute(Route route1, Route route2, Kaart kaart) {
        Route kruisingRoute = new Route(this, kaart);
        Route tempRoute1 = route1;
        Route tempRoute2 = route2;
        if (Math.random() < 0.5) {
            tempRoute1 = route2;
            tempRoute2 = route1;
        }
        //deze call vult de tussentijdse Route in
        for (int x = 0; x < kruisingRoute.getSteden().size() / 2; x++) {  
            kruisingRoute.getSteden().set(x, tempRoute1.getSteden().get(x));
        }
        return vullNullsInKruisingRoute(kruisingRoute, tempRoute2);
    }

    /**
     * Private methode die gebruikt wordt door kruisRoute() om de null waardes 
     * in kruisRoute in te vullen met waardes van  route2. De duplicaten worden
     * overslaan.
     */
    private Route vullNullsInKruisingRoute(Route kruisingRoute, Route route) {
        route.getSteden().stream().filter(x -> !kruisingRoute.getSteden().contains(x)).forEach(stadX -> {
            for (int y = 0; y < route.getSteden().size(); y++) {
                if (kruisingRoute.getSteden().get(y) == null) {
                    kruisingRoute.getSteden().set(y, stadX);
                    break;
                }
            }
        });
        return kruisingRoute;
    }

    /**
     * Methode die een Route muteert, i.e. maakt permutaties van een Route.
     *
     * @param route
     * @return route  gemuteerde Route 
     * 
     * Voorbeeld: 
     * Oorspronkelijk Route - [Aberdeen, Birmingham, Cambridge, Derry] 
     * Gemuteerde Route - [Birmingham, Aberdeen, Cambridge, Derry]
     */
    Route muteerRoute(Route route) {
        route.getSteden().stream().filter(x -> Math.random() < MUTATIE_PERCENTAGE).forEach(stadX -> {
            int y = (int) (route.getSteden().size() * Math.random());
            Stad stadY = route.getSteden().get(y);
            route.getSteden().set(route.getSteden().indexOf(stadX), stadY);
            route.getSteden().set(y, stadX);
        });
        return route;
    }

    /**
     * Methode die een Populatie muteert, i.e. muteert elke Route binnen 
     * die Populatie behalve de fitste Route.
     *
     * @param populatie
     * @return dezelfde Populatie met elke niet-elite Route gemuteerd
     */
    Populatie muteerPopulatie(Populatie populatie) {//roept muteerRoute() aan
        populatie.getRoutes().stream().filter(x -> populatie.getRoutes().indexOf(x)
                >= AANTAL_ELITE_ROUTES).forEach(x -> muteerRoute(x));
        return populatie;
    }

    /**
     * Methode die een strijdPopulatie kiest van de gegeven parameter Populatie.
     *
     * @param populatie
     * @return strijdPopulatie een Populatie van 3 willekeurige Routes van de gegeven Populatie
     * gesorteerd per fitheid.
     */
    Populatie kiesStrijdPopulatie(Populatie populatie, Kaart kaart) {
        Populatie strijdPopulatie = new Populatie(STRIJD_SELECTIE_GROOTTE, this, kaart); 
        IntStream.range(0, STRIJD_SELECTIE_GROOTTE).forEach(x -> strijdPopulatie.getRoutes().set(x,
                populatie.getRoutes().get((int) Math.random() * populatie.getRoutes().size())));
        strijdPopulatie.sorteerRoutesPerFitheid();
        return strijdPopulatie;
    }

}
