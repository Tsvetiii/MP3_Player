import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (TikaException e) {
			e.printStackTrace();
		}

	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if (title == null || title.equals("")) {
			this.title = Strings.NO_INFO;
		} else {
			this.title = title;
		}
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		if (artist == null || artist.equals("")) {
			this.artist = Strings.NO_INFO;
		} else {
			this.artist = artist;
		}
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		if (album == null || album.equals("")) {
			this.album = Strings.NO_INFO;
		} else {
			this.album = album;
		}
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		if (genre == null || genre.equals("")) {
			this.genre = Strings.NO_INFO;
		} else {
			this.genre = genre;
		}
	}

}
