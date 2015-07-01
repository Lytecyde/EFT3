/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eft;

import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import java.io.FileWriter;

import com.opencsv.CSVWriter;
import java.io.IOException;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class EFT extends Applet
        implements ActionListener, MouseListener, MouseMotionListener {

    long tStart = System.currentTimeMillis();
    ////////////////////////////////////////////////////////////////////////////
    //
    // class Segment
    //
    ////////////////////////////////////////////////////////////////////////////
    private String csv;

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
        EFT g = new EFT();
        myFrame  = new JFrame("EFT Test");
        myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        g.init();
        myFrame.setPreferredSize(new Dimension(700, 300));
        myFrame.add(g);

        myFrame.pack();

        scoresFrame = new JFrame("Tulemuste tabel");
        scoresFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        scoresFrame.setPreferredSize(new Dimension(400, 300));
        ta = new JTextArea();
        scoresFrame.add(ta);
        scoresFrame.pack();

        scoresFrame.setVisible(true);
        myFrame.setVisible(true);
        myFrame.toFront();
    }
    private static JFrame myFrame;
    private static JFrame scoresFrame;
    private static JTextArea ta;
    private static TextField nameField ;
    
    private final int TOP = 100;
    private final int LEFT = 5;
    private final int HEIGHT = 90;
    private final int WIDTH = 590;
    private final int TARGET_WIDTH = 90;
    private final int FIGURE_SIZE = 25;//was 50
    private final int s4X = (LEFT + ((TARGET_WIDTH - FIGURE_SIZE) / 2));
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

    private final int MAXTESTS = 20;
    private final int NOTRUN = 0;
    private final int RUNNING = 1;
    private final int HASRUN = 2;
    private int testcount = 0;
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
    private int trialsPerTestCount;

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
    private int clickCounter;
    public String scoreStr;
    
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

        random.setSeed(System.currentTimeMillis());

        // Pick the zone.
        zoneX = random.nextInt(WIDTH - (TARGET_WIDTH + 2 * FIGURE_SIZE)) + LEFT + TARGET_WIDTH;

        zoneY = random.nextInt(HEIGHT - 2 * FIGURE_SIZE) + TOP;

        // Pick the figure to be found.
        //wally = random.nextInt(FIGMAX);
        //Pick 4 figure types to be found --"The Wally"
        //long tNow = 0;
        for (int i = 0; i < 4; i++) {

            search4[i] = randomGenerator(FIGMAX);

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
        clickCounter = 0;
        populateFigures();
        csv = "";
        scores = new long[MAXTESTS];

        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // We support a minimal mode for a consistent large population test.
        mode = new String(mode);

        if (!mode.equals("minimal")) {
            noiseLabel = new Label("Müra = ");
            noiseWidget = new TextField("10", 2);
            noiseWidget.setEditable(false);
            testsLabel = new Label("Teste = ");
            testsWidget = new TextField("10", 2);
            testsWidget.setEditable(false);
            
            add(noiseLabel);
            add(noiseWidget);

            add(testsLabel);
            add(testsWidget);
            
        }
        
        nameField = new TextField(16);
            
        go = new Button("Läks!");
        restart = new Button("Restart");

        add(nameField);
        add(go);
        add(restart);
        
        go.addActionListener(this);
        restart.addActionListener(this);

        addMouseListener(this);

        go.setEnabled(true);
        restart.setEnabled(false);
        //nameField.setVisible(true);
        state = NOTRUN;
        
    }

    public void save(String csvData) {
        String name = nameField.getText().replaceAll(" ", "");
        String csv = "data"+name+".csv";
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(csv));
            //Create record
            String[] line = csvData.split(";");
            for (String s : line) {
                String[] record = s.split(",");
                //Write the record to file
                writer.writeNext(record);
            }

            //close the writer
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(EFT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics g) {

        g.setColor(Color.black);
        g.drawLine(LEFT, TOP, LEFT + WIDTH, TOP);//top
        g.drawLine(LEFT, TOP + HEIGHT, LEFT + WIDTH, TOP + HEIGHT);//left
        g.drawLine(LEFT, TOP, LEFT, TOP + HEIGHT);//right
        g.drawLine(LEFT + TARGET_WIDTH, TOP, LEFT + TARGET_WIDTH, TOP + HEIGHT);//target box
        g.drawLine(LEFT + WIDTH, TOP, LEFT + WIDTH, TOP + HEIGHT);

        if (state == NOTRUN) {
            if (!mode.equals("minimal")) {
                //removed because the number of tests is limited to 10 in code
                //g.drawString("Vali mitu segavat figuuri võtta ning mitu testi teha.", 110, 120);
            }

            g.drawString("Otsitav figuur on nähtav kastis mis vasakul.", 110, 140);
            g.drawString("Leia see figuur käesoleva kasti visuaalsest mürast.", 110, 160);

        } else if (state == HASRUN) {
            long total = 0;

            for (int i = 0; i < testsValue; i++) {
                total += scores[i];
            }

            g.drawString("Keskmine aeg millisekundites (ms): " + Long.toString(total / testsValue), 110, 120);
            g.drawString("Mitu klikki testi kohta kulus: " + Integer.toString(clickCounter), 110, 138);

            if (scores.length > 0  && clickCounter != 0) {
                final int IDEAL_TIME = 300 * testsValue;
                int pointScore = ((int) (total / testsValue) - IDEAL_TIME) * (clickCounter - testsValue + 1);
                scoreStr = "Test nr: EFT" + Integer.toString(testcount)
                        + "T" + Integer.toString(testsValue) + "M" + Integer.toString(noiseValue)
                        + ",  Aeg(ms): " + Long.toString(total / testsValue)
                        + ",  Klikke: " + Integer.toString(clickCounter)
                        + ",  Punkte: " + Integer.toString(pointScore);
                csv = csv+scoreStr + ";\n";
                ta.append(scoreStr);
                ta.append(";\n");
                testcount++;
            }
            else;
            if (testcount == 6){
                save(csv);
                myFrame.dispose();
            }
            else;
            //reinit the number of clicks
            clickCounter = 0;
        } else if (state == RUNNING) {
            //TARGET BOX
            displayFigure(g, search4[0], s4X, s4Y);
            displayFigure(g, search4[1], s4X + FIGURE_SIZE, s4Y);
            displayFigure(g, search4[2], s4X, s4Y + FIGURE_SIZE);
            displayFigure(g, search4[3], s4X + FIGURE_SIZE, s4Y + FIGURE_SIZE);

            //WALLY the composite FIGURE
            displayFigure(g, search4[0], zoneX, zoneY);
            displayFigure(g, search4[1], zoneX + FIGURE_SIZE, zoneY);
            displayFigure(g, search4[2], zoneX, zoneY + FIGURE_SIZE);
            displayFigure(g, search4[3], zoneX + FIGURE_SIZE, zoneY + FIGURE_SIZE);
            // Pick and place the correct number of noise figures.

            int count = 0;

            while (count < noiseValue) {
                Random[] figCodeRandomiser;
                figCodeRandomiser = new Random[4];
                int[] figCode = new int[4];
                for (int i = 0; i < 4; i++) {
                    figCode[i] = randomGenerator(FIGMAX);
                }

//                if ((figCode = random.nextInt(FIGMAX))==wally) {
//                    continue;
//                }
                if ((figCode[0] == search4[0])
                        && (figCode[1] == search4[1])
                        && (figCode[2] == search4[2])
                        && (figCode[3] == search4[3])) {
                    continue;
                }

                int xn = random.nextInt(WIDTH - (TARGET_WIDTH + 2 * FIGURE_SIZE)) + LEFT + TARGET_WIDTH;
                int yn = random.nextInt(HEIGHT - 2 * FIGURE_SIZE) + TOP;

                displayFigure(g, figCode[0], xn, yn);

                displayFigure(g, figCode[1], xn + FIGURE_SIZE, yn);

                displayFigure(g, figCode[2], xn, yn + FIGURE_SIZE);

                displayFigure(g, figCode[3], xn + FIGURE_SIZE, yn + FIGURE_SIZE);

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
            trialsPerTestCount = 0;
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

        if (state == RUNNING) {
            clickCounter++;
        }
        if (state == RUNNING && x > zoneX && x < zoneX + FIGURE_SIZE * 2 && y > zoneY && y < zoneY + FIGURE_SIZE * 2) {
            scores[trialsPerTestCount++] = System.currentTimeMillis() - startTime;
            if (trialsPerTestCount == testsValue) {
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
