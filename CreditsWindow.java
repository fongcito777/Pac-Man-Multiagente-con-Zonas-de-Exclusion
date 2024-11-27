import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CreditsWindow {
    private JFrame frame;
    private JTextArea textArea;
    private JLabel picLabel;

    public CreditsWindow() {
        frame = new JFrame("Credits");
        textArea = new JTextArea(
                "Ingenieria en Sistemas y Graficas Computacionales \n\n" +
                        "Fundamentos de Programacion en Paralelo \n \n" +
                        "Integrantes: \n" +
                        "Jorge Alberto Fong Alvarez\n" +
                        "Pablo Armando Uscanga Camacho \n\n" +
                        "Profesor: \n" +
                        "Dr. Juan Carlos Lopez Pimentel\n" +
                        "Fecha: 27/11/2024"
        );
        textArea.setEditable(false); // Make the text area read-only

        try {
            BufferedImage myPicture = ImageIO.read(new File("upm.png"));
            picLabel = new JLabel(new ImageIcon(myPicture));
        } catch (IOException e) {
            e.printStackTrace();
            picLabel = new JLabel("Image not found");
        }

        frame.setLayout(new BorderLayout());
        frame.add(picLabel, BorderLayout.NORTH);
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
