package com.codekidlabs.fontio.helpers;


import com.codekidlabs.bruno.constants.References;

import java.io.File;

public class FileHelper {

    public static void createWorkstation() {
        File file = new File(References.FONTIO_DIR);
        if(!file.exists()) {
            file.mkdirs();
        }
    }
}
