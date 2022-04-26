package vue_controleur;

import modele.Jeu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu2048 extends JFrame {
    private JSpinner gameSizeSpinner;
    public Menu2048() {
        setTitle("2048 Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setForeground(Color.decode("#6B4B28"));
        setLocationRelativeTo(null); // Placer au centre de l'écran

        // Initialisation panel principal
        JPanel mainPane = (JPanel) getContentPane();
        mainPane.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Titre
        JLabel titleLabel = new JLabel("2048 Game");
        titleLabel.setFont(new Font("Montserrat Black", Font.PLAIN, 36));
        titleLabel.setForeground(Color.decode("#6B4B28"));
        titleLabel.setBorder(new EmptyBorder(0, 20, 20, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPane.add(titleLabel, gbc);

        // Menu avec les boutons
        JPanel selectionPane = new JPanel();
        selectionPane.setLayout(new BoxLayout(selectionPane, BoxLayout.Y_AXIS));
        selectionPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel pour la selection de la taille
        JPanel selectSizePanel = new JPanel();
        JLabel selectSizeLabel = new JLabel("Choisir la taille du jeu :");
        SpinnerModel model = new SpinnerNumberModel(4, 2, 10, 1);
        this.gameSizeSpinner = new JSpinner(model);

        selectSizePanel.add(selectSizeLabel);
        selectSizePanel.add(this.gameSizeSpinner);
        selectSizePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Bouton jeu
        JButton jouerBtn = new JButton("Jouer");
        jouerBtn.addActionListener(new PlayListener(this));
        jouerBtn.setFont(new Font("Montserrat", Font.BOLD, 16));
        jouerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Bouton quitter
        JButton quitterBtn = new JButton("Quitter");
        quitterBtn.addActionListener(new CloseListener());
        quitterBtn.setFont(new Font("Montserrat", Font.BOLD, 16));
        quitterBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Rassemblement des elements du menu
        selectionPane.add(selectSizePanel);
        selectionPane.add(jouerBtn);
        selectionPane.add(Box.createRigidArea(new Dimension(5, 5)));
        selectionPane.add(quitterBtn);
        mainPane.add(selectionPane, gbc);

        // On affiche
        setContentPane(mainPane);
        this.setVisible(true);
    }

    public int getGameSize() {
        return (Integer) this.gameSizeSpinner.getValue();
    }

    private class PlayListener implements ActionListener {
        private Menu2048 menu;
        public PlayListener(Menu2048 menu) {
            super();
            this.menu = menu;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            mainSwing(this.menu.getGameSize());
            this.menu.dispose();
        }
    }

    private class CloseListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    public void mainSwing(int size) {
        Jeu jeu = new Jeu(size);
        Swing2048 vue = new Swing2048(jeu);
        jeu.addObserver(vue);

        vue.setVisible(true);
    }
}
