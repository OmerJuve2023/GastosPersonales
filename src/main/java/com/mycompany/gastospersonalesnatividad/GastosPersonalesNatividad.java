
package com.mycompany.gastospersonalesnatividad;

import controller.ExpenseController;
import javax.swing.SwingUtilities;

public class GastosPersonalesNatividad {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ExpenseController controller = new ExpenseController();
            controller.run();
        });
    }
}
