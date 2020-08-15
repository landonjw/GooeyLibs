import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExampleMockTests {

	@Test
	public void listHasOneLength() {
		List list = mock(List.class);
		when(list.size()).thenReturn(1);
		assertEquals(list.size(), 1);
	}

}