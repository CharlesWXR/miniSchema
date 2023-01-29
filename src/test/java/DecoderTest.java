import cn.edu.njnu.minischema.base.ConsoleDecoder;
import cn.edu.njnu.minischema.base.Decoder;
import org.junit.Test;

import java.util.Scanner;

public class DecoderTest {
	@Test
	public void testExecutor() {
		Decoder decoder = new Decoder();
		String code = "(:= a 5)\n(output _GlobalVariables)";
		decoder.execute(code);
	}
}
