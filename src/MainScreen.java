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
import java.io.FilenameFilter;
import java.io.IOException;
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
import javazoom.jl.player.advanced.AdvancedPlayer;

import javax.swing.JLabel;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.Point;

public class MainScreen extends JFrame {

	private JTextField searchField;
	private JComboBox<String> categoryChoice;
	private DefaultTableModel tableModel;
	private JTable table;
	private TableRowSorter<TableModel> rowSorter;
	private int lastSelectedColumnIndex;
	private ArrayList<Song> songs;
	private boolean isPlaying;
	private String currentPlayingSongPath = null;
	private FileInputStream FIS;
	private BufferedInputStream BIS;
	private AdvancedPlayer player;

	/**
	 * Constructor
	 */
	public MainScreen() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setDefaultFrameTitle();
		setBounds(100, 100, 800, 600);
		setUIComponents();

		songs = new ArrayList<>();
		isPlaying = false;
	}

	/**
	 * Set title string
	 */
	private void setDefaultFrameTitle() {
		setTitle(Strings.TITLE_NAME);
	}

	/**
	 * Load UI components
	 */
	private void setUIComponents() {
		setMenu();

		JPanel controlsPanel = new JPanel();
		getContentPane().add(controlsPanel, BorderLayout.SOUTH);

		Button btnPlay = new Button("PLAY");
		btnPlay.setPreferredSize(new Dimension(50, 30));
		controlsPanel.add(btnPlay);
		btnPlay.addActionListener(btnPlayAL);

		Button btnStop = new Button("STOP");
		btnStop.setPreferredSize(new Dimension(50, 30));
		controlsPanel.add(btnStop);
		btnStop.addActionListener(btnStopAL);

		JPanel settingsPanel = new JPanel();
		getContentPane().add(settingsPanel, BorderLayout.NORTH);

		JLabel lblSearch = new JLabel("Search:");

		searchField = new JTextField();
		searchField.setColumns(10);

		categoryChoice = new JComboBox<>(Strings.CATEGORIES);
		categoryChoice.addActionListener(categoryChoiceAL);

		settingsPanel.add(lblSearch);
		settingsPanel.add(searchField);
		settingsPanel.add(categoryChoice);

		setPlaylist();

	}

	/**
	 * Set a menu components
	 */
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

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);

		mntmAddFile.addActionListener(addFileAL);
		mntmAddFolder.addActionListener(addFolderAL);
		mntmRemoveFile.addActionListener(removeFileAL);
		mntmRemoveAllFiles.addActionListener(removeAllFilesAL);
		mntmExit.addActionListener(exitAL);
		mntmAbout.addActionListener(helpAboutAL);
	}

	/**
	 * Set a JTable for playlist
	 */
	@SuppressWarnings("serial")
	private void setPlaylist() {

		tableModel = new DefaultTableModel(null, Strings.CATEGORIES) {

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

		searchField.getDocument().addDocumentListener(tableSearchAL);
		table.getTableHeader().addMouseListener(tableHeaderClickAL);
		table.addMouseListener(doubleClickTableRow);

	}

	/**
	 * Update playlist with song's metadata
	 */
	private void updatePlaylist() {

		if (this.songs != null) {
			tableModel.setRowCount(0);

			int size = this.songs.size();
			String[][] data = new String[size][Strings.CATEGORIES.length];

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

	/**
	 * 
	 * @param selectedRow
	 * @return Returns number of selected row index in list of songs. Returns
	 *         value -1 when no match with selection in songs list.
	 */
	private int getSelectedSongIndex(Vector<String> selectedRow) {
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

	/**
	 * 
	 * @param searchField
	 * @return Returns RowFilter specified by user text input and category
	 *         choice. For empty string for search, method returns null.
	 */
	private RowFilter<TableModel, Integer> getSearchFilter(String searchField) {
		RowFilter<TableModel, Integer> rowFilter = RowFilter.regexFilter("(?i)" + searchField,
				categoryChoice.getSelectedIndex());

		if (searchField.trim().length() == 0) {
			return null;
		} else {
			return rowFilter;
		}
	}

	/**
	 * Sorting JTable data
	 * 
	 * @param colIndex
	 */
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

	/**
	 * Get path to music folder on user
	 * 
	 * @return Returns path string.
	 */
	private String getUserDefaultMusicFolder() {
		return System.getProperty("user.home") + System.getProperty("file.separator") + Strings.MUSIC_FOLDER_NAME;
	}

	/**
	 * Play a song.
	 * 
	 * @param path
	 *            - song absolute path on drive
	 */
	private void play(String path) {

		try {
			FIS = new FileInputStream(path);
			BIS = new BufferedInputStream(FIS);

			player = new AdvancedPlayer(BIS);

		} catch (IOException | JavaLayerException e) {
			currentPlayingSongPath = null;
			JOptionPane.showMessageDialog(MainScreen.this, Strings.FILE_NOT_FOUND);
		}

		new Thread() {
			@Override
			public void run() {
				try {
					isPlaying = true;
					currentPlayingSongPath = path;
					player.play();

				} catch (JavaLayerException e) {
					currentPlayingSongPath = null;
					JOptionPane.showMessageDialog(MainScreen.this, Strings.FILE_NOT_FOUND);
				}
			}
		}.start();

	}

	/**
	 * Stop playing a song
	 */
	private void stop() {
		if (player != null) {
			player.close();
			isPlaying = false;
			setDefaultFrameTitle();
		}
	}

	/**
	 * Get a selected row in JTable and launch song playing
	 * 
	 * @param row
	 *            - selected row in JTable
	 */
	private void playSelectedRow(int row) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		Vector<?> rowData = model.getDataVector();
		int modelRow = table.convertRowIndexToModel(row);

		@SuppressWarnings("unchecked")
		Vector<String> rowValue = (Vector<String>) rowData.get(modelRow);

		int index = getSelectedSongIndex(rowValue);
		if (index != -1) {
			if (isPlaying) {
				stop();
			}
			play(songs.get(index).getPath());
			setTitle(Strings.TITLE_NAME + ": " + songs.get(index).getTitle());
		}

	}

	/**
	 * ActionListener for file->addFile menu. Add selected file like song in
	 * JTable and song's list
	 */
	ActionListener addFileAL = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser(getUserDefaultMusicFolder());
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

	/**
	 * ActionListener for file->addFolder menu. Add songs from selected folder
	 * in JTable and song's list
	 */
	ActionListener addFolderAL = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser(getUserDefaultMusicFolder());
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

	/**
	 * ActionListener for file->removeFile menu. Remove selected songs from
	 * JTable and song's list
	 */
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

						if (isPlaying && currentPlayingSongPath.equals(songs.get(index).getPath())) {
							stop();
						}

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

	/**
	 * ActionListener for file->removeAllFiles menu. Remove all songs in JTable
	 * and song's list
	 */
	ActionListener removeAllFilesAL = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (isPlaying) {
				stop();
			}
			songs.clear();
			tableModel.setRowCount(0);
		}
	};

	/**
	 * ActionListener for file->exit menu. Stop playing song and close a frame.
	 */
	ActionListener exitAL = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (isPlaying) {
				stop();
			}
			dispose();
		}
	};

	/**
	 * ActionListener for help->about menu. Display a information dialog about
	 * program
	 */
	ActionListener helpAboutAL = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(MainScreen.this, Strings.HELP_ABOUT_MESSAGE, "About",
					JOptionPane.INFORMATION_MESSAGE);
		}
	};

	/**
	 * ActionListener for play button
	 */
	ActionListener btnPlayAL = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			int selectedRows = table.getSelectedRowCount();

			if (selectedRows == 1) {
				int row = table.getSelectedRow();
				playSelectedRow(row);
			} else if (selectedRows == 0) {
				JOptionPane.showMessageDialog(MainScreen.this, Strings.NOT_SELECTED);
			} else {
				JOptionPane.showMessageDialog(MainScreen.this, Strings.MORE_SELECTED);
			}

		}
	};

	/**
	 * ActionListener for stop button
	 */
	ActionListener btnStopAL = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			stop();
		}
	};

	/**
	 * ActionListener for drop-down menu with song's categories of metadata
	 */
	ActionListener categoryChoiceAL = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			rowSorter.setRowFilter(getSearchFilter(searchField.getText()));
		}
	};

	/**
	 * DocumentListener for change JTable content by sort filter
	 */
	DocumentListener tableSearchAL = new DocumentListener() {

		@Override
		public void insertUpdate(DocumentEvent e) {
			rowSorter.setRowFilter(getSearchFilter(searchField.getText()));
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			rowSorter.setRowFilter(getSearchFilter(searchField.getText()));
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	};

	/**
	 * MouseListener for click on JTable header for sorting songs
	 */
	MouseListener tableHeaderClickAL = new MouseAdapter() {

		@Override
		public void mouseClicked(MouseEvent e) {
			int colIndex = table.getColumnModel().getColumnIndexAtX(e.getX());
			sortTable(colIndex);
		}
	};

	/**
	 * MouseListener for double click on JTable row for play song
	 */
	MouseListener doubleClickTableRow = new MouseAdapter() {
		public void mousePressed(MouseEvent me) {
			table = (JTable) me.getSource();
			Point p = me.getPoint();
			int row = table.rowAtPoint(p);
			if (me.getClickCount() == 2 && row != -1) {
				playSelectedRow(row);
			}
		}
	};
}
