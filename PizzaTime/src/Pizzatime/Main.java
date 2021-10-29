package Pizzatime;

import java.awt.*;
import java.awt.EventQueue;

import javax.swing.*;
 class Main extends JFrame {
	 
	  public Main() {

	        initUI();
	    }

	    private void initUI() {

	        add(new Model());

	        setTitle("Pizza Time");
	        setDefaultCloseOperation(EXIT_ON_CLOSE);
	        setSize(800	, 600);
	        setLocationRelativeTo(null);
	    }

	    public static void main(String[] args) {

	        EventQueue.invokeLater(() -> {

	            var ex = new Main();
	            ex.setVisible(true);
	        });

}
 }
 
