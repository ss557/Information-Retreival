/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.parsers;

import java.io.File;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.buffalo.cse.ir.wikiindexer.IndexerConstants;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaParser;

/**
 
 * 
 */
public class Parser {
	/* */
	private final Properties props;
	private int id;
	private String ts,author,title;
	private String text,utfText;
	private StringBuilder characters;
	private int cnt=0;
	private Collection<WikipediaDocument> wikiDocs;
	/**
	 * 
	 * @param idxConfig
	 * @param parser
	 */
	public Parser(Properties idxProps) {
		props = idxProps;
		//System.out.println("parser constructor");
	}

	/* TODO: Implement this method */
	/**
	 * 
	 * @param filename
	 * @param docs
	 */
	public void parse(String filename, Collection<WikipediaDocument> docs) {
		//System.out.println(filename);
		if(filename == null || filename == ""){
			//System.out.println("Filename cannot be null or empty.Please specify the filename in properties.config.");
			return;
		}
		File f = new File(filename);
		if(f.exists()){
			
		} else {
			return;
		}
		
		try {
			wikiDocs = docs;
			SAXParserFactory saxFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxFactory.newSAXParser();
			DefaultHandler handler = new DefaultHandler(){
				
				private boolean revFlag,textFlag,pageFlag,ttlFlag,pageidFlag,tsFlag,authorFlag;
				
				
				public void startElement(String uri, String localName, String qName,
						Attributes attributes) throws SAXException {

					
					if (qName.equalsIgnoreCase("page")) {
						pageFlag = true;
					}
					if(qName.equalsIgnoreCase("revision")){
						revFlag = true;
					}
					if(pageFlag){
						if(qName.equalsIgnoreCase("title")) {
							ttlFlag = true;
						}
						if(qName.equalsIgnoreCase("id") && !revFlag) {
							pageidFlag = true;
						}
						if(qName.equalsIgnoreCase("timestamp")) {
							tsFlag = true;
						}
						if(qName.equalsIgnoreCase("username")||qName.equalsIgnoreCase("ip")) {
							authorFlag = true;
						}
						if(qName.equalsIgnoreCase("text")) {
							textFlag = true;
							characters = new StringBuilder(100);
						}
					}
				}
				
				public void characters(char ch[], int start, int length) throws SAXException {
					 
					if (ttlFlag) {
						title = new String(ch, start, length);
						//System.out.println("Title : " + new String(ch, start, length));
						ttlFlag = false;
					}
					if (pageidFlag) {
						id = Integer.parseInt(new String(ch, start, length));
						//System.out.println("Page Id : " + new String(ch, start, length));
						pageidFlag = false;
					}
					if (tsFlag) {
						ts = new String(ch, start, length);
						//System.out.println("Time : " + new String(ch, start, length));
						tsFlag = false;
					}
					if (authorFlag) {
						author = new String(ch, start, length);
						//System.out.println("Author : " + new String(ch, start, length));
						authorFlag = false;
					}
					if (textFlag) {
						characters.append(new String(ch,start,length));
						//System.out.println("Text : " + characters);
						
						
					}
					 
				}

				public void endElement(String uri, String localName, String qName)
						throws SAXException {

					//System.out.println("End Element :" + qName);
					if (qName.equalsIgnoreCase("page")) {
						pageFlag = false;
						 
						try {
							WikipediaDocument wikiDoc = new WikipediaDocument(id,ts,author,title);
							wikiDoc = WikipediaParser.parseText(characters.toString(),wikiDoc);
							//System.out.println(wikiDoc.getId());
							add(wikiDoc,wikiDocs);
						} catch(Exception e) {
							e.printStackTrace();
						}
						
						//System.out.println();
					}
					if (qName.equalsIgnoreCase("revision")) {
						revFlag = false;
					}
					if(qName.equalsIgnoreCase("text")){
						textFlag = false;
					}
					
				}
			};
			saxParser.parse(filename, handler);
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to add the given document to the collection. PLEASE USE THIS
	 * METHOD TO POPULATE THE COLLECTION AS YOU PARSE DOCUMENTS For better
	 * performance, add the document to the collection only after you have
	 * completely populated it, i.e., parsing is complete for that document.
	 * 
	 * @param doc
	 *            : The WikipediaDocument to be added
	 * @param documents
	 *            : The collection of WikipediaDocuments to be added to
	 */
	private synchronized void add(WikipediaDocument doc,
			Collection<WikipediaDocument> documents) {
		//System.out.println(doc.getTitle());
		documents.add(doc);
	}
}
