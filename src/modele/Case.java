package modele;

import java.awt.*;

public class Case {
    private Jeu jeu;
    private int valeur;
    private Color couleur;
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
        setValeur(_valeur);
    }

    public int getValeur() {
        return valeur;
    }
    public void setValeur(int newVal) {
        valeur = newVal;
        int colorIndex = getExponentielValeur() - 1;
        if (colorIndex >= 0 && colorIndex < COULEURS_CASES.length)
            couleur = COULEURS_CASES[getExponentielValeur() - 1];
        else
            couleur = COULEURS_CASES[0];
    }
    public Color getCouleur() {
        return couleur;
    }
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

            if (voisinSelected == null) {
                jeu.moveCase(dir, this);
                hasMoved = true;
            }
            else if (voisinSelected.getValeur() == this.valeur) {
                setValeur(valeur*2);
                // La fonction supprime la case d'arrivée
                // TODO : La fonction ne supprime pas la case dans la table de hashage (que dans le tableau)
                jeu.moveCase(dir, this);
                hasMoved = true;
                IsFinish = true; // TODO : Vérifier si necessaire
            }
            else if (voisinSelected.getValeur() == -1 || voisinSelected.getValeur() != this.valeur) {
                IsFinish = true;
            }
            else {
                voisinSelected.deplacer(dir);
            }
        }

        return hasMoved;
    }
}
