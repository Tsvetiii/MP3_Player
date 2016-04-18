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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.JLabel;
import java.awt.Button;
import javax.swing.JSlider;

public class MainScreen extends JFrame {

	public static final String[] categories = { "Name", "Author", "Album", "Genre" };

	private JTextField textField;
	private JComboBox<String> categoryChoice;
	private DefaultTableModel tableModel;
	private JTable table;
	private TableRowSorter<TableModel> rowSorter;
	private RowFilter<TableModel, Integer> rowFilter;
	private JFileChooser fc;
	private int lastSelectedColumnIndex;

	public MainScreen() {

		setTitle("MP3 Player");
		setBounds(100, 100, 600, 500);
		setMenu();
		setUIComponents();

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

	private void setUIComponents() {

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

		categoryChoice = new JComboBox<>(categories);
		categoryChoice.addActionListener(categoryChoiceAL);

		settingsPanel.add(lblSearch);
		settingsPanel.add(textField);
		settingsPanel.add(categoryChoice);

		String[][] data = { { "Kathy", "Smith", "Snowboarding", "Rock" }, { "John", "Doe", "Rowing", "Metal" },
				{ "Sue", "Black", "Knitting", "Turbo Folk" }, { "Jane", "White", "Speed reading", "Country" },
				{ "Joe", "Brown", "Pool", "Hip-hop" }, { "Kathy", "Smith", "Snowboarding", "Rock" },
				{ "John", "Doe", "Rowing", "Metal" }, { "Sue", "Black", "Knitting", "Turbo Folk" },
				{ "Jane", "White", "Speed reading", "Country" }, { "Joe", "Brown", "Pool", "Hip-hop" },
				{ "Kathy", "Smith", "Snowboarding", "Rock" }, { "John", "Doe", "Rowing", "Metal" },
				{ "Sue", "Black", "Knitting", "Turbo Folk" }, { "Jane", "White", "Speed reading", "Country" },
				{ "Joe", "Brown", "Pool", "Hip-hop" }, { "Kathy", "Smith", "Snowboarding", "Rock" },
				{ "John", "Doe", "Rowing", "Metal" }, { "Sue", "Black", "Knitting", "Turbo Folk" },
				{ "Jane", "White", "Speed reading", "Country" }, { "Joe", "Brown", "Pool", "Hip-hop" },
				{ "Kathy", "Smith", "Snowboarding", "Rock" }, { "John", "Doe", "Rowing", "Metal" },
				{ "Sue", "Black", "Knitting", "Turbo Folk" }, { "Jane", "White", "Speed reading", "Country" },
				{ "Joe", "Brown", "Pool", "Hip-hop" }, { "Kathy", "Smith", "Snowboarding", "Rock" },
				{ "John", "Doe", "Rowing", "Metal" }, { "Sue", "Black", "Knitting", "Turbo Folk" },
				{ "Jane", "White", "Speed reading", "Country" }, { "Joe", "Brown", "Pool", "Hip-hop" } };

		setPlaylist(data);

	}

	private void setPlaylist(String[][] data) {

		tableModel = new DefaultTableModel(data, categories);
		table = new JTable(tableModel);
		rowSorter = new TableRowSorter<>(table.getModel());

		table.setRowSorter(rowSorter);
		table.setAutoCreateColumnsFromModel(false);
		table.getTableHeader().setReorderingAllowed(false);
		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(table.getTableHeader());
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		table.setFillsViewportHeight(true);
		lastSelectedColumnIndex = -1;
		sortTable(0);

		textField.getDocument().addDocumentListener(tableSearchAL);
		table.getTableHeader().addMouseListener(tableHeaderClickAL);

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

			// sorting songs
			sortTable(0);
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

			// sorting songs
			sortTable(0);
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

	DocumentListener tableSearchAL = new DocumentListener() {

		@Override
		public void insertUpdate(DocumentEvent e) {
			String text = textField.getText();
			rowFilter = RowFilter.regexFilter("(?i)" + text, categoryChoice.getSelectedIndex());

			if (text.trim().length() == 0) {
				rowSorter.setRowFilter(null);
			} else {
				rowSorter.setRowFilter(rowFilter);
			}
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			String text = textField.getText();
			rowFilter = RowFilter.regexFilter("(?i)" + text, categoryChoice.getSelectedIndex());

			if (text.trim().length() == 0) {
				rowSorter.setRowFilter(null);
			} else {
				rowSorter.setRowFilter(rowFilter);
			}
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	};

	MouseListener tableHeaderClickAL = new MouseAdapter() {

		@Override
		public void mouseClicked(MouseEvent e) {
			int colIndex = table.getColumnModel().getColumnIndexAtX(e.getX());
			sortTable(colIndex);
		}
	};

	ActionListener categoryChoiceAL = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			String text = textField.getText();

			if (text.trim().length() == 0) {
				rowSorter.setRowFilter(null);
			} else {
				rowFilter = RowFilter.regexFilter("(?i)" + text, categoryChoice.getSelectedIndex());
				rowSorter.setRowFilter(rowFilter);
			}
		}
	};

	private void sortTable(int colIndex) {

		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		SortOrder order;

		if (lastSelectedColumnIndex == colIndex) {
			order = SortOrder.DESCENDING;
			lastSelectedColumnIndex = -1;
		} else {
			order = SortOrder.ASCENDING;
			lastSelectedColumnIndex = colIndex;
		}

		sortKeys.add(new RowSorter.SortKey(colIndex, order));
		rowSorter.setSortKeys(sortKeys);

	}

	private String getUserDefaultMusicFolder() {
		return System.getProperty("user.home") + System.getProperty("file.separator") + "Music";
	}

}
