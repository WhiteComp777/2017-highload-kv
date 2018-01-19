package ru.mail.polis.whitecomp777;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Created by root on 09/10/2017.
 */
public class MyFileDAO implements MyDAO {

    public MyFileDAO(@NotNull final File dir) {
        this.dir = dir;
    }

    @NotNull
    private final File dir;

    private String getLegalFilename(String filename){
        return  String.valueOf(filename.hashCode()) + filename.replaceAll("[:\\\\/*?|<>]", "");
    }

    @NotNull
    private File getFile(@NotNull final String key) throws IOException{
        return new File(dir, getLegalFilename(key));

    }

    public byte[] readFromInputStream(InputStream is) throws IOException{
        byte[] buf = new byte[8192];
        int len = 0;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        while (len != -1) {
            os.write(buf, 0, len);
            len = is.read(buf);
        }
        return os.toByteArray();
    }

    @NotNull
    private byte[] readFromFile(String fileName) throws IOException {
        final File file = getFile(fileName);
        InputStream is = new FileInputStream(file);
        return readFromInputStream(is);
    }


    @NotNull
    @Override
    public byte[] get(@NotNull final String key) throws NoSuchElementException, IllegalArgumentException, IOException {
        return readFromFile(key);
    }

    @Override
    public void upsert(@NotNull final String key, @NotNull final byte[] value) throws IllegalArgumentException, IOException {
        try(OutputStream os = new FileOutputStream(getFile(key))){
            os.write(value);
        }
    }

    @Override
    public void delete(@NotNull final String key) throws IllegalArgumentException, IOException {
        getFile(key).delete();
    }
}
