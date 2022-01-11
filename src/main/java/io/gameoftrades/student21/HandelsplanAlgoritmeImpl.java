/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
// TO DO kijk of we nog kapitaal hebben implementeren
// kijk of er uberhaubt ruimte is.
// na elke stap/actie moet het echt uitgeveord worden en moet handelspositie worden geupdate. 
package io.gameoftrades.student21;

import io.gameoftrades.model.Wereld;
import io.gameoftrades.model.markt.actie.*;
import io.gameoftrades.model.markt.Handelsplan;
import io.gameoftrades.model.markt.actie.HandelsPositie;
import io.gameoftrades.model.algoritme.HandelsplanAlgoritme;
import io.gameoftrades.model.algoritme.SnelstePadAlgoritme;
import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Pad;
import io.gameoftrades.model.kaart.Richting;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.model.markt.Handel;
import io.gameoftrades.model.markt.HandelType;
import io.gameoftrades.model.markt.Handelswaar;
import io.gameoftrades.model.markt.Markt;
import io.gameoftrades.model.markt.actie.Actie;
import io.gameoftrades.model.markt.actie.NavigeerActie;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Fleur
 */
public class HandelsplanAlgoritmeImpl implements HandelsplanAlgoritme {
 List<Actie> actieList;
 SnelstePadAlgoritmeImpl snp = new SnelstePadAlgoritmeImpl();
 Pad pad;
    /**
     * Decision tree maakt een handelsplan dat zo veel mogelijk winst maakt in
     * zo min mogelijk tijd (opdracht 4).
     *
     * @param wereld de wereld waarvoor het handelsplan gemaakt moet worden.
     * @param hp de handelspositie
     * @return het plan.
     */
    @Override
    public Handelsplan bereken(Wereld wereld, HandelsPositie hp) {
        List<Actie> actieList = new ArrayList<>();
        Actie a = null;
        List<NavigeerActie> temp = new ArrayList<>();
        SnelstePadAlgoritmeImpl snp = new SnelstePadAlgoritmeImpl();
        //   HandelsPositie result = null;
        //System.out.println(hp.getRuimte());
        // System.out.println("handelspositie initial: " + hp.getMaxActie());
//        System.out.println("hp.actie " + hp.getMaxActie());

        Handelsplan hPlan = new Handelsplan(actieList);
        int tel = 0;
        for (int i = 0; i < hp.getMaxActie(); i++) { // go on until MaxActie is gone. We need to find a way to detect during the loop if we will run out of money/ refine the treshold of 0.
            tel++;
            if (hp.getMaxActie() == 1 || hp.isKlaar()) {
                System.out.println("last actie" + hp.getMaxActie());
                actieList.add(new StopActie());
                break;

            }
            if (hp.getKapitaal() <= 0 && hp.getVoorraad().isEmpty()) {
                actieList.add(new StopActie());
                break;

            }
            if (hp.getKapitaal() <= 0 && !hp.getVoorraad().isEmpty()) { // if there is voorraad but no more capital, it is time to sell. 
                System.out.println("TREE 3...");
                System.out.println("DESTINATION" + besteVerkoopBestemming(wereld, hp));

                 pad = snp.bereken(wereld.getKaart(), hp.getStad().getCoordinaat(), besteVerkoopBestemming(wereld, hp).getCoordinaat());
                BeweegActie bActie = new BeweegActie(wereld.getKaart(), hp.getStad(), besteVerkoopBestemming(wereld, hp), pad);

                actieList.addAll(bActie.naarNavigatieActies());

                a = new VerkoopActie(wereld.getMarkt().getHandel().get(i));

                if (a.isMogelijk(hp)) {
                    System.out.println("TRUE");
                    VerkoopActie actieVerkoop = new VerkoopActie(wereld.getMarkt().getHandel().get(i));
                    actieList.add(actieVerkoop);

                    hp = actieVerkoop.voerUit(hp);

                }

                //CHECK SNELPAD
                //CHECK BEST NEXT SELLING DEST
                //VERKOOP ACTIE
                //VOERUIT
//                actieList.addAll(new BeweegActie(wereld.getKaart(),
//                        hp.getStad(),
//                        besteVerkoopBestemming(wereld, hp),
//                        new SnelstePadAlgoritmeImpl().bereken(wereld.getKaart(), hp.getStad().getCoordinaat(), besteVerkoopBestemming(wereld, hp).getCoordinaat())).naarNavigatieActies());
//
////                for (int t = 0; i < temp.size(); t++) {
////                    temp.get(t).voerUit(hp);
////                }
//
//                for (Handel h : wereld.getMarkt().getVraag()) {
//                    if (h.getStad().equals(besteVerkoopBestemming(wereld, hp))) {
//                        actieList.add(a = new VerkoopActie(h));
//                        a.voerUit(hp);
//
//                    }
//                }
            }
            if (hp.getKapitaal() > 0 && hp.getVoorraad().isEmpty()) {
                System.out.println("TREE 4...");
                //CHECK SNELPAD
                //CHECK BEST NEXT BUYING DEST
                //KOOP ACTIE
                //VOERUIT

                 pad = snp.bereken(wereld.getKaart(), hp.getStad().getCoordinaat(), besteInkoopBestemming(wereld, hp).getStad().getCoordinaat());
                BeweegActie bActie = new BeweegActie(wereld.getKaart(), hp.getStad(), besteInkoopBestemming(wereld, hp).getStad(), pad);

                actieList.addAll(bActie.naarNavigatieActies());

                System.out.println("BUY dest. : " + besteInkoopBestemming(wereld, hp));
                a = new KoopActie(wereld.getMarkt().getHandel().get(i));

                if (a.isMogelijk(hp)) {
                    System.out.println("TRUE");
                    KoopActie actieKoop = new KoopActie(wereld.getMarkt().getHandel().get(i));
                    actieList.add(actieKoop);
                    System.out.println("NAV" + actieList.toString());

                    hp = actieKoop.voerUit(hp);
                    //System.out.println("kap: " + result.getKapitaal() + " /winst " + + result.getTotaalWinst() + " /getVoorraad " + result.getVoorraad().toString());
                    //result = hp;

                } else {
                    System.out.println("sell?");
                }

            } else if (hp.getKapitaal() > 0 && !hp.getVoorraad().isEmpty()) {
                //BUY OR SELL
            }

        }
        System.out.println("result outside IF:  " + hp);

        System.out.println("hp max" + hp.getMaxActie());
//                    System.out.println("hp max" + result.getMaxActie());
        System.out.println("tel:" + tel);
        System.out.println("HandelPLAN: ");
        System.out.println(actieList.toString());
        System.out.println("");

        return hPlan;
    }

