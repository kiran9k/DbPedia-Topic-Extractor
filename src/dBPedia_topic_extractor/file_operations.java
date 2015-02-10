package dBPedia_topic_extractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class file_operations {
	public static  String read_from_file(String fileName)
	{   // The name of the file to open.
        
        // This will reference one line at a time
        //String line = null;
        String output="";
        try {
        	 File fileDir = new File(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null ) {
            	if(line.length()<=1)
            	{
            		line = br.readLine();
            		continue;
            	}
                sb.append(line);
                sb.append(". ");
                line = br.readLine();
            }
           output = sb.toString();
        } finally {
            br.close();
        }
        }
     
        catch(FileNotFoundException ex) 
        {
            System.out.println( "Unable to open file '" + fileName + "'");			
        }
        catch(IOException ex) {
            System.out.println("Error reading file '"+ fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }
        return output;
	}
	public static void write_to_file(String content,String filename)
	{
		try {			 
			//String content = "This is the content to write into file"; 
			File file = new File(filename);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			} 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
