import java.io.BufferedReader;
import java.io.IOException;

public class Handler {
	
	String currentLine;
	BufferedReader br;
	Handler(BufferedReader br) {
		this.br = br;
		readFromFile();
	}
	
	public void readFromFile() {
		try {
			this.currentLine = this.br.readLine();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public String getCurrentLine() {
		return this.currentLine;
	}

    public String readAndRemoveFromFile() {
    	String previousCurrentLine = getCurrentLine();
    	readFromFile();
    	return previousCurrentLine;
    }
    
    public void close() {
    	try {
			this.br.close();
		} catch (IOException e) {
			System.out.println(e);
		}
    }
    
    public boolean isEmpty() {
    	if(this.currentLine == null) {
    		close();
    		return true;
    	}
    	return false;
    }
}