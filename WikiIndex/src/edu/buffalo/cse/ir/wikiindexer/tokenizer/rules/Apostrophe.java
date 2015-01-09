/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

/**
 * An implementation of the Porter stemmer for English THis is from the author's
 * website directly Wrapped in the framework class
 * 
 
 * 
 */
// example of annotation, for classes you write annotate accordingly
@RuleClass(className = RULENAMES.APOSTROPHE)
public class Apostrophe implements TokenizerRule {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerRule#apply(edu.buffalo
	 * .cse.ir.wikiindexer.tokenizer.TokenStream)
	 */
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null) {
			String token;
			stream.reset();
			Hashtable<String, String> apostrophe_lists = new Hashtable<String, String>();
			String substr_extract = new String();
			
			
			apostrophe_lists.put("'m", " am");
			apostrophe_lists.put("'re", " are");
			apostrophe_lists.put("'d", " had");
			apostrophe_lists.put("'ll", " will");
			apostrophe_lists.put("'ve", " have");
			apostrophe_lists.put("'em", "them");
			apostrophe_lists.put("t's", "t is");
			apostrophe_lists.put("wo", "will");
			apostrophe_lists.put("can't", "can not");
			apostrophe_lists.put("shan't", "shall not");
			apostrophe_lists.put("they'd", "they would");
			apostrophe_lists.put("let's", "let us");
			apostrophe_lists.put("o'", "of ");
			apostrophe_lists.put("o'clock", "of the clock");
			apostrophe_lists.put("aren't", "are not");
			apostrophe_lists.put("can't", "cannot");
			apostrophe_lists.put("couldn't", "could not");
			apostrophe_lists.put("didn't", "did not");
			apostrophe_lists.put("doesn't", "does not");
			apostrophe_lists.put("don't", "do not");
			apostrophe_lists.put("hadn't", "had not");
			apostrophe_lists.put("hasn't", "has not");
			apostrophe_lists.put("haven't", "have not");
			apostrophe_lists.put("he'd", "he had");
			apostrophe_lists.put("he'll", "he will");
			apostrophe_lists.put("he's", "he is,he has");
			apostrophe_lists.put("I'd", "I had");
			apostrophe_lists.put("I'll", "I will");
			apostrophe_lists.put("I'm", "I am");
			apostrophe_lists.put("I've", "I have");
			apostrophe_lists.put("isn't", "is not");
			apostrophe_lists.put("it's", "it is");
			apostrophe_lists.put("let's", "let us");
			apostrophe_lists.put("mustn't", "must not");
			apostrophe_lists.put("she'd", "she had");
			apostrophe_lists.put("she'll","she will");
			apostrophe_lists.put("She'll","She will");
			apostrophe_lists.put("she's", "she is");
			apostrophe_lists.put("she's", "she is");
			apostrophe_lists.put("should've", "should have");
			apostrophe_lists.put("shouldn't", "should not");
			apostrophe_lists.put("that's", "that is");
			apostrophe_lists.put("there's","there is");
			apostrophe_lists.put("they'd","they had");
			apostrophe_lists.put("They'd","They would");
			apostrophe_lists.put("they'll","they will");
			apostrophe_lists.put("they're", "they are");
			apostrophe_lists.put("they've", "they have");
			apostrophe_lists.put("we'd", "we had");
			apostrophe_lists.put("we're", "we are");
			apostrophe_lists.put("we've", "we have");
			apostrophe_lists.put("weren't", "were not");
			apostrophe_lists.put("what'll","what will");
			apostrophe_lists.put("what're", "what are");
			apostrophe_lists.put("what's", "what is");
			apostrophe_lists.put("what've", "what have");
			apostrophe_lists.put("where's","where is");
			apostrophe_lists.put("who'd", "who had");
			apostrophe_lists.put("who'll","who will");
			apostrophe_lists.put("who're", "who are");
			apostrophe_lists.put("who's", "who is");
			apostrophe_lists.put("who've", "who have");
			apostrophe_lists.put("would've", "would have");
			apostrophe_lists.put("won't", "will not");
			apostrophe_lists.put("wouldn't", "would not");
			apostrophe_lists.put("you'd", "you had");
			apostrophe_lists.put("you'll","you will");
			apostrophe_lists.put("you're", "you are");
			apostrophe_lists.put("you've", "you have");
			apostrophe_lists.put("Should've", "Should have");
			apostrophe_lists.put("Put 'em", "Put them");
			apostrophe_lists.put("put 'em", "put them");
			while (stream.hasNext()) {
				boolean flag_key_value_lookup = true;
				token = stream.next();
				if (token != "") {
					{
						if (token.matches(".*[\\']+.*")) {

							
							
							

							if (apostrophe_lists.containsKey(token)) {
								token = apostrophe_lists.get(token);
								flag_key_value_lookup = false;
							
							}else if(token.matches(".*[\\']s")){
								token=token.replaceAll("[\\'][a-z]", "");
							}else if(token.matches(".*[\\'].*")){
								token=token.replaceAll("[\\']", "");
							}
							/*
							 * else if (token.matches("(ca|wo|sha|Ca|Wo|Sha)" +
							 * "n[\\']t")) {
							 * System.out.println("cant and shant"); String
							 * substr_extract1 = replace(token, "n't", "");
							 * substr_extract = token .substring(token.length()
							 * - 3); token = replace(token, substr_extract1,
							 * apostrophe_lists.get(substr_extract1)
							 * .toString()); } else if
							 * (token.matches("[A-Za-z]+[\\'][a-z][a-z]")) {
							 * substr_extract = token .substring(token.length()
							 * - 3); } else if (token.matches("[A-Za-z]+" +
							 * "[\\']" + "(d|m)")) { substr_extract = token
							 * .substring(token.length() - 2); if
							 * (token.matches("(.*y'd)|(.*t's)")) substr_extract
							 * = token.substring(token .length() - 3); } else if
							 * ((token.matches("[a-zA-Z]+" + "n[\\'][a-z]")) |
							 * (token.matches(".*" + "^e" + "t[\\']s"))) {
							 * substr_extract = token .substring(token.length()
							 * - 3); } else if (token.matches(".*" +
							 * "et[\\']s")) { substr_extract = token
							 * .substring(token.length() - 4); } else if
							 * (token.matches("(o'|O').*")) { if
							 * (token.matches(".*(o'clock)")) substr_extract =
							 * "o'c"; else substr_extract = "o'"; if
							 * (token.matches(".*(O'Clock)")) substr_extract =
							 * "O'C"; else substr_extract = "O'"; } else if
							 * (token.matches(".*[\\']em")) { substr_extract =
							 * "'em"; } else if (token.matches(".*[\\']s")) {
							 * substr_extract = replace(token, "('s)", "");
							 * token = token.substring(0,
							 * substr_extract.length()); flag_key_value_lookup =
							 * false; } else { flag_key_value_lookup = false;
							 * token = replace(token, "[\\']", ""); }
							 * 
							 * if (flag_key_value_lookup) { token =
							 * replace(token, substr_extract,
							 * apostrophe_lists.get(substr_extract)
							 * .toString()); }
							 */

							String[] tok = token.split(" ");
							stream.previous();
							stream.set(tok);
							stream.next();
						}
					}

				}
			}
		}
	}

	/**
	 * Method to check if the given token is purely alphabetic in nature
	 * 
	 * @param token
	 *            : The token to be checked
	 * @return true if consists only of characters, false otherwise
	 */
	private static String replace(String s, String input, String output) {
		StringBuffer sb = new StringBuffer();
		Pattern pattern = Pattern.compile(input);
		Matcher matcher = pattern.matcher(s);
		while (matcher.find()) {
			matcher.appendReplacement(sb, output);
		}
		sb = matcher.appendTail(sb);
		return sb.toString();
	}

	/*
	 * private static int isApostrophe(String token) { // case return 0 ; for
	 * case that dont match values in hashmap // case return 1 ; for case like
	 * 're 've // case return 2 ; for case like 'm 'd // case return 3 ; for
	 * case like n't it's that's // case return 4 ; for case like can't won't
	 * shan't // case return 5 ; for case like o' o'clock // case return 6 ; for
	 * case like et'us // case return 7 ; for case like 'em if
	 * (token.matches("[A-Za-z]+[\\'][a-z][a-z]")) { return 1; } else if
	 * (token.matches("[A-Za-z]+" + "[\\']" + "(d|m)")) { return 2; } else if
	 * (token.matches("(ca|wo|sha|Ca|Wo|Sha)" + "n[\\']t")) { return 4; } else
	 * if ((token.matches("[a-zA-Z]+" + "n[\\'][a-z]")) | (token.matches(".*" +
	 * "^e" + "t[\\']s"))) { return 3; } else if ((token.matches(".*" +
	 * "et[\\']s"))) { return 6; } else if (token.matches("(o'|O').*")) return
	 * 5; else if (token.matches(".*[\\']em")) return 7;
	 * 
	 * return 0;
	 * 
	 * }
	 */
}
