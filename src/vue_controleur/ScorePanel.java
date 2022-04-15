package vue_controleur;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ScorePanel extends JPanel {
    private JLabel scoreLabel;

    public ScorePanel(String name, int score) {
        JPanel scorePane = new JPanel();
        scorePane.setLayout(new BoxLayout(scorePane, BoxLayout.Y_AXIS));
        scorePane.setBackground(Color.decode("#cdc1b4"));
        scorePane.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel scoreTitle = new JLabel(name);
        scoreTitle.setFont(new Font("Montserrat", Font.BOLD, 14));
        scoreTitle.setForeground(Color.decode("#6B4B28"));
        scoreTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.scoreLabel = new JLabel("" + score);
        scoreLabel.setFont(new Font("Montserrat", Font.BOLD, 20));
        scoreLabel.setForeground(Color.decode("#6B4B28"));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        scorePane.add(scoreTitle);
        scorePane.add(scoreLabel);

        this.add(scorePane);
    }

    public void setScoreLabel(int val) {
        this.scoreLabel.setText("" + val);
    }
}
