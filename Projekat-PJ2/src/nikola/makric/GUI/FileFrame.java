package nikola.makric.GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.io.File;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;

public class FileFrame extends JFrame {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	

	/**
	 * Create the dialog.
	 */
	public FileFrame(String contentsOfFile) {
		Image img = new ImageIcon(this.getClass().getResource("/diamond.png")).getImage();
		setIconImage(img);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(720,480);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JTextArea textArea = new JTextArea();
			textArea.setEditable(false);
			contentPanel.add(textArea, BorderLayout.CENTER);
			textArea.setText(contentsOfFile);
		}
		
		setVisible(true);
		
	}

}
