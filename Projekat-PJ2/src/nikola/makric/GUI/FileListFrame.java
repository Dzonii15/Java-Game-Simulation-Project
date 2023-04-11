package nikola.makric.GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Font;
import java.awt.Component;
import javax.swing.border.MatteBorder;

public class FileListFrame extends JFrame {

	private JPanel contentPane;
	private JTable table;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					//FileFrame frame = new FileFrame();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 */
	public FileListFrame(File[] files) {
		Image img = new ImageIcon(this.getClass().getResource("/diamond.png")).getImage();
		setIconImage(img);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 720, 480);
		Object[][]data = new Object[files.length][1];
		for(int i = 0;i<files.length;i++)
		{
			data[i][0]=files[i].getName();
		}
		String []columnNames = {"Odigrane Partije"};
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		table = new JTable(10,10);
		table.setAlignmentX(Component.LEFT_ALIGNMENT);
		table.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
		table.setBackground(Color.PINK);
		table.addMouseListener(new MouseAdapter()
				{
					@Override
					public void mouseClicked(MouseEvent e)
					{
						JTable target =(JTable)e.getSource();
						int row = target.getSelectedRow();
						int column = target.getSelectedColumn();
						String opcija = (String)table.getValueAt(row, column);
						File file = new File("PrethodneIgre\\"+opcija);
						BufferedReader reader = null;
						try {
							reader = new BufferedReader(new FileReader (file));
						} catch (FileNotFoundException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
					    String         line = null;
					    StringBuilder  stringBuilder = new StringBuilder();
					    String         ls = System.getProperty("line.separator");

					    try {
					        while((line = reader.readLine()) != null) {
					            stringBuilder.append(line);
					            stringBuilder.append(ls);
					        }

					        String result =  stringBuilder.toString();
					        new FileFrame(result);
					    }catch(Exception ex)
					    {
					    	
					    }finally {
					        try {
								reader.close();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
					    }
						
					}
				});
		
		contentPane.setLayout(new BorderLayout(0, 0));
		table.setModel(new DefaultTableModel(data,columnNames)
				{
					boolean[]columnEditables = new boolean[] {false};
					public boolean isCellEditable(int row,int column)
					{
						return columnEditables[column];
					}
				});
		
		contentPane.add(table);
		setVisible(true);
	}
}
