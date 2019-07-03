package testing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author: Crysfel Villa
 * Created: Friday, June 03, 2005 4:54:59 PM
 * Modified: Friday, June 03, 2005 4:54:59 PM
 */


public class Copiar{

    /**
     * Copia un directorio con todo y su contendido
     * @param srcDir
     * @param dstDir
     * @throws IOException
     */
    public void copyDirectory(File srcDir, File dstDir) throws IOException {
        if (srcDir.isDirectory()) {
            if (!dstDir.exists()) {
                dstDir.mkdir();
            }

            String[] children = srcDir.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(srcDir, children[i]),
                    new File(dstDir, children[i]));
            }
        } else {
            copy(srcDir, dstDir);
        }
    }

    /**
     * Copia un solo archivo
     * @param src
     * @param dst
     * @throws IOException
     */
    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);


        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}