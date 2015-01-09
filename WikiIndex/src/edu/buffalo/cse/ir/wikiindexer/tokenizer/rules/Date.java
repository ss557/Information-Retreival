/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.Hashtable;

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
@RuleClass(className = RULENAMES.DATES)
public class Date implements TokenizerRule {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerRule#apply(edu.buffalo
	 * .cse.ir.wikiindexer.tokenizer.TokenStream)
	 */
	public void apply(TokenStream stream) throws TokenizerException {
		int count = 0;

		boolean flag_year = false, flag_month = false, flag_date = false;
		boolean yearIsAD = false, yearIsBC = false, yearIsfromto = false, Timeiset = false, flag_delete = false, flag_check_utf = false;
		String tokenmom = "", tokendd = "", tokenyear = "";
		String tokenyear2 = "year", tokenAD_BC = "", tokentype = "", time = "", tokentypeutc = "";
		if (stream != null) {
			String token;

			while (stream.hasNext()) {
				token = stream.next();
				if (token == null) {
				} else {
					//System.out.println(token);
					Hashtable<String, String> month_lists = new Hashtable<String, String>();
					month_lists.put("january", "01");
					month_lists.put("february", "02");
					month_lists.put("march", "03");
					month_lists.put("april", "04");
					month_lists.put("may", "05");
					month_lists.put("june", "06");
					month_lists.put("july", "07");
					month_lists.put("august", "08");
					month_lists.put("september", "09");
					month_lists.put("october", "10");
					month_lists.put("november", "11");
					month_lists.put("december", "12");
					String token1 = token;
					if (token1.matches(".*[\\,]")) {
						token = token1.replaceAll("[\\,]", "");
						token1 = ",";
					} else if (token1.matches(".*[\\.]")) {
						token = token1.replaceAll("[\\.]", "");
						token1 = ".";
					} else {
						token1 = "";
					}
					if (token.matches("[0-9]{0,3}")) {
						tokenAD_BC = stream.next();
						stream.previous();
						if (tokenAD_BC.matches("(BC|BC.|B.C|B.C.)")) {
							yearIsBC = true;
							yearIsAD = false;
							stream.next();
							stream.previous();
							stream.remove();
						} else if (tokenAD_BC.matches("(AD|AD.|A.D|A.D.)")) {
							yearIsAD = true;
							yearIsBC = false;
							stream.next();
							stream.previous();
							stream.remove();
						} else {
							yearIsBC = false;
							yearIsAD = false;
						}
						if (((yearIsBC) || (yearIsAD)) && (!flag_year)) {
							tokenyear = isYear(token, yearIsBC);
							flag_year = true;
							flag_delete = false;
						} else {
							if ((!flag_date))
								tokendd = isDay(token);
							if (tokendd.equals("not date")) {
							} else {
								flag_date = true;
								String tokenyy = stream.next();
								stream.previous();
								if ((flag_month)& (!tokenyy.matches("[0-9]+[a-zA-Z\\.\\,]*"))) {
									count++;
								}
								flag_delete = false;
							}
						}
					} else if (token.matches("[0-9][0-9][0-9][0-9][\\-]*[0-9]*[0-9]*.*")) {
						if (token.matches("[0-9][0-9][0-9][0-9][\\-][0-9][0-9].*")) {
							token = token.replaceAll("[^0-9\\-]", "");
							if (!flag_year) {
								tokenyear2 = isYear(token.substring(0, 2)+ token.substring(token.length() - 2),yearIsBC);
								tokenyear = isYear(token.substring(0, 4),yearIsBC);
								}
							flag_year = true;
							flag_delete = false;
						} else {
							if (!flag_year) {
								tokenyear = isYear(token, yearIsBC);
							}
							flag_year = true;
							flag_delete = false;
						}
					} else if (token.matches("([0-9]:[0-5][0-9][^\\:]*)|([0-1][0-2]:[0-5][0-9][^\\:]*)")) {// time match
						if (token.matches(".*(?i)(am|pm|am.|pm.)")) {
							tokentype = token.replaceAll("(1[012]|[1-9]):[0-5][0-9]", "");
						}
						else {
							tokentype = stream.next().toUpperCase();
							stream.previous();
							if (tokentype.matches("(AM|AM.|A.M|A.M.|PM|PM.|P.M|P.M.)")) {
								stream.remove();
							}
						}
						if (tokentypeutc.matches("(?i)(UTC)")) {
							flag_delete = true;
						} else {
							time = returnTime(token, tokentype);
							Timeiset = true;
						}
					}
					else if (token.matches("([0-2][0-4]:[0-5][0-9]:[0-5][0-9])")) {
						tokentype = "24hr";
						tokentypeutc = stream.next().toUpperCase();
						stream.previous();
						if (tokentypeutc.matches("(?i)(UTC)")) {
							flag_delete = true;
							flag_check_utf = true;
						}
						time = returnTime(token, tokentype);
						Timeiset = true;
					} else {
						if (!flag_month) {
							tokenmom = isMonth(token);
							if (month_lists.containsKey(isMonth(tokenmom))) {
								tokenmom = month_lists.get((isMonth(tokenmom)));
								flag_delete = false;
								flag_month = true;
								stream.previous();
								stream.remove();
							}
						}
					}

					/*
					 * if (((flag_month) & (flag_date)) | ((yearIsAD) &
					 * (yearIsBC))) {
					 * 
					 * yearIsLeap = ((Integer.parseInt(tokenyear) % 4 == 0) ?
					 * true : false); }
					 */
					if (flag_delete) {
						stream.previous();
						stream.remove();
					}
					if ((flag_date) & (flag_month)) {
						count++;
					}
					if ((flag_date) & (flag_month) & (flag_year)) {
						String Date = tokenyear + tokenmom + tokendd;
						if (flag_check_utf) {
							Date += " " + time;
							flag_check_utf = false;
						}
						//System.out.println("return date for the all input "+ Date);
						Date += token1;
						stream.previous();
						stream.remove();
						stream.previous();
						stream.set(Date);
						stream.next();
						flag_month = false;
						flag_date = false;
						flag_year = false;
					} else if ((flag_year)) {
						tokenAD_BC = tokenAD_BC.replaceAll("(BC|B.C|A.D|AD)","");
						String Date = tokenyear + "0101";
						
						if (tokenyear2.equals("year")) {
						} else {
							Date += "-" + tokenyear2 + "0101" + token1;
						}
						
						//System.out.println("return date for the input is only year "+ Date);
						Date += tokenAD_BC;
						stream.previous();
						stream.set(Date);
						stream.next();
						flag_month = false;
						flag_date = false;
						flag_year = false;
					} else if ((Timeiset) & (!flag_check_utf)) {
						tokentype = tokentype.replaceAll("(AM|A.M|PM|P.M)", "");
						time = time + tokentype + token1;
						//System.out.println("setting the time value as '" + time	+ "'");
						stream.previous();
						stream.set(time);
						stream.next();
						Timeiset = false;

					} else if ((count > 1) & (flag_date) & (flag_month)) {
						String Date = "1900" + tokenmom + tokendd + token1;
						//System.out.println("return date for the input is only month and day "+ Date);
						stream.previous();
						stream.set(Date);
						stream.next();
						flag_month = false;
						flag_date = false;
						flag_year = false;

					}

				}
			}
		}
	}

	private static String isDay(String date) {
		if (date.matches("(([0-3][0-1])|([0-2][0-9])|([1-9]))")) {
			//System.out.println("month with 31 days ");
			if (date.matches("[1-9]"))
				return "0" + date;
			else
				return date;// single date value
		} else {
			return "not date";
		}
	}
