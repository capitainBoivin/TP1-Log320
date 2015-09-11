import java.io.*;
import java.net.URL;

public class Compresseur {
	
	private static File getFile() {
		BufferedReader buffReader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Entrer le lien du texte � d�compresser.");
		File file=null;
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
	
	public void readFile(File f){
		BufferedReader br = null;

		try {
			String current;

			br = new BufferedReader(new FileReader(f));

			while ((current = br.readLine()) != null) {
				System.out.println(current);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}	
	}



}
