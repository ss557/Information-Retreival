/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import edu.buffalo.cse.ir.wikiindexer.FileUtil;

/**
 
 * Common class to do all the parameterized loading
 * Any junit test class that needs a properties file MUST extend this class
 * Others can stay as are
 */
@RunWith(Parameterized.class)
public class PropertiesBasedTest {
protected Properties idxProps;
	
	public PropertiesBasedTest(Properties props) {
		this.idxProps = props;
	}
	
	@Parameters
	public static Collection<Object[]> generateData() {
		String propFile = System.getProperty("PROPSFILENAME");
		try {
			Properties p = FileUtil.loadProperties("C:\\Users\\Srivathsa\\workspace\\WikipediaIndexer\\files\\properties.config");
			return Arrays.asList(new Object[][]{{p}});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
