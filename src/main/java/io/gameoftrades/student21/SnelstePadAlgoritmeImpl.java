package io.gameoftrades.student21;

import io.gameoftrades.debug.Debuggable;
import io.gameoftrades.debug.Debugger;
import io.gameoftrades.debug.DummyDebugger;
import io.gameoftrades.model.algoritme.SnelstePadAlgoritme;
import io.gameoftrades.model.kaart.Coordinaat;
import static io.gameoftrades.model.kaart.Coordinaat.op;
import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Pad;
import io.gameoftrades.model.kaart.Richting;
import io.gameoftrades.model.kaart.Terrein;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author een implementerende klasse voor een interface
 * SnelstePadAlgoritme
 *
 */
public class SnelstePadAlgoritmeImpl implements SnelstePadAlgoritme, Debuggable {

    private Debugger debug = new DummyDebugger();

    @Override
    public void setDebugger(Debugger debugger) {
        this.debug = debugger;
    }

    private Pad pad;


    @Override
    public Pad bereken(Kaart kaart, Coordinaat start, Coordinaat eind) {
        pad = new PadImpl();

        List<Node> nodePath = findPad(kaart, start, eind);

        // Deze loop array van nodes vraagt elke keer voor 2 coördinaten
        // genereert een Richting en voegt het toe aan een ArrayList van Richtingen in PadImpl instantie
        // We cast pad naar PadImpl voor toegang tot gemaakte methodes
        for (int i = 0; i < nodePath.size()-1; i++){
        ((PadImpl)pad).setRichtingenAL(Richting.tussen(nodePath.get(i).tileCoord, nodePath.get(i+1).tileCoord));
        }

        // converteren naar een simpel array van Richtingen
        ((PadImpl)pad).arrayListRichtingenToArray();
       //System.out.println("Richting[] as a String: " + Arrays.toString(((PadImpl)pad).getBewegingen()));

        ((PadImpl)pad).setNodePathLocal(nodePath); // save node path in pad voor de andere methoden
        debug.debugPad(kaart, start, pad);
        return pad;
    }

    /**
     * Klasse om nodes te  sorteren in array,  afhankelijk van hun F Cost.
     */
    private Comparator<Node> nodeSorter = new Comparator<Node>(){
        @Override
        public int compare(Node n0, Node n1) {
            if(n1.fCost < n0.fCost){return 1;} //index van de array omhoog verplaatsen.
            if(n1.fCost > n0.fCost){return -1;} //omlaag verplaatsen.
            return 0; //do nothing
        }

    };


    /**
     * Methode voor de uitvoering van A * gebruik Node klasse objecten
     *
     * @param start
     * @param goal
     * @return array list van Nodes, i.e. pathNodes
     */
    public List<Node> findPad(Kaart kaart, Coordinaat start, Coordinaat goal) {             //moest kaart toevoegen om de terreintypes te krijgen.
        List<Node> openList = new ArrayList<>();
        List<Node> closedList = new ArrayList<>();
        Node current = new Node(start, null, 0, getAfstandH(start, goal)); //dit node heeft geen parrent, daarom staat er NULL.
        openList.add(current);  //Dit is het einde van de initialisatie van de A * algoritme

        while (openList.size() > 0) {
            Collections.sort(openList, nodeSorter);
            current = openList.get(0);

            if (current.tileCoord.equals(goal))
            {
             List<Node> pathNodes = new ArrayList<>();
             while (current.parent != null){  //retracing stappen van goal naar start
                 pathNodes.add(current);
                 current = current.parent;
            }
             // Hier beneden voegen we de start node opnieuw. (Deze Current zal begin coördinaten hebben)
             pathNodes.add(current);

             openList.clear();
             closedList.clear();

             Collections.reverse(pathNodes); //omgekeerd pathNode

             return pathNodes; //return gereconstrueerd en REVERSED pathNodes

            }
            openList.remove(current);
            closedList.add(current);
            for (int i = 1; i < 9; i++) {
                /*
                |0|1|2|
                |3|4|5|
                |6|7|8|

                de centrale node (current) en de diagonalen overslaan.
                */
                if (i==2) continue;
                if (i==4) continue;
                if (i==6) continue;
                if (i==8) continue;
                int x = current.tileCoord.getX();
                int y = current.tileCoord.getY();
                //kijk op de current adjacencies
                int xi = (i % 3) - 1; //xi is x richting, zal ofwel -1, 0 of 1 zijn.
                int yi = (i / 3) - 1; //dezelfde voor y
                Coordinaat curCoord = op(x, y);
                Coordinaat atCoord = op(x+xi, y+yi); //at is potentieel andere node
                if (atCoord.getX()<0 || atCoord.getX() > 9) continue;
                if (atCoord.getY()<0 || atCoord.getY() > 9) continue;
                Terrein at = kaart.getTerreinOp(atCoord);
                if (at == null) continue;
                if (at.getTerreinType().getLetter() == 'Z') continue;
                double gCost = current.gCost + at.getTerreinType().getBewegingspunten();
                double hCost = getAfstandH(curCoord, goal);
                Node node = new Node(atCoord, current, gCost, hCost);
                if(coordInList(closedList, atCoord) && gCost >= current.gCost) continue;
                if(!coordInList(openList, atCoord) || (gCost < current.gCost)) openList.add(node);

                //een test
                ArrayList<Coordinaat> openListCoord = new ArrayList<>();
                for (int j = 0; j < openList.size(); j++) {
                    openListCoord.add(openList.get(j).tileCoord);
                }
            }
        }
        closedList.clear(); //openList is al clear
        System.out.println("Failed");
        return null; //als pathNode is niet gevonden

    }


    /**
     * is een methode dat checkt of een coordinaat(niet een node) is in een list
     */

    private boolean coordInList(List<Node> list, Coordinaat coordinaat){
        for (Node n : list){
            if (n.tileCoord.equals(coordinaat)) return true;
            }
        return false;
        }


    /**
     * Methode dat de H afstand berekend met gebruik van Pythagoras theorem
     *
     * @param tile
     * @param goal
     * @return double H afstand
     */
    private double getAfstandH(Coordinaat tile, Coordinaat goal) {
        double dx = tile.getX() - goal.getX();
        double dy = tile.getY() - goal.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

}
