package nikola.makric.GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Color;
import javax.swing.border.MatteBorder;

import nikola.makric.Simulation;
import nikola.makric.TimeCounterThread;
import nikola.makric.boardobjects.Player;
import nikola.makric.boardobjects.figures.FigureColour;
import nikola.makric.boardobjects.figures.GameFigure;
import nikola.makric.boardobjects.spawns.Ghost;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JSplitPane;
import java.awt.Cursor;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import java.awt.Font;
import javax.swing.border.LineBorder;
import javax.swing.JTable;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame {

	/**
	 * Launch the application.
	 */
	public static ArrayList<Integer> putanja = new ArrayList<>();
	private ArrayList<JLabel> matrica = new ArrayList<>();
	private ArrayList<JLabel> igraci = new ArrayList<>();
	private ArrayList<JLabel>figureIgraca = new ArrayList<>();
	private JLabel pictureLabel;
	private JLabel simulationDuration;
	private JLabel opisPoteza = new JLabel();
	public static boolean paused = false;
	public static long pauseTime = 0;
	private static long pauseStartTime;
	private static long pauseEndTime;
	/**
	 * Create the frame.
	 */
	public MainFrame() {
		//postavljamo naslov datog prozora
		setTitle("DiamondCircle");
		//pozadinu osnovnog prozora
		getContentPane().setBackground(Color.WHITE);
		setForeground(Color.WHITE);
		setBackground(Color.WHITE);
		//Postavljamo ikonicu
		Image img = new ImageIcon(this.getClass().getResource("/diamond.png")).getImage();
		setIconImage(img);
		//ne moze se mijenjati velicina prozora
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1280, 720);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{73};
		gridBagLayout.rowHeights = new int[]{150, 500};
		gridBagLayout.columnWeights = new double[]{1.0};
		gridBagLayout.rowWeights = new double[]{1.0, 1.0};
		getContentPane().setLayout(gridBagLayout);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		panel.setBackground(Color.WHITE);
		panel.setLayout(null);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		getContentPane().add(panel, gbc_panel);
		
		JButton btnNewButton = new JButton("Pokreni/Zaustavi");
		btnNewButton.setBackground(Color.PINK);
		btnNewButton.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		btnNewButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
		btnNewButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnNewButton.setBounds(1050, 33, 200, 100);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!paused)
				{
					pauseStartTime = new Date().getTime();
					paused = true;
				}else
				{
					paused = false;
					pauseEndTime = new Date().getTime();
					pauseTime+= (pauseEndTime - pauseStartTime);
					
					synchronized(Simulation.objectToLock)
					{
						
						Simulation.objectToLock.notify();
						
					}
					synchronized(Ghost.objectToLockGhost)
					{
						Ghost.objectToLockGhost.notify();
					}
					synchronized(TimeCounterThread.objectToLockTimeThread)
					{
						TimeCounterThread.objectToLockTimeThread.notify();
					}
				}
			}
		}); 
		panel.add(btnNewButton);
		
		JLabel lblNewLabel = new JLabel("DiamondCircle");
		lblNewLabel.setBorder(new LineBorder(Color.BLACK, 4));
		lblNewLabel.setOpaque(true);
		lblNewLabel.setForeground(Color.RED);
		lblNewLabel.setBackground(Color.PINK);
		lblNewLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 25));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(450, 33, 350, 100);
		panel.add(lblNewLabel);
		
		var files = new File("PrethodneIgre").listFiles();
		JLabel lblNewLabel_1 = new JLabel("Trenutni broj odigranih igara: "+files.length);
		lblNewLabel_1.setOpaque(true);
		lblNewLabel_1.setBackground(Color.PINK);
		lblNewLabel_1.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		lblNewLabel_1.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(50, 33, 300, 100);
		panel.add(lblNewLabel_1);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.ORANGE);
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 1;
		getContentPane().add(panel_1, gbc_panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerSize(0);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		panel_1.add(splitPane);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		panel_2.setBackground(Color.PINK);
		splitPane.setLeftComponent(panel_2);
		panel_2.setLayout(null);
		int xPosition=220;
		for(int i=0;i<Simulation.numberOfPlayers;i++)
		{
			Player igrac = Simulation.players[i];
			FigureColour bojaFigurica = igrac.getColourOfFigures();
			JLabel temp = new JLabel(igrac.getName());
			if(bojaFigurica.equals(FigureColour.RED))
			{
				temp.setForeground(Color.RED);
			}else if(bojaFigurica.equals(FigureColour.BLUE))
			{
				temp.setForeground(Color.BLUE);
			}else if(bojaFigurica.equals(FigureColour.GREEN))
			{
				temp.setForeground(Color.GREEN);
			}else
			{
				temp.setForeground(Color.YELLOW);
			}
			temp.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
			temp.setBounds(xPosition, 10, 100, 20);
			panel_2.add(temp);
			igraci.add(temp);
			xPosition+=220;
		}
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setDividerSize(0);
		splitPane_1.setDoubleBuffered(true);
		splitPane.setRightComponent(splitPane_1);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		panel_3.setBackground(Color.WHITE);
		splitPane_1.setLeftComponent(panel_3);
		panel_3.setLayout(new GridLayout(16, 1, 0, 0));
		for(int i=0;i<Simulation.numberOfPlayers;i++)
		{
			for(int j=0;j<4;j++)
			{
				JLabel temp = new JLabel(Simulation.players[i].getPlayerFigures().get(j).getName());
				temp.setHorizontalAlignment(SwingConstants.CENTER);
				temp.setFont(new Font("Comic Sans MS", Font.PLAIN, 11));
				FigureColour bojaFigurica = Simulation.players[i].getColourOfFigures();
				if(bojaFigurica.equals(FigureColour.RED))
				{
					temp.setForeground(Color.RED);
				}else if(bojaFigurica.equals(FigureColour.BLUE))
				{
					temp.setForeground(Color.BLUE);
				}else if(bojaFigurica.equals(FigureColour.GREEN))
				{
					temp.setForeground(Color.GREEN);
				}else
				{
					temp.setForeground(Color.YELLOW);
				}
				temp.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						for(GameFigure figura : Simulation.allOfTheFigures)
						{
							if(figura.getName().equals(temp.getText())) {
						     new FigurePathFrame(temp.getText(), figura.getFigurePathClone());
						     break;
							}
						}
						
					}
				});
				panel_3.add(temp);
			}
		}		
		JSplitPane splitPane_2 = new JSplitPane();
		splitPane_2.setDividerSize(0);
		splitPane_2.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_1.setRightComponent(splitPane_2);
		
		JSplitPane splitPane_3 = new JSplitPane();
		splitPane_3.setDividerSize(0);
		splitPane_2.setRightComponent(splitPane_3);
		
		JPanel panel_6 = new JPanel();
		panel_6.setBackground(Color.WHITE);
		splitPane_3.setLeftComponent(panel_6);
		panel_6.setLayout(new BorderLayout(0, 0));
		
		opisPoteza.setBackground(Color.WHITE);
		opisPoteza.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		opisPoteza.setHorizontalAlignment(SwingConstants.CENTER);
		panel_6.add(opisPoteza, BorderLayout.CENTER);
		
		JPanel panel_7 = new JPanel();
		panel_7.setBackground(Color.WHITE);
		splitPane_3.setRightComponent(panel_7);
		panel_7.setLayout(null);
		
		JButton btnNewButton_1 = new JButton("Fajlovi sa rezultatima");
		btnNewButton_1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnNewButton_1.setBounds(75, 15, 200, 40);
		btnNewButton_1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				new FileListFrame(files);
			}
			
		});
		panel_7.add(btnNewButton_1);
		splitPane_3.setDividerLocation(700);
		
		JSplitPane splitPane_4 = new JSplitPane();
		splitPane_4.setDividerSize(0);
		splitPane_2.setLeftComponent(splitPane_4);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		panel_4.setBackground(Color.WHITE);
		splitPane_4.setLeftComponent(panel_4);
		panel_4.setLayout(null);
		
		JPanel panel_9 = new JPanel();
		panel_9.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		panel_9.setBounds(180, 29, 400, 300);
		panel_4.add(panel_9);
		//DEFINISANJE MATRICE
		panel_9.setLayout(new GridLayout(Simulation.matrixDimensions, Simulation.matrixDimensions, 2, 2));
		//DODAVANJE LABELA MATRICE
		for(int i = 0; i < Simulation.matrixDimensions*Simulation.matrixDimensions; i++) { // dodavanje
			JLabel temp = new JLabel("" + (i + 1));
			temp.setBorder(new LineBorder(new Color(0, 0, 0), 2));
			temp.setHorizontalAlignment(SwingConstants.CENTER);
			temp.setForeground(Color.PINK);
		    temp.setOpaque(true);
		    if(Simulation.pathOfTheGame.contains(i+1))
			temp.setBackground(Color.LIGHT_GRAY);
		    else temp.setBackground(Color.WHITE);
			panel_9.add(temp);
			matrica.add(temp);
		}
		
