/*
 * This file is part of AllSummarizer project
 * 
 * Copyright 2013 Abdelkrime Aries <kariminfo0@gmail.com>
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

package dz.aak.as.process.reduction;

import java.util.List;

/**
 * This interface is used for sentence compression. 
 * 
 * Sentence compression is the process of deleting unwanted information in a sentence. 
 * It is used to make a sentence shorter.
 * 
 * @author Abdelkrime Aries
 *
 */
public interface Compresser {

	public List<String> compressChoices(String inStr);
	
	public String compress(String inStr);
	
}
