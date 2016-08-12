package hr.ericsson.pegasus.gui;

import hr.ericsson.pegasus.Pegasus;

import java.awt.Font;
import java.io.IOException;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JTextArea;

/**
 * <H1>Main application window</H1>
 * <HR>
 * This class holds some static data and definitions
 * accessible by other classes in this package.<BR>
 * <BR>
 * Main window is build by adding menu bar, and tab panels.<BR>
 * <BR>
 * Methods in {@link Pegasus} use functions {@link #log(String)} and
 * {@link #append(String)} for printing messages to Console Log {@link JScrollPaneLogs}.
 * <HR>
 * @author igor.delac@gmail.com
 *
 */
public class JFrameGui extends JFrame {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Holds reference for {@link JTextAreaLogs}.
	 */
	static JTextArea jLogArea;
	
	/**
	 * Default Font used across elements of GUI.
	 */
	public static Font defaultFont = new Font("Monospaced", Font.PLAIN, 16);
	
	/**
	 * Define if user wish to see in statistic table total values or delta (per sec.).
	 */
	static JCheckBoxMenuItem deltaStatistic;
	
	/**
	 * Main window, title, location, size and default close operation are set.
	 */
	public JFrameGui() {
		setTitle("Pegasus v" + Pegasus.ver);
		setSize(800, 600);
		setLocation(120, 120);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		add(new JTabbedPaneMain());
		setJMenuBar(new JMenuBarMain());
	}
	
	/**
	 * Log a line to text area.
	 * @param text single line
	 */
	public void log(String text) {
		jLogArea.append(text);
		jLogArea.append("\n");
		jLogArea.setCaretPosition(jLogArea.getText().length());
	}
	
	/**
	 * Log without LF.
	 * @param text continuous line 
	 */
	public void append(String text) {
		jLogArea.append(text);
		jLogArea.setCaretPosition(jLogArea.getText().length());
	}

	/**
	 * Start {@link Pegasus#main()} in new thread.
	 * @param args
	 */
	public void startMain(final String args) {
	
		Runnable doRun = new Runnable() {

			@Override
			public void run() {
				try {
					Pegasus.main(args.split(" "));
				} catch (IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		(new Thread(doRun, "Main Process")).start();
	}
	
}
