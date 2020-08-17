package testing;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 *
 * @author bgari
 */
public class TestDownload {

    /**
     * @param args the command line arguments
     * @throws java.net.MalformedURLException
     */
    public static void main(String[] args) throws MalformedURLException, IOException {
        /*
        URL url = new URL("https://code.jquery.com/jquery-3.5.1.min.js");
        File f = new File("jquery-3.5.1.min.js");
        FileUtils.copyURLToFile(url, f);
         */
        URL website = new URL("https://code.jquery.com/jquery-3.5.1.min.js");
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream("jquery-3.5.1.min.js");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }

}
