package org.gagauz.playground.serial;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Insets;
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
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.gagauz.playground.serial.SerialTest.PortWrapper;

public class MainWindow implements SerialEventHandler {

    private JFrame frame;
    private SerialTest currentListener;
    private JTextArea textArea;
    private Diagram2D graphAcc;
    private Diagram2D graphGyr;

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
        frame.setBounds(100, 100, 800, 600);
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

        final JButton btnConnect = new JButton("Connect");
        pnConnect.add(btnConnect);
        JButton btnRefresh = new JButton("Refresh");
        pnConnect.add(btnRefresh);

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
        textArea = new JTextArea();
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
                if (null != currentListener) {
                    currentListener.close();
                    btnConnect.setText("Connect");
                } else if (null != portWrapper && null != baudRate) {
                    btnConnect.setText("Disconnect");
                    System.out.println(portWrapper);
                    currentListener = SerialTest.initialize(portWrapper, baudRate, MainWindow.this);
                    btnSend.setEnabled(true);
                }
            }
        });

        btnRefresh.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                cbSerial.setModel(new DefaultComboBoxModel(SerialTest.getAvailablePorts()));
            }
        });
        graphGyr = new Diagram2D();
        contentPanel.add(graphGyr);
        // frame.pack();
    }

    @Override
    public void handleEvent(String inputLine) {
        String[] vals = inputLine.split(",");
        if (vals[0].startsWith("$GPRMC")) {

            System.out.println("Fix time " + vals[1]);
            System.out.println("Status " + vals[2]);
            System.out.println("Latitude " + vals[3] + vals[4]);
            System.out.println("Longitude " + vals[5] + vals[6]);
            System.out.println("Speed " + vals[7]);
            System.out.println("Track angle " + vals[8]);
            System.out.println("Date " + vals[9]);
            System.out.println("Magnetic Variation " + vals[10]);
            System.out.println("Checksum " + vals[11]);
        } else if (vals.length > 4) {
            try {
                int ax = (int) Float.parseFloat(vals[0]);
                int ay = (int) Float.parseFloat(vals[1]);
                int az = (int) Float.parseFloat(vals[2]);
                int gx = (int) Float.parseFloat(vals[3]);
                int gy = (int) Float.parseFloat(vals[4]);
                int gz = (int) Float.parseFloat(vals[5]);
                graphGyr.addPoint(Color.RED, ax / 100);
                graphGyr.addPoint(Color.GREEN, ay / 100);
                graphGyr.addPoint(Color.BLUE, az / 100);
                graphGyr.redraw();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        textArea.append(inputLine + "\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());

    }
}
