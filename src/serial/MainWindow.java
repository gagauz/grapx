package serial;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.beans.Beans;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainWindow {

    private JFrame frame;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    MainWindow window = new MainWindow();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public MainWindow() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        BorderLayout borderLayout = (BorderLayout) frame.getContentPane().getLayout();
        borderLayout.setVgap(5);
        borderLayout.setHgap(5);
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel.getLayout();
        flowLayout.setHgap(10);
        flowLayout.setAlignment(FlowLayout.LEFT);
        frame.getContentPane().add(panel, BorderLayout.NORTH);

        JLabel lblSerialPort = new JLabel("Serial port");
        panel.add(lblSerialPort);

        JComboBox comboBox = new JComboBox();
        if (!Beans.isDesignTime()) {
            comboBox.setModel(new DefaultComboBoxModel(SerialTest.getAvailablePorts()));
        }
        panel.add(comboBox);

        JButton btnConnect = new JButton("Connect");
        panel.add(btnConnect);
    }

}
