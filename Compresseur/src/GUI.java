import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class GUI extends JPanel
        implements ActionListener {
    JButton ouvrirBouton;
    JFileChooser fc;

    private File fichier;
    private Compresseur compresseur=new Compresseur();

    public GUI() {
        super(new BorderLayout());

        fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.home")));

        ouvrirBouton = new JButton("Choisir...");
        ouvrirBouton.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(ouvrirBouton);
        add(buttonPanel, BorderLayout.PAGE_START);
    }

    public void actionPerformed(ActionEvent e) {

        //Pour le bouton ouvrir
        if (e.getSource() == ouvrirBouton) {
            int returnVal = fc.showOpenDialog(GUI.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                this.fichier = fc.getSelectedFile();
                compresseur.readFile(this.fichier);
                System.out.println(this.fichier);
            }
        }
    }

    public static void creerGUI() {

        JFrame frame = new JFrame("ChoixFichier");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(new GUI());

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        GUI gui=new GUI();
        gui.creerGUI();
    }
}