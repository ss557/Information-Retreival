/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.text.Normalizer;
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
@RuleClass(className = RULENAMES.ACCENTS)
public class Accent implements TokenizerRule {

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
			while (stream.hasNext()) {
				token = stream.next();
				if (token == null) {
				} else {
					String nfdNormalizedString = Normalizer.normalize(token,
							Normalizer.Form.NFD);
					Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
					token = pattern.matcher(nfdNormalizedString).replaceAll("");
				}
				token = replace(token,"[ ][ ]*", " ");
				if (token.matches("[ ]+.*")) {
					token = token.replaceFirst("[ ]", "");
				}
				if (token.matches(".*[ ]+")) {
					token = token.substring(0, token.length() - 1);
				}
				stream.previous();
				stream.set(token);
				stream.next();
			}
		}
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
	/**
	 * Method to check if the given token is purely alphabetic in nature
	 * 
	 * @param token
	 *            : The token to be checked
	 * @return true if consists only of characters, false otherwise
	 */
	
}
