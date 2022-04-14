package modele;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Random;

public class Jeu extends Observable {

    private Case[][] tabCases;
    private HashMap<Case, Point> hashCases;
    private Etat etatJeu;
    private static int WIN_SCORE = 2048;

    public Jeu(int size) {
        tabCases = new Case[size][size];
        hashCases = new HashMap<Case, Point>();
        etatJeu = Etat.EnCours;
        depart();
    }

    public int getSize() {
        return tabCases.length;
    }

    public Point getCoordinate(Case caseObj) {
        return hashCases.get(caseObj);
    }

    public Case getCase(int i, int j) {
        if (IsInBound(i, j))
            return tabCases[i][j];
        else
            return new Case(-1, this); // Placeholder pour dire que c'est une bordure
    }

    public boolean IsInBound(int i, int j) {
        return i >= 0 && j >= 0 && i < tabCases.length && j < tabCases[i].length;
    }

    public Case getVoisin(Point currentCoord, Direction dir) {
        Point voisinCoord = getCoordVoisin(currentCoord, dir);
        return getCase(voisinCoord.x, voisinCoord.y);
    }

    public Point getCoordVoisin(Point currentCoord, Direction dir) {
        Point coordVoisin = null;
        switch (dir) {
            case haut:
                coordVoisin = new Point(currentCoord.x - 1, currentCoord.y);
                break;
            case bas:
                coordVoisin = new Point(currentCoord.x + 1, currentCoord.y);
                break;
            case gauche:
                coordVoisin = new Point(currentCoord.x, currentCoord.y - 1);
                break;
            case droite:
                coordVoisin = new Point(currentCoord.x, currentCoord.y + 1);
                break;
        }
        return coordVoisin;
    }

    public void AddCase(int valeur, int i, int j) {
        tabCases[i][j] = new Case(valeur, this);
        hashCases.put(tabCases[i][j], new Point(i, j));
    }

    /**
     * Déplace la case d'une "unité" vers la direction souhaité et supprime la case d'arrivée
     * @param dir Direction dans laquel la case va bouger
     * @param caseObj Case à déplacer
     */
    public void moveCase(Direction dir, Case caseObj) {
        Point currentCase = getCoordinate(caseObj);
        Point newCase = getCoordVoisin(currentCase, dir);

        if (IsInBound(newCase.x, newCase.y)) {
            // On enlève la case d'arrivée s'il y en a une
            if (tabCases[newCase.x][newCase.y] != null)
                hashCases.remove(tabCases[newCase.x][newCase.y]);

            hashCases.put(caseObj, newCase);
            tabCases[currentCase.x][currentCase.y] = null;
            tabCases[newCase.x][newCase.y] = caseObj;
        }
    }

    public void addRandomCase(int val) {
        int randI;
        int randJ;
        do {
            Random rand = new Random();
            randI = rand.nextInt(tabCases.length);
            randJ = rand.nextInt(tabCases[randI].length);
        } while (tabCases[randI][randJ] != null);

        AddCase(val, randI, randJ);
    }

    public boolean deplacementPossible() {
        boolean isPossible = false;
        for (Map.Entry<Case, Point> entry : hashCases.entrySet()) {
            Case c = entry.getKey();
            Point coord = entry.getValue();
            Case haut = getVoisin(coord, Direction.haut);
            Case bas = getVoisin(coord, Direction.bas);
            Case gauche = getVoisin(coord, Direction.gauche);
            Case droite = getVoisin(coord, Direction.droite);
            isPossible = isPossible
                || haut == null || c.getValeur() == haut.getValeur()
                || bas == null || c.getValeur() == bas.getValeur()
                || gauche == null || c.getValeur() == gauche.getValeur()
                || droite == null || c.getValeur() == droite.getValeur();
        }
        return isPossible;
    }

    public void depart() {
        new Thread() { // permet de libérer le processus graphique ou de la console
            public void run() {
                addRandomCase(2);
                addRandomCase(2);
                setChanged();
                notifyObservers();
            }
        }.start();
    }

    public void action(Direction dir) {
        new Thread() { // permet de libérer le processus graphique ou de la console
            public void run() {
                // TODO : Façon plus élégante
                boolean hasMoved = false;
                switch (dir) {
                    case haut:
                        for (int j = 0; j < tabCases[0].length; j++) {
                            for (int i = 1; i < tabCases.length; i++) {
                                if (tabCases[i][j] != null)
                                    hasMoved = tabCases[i][j].deplacer(dir) || hasMoved;
                            }
                        }
                        break;
                    case bas:
                        for (int j = 0; j < tabCases[0].length; j++) {
                            for (int i = tabCases.length - 2; i >= 0; i--) {
                                if (tabCases[i][j] != null)
                                    hasMoved = tabCases[i][j].deplacer(dir) || hasMoved;
                            }
                        }
                        break;
                    case droite:
                        for (int i = 0; i < tabCases.length; i++) {
                            for (int j = tabCases[0].length - 2; j >= 0; j--) {
                                if (tabCases[i][j] != null)
                                    hasMoved = tabCases[i][j].deplacer(dir) || hasMoved;
                            }
                        }
                        break;
                    case gauche:
                        for (int i = 0; i < tabCases.length; i++) {
                            for (int j = 1; j < tabCases[0].length; j++) {
                                if (tabCases[i][j] != null)
                                    hasMoved = tabCases[i][j].deplacer(dir) || hasMoved;
                            }
                        }
                        break;
                }

                if (hasMoved) {
                    // On fait apparaitre une nouvelle case : de valeur 2 ou 4
                    Random rand = new Random();
                    int valRand = rand.nextInt(2) == 1 ? 2 : 4;
                    addRandomCase(valRand);
                }

                if (!deplacementPossible()) {
                    etatJeu = Etat.Perdu;
                    System.out.println("Perdu !");
                }

                // TODO : Gagner partie si score atteint
                // if (bestScore == WIN_SCORE)
                // gamestate = State.won;

                // TODO : Comptabiliser score

                setChanged();
                notifyObservers();
            }

        }.start();


    }
}