/*
	private static String isDayafter(String date, String mom, boolean yearIsLeap) {
		// //System.out.println(mom + " ;" + date);
		if (mom.matches("01|03|05|07|08|10|12")) {
			if (date.matches(".*(([0-3][0-1])|([0-2][0-9])|([1-9])).*")) {
				//System.out.println("month with 31 days ");
				if (date.matches("[1-9]"))
					return "0" + date;
				else
					return date;// single date value
			} else {
				return "not date";
			}// not a date
		} else if (mom.equals("04|06|09|11")) {
			if (date.matches(".*(([0-3][0])|([0-2][0-9])|([1-9])).*")) {
				//System.out.println("month with 30 days ");
				if (date.matches("[1-9]"))
					return "0" + date;
				else
					return date;// single date value
			} else {
				return "not date";
			}// not a date
		} else if (mom.equals("02")) {
			if (yearIsLeap) {
				if (date.matches(".*(([0-2][0-8])|([1-9])).*")) {
					//System.out.println("month is february leap year");
					if (date.matches("[1-9]"))
						return "0" + date;
					else
						return date;// single date value
				} else {
					return "not date";
				}// not a date

			} else {
				if (date.matches(".*(([0-2][0-9])|([1-9])).*"))

				{
					//System.out.println("month is february ");
					if (date.matches("[1-9]"))
						return "0" + date;
					else
						return date;// single date value
				} else {
					return "not date";
				}// not a date

			}
		} else
			return "not date";
	}
*/
	private static String isMonth(String token) {
		token = token.toLowerCase();
		Hashtable<String, String> month_lists = new Hashtable<String, String>();
		month_lists.put("jan", "january");
		month_lists.put("feb", "february");
		month_lists.put("mar", "march");
		month_lists.put("apr", "april");
		month_lists.put("may", "may");
		month_lists.put("june", "june");
		month_lists.put("july", "july");
		month_lists.put("aug", "august");
		month_lists.put("sep", "september");
		month_lists.put("oct", "october");
		month_lists.put("nov", "november");
		month_lists.put("dec", "december");

		if (month_lists.containsKey(token)) {
			return month_lists.get(token);
		} else if (month_lists.containsValue(token)) {
			return token;
		}
		return "not a month";

	}

	private static String isYear(String year, boolean isBC) {
		String appendYear = "";
		for (int i = 0; i < (4 - year.length()); i++) {
			appendYear += "0";
		}
		if (year.matches("[0-9]{1,4}")) {
			if (isBC)
				return "-" + appendYear + year;
			else
				return appendYear + year;
		} else {
			return "not a year";
		}
	}

	private static String returnTime(String time, String type) {
		time = time.replaceAll("(AM|A.M|PM|P.M)[\\.]*", "");
		if (type.matches("(AM|AM.|A.M|A.M.)")) {
			return time + ":00";
		} else if (type.matches("(PM|PM.|P.M|P.M.)")) {
			String[] timeinPM = time.split(":");
			return Integer.toString(Integer.parseInt(timeinPM[0]) + 12) + ":"
					+ timeinPM[1] + ":00";
		} else if (type.equals("24hr")) {
			return time;
		}
		return "not a time";

	}

}