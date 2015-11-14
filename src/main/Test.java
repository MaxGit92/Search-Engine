package main;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;
import org.math.plot.plotObjects.BaseLabel;

import modeles.PageRank;

public class Test {
	
	public static void main(String[] args) throws IOException, AWTException {
		/*TreeMap<String, ArrayList<Double>> test = new TreeMap<String, ArrayList<Double>>();
		ArrayList<Double> l = new ArrayList<Double>();
		l.add((double) 5);
		ArrayList<Double> m = new ArrayList<Double>();
		m.add((double) 10);
		test.put("lea", l);
		test.put("lea", m);
		test.put("maxence", m);
		//test.remove("lea");
		System.out.println(test);*/
		/*
		HashMap<String, Double> test = new HashMap<String, Double>();
		

		test.put("aa", (double) 12);
		test.put("aa", test.get("aa")/2);
		System.out.println(test);
		
		System.out.println(Runtime.getRuntime().availableProcessors() + 1);
		
		RandomAccessFile index1 = new RandomAccessFile("cisi_index", "r");
		RandomAccessFile index2 = new RandomAccessFile("cisi_index", "r");
		
		
		for(int i = 0; i < 3; i++){
			System.out.println(index1.toString());
			System.out.println(index2.readLine());
		}
		*/
		double[] x1 = {1,2,3,4,5,6,7,8};
		double[] y1 = {1,4,9,16,25,36,49,64};

		double[] x2 = {1,2,3,4,5,6,7,8};
		double[] y2 = {1,2,3,4,5,6,7,8};
		// create your PlotPanel (you can use it as a JPanel)
		Plot2DPanel plot = new Plot2DPanel();

		BaseLabel title = new BaseLabel("...My nice plot...", Color.RED, 0.5, 1.1);
        title.setFont(new Font("Courier", Font.BOLD, 20));
        plot.addPlotable(title);
        plot.addLegend("EAST");

		
		// add a line plot to the PlotPanel
		plot.addLinePlot("Fonction Carr�e", x1, y1);
		plot.addLinePlot("Fonction lin�aire", x2, y2);
		// change name of axes
        plot.setAxisLabels("Abscisses", "Ordonnees");
        plot.getAxis(0).setLabelPosition(0.5, -0.15);
        plot.getAxis(1).setLabelPosition(-0.15, 0.5);

		// put the PlotPanel in a JFrame, as a JPanel
		JFrame frame = new JFrame("a plot panel");
        frame.setSize(600, 600);
		frame.setContentPane(plot);
		frame.setVisible(true);
		try
        {
            BufferedImage image = new BufferedImage(plot.getWidth(), plot.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = image.createGraphics();
            frame.paint(graphics2D);
            ImageIO.write(image,"jpeg", new File("jmemPractice.jpeg"));
        }
        catch(Exception exception)
        {
            //code
        }

	}
}