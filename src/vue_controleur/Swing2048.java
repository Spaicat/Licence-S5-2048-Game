package vue_controleur;

import modele.Case;
import modele.Direction;
import modele.Etat;
import modele.Jeu;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

public class Swing2048 extends JFrame implements Observer {
    private static final int PIXEL_PER_SQUARE = 100;
    // tableau de cases : i, j -> case graphique
    private JLabel[][] tabC;
    private TopInfoPanel topInfoPanel;
    private JPanel gamePane;
    private Jeu jeu;

    public Swing2048(Jeu _jeu) {
        jeu = _jeu;
        setTitle("2048");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(jeu.getSize() * PIXEL_PER_SQUARE, jeu.getSize() * PIXEL_PER_SQUARE);
        setLocationRelativeTo(null); // Placer au centre de l'écran

        JPanel mainPane = (JPanel) getContentPane();

        /* ----- Informations (score, record ...) ----- */
        double titleValue = 128*Math.pow(2, jeu.getSize());
        this.topInfoPanel = new TopInfoPanel("" + (int)titleValue);
        mainPane.add(topInfoPanel, BorderLayout.NORTH);

        /* ----- Plateau 2048 ----- */
        this.gamePane = new JPanel(new BorderLayout());
        initBoard();
        mainPane.add(gamePane);

        // Démarre les processus
        setContentPane(mainPane);
        ajouterEcouteurClavier();
        rafraichir();
    }

    public void initBoard() {
        JPanel boardPane = new JPanel(new GridLayout(jeu.getSize(), jeu.getSize()));

        this.tabC = new JLabel[jeu.getSize()][jeu.getSize()];
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
        gamePane.add(boardPane, BorderLayout.CENTER);
    }

    /**
     * Correspond à la fonctionnalité de Vue : affiche les données du modèle
     */
    private void rafraichir()  {
        Swing2048 that = this;
        SwingUtilities.invokeLater(new Runnable() { // demande au processus graphique de réaliser le traitement
            @Override
            public void run() {
                if (jeu.getEtatJeu() == Etat.Perdu) {

                    // Menu pour recommencer
                    JPanel popup = new JPanel();
                    popup.setLayout(new GridBagLayout());

                    JLabel popupText = new JLabel("Vous avez perdu !", SwingConstants.CENTER);
                    popupText.setOpaque(true);

                    JButton popupBtn = new JButton("Recommencer");
                    popupBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            gamePane.removeAll();
                            jeu = new Jeu(jeu.getSize());
                            initBoard();
                            jeu.addObserver(that);
                            rafraichir();
                        }
                    });

                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.gridwidth = GridBagConstraints.REMAINDER;
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    popup.add(popupText, gbc);
                    popup.add(popupBtn, gbc);
                    popup.setOpaque(true);

                    gamePane.removeAll();
                    gamePane.add(popup);
                    gamePane.revalidate();
                }
                else {
                    for (int i = 0; i < jeu.getSize(); i++) {
                        for (int j = 0; j < jeu.getSize(); j++) {
                            Case c = jeu.getCase(new Point(i, j));

                            if (c == null) {
                                tabC[i][j].setText("");
                                tabC[i][j].setBackground(Color.decode("#cdc1b4"));
                            } else {
                                tabC[i][j].setText(c.getValeur() + "");
                                tabC[i][j].setBackground(c.getCouleur());
                            }
                        }
                    }
                }
                topInfoPanel.setScore(jeu.getScore());
                topInfoPanel.setHighscore(jeu.getHighScore());
            }
        });
    }

    /**
     * Implémentation d'une classe abstraite action qui correspond au controleur
     */
    private class MoveAction extends AbstractAction {
        Direction direction;
        MoveAction(Direction direction) {
            this.direction = direction;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            jeu.action(direction);
        }
    }
    /**
     * Correspond à la fonctionnalité de Contrôleur : écoute les évènements, et déclenche des traitements sur le modèle
     */
    private void ajouterEcouteurClavier() {
        String MOVE_UP = "move up";
        String MOVE_DOWN = "move down";
        String MOVE_LEFT = "move left";
        String MOVE_RIGHT = "move right";

        // Utilisation de keybindings au lieu de keylisteners car sinon cela pose des problèmes de focus (quand on change de JPanel)
        this.gamePane.getInputMap().put(KeyStroke.getKeyStroke("UP"), MOVE_UP);
        this.gamePane.getActionMap().put(MOVE_UP, new MoveAction(Direction.haut));

        this.gamePane.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), MOVE_DOWN);
        this.gamePane.getActionMap().put(MOVE_DOWN, new MoveAction(Direction.bas));

        this.gamePane.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), MOVE_LEFT);
        this.gamePane.getActionMap().put(MOVE_LEFT, new MoveAction(Direction.gauche));

        this.gamePane.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), MOVE_RIGHT);
        this.gamePane.getActionMap().put(MOVE_RIGHT, new MoveAction(Direction.droite));
    }

    @Override
    public void update(Observable o, Object arg) {
        rafraichir();
    }
}