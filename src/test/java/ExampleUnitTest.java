import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ExampleUnitTest {

	@Test
	public void oneEqualsOne() {
		int one = 1;
		assertEquals(one, 1);
	}

	@Test
	public void oneDoesNotEqualTwo() {
		int one = 1;
		assertNotEquals(one, 2);
	}

}