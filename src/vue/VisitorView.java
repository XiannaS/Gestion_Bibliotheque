package vue;

import javax.swing.*;
import controllers.LivreController;
import model.Livre;

import java.awt.*;
import java.util.List;

public class VisitorView extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LivreController livreController;

    public VisitorView(BibliothequeApp app) { // Accepter une instance de BibliothequeApp
        livreController = new LivreController("C:/Eclipse/gestionbibli/src/main/resources/ressources/books.csv");
        setTitle("Livres Disponibles");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panneau pour afficher les livres
        JPanel booksPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        JScrollPane scrollPane = new JScrollPane(booksPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Charger les livres et les afficher
        List<Livre> livres = livreController.getAllLivres();
        for (Livre livre : livres) {
            JButton bookButton = new JButton(livre.getTitre());
            bookButton.addActionListener(e -> {
                // Afficher les détails du livre
                JOptionPane.showMessageDialog(this, livre.toString(), "Détails du Livre", JOptionPane.INFORMATION_MESSAGE);
            });
            booksPanel.add(bookButton);
        }

        // Ajouter un bouton pour retourner à la page de connexion
        JButton backButton = new JButton("Retour à la Connexion");
        backButton.addActionListener(e -> {
            new LoginView(app).setVisible(true); // Passer l'instance de BibliothequeApp
            dispose(); // Fermer la vue des visiteurs
        });
        add(backButton, BorderLayout.SOUTH);
    }
}