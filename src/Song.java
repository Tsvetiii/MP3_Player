import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JOptionPane;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Song {
	private String title;
	private String artist;
	private String album;
	private String genre;
	private String path;

	/**
	 * Constructor
	 * 
	 * @param file - mp3 file source
	 */
	public Song(File file) {

		setPath(file.getPath());

		try {

			InputStream input = new FileInputStream(new File(path));
			ContentHandler handler = new DefaultHandler();
			Metadata metadata = new Metadata();
			Parser parser = new Mp3Parser();
			ParseContext parseCtx = new ParseContext();
			parser.parse(input, handler, metadata, parseCtx);
			input.close();

			setTitle(metadata.get("title"));
			setArtist(metadata.get("xmpDM:artist"));
			setAlbum(metadata.get("xmpDM:album"));
			setGenre(metadata.get("xmpDM:genre"));

		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, Strings.FILE_NOT_FOUND);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (TikaException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Getter for song absolute path
	 * @return Returns string with absolute path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Setter for song absolute path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Getter for song title
	 * @return Returns song title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Setter for song title
	 */
	public void setTitle(String title) {
		if (title == null || title.equals("")) {
			this.title = Strings.NO_INFO;
		} else {
			this.title = title;
		}
	}

	/**
	 * Getter for song artist
	 * @return Returns song artist
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * Setter for song artist
	 */
	public void setArtist(String artist) {
		if (artist == null || artist.equals("")) {
			this.artist = Strings.NO_INFO;
		} else {
			this.artist = artist;
		}
	}

	/**
	 * Getter for album
	 * @return Returns song album
	 */
	public String getAlbum() {
		return album;
	}

	/**
	 * Setter for album
	 */
	public void setAlbum(String album) {
		if (album == null || album.equals("")) {
			this.album = Strings.NO_INFO;
		} else {
			this.album = album;
		}
	}

	/**
	 * Getter for genre
	 * @return Returns song genre
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * Setter for genre
	 */
	public void setGenre(String genre) {
		if (genre == null || genre.equals("")) {
			this.genre = Strings.NO_INFO;
		} else {
			this.genre = genre;
		}
	}

}
