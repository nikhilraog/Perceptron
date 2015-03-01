import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;


public class PostingList {

	public String doc_name;
	public int term_frequency;
	public String this_word;



	public PostingList(String filename, String word, String filepath) throws Exception{
		doc_name = filename;
		int term_count =0;

		File f = new  File(filepath);
		BufferedReader reader = new BufferedReader(new FileReader(f));

		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			for (String w : line.split("\\W+")) {
				
				String _w = w.toLowerCase();
				if(_w.equals(word)){
					term_count = term_count+1;
				}

			}
		}
		reader.close();

		this_word = word;
		term_frequency = term_count;


	}



	@Override
	public String toString() {
		return "PostingList [doc_name=" + doc_name + ", term_frequency="
				+ term_frequency + "]";
	}

}



