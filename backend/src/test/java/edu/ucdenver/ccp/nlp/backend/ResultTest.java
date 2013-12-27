package edu.ucdenver.ccp.nlp.backend;
import org.junit.Test;
import java.util.List;
import edu.ucdenver.ccp.nlp.backend.orm.PrimitiveResult;
import edu.ucdenver.ccp.nlp.backend.orm.CompoundResult;


public class ResultTest {

	@Test
	public void test() {
		PrimitiveResult agent = new PrimitiveResult("ABC", "1", 1, 10, 1, "123456", "pmid");
		PrimitiveResult theme = new PrimitiveResult("DEF", "2", 20, 30, 1, "123456", "pmid");
		PrimitiveResult location = new PrimitiveResult("GHI", "3", 100, 110, 1, "123456", "pmid");
	
		CompoundResult cr = new CompoundResult(agent, theme, location);
		System.out.println("BEFORE:" + cr.toString());
		ResultProvider pr = new ResultProvider();
		pr.insertCompoundResult(cr);
		List<CompoundResult> list = pr.getCompoundResults();
		for (CompoundResult cr2 : list ) {
			System.out.println(cr2);
		}
	}

}
