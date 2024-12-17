package main;

import javax.swing.SwingUtilities;

import vue.BibliothequeApp;

public class Main {
    public static void main(String[] args) {
        // Lancement de l'interface graphique
        SwingUtilities.invokeLater(() -> {
            new BibliothequeApp().setVisible(true);
        });
    }
}
