package org.gagauz.playground.serial;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;

public class Diagram2D extends JPanel {
    private static final long serialVersionUID = -6538441838232818043L;
    private static final int tickSpace = 10;
    private static final int divider = 10;
    private int width, height;
    private Graphics graphix;
    private Map<Color, LimitedQueue> yCoords = new HashMap<Color, LimitedQueue>(3);
    private int[] xCoords;
    private int[] xCoordsFull;

    public Diagram2D() {
        super();
        setDoubleBuffered(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (null == graphix) {
            graphix = getGraphics();
            Rectangle b = getBounds();
            width = b.width;
            height = b.height;

            xCoords = new int[width / divider / 2];
            xCoordsFull = new int[width / 2];
            for (int i = 0; i < width / divider / 2; i++) {
                xCoords[i] = i * divider;
            }
            for (int i = 0; i < width / 2; i++) {
                xCoordsFull[i] = i;
            }
            clearRect();
        }
    }

    private LimitedQueue getQueue(Color color) {
        LimitedQueue queue = yCoords.get(color);
        if (null == queue) {
            queue = new LimitedQueue(width / divider / 2);
            yCoords.put(color, queue);
        }
        return queue;
    }

    private void clearRect() {
        graphix.setColor(Color.WHITE);
        graphix.fillRect(0, 0, width, height);
        graphix.setColor(Color.BLACK);
        graphix.drawLine(0, height / 2, width, height / 2);
        graphix.drawRect(0, 0, width - 1, height - 1);
        for (int i = 0; i < width; i += tickSpace) {
            graphix.drawLine(i, height / 2 - 2, i, height / 2 + 2);
        }
    }

    private void drawStack() {
        for (Entry<Color, LimitedQueue> e : yCoords.entrySet()) {
            graphix.setColor(e.getKey());
            int[] yy0 = e.getValue().toIntArray();
            int[] xx0 = new int[yy0.length];
            System.arraycopy(xCoords, 0, xx0, 0, yy0.length);
            int max = xx0[xx0.length - 1];
            BSpline spline = new BSpline(xx0, yy0);
            int[] yy = new int[xCoordsFull.length];
            for (int i = 0; i < max; i++)
                yy[i] = (int) spline.getValue(i);
            graphix.drawPolyline(xCoordsFull, yy, xCoordsFull.length);
        }
    }

    public void addPoint(Color color, int y) {
        y = height / 2 - y;
        getQueue(color).add(y);
    }

    public void redraw() {
        clearRect();
        drawStack();
    }

    class LimitedQueue extends LinkedList<Integer> {

        private int limit;

        public LimitedQueue(int limit) {
            this.limit = limit;
        }

        @Override
        public boolean add(Integer o) {
            super.addLast(o);
            int delta = size() - limit;
            for (int i = 0; i < delta; i++) {
                removeFirst();
            }
            return true;
        }

        public int[] toIntArray() {
            int[] intArray = new int[size()];
            Iterator<Integer> it = iterator();
            int s = 0;
            while (it.hasNext()) {
                intArray[s] = it.next();
                s++;
            }
            return intArray;
        }
    }
}
