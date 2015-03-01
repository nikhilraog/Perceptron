import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@SuppressWarnings("unused")
public class PerceptronMain {


	static HashMap<String, ArrayList<PostingList>> post_map = new HashMap<String, ArrayList<PostingList>>(); 
	static Set<String> ham_fileset = new HashSet<String>();
	static Set<String> spam_fileset = new HashSet<String>();
	static Set<String> all_fileset = new HashSet<String>();
	static Set<String> vocab_set = new HashSet<String>();
	static HashMap<String, HashSet<String>> st = new HashMap<String, HashSet<String>>();
	static HashMap<String, HashMap<String,Integer>> ham_filemap = new HashMap<String, HashMap<String,Integer>>();
	static HashMap<String, HashMap<String,Integer>> spam_filemap = new HashMap<String, HashMap<String,Integer>>();

	public static void main(String[] args) throws Exception {


		String dir_location = args[0];

		File dir_spam_train = new File(dir_location+"/train/spam");
		File dir_ham_train = new File(dir_location+"/train/ham");

		File dir_spam_test = new File(dir_location+"/test/spam");
		File dir_ham_test = new File(dir_location+"/test/ham");


		for (File file : dir_ham_train.listFiles()) {
			String current_filename= file.getName();
			HashMap<String, Integer> ham_filevocab = new HashMap<String, Integer>();
			ham_fileset.add(current_filename);
			all_fileset.add(current_filename);

			BufferedReader reader = new BufferedReader(new FileReader(file));
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				for (String _word : line.split("[^a-zA-Z+]")) {// \\W+
					String word = _word.toLowerCase();

					/*if (stopwords.contains(word))
	    					continue;*/
					HashSet<String> filenames = st.get(word);
					if (filenames == null) {
						filenames = new HashSet<String>();
						filenames.add(file.getName());
						st.put(word,filenames );

					}
					filenames.add(file.getName());


					/*	ArrayList<PostingList> p_list = (post_map.get(word));// get the posting list for a word
					if(p_list == null){
						//empty create a new posting list
						p_list = new ArrayList<PostingList>();
						PostingList pl = new PostingList(file.getName(), word, file.getAbsolutePath());
						p_list.add(pl);
						post_map.put(word, p_list);
					}
					else{
						boolean bFound = false;
						for(int i = 0; i<p_list.size();i++){
							if( p_list.get(i).doc_name.equals(file.getName()) ){
								bFound = true;
								break;
							}								
						}
						if(bFound == false){	
							PostingList pl = new PostingList(file.getName(), word, file.getAbsolutePath());
							p_list.add(pl);
							post_map.put(word, p_list);
						}
					}
					 */


					if(ham_filevocab.containsKey(word)){
						ham_filevocab.put(word, ham_filevocab.get(word)+1);
					}
					else{
						ham_filevocab.put(word, 1);
					}

				}
			}
			ham_filemap.put(current_filename, ham_filevocab);


		}
		/*for(Entry<String, HashMap<String, Integer>> entr: ham_filemap.entrySet()){
			System.out.println(entr.getKey() + " // "+ entr.getValue());
		}*/

		for (File file : dir_spam_train.listFiles()) {
			String current_filename= file.getName();
			HashMap<String, Integer> spam_filevocab = new HashMap<String, Integer>();

			spam_fileset.add(current_filename);
			all_fileset.add(current_filename);

			BufferedReader reader = new BufferedReader(new FileReader(file));
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				for (String _word : line.split("[^a-zA-Z+]")) {
					String word = _word.toLowerCase();

					//if (stopwords.contains(word))
					//	continue;
					HashSet<String> filenames = st.get(word);
					if (filenames == null) {
						filenames = new HashSet<String>();
						filenames.add(file.getName());
						st.put(word,filenames );

					}
					filenames.add(file.getName());


					/*ArrayList<PostingList> p_list = (post_map.get(word));// get the posting list for a word
					if(p_list == null){
						//empty create a new posting list
						p_list = new ArrayList<PostingList>();
						PostingList pl = new PostingList(file.getName(), word, file.getAbsolutePath());
						p_list.add(pl);
						post_map.put(word, p_list);
					}
					else{
						boolean bFound = false;
						for(int i = 0; i<p_list.size();i++){
							if( p_list.get(i).doc_name.equals(file.getName()) ){
								bFound = true;
								break;
							}								
						}
						if(bFound == false){	
							PostingList pl = new PostingList(file.getName(), word, file.getAbsolutePath());
							p_list.add(pl);
							post_map.put(word, p_list);
						}
					}*/


					if(spam_filevocab.containsKey(word)){
						spam_filevocab.put(word, spam_filevocab.get(word)+1);
					}
					else{
						spam_filevocab.put(word, 1);
					}
				}
			}
			spam_filemap.put(current_filename, spam_filevocab);

		}
		System.out.println("done.. now training");

		Perceptron perceptron = new Perceptron(ham_fileset, spam_fileset, all_fileset,ham_filemap,spam_filemap,post_map,st);
		perceptron.perceptronTrain();
		System.out.println("done.. now Testing..");

		int h_count =0;
		for(File f : dir_ham_test.listFiles()){
			HashMap<String, Integer> thismap = new HashMap<String, Integer>();
			BufferedReader reader = new BufferedReader(new FileReader(f));

			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				for (String _word : line.split("[^a-zA-Z+]")) {
					String word = _word.toLowerCase();
					if(thismap.containsKey(word)){
						thismap.put(word, thismap.get(word)+1);
					}
					else{
						thismap.put(word, 1);
					}			
				}
			}
			reader.close();
			int result = perceptron.perceptronTest(thismap);
			if(result == -1){
				h_count++;
			}
		}
		System.out.println(h_count);
		
		
		int s_count =0;
		for(File f : dir_spam_test.listFiles()){


			HashMap<String, Integer> thismap = new HashMap<String, Integer>();
			BufferedReader reader = new BufferedReader(new FileReader(f));

			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				for (String _word : line.split("[^a-zA-Z+]")) {
					String word = _word.toLowerCase();
					if(thismap.containsKey(word)){
						thismap.put(word, thismap.get(word)+1);
					}
					else{
						thismap.put(word, 1);
					}			
				}
			}
			reader.close();
			int result = perceptron.perceptronTest(thismap);
			if(result == 1){
				s_count++;
			}
		}
		System.out.println(s_count);

	}
}

