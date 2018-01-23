package ru.mail.polis.whitecomp777;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by root on 23/01/2018.
 */
public class MyHelper {
    public static byte[] inpStreamToByteArr(InputStream is) throws IOException {
        byte[] buf = new byte[8192];
        int len = 0;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        while (len != -1) {
            os.write(buf, 0, len);
            len = is.read(buf);
        }
        return os.toByteArray();
    }
}
