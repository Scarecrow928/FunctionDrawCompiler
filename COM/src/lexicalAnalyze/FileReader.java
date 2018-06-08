package lexicalAnalyze;
import java.io.*;
import java.util.regex.Pattern;

public class FileReader {
	private File file;
	private DataInputStream dataInputStream;
	private char buf;
	private int position;

	public FileReader(String filename) throws IOException {
		super();
		this.file = new File(filename);
		this.dataInputStream = new DataInputStream(new FileInputStream(file));
		this.position = 0;
		buf = (char) this.dataInputStream.readByte();
		
	}

	public char next() throws IOException {
		try {
			if (position == 0) {
				position = 1;
				return buf;
			} else {
				buf = (char) this.dataInputStream.readByte();
				return buf;
			}
		} catch (EOFException e) {
			buf = (char) 0;
			return buf;
		}
	}
	
	public char back() {
		if (position == 0) {
			return 0;
		} else {
			position = 0;
			return buf;
		}
	}
	
	public static void main(String[] args) {
		try {
			String fileName = "aaa.ddd";
			String newFileName = fileName.substring(0, fileName.lastIndexOf('.'));
			System.out.println(newFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
