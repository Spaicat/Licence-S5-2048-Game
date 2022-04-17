package vue_controleur;

import modele.Case;
import modele.Direction;
import modele.Jeu;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

public class Console2048 extends Thread implements Observer {

    private Jeu jeu;



    public Console2048(Jeu _jeu) {
        jeu = _jeu;
    }


    @Override
    public void run() {
        while(true) {
            afficher();

            synchronized (this) {
                ecouteEvennementClavier();
                try {
                    wait(); // lorsque le processus s'endort, le verrou sur this est relâché, ce qui permet au processus de ecouteEvennementClavier()
                    // d'entrer dans la partie synchronisée, ce verrou évite que le réveil du processus de la console (update(..)) ne soit exécuté avant
                    // que le processus de la console ne soit endormi

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Correspond à la fonctionnalité de Contrôleur : écoute les évènements, et déclenche des traitements sur le modèle
     */
    private void ecouteEvennementClavier() {

        final Object _this = this;

        new Thread() {
            public void run() {

                synchronized (_this) {
                    boolean end = false;

                    while (!end) {
                        String s = null;
                        try {
                            s = Character.toString((char)System.in.read());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        switch (Objects.requireNonNull(s)) {
                            case "8": jeu.action(Direction.haut); end = true; break;
                            case "2": jeu.action(Direction.bas); end = true; break;
                            case "4": jeu.action(Direction.gauche); end = true; break;
                            case "6": jeu.action(Direction.droite); end = true; break;
                        }
                    }
                }
            }
        }.start();
    }

    public static String centerString (int width, String s) {
        return String.format("%-" + width  + "s", String.format("%" + (s.length() + (width - s.length()) / 2) + "s", s));
    }

    private void printSeparateLine() {
        for (int j = 0; j < jeu.getSize(); j++) {
            System.out.print("——————————");
        }
        System.out.println("—");
    }

    private void printScore() {
        String scoreString = "Score : " + jeu.getScore() + " — Meilleur : " + jeu.getHighScore();
        System.out.println(centerString(10*jeu.getSize(), scoreString));
    }


    /**
     * Correspond à la fonctionnalité de Vue : affiche les données du modèle
     */
    private void afficher()  {
        System.out.print("\033[H\033[J"); // permet d'effacer la console (ne fonctionne pas toujours depuis la console de l'IDE)

        printScore();
        printSeparateLine();
        for (int i = 0; i < jeu.getSize(); i++) {
            for (int j = 0; j < jeu.getSize(); j++) {
                Case c = jeu.getCase(new Point(i, j));
                String out = "-";
                if (c != null) {
                    //System.out.format("%5.5s", c.getValeur());
                    out = "" + c.getValeur();
                }
                System.out.print("|" + centerString(9, out));
            }
            System.out.println("|");
            printSeparateLine();
        }
    }

    private void raffraichir() {
        synchronized (this) {
            try {
                notify();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void update(Observable o, Object arg) {
        raffraichir();
    }
}
