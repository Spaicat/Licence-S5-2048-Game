import modele.Jeu;
import vue_controleur.Console2048;
import vue_controleur.Swing2048;

public class Main {
    public static void main(String[] args) {
        //mainConsole();
        mainSwing();
    }

    public static void mainConsole() {
        Jeu jeu = new Jeu(4);
        Console2048 vue = new Console2048(jeu);
        jeu.addObserver(vue);

        vue.start();

    }

    public static void mainSwing() {
        Jeu jeu = new Jeu(4);
        Swing2048 vue = new Swing2048(jeu);
        jeu.addObserver(vue);

        vue.setVisible(true);
    }
}