//		for(int i = 0; i < matrica.size(); i++) {
//			if(putanja.contains(i)) {
//				matrica.get(i).setText("O");
//				matrica.get(i).setForeground(Color.BLACK);
//			}}
		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		panel_5.setBackground(Color.WHITE);
		splitPane_4.setRightComponent(panel_5);
		panel_5.setLayout(null);
		
		JPanel panel_8 = new JPanel();
		panel_8.setBackground(Color.WHITE);
		panel_8.setBounds(27, 11, 300, 340);
		panel_5.add(panel_8);
		panel_8.setLayout(null);
		
		JLabel lblNewLabel_23 = new JLabel("Trenutna karta");
		lblNewLabel_23.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
		lblNewLabel_23.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_23.setBounds(75, 11, 150, 38);
		panel_8.add(lblNewLabel_23);
		JLabel pictureLabel = new JLabel("");
		this.pictureLabel = pictureLabel;
	//	lblNewLabel_24.setIcon(new ImageIcon(MainFrame.class.getResource("/Images/specialcard.png")));
		pictureLabel.setBounds(0, 38, 300, 300);
		panel_8.add(pictureLabel);
		
		simulationDuration = new JLabel();
		simulationDuration.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
		simulationDuration.setHorizontalAlignment(SwingConstants.CENTER);
		simulationDuration.setBounds(68, 355, 216, 30);
		panel_5.add(simulationDuration);
		splitPane_4.setDividerLocation(700);
		splitPane_2.setDividerLocation(395);
		splitPane_1.setDividerLocation(200);
		splitPane.setDividerLocation(50);
		setVisible(true);
		
	
	}
	public  void setTime(String time)
	{
		simulationDuration.setText(time);
	}
	public synchronized  void postaviPozicijuNaMatricu(int staraPozicija,int novaPozicija,FigureColour bojaFigure,String imeFigurice)
	{
		int counter = 1;
		for(var labela : matrica) { 
			if(counter==staraPozicija)
			{
				labela.setForeground(Color.PINK);
				labela.setText(Integer.toString(staraPozicija));
			}else if(counter==novaPozicija)
			{
				if(bojaFigure.equals(FigureColour.RED))
				{
					labela.setForeground(Color.RED);
				}else if(bojaFigure.equals(FigureColour.BLUE))
				{
					labela.setForeground(Color.BLUE);
				}else if(bojaFigure.equals(FigureColour.GREEN))
				{
					labela.setForeground(Color.GREEN);
				}else
				{
					labela.setForeground(Color.YELLOW);
				}
				labela.setText(imeFigurice);
			}
			counter++;
		}
	}
	public synchronized void postaviIzvucenuSliku(String putanjaDoSlike)
	{
		ImageIcon img = new ImageIcon(this.getClass().getResource(putanjaDoSlike));
		this.pictureLabel.setIcon(img);
	}
	public void postaviBojuPolja(int pozicija,Color boja)
	{
		int counter =1;
		for(var labela : matrica)
		{
			if(counter==pozicija)
			{
				labela.setForeground(boja);
				break;
			}
			counter++;
		}
	}
	public synchronized void postaviBrojNaLabelu(int pozicija)
	{

		int counter =1;
		for(var labela : matrica)
		{
			if(counter==pozicija)
			{
				labela.setText(Integer.toString(pozicija));
				break;
			}
			counter++;
		}
	}
	public synchronized void  drawDiamond(int position)
	{
		int counter = 1;
		for(var labela : matrica)
		{
			if(counter==position)
			{
				ImageIcon img = new ImageIcon(this.getClass().getResource("/mushroom.png"));
				labela.setIcon(img);
				break;
			}
			counter++;
		}
	}
	public  synchronized void removeDiamond(int position)
	{
		int counter = 1;
		for(var labela : matrica)
		{
			if(counter==position)
			{
				labela.setBackground(Color.LIGHT_GRAY);
				labela.setIcon(null);
				break;
			}
			counter++;
		}
	}
	public synchronized void drawHole(int position)
	{
		int counter = 1;
		for(var labela : matrica)
		{
			if(counter==position)
			{
				labela.setBackground(Color.BLACK);
				break;
			}
			counter++;
		}
	}
	public synchronized void removeHole(int position)
	{
		int counter = 1;
		for(var labela : matrica)
		{
			if(counter==position)
			{
				labela.setBackground(Color.LIGHT_GRAY);
				labela.revalidate();
				break;
			}
			counter++;
		}
	}
	public void setPotez(String potez)
	{
		this.opisPoteza.setText(potez);
	}
}
