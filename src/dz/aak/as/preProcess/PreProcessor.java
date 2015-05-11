/*
 * This file is part of AllSummarizer project
 * 
 * Copyright 2013-2015 Abdelkrime Aries <kariminfo0@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dz.aak.as.preProcess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import dz.aak.as.preProcess.def.DefNormalizer;
import dz.aak.as.preProcess.def.DefSWEliminator;
import dz.aak.as.preProcess.def.DefSegmenter;
import dz.aak.as.preProcess.def.DefStemmer;
import dz.aak.as.preProcess.lang.PreProcessInfo;
import dz.aak.as.preProcess.lang.Normalizer;
import dz.aak.as.preProcess.lang.SWEliminator;
import dz.aak.as.preProcess.lang.Segmenter;
import dz.aak.as.preProcess.lang.Stemmer;
import dz.aak.as.tools.Data;
import dz.aak.as.tools.JarLoader;


public class PreProcessor {

	// In multi-documents summarization, we have many texts
	private List<String> Texts = new ArrayList<String>();

	private Data data;

	private PreProcessInfo info = null;
	private Normalizer normalizer = null;
	private Segmenter segmenter = null;
	private SWEliminator sweliminator = null;
	private Stemmer stemmer = null;


	public PreProcessor(String lang, Data data){
		setLanguage(lang);
		this.data = data;
	}


	public void setLanguage(String lang){
		
		JarLoader jarLoader = 
				new JarLoader("preProcess/", "aak/as/preProcess", PreProcessInfo.version);
		
		info = jarLoader.getInfoService(lang, PreProcessInfo.class);

		normalizer = jarLoader.getLangService(info, Normalizer.class);
		if (normalizer == null){
			System.out.println(lang + ": No Normalizer, using default");
			normalizer = new DefNormalizer();
		}
		
		segmenter = jarLoader.getLangService(info,Segmenter.class);
		if (segmenter == null){
			System.out.println(lang + ": No Segmenter, using default");
			segmenter = new DefSegmenter();
		}
		
		sweliminator = jarLoader.getLangService(info, SWEliminator.class);
		if (sweliminator == null){
			System.out.println(lang + ": No SWEliminator, using default");
			sweliminator = new DefSWEliminator();
		}
		
		stemmer = jarLoader.getLangService(info, Stemmer.class);
		
		if (stemmer == null){
			System.out.println(lang + ": No Stemmer, using default");
			stemmer = new DefStemmer();
		}

	}

	public void clearText(){
		Texts.clear();
	}

	public void addText(String text){
		Texts.add(text);	
	}

	public void preProcess(){

		List<String> sentences = new ArrayList<String>();
		HashMap<Integer, Integer> sentPos = new HashMap<Integer, Integer>();

		int sentMergePos = 0;
		for (String text: Texts){
			text = normalizer.normalize(text);
			List<String> sentInText = segmenter.splitToSentences(text);
			int textPos = - 1 - sentMergePos;
			sentPos.put(textPos, sentInText.size());
			for (int i=1; i<= sentInText.size(); i++){
				sentPos.put(sentMergePos, i);
				sentMergePos++;
			}
			sentences.addAll(sentInText);
		}


		List<List<String>> sentWords = PreProcess(sentences);

		//System.out.println("====>" + sentPos.size());
		data.setSentPos(sentPos);
		data.setSentences(sentences);
		data.setSentWords(sentWords);

	}


	public List<List<String>> PreProcess (List<String> Sents){

		List<List<String>> sentWords = new ArrayList<List<String>>();
		List<Integer> nbrWords = new ArrayList<Integer>(); //number of words in the sentences including stop-words

		for (String sent: Sents){
			List<String> segSent = segmenter.segmentWords(sent);

			nbrWords.add(segSent.size());

			sweliminator.deleteSW(segSent);

			segSent = stemmer.stemListWords(segSent);

			sentWords.add(segSent);
		}

		data.setNbrWords(nbrWords);

		return sentWords;
	}


	public void preProcess(String inTxt){

		inTxt = normalizer.normalize(inTxt);

		List<String> sentences = segmenter.splitToSentences(inTxt);
		HashMap<Integer, Integer> sentPos = new HashMap<Integer, Integer>();
		List<List<String>> sentWords = PreProcess(sentences);

		sentPos.put(-1, sentences.size());
		for (int i=0; i< sentences.size(); i++)
			sentPos.put(i, i+1);

		data.setSentences(sentences);
		data.setSentPos(sentPos);
		data.setSentWords(sentWords);


	}
	

}
