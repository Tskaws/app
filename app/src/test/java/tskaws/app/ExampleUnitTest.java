package tskaws.app;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.List;
/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void eventItemCreated() throws Exception {
        EventItem item = new EventItem();
        assertTrue(item instanceof EventItem);
    }
    @Test
    public void eventItemSetters() throws Exception {
        EventItem item = new EventItem();
        item.setTitle("Cool Title");

        assertTrue(item.getTitle().equals("Cool Title"));

        item.setDescription("Cool Description");
        assertTrue(item.getDescription().equals("Cool Description"));
    }
    @Test
    public void crawlerCrawls() throws Exception {
        List<EventItem> list = Crawler.crawl();
        assertTrue(list.size() != 0);
    }
}