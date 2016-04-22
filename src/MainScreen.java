import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import javax.swing.JTable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import javax.swing.JLabel;
import java.awt.Button;
import javax.swing.JSlider;

public class MainScreen extends JFrame {

	public static final String[] CATEGORIES = { "Name", "Author", "Album", "Genre" };
	
	private JTextField textField;
	private JComboBox<String> categoryChoice;
	private DefaultTableModel tableModel;
	private JTable table;
	private TableRowSorter<TableModel> rowSorter;
	private JFileChooser fc;
	private int lastSelectedColumnIndex;
	private ArrayList<Song> songs;

	public MainScreen() {

		setTitle("MP3 Player");
		setBounds(100, 100, 600, 500);
		setUIComponents();

		songs = new ArrayList<>();

	}

	private void setUIComponents() {
		setMenu();

		JPanel controlsPanel = new JPanel();
		getContentPane().add(controlsPanel, BorderLayout.SOUTH);

		Button btnPlay = new Button("PLAY");
		controlsPanel.add(btnPlay);
		btnPlay.addActionListener(btnPlayAL);

		JSlider slider = new JSlider();
		slider.setValue(0);
		controlsPanel.add(slider);

		Button btnStop = new Button("STOP");
		controlsPanel.add(btnStop);
		btnStop.addActionListener(btnStopAL);

		JPanel settingsPanel = new JPanel();
		getContentPane().add(settingsPanel, BorderLayout.NORTH);

		JLabel lblSearch = new JLabel("Search:");

		textField = new JTextField();
		textField.setColumns(10);

		categoryChoice = new JComboBox<>(CATEGORIES);
		categoryChoice.addActionListener(categoryChoiceAL);

		settingsPanel.add(lblSearch);
		settingsPanel.add(textField);
		settingsPanel.add(categoryChoice);

		setPlaylist();

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

	@SuppressWarnings("serial")
	private void setPlaylist() {

		tableModel = new DefaultTableModel(null, CATEGORIES) {

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

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

	private void updatePlaylist() {

		if (this.songs != null) {
			tableModel.setRowCount(0);

			int size = this.songs.size();
			String[][] data = new String[size][CATEGORIES.length];

			for (int i = 0; i < size; i++) {

				data[i][0] = this.songs.get(i).getTitle();
				data[i][1] = this.songs.get(i).getArtist();
				data[i][2] = this.songs.get(i).getAlbum();
				data[i][3] = this.songs.get(i).getGenre();

				tableModel.addRow(data[i]);
			}

			tableModel.fireTableDataChanged();
			sortTable(0);
		}
	}

	public int getSelectedSongIndex(Vector<String> selectedRow) {
		int index = -1;

		String[] rowValues = { (String) selectedRow.get(0), (String) selectedRow.get(1), (String) selectedRow.get(2),
				(String) selectedRow.get(3) };

		for (Song s : this.songs) {
			index++;
			if (s.getTitle().equals(rowValues[0]) && s.getArtist().equals(rowValues[1])
					&& s.getAlbum().equals(rowValues[2]) && s.getGenre().equals(rowValues[3])) {
				return index;
			}
		}

		return -1;
	}

	public RowFilter<TableModel, Integer> getSearchFilter(String searchField) {
		RowFilter<TableModel, Integer> rowFilter = RowFilter.regexFilter("(?i)" + searchField,
				categoryChoice.getSelectedIndex());

		if (searchField.trim().length() == 0) {
			return null;
		} else {
			return rowFilter;
		}
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
				songs.add(new Song(file));

				updatePlaylist();
			}
		}
	};

	ActionListener addFolderAL = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			fc = new JFileChooser(getUserDefaultMusicFolder());
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

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

				for (File f : files) {
					songs.add(new Song(f));
				}

				updatePlaylist();
			}

		}
	};

	ActionListener removeFileAL = new ActionListener() {

		@SuppressWarnings("rawtypes")
		@Override
		public void actionPerformed(ActionEvent e) {

			if (table.getSelectedRowCount() > 0) {
				List<Vector> selectedRows = new ArrayList<>();
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				Vector rowData = model.getDataVector();
				for (int row : table.getSelectedRows()) {
					int modelRow = table.convertRowIndexToModel(row);

					@SuppressWarnings("unchecked")
					Vector<String> rowValue = (Vector) rowData.get(modelRow);

					int index = getSelectedSongIndex(rowValue);
					if (index != -1) {
						songs.remove(index);
					}

					selectedRows.add(rowValue);
				}

				for (Vector rowValue : selectedRows) {
					int rowIndex = rowData.indexOf(rowValue);
					model.removeRow(rowIndex);
				}
			}

			table.clearSelection();
		}
	};

	ActionListener removeAllFilesAL = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			songs.clear();
			tableModel.setRowCount(0);
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
			rowSorter.setRowFilter(getSearchFilter(textField.getText()));
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			rowSorter.setRowFilter(getSearchFilter(textField.getText()));
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
			rowSorter.setRowFilter(getSearchFilter(textField.getText()));
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

	FileInputStream FIS;
	BufferedInputStream BIS;

	public Player player;

	// method to stop playing a song
	public void stop() {
		if (player != null) {
			player.close();
		}
	}

	ActionListener btnStopAL = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			stop();

		}
	};

	// method to play a song
	public void play(String path) {
		try {
			FIS = new FileInputStream(path);
			BIS = new BufferedInputStream(FIS);

			player = new Player(BIS);

		} catch (FileNotFoundException | JavaLayerException e) {

		}

		new Thread() {
			@Override
			public void run() {
				try {
					player.play();
				} catch (JavaLayerException e) {

				}
			}
		}.start();
	}

	ActionListener btnPlayAL = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			int selectedRows = table.getSelectedRowCount();
			
			if (selectedRows == 1) {
				// List<Vector> selectedRows = new ArrayList<>();
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				Vector<?> rowData = model.getDataVector();
				for (int row : table.getSelectedRows()) {
					int modelRow = table.convertRowIndexToModel(row);

					@SuppressWarnings("unchecked")
					Vector<String> rowValue = (Vector<String>) rowData.get(modelRow);

					int index = getSelectedSongIndex(rowValue);
					if (index != -1) {
						play(songs.get(index).getPath());
						setTitle(songs.get(index).getTitle());
					}

				}
			} else if(selectedRows == 0){
				JOptionPane.showMessageDialog(MainScreen.this, "No selected song! Select a song and then click a Play button.");
			}else {
				JOptionPane.showMessageDialog(MainScreen.this, "You are selected more than one song! To play song, select one and click a Play button again.");
				table.clearSelection();
			}
						
		}
	};

}
