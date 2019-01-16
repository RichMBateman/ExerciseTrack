package com.bateman.rich.exercisetrack;

import android.content.Context;
import android.net.Uri;

import java.io.FileOutputStream;

public class FileWriter {
    /**
     * Write a file.  Returns a Uri representing this file
     * https://developer.android.com/training/data-storage/files
     * https://stackoverflow.com/questions/4926027/what-file-system-path-is-used-by-androids-context-openfileoutput
     * @param context
     */
    public static Uri writeFile(Context context, String fileName, String fileContents) {
        FileOutputStream outputStream;
        Uri fileUri = null;

        try {
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();

            fileUri = Uri.parse("content://" + context.getFilesDir() + "/" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileUri;
    }
}
