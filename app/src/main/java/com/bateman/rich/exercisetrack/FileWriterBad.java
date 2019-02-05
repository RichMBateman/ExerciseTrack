package com.bateman.rich.exercisetrack;

import android.content.Context;
import android.net.Uri;

import java.io.FileOutputStream;

/**
 * Good lord... there's already a java.io.FileWriterBad class...
 * I bet i'm doing something wrong.
 * Part of this may be write... i had to do a ton of changes until something started working.  Would like to experiment more.
 */
public class FileWriterBad {
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