    public void printactionList() {
        for (int i = 0; i < actieList.size(); i++) {
            System.out.println(actieList.get(i));

        }
    }

    /**
     *
     * @param wereld
     * @param hp huidige handelspositie
     * @return handel (met verwijzing naar stad)
     */
    public Handel besteInkoopBestemming(Wereld wereld, HandelsPositie hp) {

        int winst = 0;
        int maxWinst = 0;

        Handel handel = null;
        Handel a = null;
        Handel b = null;

        //determine the best vraag prijs - bied prijs - reiskost from current location
       // bepaal voor elke handel van type vraag de prijs en types
        for (Handel h : wereld.getMarkt().getVraag()) {
            winst = 0;
            
            handel = new Handel(h.getStad(), h.getHandelType(), h.getHandelswaar(), h.getPrijs());
            a = handel;
        
// bepaal voor elke handel j uit aanbod de prijs en types 
        
                for (Handel j : wereld.getMarkt().getAanbod()) {
                handel = new Handel(j.getStad(), j.getHandelType(), j.getHandelswaar(), j.getPrijs());
                b = handel;
                
       // vergelijk de winst tussen deze twee handels rekeninghoudend met reiskosten 
        winst = getWinstBetweenTwoHandels(wereld.getKaart(), a, b); 
// als de winst groter is dan de beste handelsroute tot nu toe, sla dan als bestemming deze handel op.
        if (winst > maxWinst) {
            maxWinst = winst;
            handel=b;
        }}}
        

        return handel;
    }
        //// copy from skype from this point \/ this will stay \/    /\ that will be replaced/\ 
    public Stad besteVerkoopBestemming(Wereld wereld, HandelsPositie hp) {

        List<Stad> stedenLijst = wereld.getSteden();
        int winst = 0;
        int maxWinst = 0;
        Stad destination = hp.getStad();
        
  
        for (Stad s : stedenLijst) {
            for (Handel h : wereld.getMarkt().getHandel()) {
                winst = 0; // for each city, check all the handels. if the handel corresponds to the city you are checkking, calculate winst. 
                if (h.getStad().equals(s)) {
                    winst = getWinst(wereld, hp.getStad(), h) * hp.getVoorraad().get(h.getHandelswaar())  - reisKost(wereld.getKaart(), hp.getStad(), h);
                    // de verkoopprijs * het aantal keer dat handelswaar in stock is - reiskosten. hp.getVoorraad().get(h.getHandelswaar()) gets the integer value at this key, which is h.getHandelswaar
                }
                if (winst > maxWinst) {
                    maxWinst = winst;
                    destination = s;
                }
            }

        }

        return destination;

    }

//  public int[][] reiskostenMatrix(Wereld wereld, HandelsPositie hp){  
//  // for each stad in steden, look up distance between this stad and the other steden 
//           for (int i = 0; i<wereld.getSteden().size();i++){
//                for (int j = 0; j<wereld.getSteden().size();j++){
//                    Pad pad;
//                    SnelstePadAlgoritme snp;
//              snp=  new SnelstePadAlgoritmeImpl();
//              pad = snp.bereken(wereld.getKaart(), wereld.getSteden().get(i).getCoordinaat(), wereld.getSteden().get(j).getCoordinaat());
//              
//                    reiskosten[i][j] =pad.getTotaleTijd();
//                }                    
//        }
//        return reiskosten;
//}
    private int getWinst(Wereld wereld, Stad a, Handel b) {

        // now we check if b is asking for anything
        if (b.getHandelType() == HandelType.BIEDT) {
            return 0;
        }

        return b.getPrijs();

    }

    private int getWinstBetweenTwoHandels(Kaart kaart, Handel a, Handel b) {
        return (b.getPrijs() - a.getPrijs()) - reisKost(kaart, a.getStad(), b);
    }

    private int reisKost(Kaart krt, Stad a, Handel b) {

        Pad pad;
        SnelstePadAlgoritme snp;
        snp = new SnelstePadAlgoritmeImpl();
        pad = snp.bereken(krt, a.getCoordinaat(), b.getStad().getCoordinaat());
        return pad.getTotaleTijd();
        // TO DO haal de reiskosten ook daadwerkelijk van de hp af. 
    }

}
