/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.io.StreamTokenizer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.Tokenizer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument.Section;

/**
 * A Callable document transformer that converts the given WikipediaDocument
 * object into an IndexableDocument object using the given Tokenizer
 * 

 * 
 */
public class DocumentTransformer implements Callable<IndexableDocument> {
	Map<INDEXFIELD, Tokenizer> tMap = new HashMap<INDEXFIELD, Tokenizer>();

	WikipediaDocument doc;

	/**
	 * Default constructor, DO NOT change
	 * 
	 * @param tknizerMap
	 *            : A map mapping a fully initialized tokenizer to a given field
	 *            type
	 * @param doc
	 *            : The WikipediaDocument to be processed
	 */
	public DocumentTransformer(Map<INDEXFIELD, Tokenizer> tknizerMap,
			WikipediaDocument doc) {
		this.doc = doc;
		this.tMap = tknizerMap;
	}

	/**
	 * Method to trigger the transformation
	 * 
	 * @throws TokenizerException
	 *             Inc ase any tokenization error occurs
	 */
	public IndexableDocument call() throws TokenizerException {
		Tokenizer t;
		INDEXFIELD ind;
		
		IndexableDocument iDoc = new IndexableDocument();
		for (Map.Entry<INDEXFIELD, Tokenizer> token : tMap.entrySet()) {
			ind = token.getKey();
			switch (ind) {
			case TERM: {
				String streamText = new String();
				TokenStream ts=null;
				List<Section> docSection = doc.getSections();
				for (int i = 0; i < docSection.size(); i++) {
					Section forStream = docSection.get(i);
					streamText = streamText + " " + forStream.getTitle() + " "
							+ forStream.getText();
				}
				t = token.getValue();
				streamText = replace(streamText,"[ ][ ]+"," ");	
				streamText = replace(streamText,"[\\n\\r]"," ");
				try {
					ts = new TokenStream(streamText);
					t.tokenize(ts);
				} catch (Exception e) {
						
				}
				//System.out.println(ts.getAllTokens());
				iDoc.addField(ind,ts );
				break;
			}
			
			case LINK: {
				Set<String> docLink = doc.getLinks();
				Iterator<String> docIter = docLink.iterator();
				String next = null;
				if(docIter.hasNext()){
					next = docIter.next();
				}
				TokenStream ts = new TokenStream(next);
				while(docIter.hasNext()){
					ts.append(docIter.next());					
				}
				t = token.getValue();
				try {
					t.tokenize(ts);
				} catch (Exception e) {
					
				}
				iDoc.addField(ind,ts );
				break;
			}
			
			case CATEGORY: {
				
				List<String> categories = doc.getCategories();
				String [] streamText = new String[categories.size()];
				TokenStream ts = new TokenStream(categories.get(0));
				for (int i = 1; i < categories.size(); i++) {
					String forStream = categories.get(i);
					streamText[i] = forStream;
				}
				ts.append(streamText);
				t = token.getValue();
				try {
					t.tokenize(ts);
				} catch (Exception e) {
					
				}
				iDoc.addField(ind,ts );
				break;
			}
			
			case AUTHOR: {
				String streamText = doc.getAuthor();
				t = token.getValue();
				TokenStream ts = new TokenStream(streamText);
				try {
					
					t.tokenize(ts);
				} catch (Exception e) {
					
				}
				iDoc.addField(ind,ts );
				break;
			}
			}
			
			
		}
		
		return iDoc;
	}
	private static String replace(String s,String input,String output){
		StringBuffer sb = new StringBuffer();
		Pattern pattern = Pattern
				.compile(input);
		Matcher matcher = pattern.matcher(s);
		while (matcher.find()) {
			matcher.appendReplacement(sb,output);
		}
		sb = matcher.appendTail(sb);
		return sb.toString();
	}

}
