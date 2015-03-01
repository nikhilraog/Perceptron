import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;


public class Perceptron {

	public static Set<String> ham_fileset = new HashSet<String>();
	public static Set<String> spam_fileset = new HashSet<String>();
	public static Set<String> all_fileset = new HashSet<String>();
	public static HashMap<String, HashMap<String, Integer>> ham_filemap = new HashMap<String, HashMap<String,Integer>>();
	public static HashMap<String, HashMap<String, Integer>> spam_filemap = new HashMap<String, HashMap<String,Integer>>(); 
	public static HashMap<String, ArrayList<PostingList>> post_map = new HashMap<String, ArrayList<PostingList>>();
	public static HashMap<String, HashSet<String>> st_map = new HashMap<String, HashSet<String>>();
	public static HashMap<String, Double> word_weight_map = new HashMap<String, Double>();
	public static double learning_rate ;
	public static double w0  = 0.01;

	public Perceptron(Set<String> ham_fileset_p, Set<String> spam_fileset_p,
			Set<String> all_fileset_p,
			HashMap<String, HashMap<String, Integer>> ham_filemap_p, HashMap<String, HashMap<String, Integer>> spam_filemap_p, HashMap<String, ArrayList<PostingList>> post_map_p, HashMap<String, HashSet<String>> st) {
		ham_fileset = ham_fileset_p;
		spam_fileset = spam_fileset_p;
		all_fileset = all_fileset_p;
		ham_filemap = ham_filemap_p;
		spam_filemap = spam_filemap_p;
		post_map = post_map_p;		
		learning_rate = new Double(0.01);
		st_map = st;

	}

	public void perceptronTrain(){


		//Assign random weights
		for(String _words : st_map.keySet()){
			double r_next =( Math.random()*(2)) -1;
			word_weight_map.put(_words, r_next);

		}

		int i=0;
		do {

			for (String filename : ham_fileset) {
				double td = -1;

				HashMap<String, Integer> word_count_of_this_file = ham_filemap
						.get(filename);
				//Calculate perceptron output
				double perceptron_output = calculate_od(filename,
						word_count_of_this_file);
				//for ham actual output we are considering is td = -1;
				double error = (td - perceptron_output);
				if (error != 0) {
					// if error is not zero i.e misclassification , therofore update weight of each 
					for (Entry<String, Integer> entryset : word_count_of_this_file
							.entrySet()) {
						String word = entryset.getKey();
						int num_of_word_occurences = entryset.getValue();
						double weight = word_weight_map.get(word);
						double delta_weight = learning_rate * error
								* num_of_word_occurences;
						//now update weight corresponding to the word
						double new_weight = weight + delta_weight;
						word_weight_map.put(word, new_weight);

					}
					w0 = w0 + learning_rate * error;
				}

			}
			for (String filename : spam_fileset) {
				double td = 1;

				HashMap<String, Integer> word_count_of_this_file = spam_filemap
						.get(filename);
				//Calculate perceptron output
				double perceptron_output = calculate_od(filename,
						word_count_of_this_file);
				//for spam actual output we are considering is td = +1;
				double error = (td - perceptron_output);
				if (error != 0) {
					// if error is not zero i.e misclassification , therofore update weight of each 
					for (Entry<String, Integer> entryset : word_count_of_this_file
							.entrySet()) {
						String word = entryset.getKey();
						int num_of_word_occurences = entryset.getValue();
						double weight = word_weight_map.get(word);
						double delta_weight = learning_rate * error
								* num_of_word_occurences;
						//now update weight corresponding to the word
						double new_weight = weight + delta_weight;
						word_weight_map.put(word, new_weight);

					}
					w0 = w0 + learning_rate * error;

				}

			}
			i++;
		} while (i < 1000);
		for(Entry<String, Double> e : word_weight_map.entrySet()){
			System.out.println(e.getKey() + " / "+ e.getValue());
		}

	}


	public double calculate_od(String filename, HashMap<String, Integer> word_count_of_this_file) {
		double weighted_sum = w0;
		for(Entry<String, Integer> entry : word_count_of_this_file.entrySet()){
			String _w = entry.getKey();
			int num_occurance = entry.getValue();
			weighted_sum = weighted_sum + word_weight_map.get(_w)*num_occurance;
		}

		if(weighted_sum>0){
			return 1;
		}
		else return -1;
	}

	public int perceptronTest(HashMap<String, Integer> thismap) {

		double sum = w0;

		for(Entry<String, Integer> e: thismap.entrySet()){
			if(word_weight_map.get(e.getKey() )!= null){
				sum = sum + e.getValue()*word_weight_map.get(e.getKey());
			}

		}
		if(sum > 0)
			return 1;
		else
			return -1;
	}


}
