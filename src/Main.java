/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author franc
 */
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        QuadroKanban model         = new QuadroKanban();
        KanbanController controller = new KanbanController(model);
        SwingUtilities.invokeLater(() -> new KanbanUI(controller));
    }
}
