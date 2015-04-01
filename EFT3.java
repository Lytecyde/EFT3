

/*
 * EFT.java - Embedded Figures Test
 * Copyright (C) 2007 Alan G. Carter
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */

/*
 * Changes
 * 15OCT07 - Alan Carter - Made minimal and experimental mode.
 *                         Reseed Random for each run.
 * 30SEP07 - Steve Dodd - corrected mouseClicked() to mousePressed().
*
*    Mik Seljamaa 4 figure test should also test us on novelty seeing at the same 
*    time
 */
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class EFT3 extends Applet
        implements ActionListener, MouseListener, MouseMotionListener {
    long tStart = System.currentTimeMillis();
    ////////////////////////////////////////////////////////////////////////////
    //
    // class Segment
    //
    ////////////////////////////////////////////////////////////////////////////

    
    
    private class Segment {

        private final int ARC = 0;
        private final int LINE = 1;

        private int x0, y0, x1, y1, start, subtend;
        private int type;

        public Segment(int x0, int y0, int x1, int y1) {
            this.x0 = x0;
            this.y0 = y0;
            this.x1 = x1;
            this.y1 = y1;

            type = LINE;
        }

        Segment(int x0, int y0, int x1, int y1, int start, int subtend) {
            this.x0 = x0;
            this.y0 = y0;
            this.x1 = x1;
            this.y1 = y1;
            this.start = start;
            this.subtend = subtend;

            type = ARC;
        }

        public void display(Graphics g, int x, int y) {

            if (type == LINE) {
                g.drawLine(x + x0, y + y0, x + x1, y + y1);
            } else if (type == ARC) {
                g.drawArc(x + x0, y + y0, x1, y1, start, subtend);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    // class Figure
    //
    ////////////////////////////////////////////////////////////////////////////
    private class Figure {

        private Segment[] segments;
        private int segmentCount = 0;

        public Figure() {
            segments = new Segment[10];
        }

        public void addSegment(Segment segment) {
            if (segmentCount < 10) {
                segments[segmentCount++] = segment;
            }
        }

        public void display(Graphics g, int x, int y) {

            for (int i = 0; i < segmentCount; i++) {
                segments[i].display(g, x, y);
            }
        }
    }

    
    ////////////////////////////////////////////////////////////////////////////
    //
    // class EFT
    //
    ////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) {
        EFT3 g = new EFT3();
        JFrame myFrame = new JFrame("Test");
        g.init();
        myFrame.setPreferredSize(new Dimension(700, 300));
        myFrame.add(g);

        myFrame.pack();
        myFrame.setVisible(true);

    }
    private final int TOP = 100;
    private final int LEFT = 5;
    private final int HEIGHT = 90;
    private final int WIDTH = 590;
    private final int TARGET_WIDTH = 90;
    private final int FIGURE_SIZE = 25;//was 50
    private final int s4X = (LEFT + ((TARGET_WIDTH - FIGURE_SIZE) / 2));
    ;
        private final int s4Y = (TOP + ((HEIGHT - FIGURE_SIZE) / 2));
    private final int FIGTRISTAB = 0;
    private final int FIGTRIUNSTAB = 1;
    private final int FIGCROSS = 2;
    private final int FIGDIAMOND = 3;
    private final int FIGLAMBDA = 4;
    private final int FIGMOONLEFT = 5;
    private final int FIGMOONRIGHT = 6;
    private final int FIGCIRCLE = 7;
    private final int FIGMAX = 8;

    private final int NOTRUN = 0;
    private final int RUNNING = 1;
    private final int HASRUN = 2;

    private String mode = "";
    private int state = NOTRUN;
    private int wally;
    private int[] search4 = {
        0, 0, 0, 0
    };
    private int zoneX;
    private int zoneY;
    private long startTime;
    private long[] scores;
    private int testsCount;

    private Label noiseLabel;
    private TextField noiseWidget;
    private int noiseValue;

    private Label testsLabel;
    private TextField testsWidget;
    private int testsValue;

    private Button go;
    private Button restart;

    private Figure figTriStab = new Figure();
    private Figure figTriUnstab = new Figure();
    private Figure figCross = new Figure();
    private Figure figDiamond = new Figure();
    private Figure figLambda = new Figure();
    private Figure figMoonLeft = new Figure();
    private Figure figMoonRight = new Figure();
    private Figure figCircle = new Figure();

    private Random random = new Random();

    ////////////////////////////////////////////////////////////////////////////
    private void populateFigures() {
        figTriStab.addSegment(new Segment(FIGURE_SIZE / 2, 0, 0, FIGURE_SIZE));
        figTriStab.addSegment(new Segment(0, FIGURE_SIZE, FIGURE_SIZE, FIGURE_SIZE));
        figTriStab.addSegment(new Segment(FIGURE_SIZE, FIGURE_SIZE, FIGURE_SIZE / 2, 0));

        figTriUnstab.addSegment(new Segment(FIGURE_SIZE / 2, FIGURE_SIZE, 0, 0));
        figTriUnstab.addSegment(new Segment(0, 0, FIGURE_SIZE, 0));
        figTriUnstab.addSegment(new Segment(FIGURE_SIZE, 0, FIGURE_SIZE / 2, FIGURE_SIZE));

        figCross.addSegment(new Segment(FIGURE_SIZE / 2, 0, FIGURE_SIZE / 2, FIGURE_SIZE));
        figCross.addSegment(new Segment(0, FIGURE_SIZE / 2, FIGURE_SIZE, FIGURE_SIZE / 2));

        figDiamond.addSegment(new Segment(FIGURE_SIZE / 2, 0, FIGURE_SIZE, FIGURE_SIZE / 2));
        figDiamond.addSegment(new Segment(FIGURE_SIZE, FIGURE_SIZE / 2, FIGURE_SIZE / 2, FIGURE_SIZE));
        figDiamond.addSegment(new Segment(FIGURE_SIZE / 2, FIGURE_SIZE, 0, FIGURE_SIZE / 2));
        figDiamond.addSegment(new Segment(0, FIGURE_SIZE / 2, FIGURE_SIZE / 2, 0));

        figLambda.addSegment(new Segment(0, 0, FIGURE_SIZE, FIGURE_SIZE));
        figLambda.addSegment(new Segment(FIGURE_SIZE / 2, FIGURE_SIZE / 2, 0, FIGURE_SIZE));

        figMoonLeft.addSegment(new Segment(0, 0, FIGURE_SIZE, FIGURE_SIZE, 90, 180));
        figMoonLeft.addSegment(new Segment(FIGURE_SIZE / 2, 0, FIGURE_SIZE / 2, FIGURE_SIZE));

        figMoonRight.addSegment(new Segment(0, 0, FIGURE_SIZE, FIGURE_SIZE, 270, 180));
        figMoonRight.addSegment(new Segment(FIGURE_SIZE / 2, 0, FIGURE_SIZE / 2, FIGURE_SIZE));

        figCircle.addSegment(new Segment(0, 0, FIGURE_SIZE, FIGURE_SIZE, 0, 360));
    }

    ////////////////////////////////////////////////////////////////////////////
    private void displayFigure(Graphics g, int figCode, int x, int y) {

        if (figCode == FIGTRISTAB) {
            figTriStab.display(g, x, y);
        } else if (figCode == FIGTRIUNSTAB) {
            figTriUnstab.display(g, x, y);
        } else if (figCode == FIGCROSS) {
            figCross.display(g, x, y);
        } else if (figCode == FIGDIAMOND) {
            figDiamond.display(g, x, y);
        } else if (figCode == FIGLAMBDA) {
            figLambda.display(g, x, y);
        } else if (figCode == FIGMOONLEFT) {
            figMoonLeft.display(g, x, y);
        } else if (figCode == FIGMOONRIGHT) {
            figMoonRight.display(g, x, y);
        } else if (figCode == FIGCIRCLE) {
            figCircle.display(g, x, y);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    private void placeFigure() {
        // Re-seed the random number generator for each run.
        Random[] r4 = {new Random(), new Random(), new Random(), new Random()
        };
        random.setSeed(System.currentTimeMillis());

        // Pick the zone.
        zoneX = random.nextInt(WIDTH - (TARGET_WIDTH + FIGURE_SIZE)) + LEFT + TARGET_WIDTH;
        zoneY = random.nextInt(HEIGHT - FIGURE_SIZE) + TOP;

		// Pick the figure to be found.
        //wally = random.nextInt(FIGMAX);
        //Pick 4 figure types to be found
        long tNow = 0;
        for (int i = 0; i < 4; i++) {
            tNow = System.currentTimeMillis();
            generator = new Random((tStart- tNow)*(i+1));         

            search4[i] = randomGenerator(FIGMAX);
            System.out.println((tNow - tStart) + i + "figure type:" +search4[i]);
                
        }

        // Remember the starting time.
        startTime = System.currentTimeMillis();
    }
    
    private Random generator = new Random(System.currentTimeMillis());

    int randomGenerator(int range) {
        return generator.nextInt(range);
    }

    ////////////////////////////////////////////////////////////////////////////

    public void init() {
       
        populateFigures();

        scores = new long[256];

        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // We support a minimal mode for a consistent large population test.
        mode = new String(mode);

        if (!mode.equals("minimal")) {
            noiseLabel = new Label("Noise = ");
            noiseWidget = new TextField("10", 2);

            testsLabel = new Label("Tests = ");
            testsWidget = new TextField("10", 2);

            add(noiseLabel);
            add(noiseWidget);

            add(testsLabel);
            add(testsWidget);
        }

        go = new Button("Go");
        restart = new Button("Restart");

        add(go);
        add(restart);

        go.addActionListener(this);
        restart.addActionListener(this);

        addMouseListener(this);

        go.setEnabled(true);
        restart.setEnabled(false);
        state = NOTRUN;

    }

    ////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics g) {
        int x = 0;
        int y = 0;
        g.setColor(Color.black);
        g.drawLine(LEFT, TOP, LEFT + WIDTH, TOP);
        g.drawLine(LEFT, TOP + HEIGHT, LEFT + WIDTH, TOP + HEIGHT);
        g.drawLine(LEFT, TOP, LEFT, TOP + HEIGHT);
        g.drawLine(LEFT + TARGET_WIDTH, TOP, LEFT + TARGET_WIDTH, TOP + HEIGHT);
        g.drawLine(LEFT + WIDTH, TOP, LEFT + WIDTH, TOP + HEIGHT);

        if (state == NOTRUN) {
            if (!mode.equals("minimal")) {
                g.drawString("Select the amount of noise figures and tests to perform.", 110, 120);
            }

            g.drawString("The figure to find is shown in the box on the left.", 110, 140);
            g.drawString("Find and click it in this box.", 110, 160);

        } else if (state == HASRUN) {
            long total = 0;

            for (int i = 0; i < testsValue; i++) {
                total += scores[i];
            }
            g.drawString("Average time (ms): " + Long.toString(total / testsValue), 110, 120);
        } else if (state == RUNNING) {
			// Draw the boxes.

            //displayFigure(g, wally, LEFT + ((TARGET_WIDTH - FIGURE_SIZE) / 2), TOP + ((HEIGHT - FIGURE_SIZE) / 2));
            //displayFigure(g, wally, zoneX, zoneY);
            for (int i = 0; i < 4; i++) {
                x = s4X + (i % 2) * 25;
                if (i > 1) {
                    y = s4Y + 25;
                } else {
                    y = s4Y;
                }
                displayFigure(g, search4[i], x, y);

            }
            for (int i = 0; i < 4; i++) {
                x = zoneX + (i % 2) * 25;
                if (i > 1) {
                    y = zoneY + 25;
                } else {
                    y = zoneY;
                }
                
                displayFigure(g, search4[i], x , y);
            }
            // Pick and place the correct number of noise figures.

            int count = 0;

            while (count < noiseValue) {
                int figCode;

                if ((figCode = random.nextInt(FIGMAX)) == wally) {
                    continue;
                }

                int xn = random.nextInt(WIDTH - (TARGET_WIDTH + FIGURE_SIZE)) + LEFT + TARGET_WIDTH;
                int yn = random.nextInt(HEIGHT - FIGURE_SIZE) + TOP;
                displayFigure(g, figCode, xn, yn);
                figCode = randomGenerator(FIGMAX);
                displayFigure(g, figCode, xn+FIGURE_SIZE, yn);
                figCode = randomGenerator(FIGMAX);
                displayFigure(g, figCode, xn, yn+FIGURE_SIZE);
                figCode = randomGenerator(FIGMAX);
                displayFigure(g, figCode, xn+FIGURE_SIZE, yn+FIGURE_SIZE);
                figCode = randomGenerator(FIGMAX);
                count++;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == go) {
            if (mode.equals("minimal")) {
                noiseValue = 10;
                testsValue = 10;
            } else {
                noiseValue = Integer.parseInt(noiseWidget.getText());
                testsValue = Integer.parseInt(testsWidget.getText());
                if (testsValue > 255) {
                    testsValue = 255;
                }
            }

            go.setEnabled(false);
            restart.setEnabled(true);
            testsCount = 0;
            state = RUNNING;
            placeFigure();
        } else if (e.getSource() == restart) {
            go.setEnabled(true);
            restart.setEnabled(false);
            state = NOTRUN;
            placeFigure();
        }

        repaint();
    }

    ////////////////////////////////////////////////////////////////////////////
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        if (state == RUNNING && x > zoneX && x < zoneX + FIGURE_SIZE*2 && y > zoneY && y < zoneY + FIGURE_SIZE*2) {
            scores[testsCount++] = System.currentTimeMillis() - startTime;
            if (testsCount == testsValue) {
                state = HASRUN;
            }
            placeFigure();
            repaint();
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    public void mouseDragged(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }
}
