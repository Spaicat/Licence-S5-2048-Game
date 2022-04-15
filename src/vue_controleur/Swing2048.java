package vue_controleur;

import modele.Case;
import modele.Direction;
import modele.Jeu;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class Swing2048 extends JFrame implements Observer {
    private static final int PIXEL_PER_SQUARE = 100;
    // tableau de cases : i, j -> case graphique
    private JLabel[][] tabC;
    private ScorePanel scorePanel;
    private ScorePanel highScorePanel;
    private Jeu jeu;

    public Swing2048(Jeu _jeu) {
        jeu = _jeu;
        setTitle("2048");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(jeu.getSize() * PIXEL_PER_SQUARE, jeu.getSize() * PIXEL_PER_SQUARE);
        setLocationRelativeTo(null); // Placer au centre de l'écran

        JPanel mainPane = (JPanel) getContentPane();

        /* ----- Informations (score, record ...) ----- */
        JPanel infoPane = new JPanel();
        infoPane.setLayout(new BoxLayout(infoPane, BoxLayout.X_AXIS));
        // Titre
        JLabel titleLabel = new JLabel("2048");
        titleLabel.setFont(new Font("Montserrat Semibold", Font.PLAIN, 36));
        titleLabel.setForeground(Color.decode("#6B4B28"));
        titleLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
        infoPane.add(titleLabel);

        JPanel scorePane = new JPanel();
        // Score
        this.scorePanel = new ScorePanel("Score", 0);
        scorePane.add(scorePanel);
        // Record
        this.highScorePanel = new ScorePanel("Best", 0);
        scorePane.add(highScorePanel);
        scorePane.setMaximumSize(scorePane.getPreferredSize());

        infoPane.add(Box.createHorizontalGlue()); // Mettre un espace entre le titre et les scores
        infoPane.add(scorePane);

        mainPane.add(infoPane, BorderLayout.NORTH);

        /* ----- Plateau 2048 ----- */
        JPanel boardPane = new JPanel(new GridLayout(jeu.getSize(), jeu.getSize()));

        tabC = new JLabel[jeu.getSize()][jeu.getSize()];
        for (int i = 0; i < jeu.getSize(); i++) {
            for (int j = 0; j < jeu.getSize(); j++) {
                Border border = BorderFactory.createLineBorder(Color.decode("#bbada0"), 5);
                tabC[i][j] = new JLabel();
                tabC[i][j].setOpaque(true);
                tabC[i][j].setBackground(Color.decode("#cdc1b4"));
                tabC[i][j].setBorder(border);
                tabC[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                tabC[i][j].setFont(new Font("Montserrat", Font.BOLD, 14));

                boardPane.add(tabC[i][j]);
            }
        }
        mainPane.add(boardPane);

        // Démarre les processus
        setContentPane(mainPane);
        ajouterEcouteurClavier();
        rafraichir();
    }

    /**
     * Correspond à la fonctionnalité de Vue : affiche les données du modèle
     */
    private void rafraichir()  {
        SwingUtilities.invokeLater(new Runnable() { // demande au processus graphique de réaliser le traitement
            @Override
            public void run() {
                for (int i = 0; i < jeu.getSize(); i++) {
                    for (int j = 0; j < jeu.getSize(); j++) {
                        Case c = jeu.getCase(new Point(i, j));

                        if (c == null) {
                            tabC[i][j].setText("");
                            tabC[i][j].setBackground(Color.decode("#cdc1b4"));
                        }
                        else {
                            tabC[i][j].setText(c.getValeur() + "");
                            tabC[i][j].setBackground(c.getCouleur());
                        }
                    }
                }
                scorePanel.setScoreLabel(jeu.getScore());
            }
        });
    }

    /**
     * Correspond à la fonctionnalité de Contrôleur : écoute les évènements, et déclenche des traitements sur le modèle
     */
    private void ajouterEcouteurClavier() {
        addKeyListener(new KeyAdapter() { // new KeyAdapter() { ... } est une instance de classe anonyme, il s'agit d'un objet qui correspond au controleur dans MVC
            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {  // on regarde quelle touche a été pressée
                    case KeyEvent.VK_LEFT : jeu.action(Direction.gauche); break;
                    case KeyEvent.VK_RIGHT : jeu.action(Direction.droite); break;
                    case KeyEvent.VK_DOWN : jeu.action(Direction.bas); break;
                    case KeyEvent.VK_UP : jeu.action(Direction.haut); break;
                }
            }
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        rafraichir();
    }
}