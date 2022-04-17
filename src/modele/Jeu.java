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
    private int score;
    private int highScore;
    private GestionFichier gestionFile;
    private static int WIN_SCORE = 2048;

    public Jeu(int size) {
        this.gestionFile = new GestionFichier("data", "highScore");
        HashMap<String, String> dataFile = gestionFile.getDataFile();

        this.highScore = 0;
        if (dataFile.size() != 0) {
            String rawHighScore = dataFile.get("highScore");
            if (!rawHighScore.equals(""))
                this.highScore = Integer.parseInt(rawHighScore);
        }

        this.tabCases = new Case[size][size];
        this.hashCases = new HashMap<>();
        this.etatJeu = Etat.EnCours;
        this.score = 0;
        depart();
    }

    public int getSize() {
        return tabCases.length;
    }

    public Point getCoordinate(Case caseObj) {
        return hashCases.get(caseObj);
    }

    public Case getCase(Point coord) {
        if (IsInBound(coord))
            return tabCases[coord.x][coord.y];
        else
            return new Case(-1, this); // Placeholder pour dire que c'est une bordure
    }

    public boolean IsInBound(Point coord) {
        return coord.x >= 0 && coord.y >= 0 && coord.x < tabCases.length && coord.y < tabCases[coord.x].length;
    }

    public Case getVoisin(Point currentCoord, Direction dir) {
        Point voisinCoord = getCoordVoisin(currentCoord, dir);
        return getCase(voisinCoord);
    }

    public Point getCoordVoisin(Point currentCoord, Direction dir) {
        return switch (dir) {
            case haut -> new Point(currentCoord.x - 1, currentCoord.y);
            case bas -> new Point(currentCoord.x + 1, currentCoord.y);
            case gauche -> new Point(currentCoord.x, currentCoord.y - 1);
            case droite -> new Point(currentCoord.x, currentCoord.y + 1);
        };
    }
    public Etat getEtatJeu() {
        return etatJeu;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int val) {
        score = val;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int val) {
        highScore = val;
    }
    public void AddCase(int valeur, Point coord) {
        tabCases[coord.x][coord.y] = new Case(valeur, this);
        hashCases.put(tabCases[coord.x][coord.y], coord);
    }

    /**
     * Déplace la case d'une "unité" vers la direction souhaité et supprime la case d'arrivée
     * @param dir Direction dans laquel la case va bouger
     * @param caseObj Case à déplacer
     */
    public void moveCase(Direction dir, Case caseObj) {
        Point currentCase = getCoordinate(caseObj);
        Point newCase = getCoordVoisin(currentCase, dir);

        if (IsInBound(newCase)) {
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

        AddCase(val, new Point(randI, randJ));
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
                    if (score >= highScore)
                        gestionFile.saveDataFile("highScore", "" + highScore);
                }

                if (!deplacementPossible()) {
                    etatJeu = Etat.Perdu;
                }

                // TODO : Gagner partie si score atteint
                // if (bestScore == WIN_SCORE)
                // etatJeu = Etat.Gagnee;

                setChanged();
                notifyObservers();
            }

        }.start();
    }
}
