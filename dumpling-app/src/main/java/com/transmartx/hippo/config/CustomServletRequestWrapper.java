package com.transmartx.hippo.config;


import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author: letxig
 */
public class CustomServletRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;
    /**
     * Construct a wrapper for the specified request.
     *
     * @param request The request to be wrapped
     */
    public CustomServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        body = toByteArray(request.getInputStream(), Integer.MAX_VALUE);
    }

    private static byte[] toByteArray(InputStream stream, int length) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(length == Integer.MAX_VALUE ? 4096 : length);

        byte[] buffer = new byte[4096];
        int totalBytes = 0, readBytes;
        do {
            readBytes = stream.read(buffer, 0, Math.min(buffer.length, length-totalBytes));
            totalBytes += Math.max(readBytes,0);
            if (readBytes > 0) {
                baos.write(buffer, 0, readBytes);
            }
        } while (totalBytes < length && readBytes > -1);

        if (length != Integer.MAX_VALUE && totalBytes < length) {
            throw new IOException("unexpected EOF");
        }

        return baos.toByteArray();
    }


    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }
    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new RequestBodyCachingInputStream(body);
    }


    private class RequestBodyCachingInputStream extends ServletInputStream {
        private byte[] body;
        private int lastIndexRetrieved = -1;
        private ReadListener listener;
        public RequestBodyCachingInputStream(byte[] body) {
            this.body = body;
        }
        @Override
        public int read() throws IOException {
            if (isFinished()) {
                return -1;
            }
            int i = body[lastIndexRetrieved + 1];
            lastIndexRetrieved++;
            if (isFinished() && listener != null) {
                try {
                    listener.onAllDataRead();
                } catch (IOException e) {
                    listener.onError(e);
                    throw e;
                }
            }
            return i;
        }
        @Override
        public boolean isFinished() {
            return lastIndexRetrieved == body.length - 1;
        }
        @Override
        public boolean isReady() {
            // This implementation will never block
            // We also never need to call the readListener from this method, as this method will never return false
            return isFinished();
        }
        @Override
        public void setReadListener(ReadListener listener) {
            if (listener == null) {
                throw new IllegalArgumentException("listener cann not be null");
            }
            if (this.listener != null) {
                throw new IllegalArgumentException("listener has been set");
            }
            this.listener = listener;
            if (!isFinished()) {
                try {
                    listener.onAllDataRead();
                } catch (IOException e) {
                    listener.onError(e);
                }
            } else {
                try {
                    listener.onAllDataRead();
                } catch (IOException e) {
                    listener.onError(e);
                }
            }
        }
        @Override
        public int available() throws IOException {
            return body.length - lastIndexRetrieved - 1;
        }
        @Override
        public void close() throws IOException {
            lastIndexRetrieved = body.length - 1;
            body = null;
        }
    }


}
