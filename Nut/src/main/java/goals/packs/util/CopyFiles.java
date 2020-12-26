package nut.goals.packs.util;

import nut.logging.Log;

import java.io.File;
import java.io.IOException;

import java.nio.file.CopyOption;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CopyFiles
{
    /** Instance logger */
    private Log log;

    private String source;
    private String dest;

    // ==========================================================================
    public CopyFiles(String source, String dest)
    {
        this.source = source;
        this.dest   = dest;
        log = new Log();
    }

    public void process() throws IOException
    {
        File destFile = new File(dest);
        if ( !destFile.exists() ) {
          log.debug("* make dir " + dest);
          destFile.mkdirs();
        }
        File sourceFile = new File(source);
        if ( sourceFile.exists() ) {
            log.debug("* copy " + source + " to " + dest);
            CopyOption options = StandardCopyOption.COPY_ATTRIBUTES;
            if (sourceFile.isDirectory())
                copyFolder(sourceFile, destFile, options);
            else {
                ensureParentFolder(destFile);
                copyFile(sourceFile, destFile, options);
            }
        } else {
          log.warn(source + " not found");
        }
    }

    // ==========================================================================
    private void copyFolder(File source, File dest, CopyOption... options) throws IOException {
        if (!dest.exists())
            dest.mkdirs();
        File[] contents = source.listFiles();
        if (contents != null) {
            for (File f : contents) {
                File newFile = new File(dest.getCanonicalPath() + File.separator + f.getName());
                if (f.isDirectory())
                    copyFolder(f, newFile, options);
                else
                    copyFile(f, newFile, options);
            }
        }
    }

    private void copyFile(File source, File dest, CopyOption... options) throws IOException {
        if (dest.exists())
            dest.delete();
        Files.copy(source.toPath(), dest.toPath(), options);
    }

    private void ensureParentFolder(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists())
            parent.mkdirs();
    }

}

