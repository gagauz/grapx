package org.gagauz.playground.serial;

import org.gagauz.playground.serial.SerialTest.PortWrapper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
        frame.setBounds(100, 100, 600, 400);
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

        JPanel pnHead = new JPanel();
        pnHead.setLayout(new BorderLayout());

        JPanel pnConnect = new JPanel();
        FlowLayout flowLayout = (FlowLayout) pnConnect.getLayout();
        flowLayout.setHgap(10);
        flowLayout.setAlignment(FlowLayout.LEFT);
        contentPanel.add(pnHead, BorderLayout.NORTH);
        pnHead.add(pnConnect, BorderLayout.NORTH);

        JLabel lblSerialPort = new JLabel("Serial port");
        pnConnect.add(lblSerialPort);

        final JComboBox cbSerial = new JComboBox();
        cbSerial.setModel(new DefaultComboBoxModel(SerialTest.getAvailablePorts()));
        pnConnect.add(cbSerial);

        final JComboBox cbDataRate = new JComboBox();
        cbDataRate.setModel(new DefaultComboBoxModel(SerialTest.getBaudRates()));
        cbDataRate.setSelectedItem(9600);
        pnConnect.add(cbDataRate);

        JButton btnConnect = new JButton("Connect");
        pnConnect.add(btnConnect);

        //

        JPanel pnSend = new JPanel();
        FlowLayout flowLayout2 = (FlowLayout) pnSend.getLayout();
        flowLayout2.setHgap(10);
        flowLayout2.setAlignment(FlowLayout.LEFT);

        JLabel lbSendData = new JLabel("Send data");
        pnSend.add(lbSendData);

        final JTextField tfCommand = new JTextField();
        tfCommand.setColumns(20);
        tfCommand.setMargin(new Insets(4, 5, 4, 5));
        pnSend.add(tfCommand);

        final JButton btnSend = new JButton("Send");
        btnSend.setEnabled(false);
        pnSend.add(btnSend);

        pnHead.add(pnSend, BorderLayout.SOUTH);

        //
        JPanel pnTextArea = new JPanel();
        contentPanel.add(pnTextArea);
        pnTextArea.setLayout(new BorderLayout(0, 0));
        final JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        pnTextArea.add(scrollPane, BorderLayout.CENTER);

        textArea.setRows(10);
        textArea.setEditable(false);
        textArea.setBorder(new EmptyBorder(5, 5, 5, 5));
        textArea.setAutoscrolls(true);

        btnSend.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String text = tfCommand.getText();
                if (null != text && !"".equals(text) && currentListener != null) {
                    currentListener.sendData(text);
                }
            }
        });

        btnConnect.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(e.getActionCommand());
                System.out.println(e.getSource());
                PortWrapper portWrapper = (PortWrapper) cbSerial.getSelectedItem();
                Integer baudRate = (Integer) cbDataRate.getSelectedItem();
                System.out.println(portWrapper);
                System.out.println(baudRate);
                if (null != portWrapper && null != baudRate) {
                    System.out.println(portWrapper);
                    if (null != currentListener) {
                        currentListener.close();
                    }
                    currentListener = SerialTest.initialize(portWrapper, baudRate, textArea);
                    btnSend.setEnabled(true);
                }
            }
        });

    }
}
