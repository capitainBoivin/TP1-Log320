import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;

public class Compresseur {
	
	private static File getFile() {
		BufferedReader buffReader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Entrer le lien du texte à décompresser.");
		File file = null;
		try {
			String filePath = "file:///" + buffReader.readLine();
			URL url = new URL(filePath);
			file = new File(url.getFile());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	}
	
	private static void readFile(File f){
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(f);
			char current;
			
			while (fileInputStream.available() > 0) {
				current = (char) fileInputStream.read();
				System.out.print(current);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	public static void main(String[] args) {
		File f = getFile();
		readFile(f);
		
	}

}
