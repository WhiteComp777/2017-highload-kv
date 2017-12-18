package ru.mail.polis.whitecomp777;

import org.jetbrains.annotations.NotNull;

import java.io.*;
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

    private static boolean isFilenameValid(String file) throws IOException{
        File f = new File(file);
        try {
            f.getCanonicalPath();
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    @NotNull
    private File getFile(@NotNull final String key) throws IOException{
        if(!isFilenameValid(key)){
            throw new IOException("Wrong chars in filename");
        }
        return new File(dir, key);
    }

    @NotNull
    @Override
    public byte[] get(@NotNull final String key) throws NoSuchElementException, IllegalArgumentException, IOException {
        final File file = getFile(key);
        final byte[] value = new byte[(int) file.length()];
        try(InputStream is = new FileInputStream(file)){
            is.read(value);
        }
        return value;
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
