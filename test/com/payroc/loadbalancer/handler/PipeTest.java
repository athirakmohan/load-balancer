package com.payroc.loadbalancer.handler;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class PipeTest extends TestCase {
    private final String TEST_DATA = "Pipe Test";

    public void testDataTransfer() throws Exception {

        ByteArrayInputStream input = new ByteArrayInputStream(TEST_DATA.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        Pipe pipe = new Pipe(input, output);
        Thread pipeThread = new Thread(pipe);
        pipeThread.start();

        pipeThread.join(5000);

        String result = new String(output.toByteArray());
        assertEquals("Pipe should transfer all data", TEST_DATA, result);
        assertFalse("Pipe thread should be finished after transfer", pipeThread.isAlive());
    }

    public void testEmptyTransfer() throws Exception {

        ByteArrayInputStream input = new ByteArrayInputStream(new byte[0]);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        Pipe pipe = new Pipe(input, output);
        Thread pipeThread = new Thread(pipe);
        pipeThread.start();
        pipeThread.join(500);

        assertEquals("Output stream should be empty", 0, output.size());
    }
}