package vue_controleur;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TopInfoPanel extends JPanel {
    private ScorePanel scorePanel;
    private ScorePanel highScorePanel;

    public TopInfoPanel(String title) {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        // Titre
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("", Font.BOLD, 40));
        titleLabel.setForeground(Color.decode("#6B4B28"));
        titleLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
        this.add(titleLabel);

        // Score et High Score
        JPanel scorePane = new JPanel();

        this.scorePanel = new ScorePanel("Score", 0);
        scorePane.add(scorePanel);

        this.highScorePanel = new ScorePanel("Meilleur", 0);
        scorePane.add(highScorePanel);
        scorePane.setMaximumSize(scorePane.getPreferredSize());

        this.add(Box.createHorizontalGlue()); // Mettre un espace entre le titre et les scores
        this.add(scorePane);
    }

    public void setScore(int val) {
        this.scorePanel.setScoreLabel(val);
    }

    public void setHighscore(int val) {
        this.highScorePanel.setScoreLabel(val);
    }
}
