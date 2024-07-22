package com.tacz.guns.util;

import com.tacz.guns.GunMod;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class IOReader {

    public static String toString(InputStream input, Charset charset) throws IOException {
        String result = IOUtils.toString(input, charset);
        return result
                .replace(GunMod.ORIGINAL_MOD_ID + ":", GunMod.MOD_ID + ":");
    }
}
