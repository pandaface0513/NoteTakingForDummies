import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List; // we don't want java.awt.List

/**
 * WordFinder is an interface for searching a word list. When the user types any
 * part of a word, the interface displays all the words that match.
 */
public class WordFinder extends JFrame implements KeyListener {

	private WordList words = new WordList();

	/**
	 * Make a WordFinder window.
	 */
	private static final int WINDOW_WIDTH = 600;
	private static final int WINDOW_HEIGHT = 400;

	// initial GUI Components

	// text field for input
	public JTextField queryText = new JTextField(30);

	// menu bar
	JMenuBar menuBar;
	JMenu menu;
	JMenuItem menuOpen, menuExit;

	// labels
	JLabel findTxt = new JLabel("Find: ");
	JLabel resultTxt = new JLabel("press enter to initial query.");

	// button
	JButton cBtn = new JButton("Clear");

	// scroll pane
	JTextArea resultText = new JTextArea(5, 5);
	JScrollPane resultScroll = new JScrollPane(resultText);

	// arraylist
	List<String> lst = new ArrayList<String>();

	// file chooser
	final JFileChooser fc = new JFileChooser();

	// file path holder
	String filePath = "words.txt";

	// file input stream
	FileInputStream in;

	public WordFinder() {
		super("Word Finder");

		// initial the JFrame
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

		// call System.exit() when user closes the window
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// initial title
		setTitle("WORD FINDER");

		// initial menu bar
		menuBar = new JMenuBar();

		menu = new JMenu("File");

		menuOpen = new JMenuItem("Open...");
		menuOpen.addActionListener(openFile);

		menuExit = new JMenuItem("Exit");
		menuExit.addActionListener(exit);

		menu.add(menuOpen);
		menu.add(menuExit);

		menuBar.add(menu);

		setJMenuBar(menuBar);

		// initial the rest of GUI
		Container c = getContentPane();

		// group layout
		GroupLayout layout = new GroupLayout(c);

		c.setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout
				.createSequentialGroup()
				.addComponent(findTxt)
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.LEADING)
								.addComponent(queryText)
								.addComponent(resultTxt)
								.addComponent(resultScroll)).addComponent(cBtn));
		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(findTxt).addComponent(queryText)
								.addComponent(cBtn)).addComponent(resultTxt)
				.addComponent(resultScroll));

		// initial text field
		queryText.setEditable(true);
		resultText.setEditable(false);
		queryText.setBackground(Color.WHITE);
		queryText.addKeyListener(this);

		// initial button
		cBtn.addActionListener(clear);
		
		//adding keyboard for clear
        cBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0), "c");
        cBtn.getActionMap().put("c", clear);

		File f = new File(filePath);
		if (f.exists()) {
			displayList(filePath);
		}else{
			//alert
			JOptionPane.showMessageDialog(null, "Can't find file " + filePath + " please open the text file manually.");
		}
	}

	private void displayList(String filePath) {
		// initial scrollpane text
		BufferedReader br = null;
		File file = new File(filePath);
		try {
			String curLine;
			int counter = 0;
			FileReader reader = new FileReader(file);
			br = new BufferedReader(reader);

			StringBuilder sb = new StringBuilder(); // a string holder, i found
													// it lessen the resource
													// load

			while ((curLine = br.readLine()) != null) {
				sb.append(curLine + "\n");
				counter++;
			}

			resultText.setText(sb.toString()); // now set the JTextField with
												// the stringbuilder
			resultText.setCaretPosition(0); // reset the scroll back to the
											// top...
			// display result on label
			resultTxt.setText(counter + " words total");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		// send the file to wordlist.java
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException a) {
			// TODO Auto-generated catch block
			a.printStackTrace();
		}
		try {
			words.load(in);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	// new abstract action for the clear button
	Action clear = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {
			queryText.setText("");
			displayList(filePath);
		}
	};

	// new abstract action for the search field
	Action openFile = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {
			int returnVal = fc.showOpenDialog(WordFinder.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				// this is where a real application would open the file.
				filePath = file.getPath();
				displayList(filePath);
			}
		}
	};

	// new abstract action for the exit option
	Action exit = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	};

	// new abstract action for the search field
	private void search(String q) {
		String tmpQuery = q;

		// try find the word in the file
		List find = words.find(tmpQuery);

		// display result on label
		if(q.isEmpty()){
			resultTxt.setText(find.size() + " words total");
		}else{
			resultTxt.setText(find.size() + " words containing ' " + q + " '");
		}
		
		// //show result list
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < find.size(); i++) {
			sb.append(find.get(i) + "\n");
		}

		resultText.setText(sb.toString());
		resultText.setCaretPosition(0); // reset the scroll back to the top...
	}

	/**
	 * Main method. Makes and displays a WordFinder window.
	 * 
	 * @param args
	 *            Command-line arguments. Ignored.
	 */
	public static void main(String[] args) {
		// In general, Swing objects should only be accessed from
		// the event-handling thread -- not from the main thread
		// or other threads you create yourself. SwingUtilities.invokeLater()
		// is a standard idiom for switching to the event-handling thread.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Make and display the WordFinder window.
				new WordFinder().show();
			}
		});
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		String tmpText = queryText.getText();
		search(tmpText);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}
}
