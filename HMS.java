import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
 public class HMS extends JFrame implements ActionListener {
    JLabel l1, l2;
    JTextField tf1;
    JPasswordField pf2;
    JButton b1, b2;
    HMS() {
        setTitle("Login Form");
        setVisible(true);
        setSize(500, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));
         l1 = new JLabel("Username", SwingConstants.CENTER);
        l2 = new JLabel("Password", SwingConstants.CENTER);
        tf1 = new JTextField();
        pf2 = new JPasswordField();
        b1 = new JButton("Login");
        b2 = new JButton("Clear");
         add(l1);
        add(tf1);
        add(l2);
        add(pf2);
        add(b1);
        add(b2);
         // Add action listener to buttons
        b1.addActionListener(this);
        b2.addActionListener(this);
    }
     public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == b1) {
            String username = tf1.getText();
            String password = new String(pf2.getPassword());
             try {
                // Connect to MySQL database
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospitalmanagement", "root", "");
                 // Create statement object
                Statement stmt = con.createStatement();
                 // Execute SQL query to verify user
                ResultSet rs = stmt.executeQuery("SELECT * FROM login WHERE NAME='" + username + "' AND PASSWORD ='" + password + "'");
                 if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login successful");
                    dispose(); // Close login window
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password");
                }
                 // Clean up resources
                rs.close();
                stmt.close();
                con.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        } else if (ae.getSource() == b2) {
            tf1.setText("");
            pf2.setText("");
        }
    }
     public static void main(String[] args) {
        new HMS();
    }
}
