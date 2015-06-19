package serial;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import serial.SerialTest.PortWrapper;

public class MainWindow {

    private JFrame frame;
    private SerialTest currentListener;

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

        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        frame.setContentPane(contentPanel);
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (null != currentListener) {
                    currentListener.close();
                }
                System.exit(0);
            }
        });
        contentPanel.setLayout(new BorderLayout(0, 0));

        JPanel panelHeader = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panelHeader.getLayout();
        flowLayout.setHgap(10);
        flowLayout.setAlignment(FlowLayout.LEFT);
        contentPanel.add(panelHeader, BorderLayout.NORTH);

        JPanel panelText = new JPanel();
        contentPanel.add(panelText);

        JLabel lblSerialPort = new JLabel("Serial port");
        panelHeader.add(lblSerialPort);

        final JComboBox serialComboBox = new JComboBox();
        serialComboBox.setModel(new DefaultComboBoxModel(SerialTest.getAvailablePorts()));
        panelHeader.add(serialComboBox);

        final JComboBox baudRateComboBox = new JComboBox();
        baudRateComboBox.setModel(new DefaultComboBoxModel(SerialTest.getBaudRates()));
        baudRateComboBox.setSelectedItem(9600);
        panelHeader.add(baudRateComboBox);

        JButton btnConnect = new JButton("Connect");
        panelHeader.add(btnConnect);
        panelText.setLayout(new BorderLayout(0, 0));

        final JTextArea textArea = new JTextArea();
        // panelText.add(textArea);

        JScrollPane scrollPane = new JScrollPane(textArea);
        panelText.add(scrollPane, BorderLayout.CENTER);

        textArea.setRows(10);
        textArea.setEditable(false);
        textArea.setBorder(new EmptyBorder(5, 5, 5, 5));
        textArea.setAutoscrolls(true);

        btnConnect.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(e.getActionCommand());
                System.out.println(e.getSource());
                PortWrapper portWrapper = (PortWrapper) serialComboBox.getSelectedItem();
                Integer baudRate = (Integer) baudRateComboBox.getSelectedItem();
                System.out.println(portWrapper);
                System.out.println(baudRate);
                if (null != portWrapper && null != baudRate) {
                    System.out.println(portWrapper);
                    if (null != currentListener) {
                        currentListener.close();
                    }
                    currentListener = SerialTest.initialize(portWrapper, baudRate, textArea);
                }
            }
        });

    }
}
