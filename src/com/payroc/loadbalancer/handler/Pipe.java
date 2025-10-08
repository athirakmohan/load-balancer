package com.payroc.loadbalancer.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Pipe implements Runnable {
    private final InputStream input;
    private final OutputStream output;

    public Pipe(InputStream input, OutputStream output) {
        this.input = input;
        this.output = output;
    }

    public void run() {
        byte[] buffer = new byte[4096];
        int bytesRead;

        try {
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
                output.flush();
            }
        } catch (IOException e) {
        }
    }
}