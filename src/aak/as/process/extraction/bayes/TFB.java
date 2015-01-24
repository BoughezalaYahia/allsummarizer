/*
 * AllSumarizer v2
 * This file is part of AllSummarizer project; an implementation of the method
 * described in this paper:
 * http://dx.doi.org/10.1117/12.2004001
 * 
 * Copyright (C) 2013  Abdelkrime Aries <kariminfo0@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package aak.as.process.extraction.bayes;

import java.util.HashMap;
import java.util.List;

public class TFB implements Feature {

	private HashMap<Integer, HashMap<String, Integer>> classWordsFreq 
					= new HashMap<Integer, HashMap<String, Integer>>();
	
	@Override
	public String getTrainParam() {
		return "classes,sentWords";
	}
	
	@Override
	public void train(List<Object> trainParam) {
		
		@SuppressWarnings("unchecked")
		HashMap<Integer, List<Integer>> classes = 
				(HashMap<Integer, List<Integer>>) trainParam.get(0);
		@SuppressWarnings("unchecked")
		List<List<String>> sentWords = 
				(List<List<String>>) trainParam.get(1);
		
		//Reset the classWordsFreq, when training this feature another time
		classWordsFreq = new HashMap<Integer, HashMap<String, Integer>>();
		
		for (int classID = 0; classID < classes.size(); classID++)
		{
			
			HashMap<String, Integer> classIWordsFreq = new HashMap<String, Integer>();
			for (int sentID : classes.get(classID))
			{
				String prevWord = ">>";
				for (String word: sentWords.get(sentID))
				{
					String biWord = prevWord + "?" + word;
					int value = (classIWordsFreq.containsKey(biWord))?classIWordsFreq.get(biWord)+1:1;
					classIWordsFreq.put(biWord, value);
					prevWord = word;
				}
			}
				
			classWordsFreq.put(classID, classIWordsFreq);
		}

	}

	@Override
	public String getScoreParam() {
		return "sentWords";
	}
	
	@Override
	public Double score(int classID, List<Object> scoreParam) {
		Double score = 0.0;

		@SuppressWarnings("unchecked")
		List<String> sentence = (List<String>) scoreParam.get(0);
		String prevWord = ">>";
		
		for (String word: sentence)
		{
			String biWord= prevWord + "?" + word;
			
			if (classWordsFreq.get(classID).containsKey(biWord))
				score += classWordsFreq.get(classID).get(biWord);

			prevWord = word;
		}
		return score;
	}

}
