package nikola.makric.GUI;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import nikola.makric.Simulation;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.awt.Dialog.ModalityType;
import java.awt.Toolkit;

public class FigurePathFrame extends JDialog {

	/**
	 * Launch the application.
	 */
	public ArrayList<JLabel> matrix = new ArrayList<>();
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					FigurePath dialog = new FigurePath("Test", null);
//					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//					dialog.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the dialog.
	 */
	public FigurePathFrame(String figureName, ArrayList<Integer> path) {
		//setIconImage(Toolkit.getDefaultToolkit().getImage(FigurePath.class.getResource("/Images/pieceicon.png")));
		setModal(true);
		setResizable(false);
		setBounds(100, 100, 500, 500);
		getContentPane().setLayout(new GridLayout(Simulation.matrixDimensions,Simulation.matrixDimensions, 0, 0));
		for(int i = 0; i < Simulation.matrixDimensions*Simulation.matrixDimensions; i++) { // dodavanje
			JLabel temp = new JLabel(Integer.toString(i+1));
			temp.setBorder(new LineBorder(new Color(0, 0, 0), 2));
			temp.setHorizontalAlignment(SwingConstants.CENTER);
			temp.setForeground(Color.WHITE);
			getContentPane().add(temp);
			matrix.add(temp);
		}
		this.setTitle(figureName);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		for(int i = 0; i < Simulation.matrixDimensions*Simulation.matrixDimensions; i++) {
			if(path.contains(i+1)) {
				matrix.get(i).setText("O");
				matrix.get(i).setForeground(Color.BLACK);
			}
		}
		
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setVisible(true);
	}

}
