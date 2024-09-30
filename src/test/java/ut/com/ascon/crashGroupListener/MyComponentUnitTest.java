package ut.com.ascon.crashGroupListener;

import org.junit.Test;
import com.ascon.crashGroupListener.api.MyPluginComponent;
import com.ascon.crashGroupListener.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}