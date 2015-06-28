package org.gagauz.playground.serial;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JTextArea;

public class SerialTest implements SerialPortEventListener {

    public static class PortWrapper {
        final CommPortIdentifier port;

        public PortWrapper(CommPortIdentifier port) {
            this.port = port;
        }

        @Override
        public String toString() {
            return port.getName();
        }
    }

    /** The port we're normally going to use. */
    private static final String PORT_NAMES[] = {
            "/dev/tty.usbserial-A9007UX1", // Mac OS X
            "/dev/ttyACM", // Raspberry Pi
            "/dev/ttyUSB", // Linux
            "COM", // Windows
    };

    private SerialPort serialPort;
    private JTextArea textArea;
    /**
     * A BufferedReader which will be fed by a InputStreamReader converting the
     * bytes into characters making the displayed results codepage independent
     */
    private BufferedReader input;
    /** The output stream to the port */
    private OutputStream output;
    /** Milliseconds to block while waiting for port open */
    private static final int TIME_OUT = 2000;
    /** Default bits per second for COM port. */
    private static final Integer[] DATA_RATES = { 110, 300, 600, 1200, 2400, 4800, 9600, 14400,
            19200, 28800, 38400, 56000, 57600, 115200 };

    private SerialTest(SerialPort serialPort, JTextArea textArea) throws IOException {
        // open the streams
        this.serialPort = serialPort;
        this.input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
        this.output = serialPort.getOutputStream();
        this.textArea = textArea;
    }

    public static PortWrapper[] getAvailablePorts() {
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        List<PortWrapper> result = new ArrayList<PortWrapper>();
        // First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().startsWith(portName)) {
                    result.add(new PortWrapper(currPortId));
                }
            }
        }
        return result.toArray(new PortWrapper[0]);
    }

    /**
     * This should be called when you stop using the port. This will prevent
     * port locking on platforms like Linux.
     */
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    /**
     * Handle an event on the serial port. Read the data and print it.
     */
    @Override
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {

            try {
                String inputLine = input.readLine();
                System.out.println(inputLine);
                if (inputLine.startsWith("$GPRMC")) {
                    String[] vals = inputLine.split(",");
                    System.out.println("Fix time " + vals[1]);
                    System.out.println("Status " + vals[2]);
                    System.out.println("Latitude " + vals[3] + vals[4]);
                    System.out.println("Longitude " + vals[5] + vals[6]);
                    System.out.println("Speed " + vals[7]);
                    System.out.println("Track angle " + vals[8]);
                    System.out.println("Date " + vals[9]);
                    System.out.println("Magnetic Variation " + vals[10]);
                    System.out.println("Checksum " + vals[11]);
                }
                textArea.append(inputLine + "\n");
                textArea.setCaretPosition(textArea.getDocument().getLength());
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
        // Ignore all the other eventTypes, but you should consider the other
        // ones.
    }

    public static SerialTest initialize(PortWrapper portWrapper, int dataRate, JTextArea textArea) {

        if (portWrapper == null) {
            System.out.println("Could not find COM port.");
            return null;
        }

        SerialPort serialPort = null;
        try {
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portWrapper.port.open(SerialTest.class.getName(), TIME_OUT);

            // set port parameters
            serialPort.setSerialPortParams(dataRate,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // add event listeners
            SerialTest serialEventListener = new SerialTest(serialPort, textArea);
            serialPort.addEventListener(serialEventListener);
            serialPort.notifyOnDataAvailable(true);
            return serialEventListener;
        } catch (Exception e) {
            System.err.println(e.toString());
            try {
                serialPort.close();
            } catch (Exception e2) {
            }
        }
        return null;
    }

    public static Object[] getBaudRates() {
        return DATA_RATES;
    }

    public void sendData(String text) {
        try {
            System.out.println(text);
            output.write(text.getBytes("latin1"));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
