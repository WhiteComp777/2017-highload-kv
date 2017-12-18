package ru.mail.polis.whitecomp777;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Created by root on 09/10/2017.
 */
public interface MyDAO {


    @NotNull
    byte[] get(@NotNull String key) throws NoSuchElementException, IllegalArgumentException, IOException;

    void upsert(@NotNull String key, @NotNull byte[] value) throws IllegalArgumentException, IOException;

    void delete(@NotNull String key) throws IllegalArgumentException, IOException;
}
