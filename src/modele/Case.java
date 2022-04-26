package modele;

import java.awt.*;

public class Case {
    private Jeu jeu;
    private int valeur;
    private Color couleur;
    private boolean fusionner;
    private static final Color[] COULEURS_CASES = {
            Color.decode("#eee4da"), // 2
            Color.decode("#ede0c8"), // 4
            Color.decode("#f2b179"), // 8
            Color.decode("#f59c64"), // 16
            Color.decode("#f67e5f"), // 32
            Color.decode("#f57144"), // 64
            Color.decode("#eed172"), // 128
            Color.decode("#edcc61"), // 256
            Color.decode("#edc850"), // 512
            Color.decode("#edc643"), // 1024
            Color.decode("#edc22e"), // 2048
            Color.decode("#86a83d"), // 4096
            Color.decode("#3fb687"), // 8192
            Color.decode("#3fb687"), // 16384
            Color.decode("#3e3c32"), // 32768
    };

    public Case(int _valeur, Jeu _jeu) {
        jeu = _jeu;
        fusionner = false;
        setValeur(_valeur);
    }

    public int getValeur() {
        return valeur;
    }

    /**
     * Change la valeur tout en changeant la couleur de la case
     * @param newVal La valeur de la case à changer
     */
    public void setValeur(int newVal) {
        valeur = newVal;
        int colorIndex = getExponentielValeur() - 1; // On veut l'index, qui commence à 0 et pas à 1
        if (colorIndex >= 0 && colorIndex < COULEURS_CASES.length)
            couleur = COULEURS_CASES[colorIndex];
        else
            couleur = COULEURS_CASES[0];
    }
    public Color getCouleur() {
        return couleur;
    }

    public boolean isFusionner() {
        return fusionner;
    }

    public void setFusionner(boolean fusionner) {
        this.fusionner = fusionner;
    }

    /**
     * Donne l'exponentiel de 2 par rapport à la valeur de la case, ex : val = 8 = 2³ donc exponentiel est 3
     * @return L'exponentiel de 2 correspondant à la valeur
     */
    public int getExponentielValeur() {
        int tempVal = valeur;
        int compteur = 0;
        while (tempVal > 1) {
            tempVal /= 2;
            compteur++;
        }
        return compteur;
    }

    public boolean deplacer(Direction dir) {
        boolean hasMoved = false;
        boolean IsFinish = false;
        while(!IsFinish) {
            Point currentCoord = jeu.getCoordinate(this);
            // On récupère le voisin de notre case (celui dans la bonne direction)
            Case voisinSelected = jeu.getVoisin(currentCoord, dir);

            // Il n'y a pas de voisin => case vide
            if (voisinSelected == null) {
                jeu.moveCase(dir, this);
                hasMoved = true;
            }
            // Le voisin à la même valeur que la case et n'a pas déjà été fusionné à ce tour
            else if (voisinSelected.getValeur() == this.valeur && !voisinSelected.isFusionner()) {
                setValeur(valeur*2);
                jeu.setScore(jeu.getScore() + valeur); // À chaque fusion, on ajoute le résultat au score
                if (jeu.getScore() > jeu.getHighScore()) jeu.setHighScore(jeu.getScore());
                jeu.moveCase(dir, this);
                hasMoved = true;
                IsFinish = true;
                this.fusionner = true;
            }
            // Le voisin est une bordure (valeur de -1), le voisin à une valeur différente de la case ou la case ne peut pas fusionner car le voisin l'a déjà été
            else {
                IsFinish = true;
            }
        }

        return hasMoved;
    }
}
