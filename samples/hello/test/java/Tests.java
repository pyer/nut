import nut.annotations.Ignore;
import nut.annotations.Test;

import static nut.Assert.assertEquals;

public class Tests
{
    @Test
    public void testPass() {
        assertEquals( 1, 1 );
    }

    @Test
    public void testFail() {
        assertEquals( 1, 2 );
    }

    @Ignore
    public void testIgnore() {
        assertEquals( 1, 3 );
    }

}
