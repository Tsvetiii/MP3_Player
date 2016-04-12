import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import javax.swing.JTable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import java.awt.Button;
import javax.swing.JSlider;

public class MainScreen extends JFrame {
	
	public static final String[] categories = { "Name", "Author", "Album", "Genre" };
	
	private JTextField textField;
	private DefaultTableModel tableModel;
	private JTable table;
	private JFileChooser fc;
	
	String hahohihi;

	public MainScreen() {
		setTitle("MP3 Player");
		setBounds(100, 100, 600, 500);
		
//		String[][] data = { { "Kathy", "Smith", "Snowboarding", "Rock" }, { "John", "Doe", "Rowing", "Metal" },
//				{ "Sue", "Black", "Knitting", "Turbo Folk" }, { "Jane", "White", "Speed reading", "Country" },
//				{ "Joe", "Brown", "Pool", "Hip-hop" }, { "Kathy", "Smith", "Snowboarding", "Rock" },
//				{ "John", "Doe", "Rowing", "Metal" }, { "Sue", "Black", "Knitting", "Turbo Folk" },
//				{ "Jane", "White", "Speed reading", "Country" }, { "Joe", "Brown", "Pool", "Hip-hop" },
//				{ "Kathy", "Smith", "Snowboarding", "Rock" }, { "John", "Doe", "Rowing", "Metal" },
//				{ "Sue", "Black", "Knitting", "Turbo Folk" }, { "Jane", "White", "Speed reading", "Country" },
//				{ "Joe", "Brown", "Pool", "Hip-hop" }, { "Kathy", "Smith", "Snowboarding", "Rock" },
//				{ "John", "Doe", "Rowing", "Metal" }, { "Sue", "Black", "Knitting", "Turbo Folk" },
//				{ "Jane", "White", "Speed reading", "Country" }, { "Joe", "Brown", "Pool", "Hip-hop" },
//				{ "Kathy", "Smith", "Snowboarding", "Rock" }, { "John", "Doe", "Rowing", "Metal" },
//				{ "Sue", "Black", "Knitting", "Turbo Folk" }, { "Jane", "White", "Speed reading", "Country" },
//				{ "Joe", "Brown", "Pool", "Hip-hop" }, { "Kathy", "Smith", "Snowboarding", "Rock" },
//				{ "John", "Doe", "Rowing", "Metal" }, { "Sue", "Black", "Knitting", "Turbo Folk" },
//				{ "Jane", "White", "Speed reading", "Country" }, { "Joe", "Brown", "Pool", "Hip-hop" } };

		setMenu();

		JPanel controlsPanel = new JPanel();
		getContentPane().add(controlsPanel, BorderLayout.SOUTH);

		Button btnPlay = new Button("PLAY");
		controlsPanel.add(btnPlay);

		JSlider slider = new JSlider();
		slider.setValue(0);
		controlsPanel.add(slider);

		Button btnStop = new Button("STOP");
		controlsPanel.add(btnStop);

		JPanel settingsPanel = new JPanel();
		getContentPane().add(settingsPanel, BorderLayout.NORTH);

		JLabel lblSearch = new JLabel("Search:");

		textField = new JTextField();
		textField.setColumns(10);

		JComboBox<String> categoryChoice = new JComboBox<>(categories);

		settingsPanel.add(lblSearch);
		settingsPanel.add(textField);
		settingsPanel.add(categoryChoice);

		tableModel = new DefaultTableModel(null, categories);
		table = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		table.setFillsViewportHeight(true);
	}

	private void setMenu() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmAddFile = new JMenuItem("Add file");
		mnFile.add(mntmAddFile);

		JMenuItem mntmAddFolder = new JMenuItem("Add folder");
		mnFile.add(mntmAddFolder);

		mnFile.addSeparator();

		JMenuItem mntmRemoveFile = new JMenuItem("Remove");
		mnFile.add(mntmRemoveFile);

		JMenuItem mntmRemoveAllFiles = new JMenuItem("Remove All");
		mnFile.add(mntmRemoveAllFiles);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);

		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);

		mntmAddFile.addActionListener(addFileAL);
		mntmAddFolder.addActionListener(addFolderAL);
		mntmRemoveFile.addActionListener(removeFileAL);
		mntmRemoveAllFiles.addActionListener(removeAllFilesAL);
		mntmExit.addActionListener(exitAL);
	}

	ActionListener addFileAL = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			fc = new JFileChooser(getUserDefaultMusicFolder());
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setAcceptAllFileFilterUsed(false);
			FileFilter filter = new FileNameExtensionFilter("MP3 File", "mp3");
			fc.addChoosableFileFilter(filter);
			int returnVal = fc.showOpenDialog(MainScreen.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
			}

		}
	};

	ActionListener addFolderAL = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			fc = new JFileChooser(getUserDefaultMusicFolder());
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			// create new filename filter
			FilenameFilter fileNameFilter = new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					if (name.lastIndexOf('.') > 0) {
						int lastIndex = name.lastIndexOf('.');
						String str = name.substring(lastIndex);
						if (str.equals(".mp3") || str.equals(".MP3")) {
							return true;
						}
					}
					return false;
				}
			};

			int returnVal = fc.showOpenDialog(MainScreen.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File[] files = fc.getSelectedFile().listFiles(fileNameFilter);
			}
		}
	};

	ActionListener removeFileAL = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			int[] lines = table.getSelectedRows();
			if (lines.length > 0) {
				for (int i = 0; i < lines.length; i++) {
					lines[i] = table.convertRowIndexToModel(lines[i]);
				}
				Arrays.sort(lines);
				for (int i = lines.length - 1; i >= 0; i--) {
					tableModel.removeRow(lines[i]);
				}
			}

			table.clearSelection();
		}
	};

	ActionListener removeAllFilesAL = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			DefaultTableModel tm = (DefaultTableModel) table.getModel();
			tm.setRowCount(0);
		}
	};

	ActionListener exitAL = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	};

	private String getUserDefaultMusicFolder() {
		return System.getProperty("user.home") + System.getProperty("file.separator") + "Music";
	}

}
