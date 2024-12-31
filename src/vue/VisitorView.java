package vue;

import javax.swing.*;
import controllers.LivreController;
import model.Livre;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.stream.Collectors;

public class VisitorView extends JFrame {
    private static final long serialVersionUID = 1L;
    private LivreController livreController;
    private JPanel booksPanel;
    private JTextField searchField;
    private JComboBox<String> searchCriteriaComboBox;

    public VisitorView(BibliothequeApp app) {
        livreController = new LivreController(null, null, null);
        setTitle("Livres Disponibles");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panneau pour afficher les livres
        booksPanel = new JPanel(new GridLayout(0, 4, 10, 10));
        JScrollPane scrollPane = new JScrollPane(booksPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Barre de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchField = new JTextField(20);
        searchCriteriaComboBox = new JComboBox<>(new String[]{"Titre", "Auteur", "Genre"});
        JButton searchButton = new JButton("Rechercher");

        searchPanel.add(new JLabel("Rechercher par :"));
        searchPanel.add(searchCriteriaComboBox);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        // Action de recherche
        searchButton.addActionListener(e -> updateBookDisplay());

        // Ajouter un bouton pour retourner à la page de connexion
        JButton backButton = new JButton("Retour à la Connexion");
        backButton.setBackground(new Color(60, 179, 113));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            new LoginView(app).setVisible(true);
            dispose();
        });
        add(backButton, BorderLayout.SOUTH);

        // Charger les livres initiaux
        updateBookDisplay();
    }

    private void updateBookDisplay() {
        booksPanel.removeAll();
        List<Livre> livres = livreController.getAllLivres();

        // Filtrer les livres en fonction de la recherche
        String searchCriteria = (String) searchCriteriaComboBox.getSelectedItem();
        String searchText = searchField.getText().trim().toLowerCase();

        if (!searchText.isEmpty()) {
            livres = livres.stream()
                    .filter(livre -> {
                        switch (searchCriteria) {
                            case "Titre":
                                return livre.getTitre().toLowerCase().contains(searchText);
                            case "Auteur":
                                return livre.getAuteur().toLowerCase().contains(searchText);
                            case "Genre":
                                return livre.getGenre().toLowerCase().contains(searchText);
                            default:
                                return false;
                        }
                    })
                    .collect(Collectors.toList());
        }

        for (Livre livre : livres) {
            JPanel bookPanel = createBookPanel(livre);
            booksPanel.add(bookPanel);
        }

        booksPanel.revalidate();
        booksPanel.repaint();
    }

    private JPanel createBookPanel(Livre livre) {
        JPanel bookPanel = new JPanel(new BorderLayout());
        bookPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        ImageIcon bookImage = new ImageIcon(new ImageIcon(livre.getImageUrl()).getImage().getScaledInstance(100, 150, Image.SCALE_SMOOTH));
        JLabel imageLabel = new JLabel(bookImage);

        JLabel titleLabel = new JLabel(livre.getTitre());
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        bookPanel.add(imageLabel, BorderLayout.CENTER);
        bookPanel.add(titleLabel, BorderLayout.SOUTH);

        bookPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showBookDetails(livre);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                bookPanel.setBackground(new Color(173, 216, 230));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                bookPanel.setBackground(null);
            }
        });

        return bookPanel;
    }

    private void showBookDetails(Livre livre) {
        JDialog detailsDialog = new JDialog(this, "Détails du Livre", true);
        detailsDialog.setLayout(new BorderLayout());

        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setText("Titre : " + livre.getTitre() + "\n" +
                            "Auteur : " + livre.getAuteur() + "\n" +
                            "Année : " + livre.getAnneePublication() + "\n" +
                            "ISBN : " + livre.getIsbn() + "\n" +
                            "Description : " + livre.getDescription() + "\n" +
                            "Disponible : " + (livre.isDisponible() ? "Oui" : "Non"));
        detailsDialog.add(new JScrollPane(detailsArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton borrowButton = new JButton("Emprunter");
        borrowButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, "Voulez-vous vous connecter pour emprunter ce livre?", "Emprunter Livre", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                new LoginView(new BibliothequeApp()).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez voir un bibliothécaire pour emprunter ce livre.");
            }
        });

        JButton closeButton = new JButton("Fermer");
        closeButton.addActionListener(e -> detailsDialog.dispose());

        buttonPanel.add(borrowButton);
        buttonPanel.add(closeButton);
        detailsDialog.add(buttonPanel, BorderLayout.SOUTH);

        detailsDialog.setSize(400, 300);
        detailsDialog.setLocationRelativeTo(this);
        detailsDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VisitorView(new BibliothequeApp()).setVisible(true));
    }
}
