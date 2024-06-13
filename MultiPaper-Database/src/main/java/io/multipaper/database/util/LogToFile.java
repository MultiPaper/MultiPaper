package io.multipaper.database.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class LogToFile {

    // TODO Use log4j instead

    public static void init() {
        File file = new File("master.log");
        renameFile(file);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            System.setOut(new PrintStream(new ForkedOutputStream(System.out, new BufferedOutputStream(fileOutputStream)), true));
            System.setErr(new PrintStream(new ForkedOutputStream(System.err, new BufferedOutputStream(fileOutputStream)), true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void renameFile(File file) {
        File oldFile = new File("master_old.log");

        if (oldFile.exists()) {
            oldFile.delete();
        }

        if (file.exists()) {
            file.renameTo(oldFile);
        }
    }

    private static class ForkedOutputStream extends OutputStream {

        private final OutputStream out1;
        private final OutputStream out2;

        public ForkedOutputStream(OutputStream out1, OutputStream out2) {
            this.out1 = out1;
            this.out2 = out2;
        }


        @Override
        public void write(int b) throws IOException {
            out1.write(b);
            out2.write(b);
        }

        @Override
        public void write(byte b[], int off, int len) throws IOException {
            out1.write(b, off, len);
            out2.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            out1.flush();
            out2.flush();
        }

        @Override
        public void close() throws IOException {
            out1.close();
            out2.close();
        }
    }
}
