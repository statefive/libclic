/**
 * Copyright 2019 www.statefive.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.statefive.clic.properties;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Test-based input stream.
 * 
 * <p>
 * Only the {@link #read()} method is used in this class.
 *
 * @author rich
 */
public class BadInputTestStream extends InputStream {

    /**
     * Always throws an exception.
     * 
     * @return N/A.
     * 
     * @throws IOException 
     */
    @Override
    public int read() throws IOException {
        throw new IOException("Failed to read stream.");
    }

    @Override
    public long transferTo(OutputStream out) throws IOException {
        return super.transferTo(out);
    }

    @Override
    public boolean markSupported() {
        return super.markSupported();
    }

    @Override
    public void reset() throws IOException {
        super.reset();
    }

    @Override
    public void mark(int readlimit) {
        super.mark(readlimit);
    }

    @Override
    public void close() throws IOException {
        super.close();
    }

    @Override
    public int available() throws IOException {
        return super.available();
    }

    @Override
    public void skipNBytes(long n) throws IOException {
        super.skipNBytes(n);
    }

    @Override
    public long skip(long n) throws IOException {
        return super.skip(n);
    }

    @Override
    public int readNBytes(byte[] b, int off, int len) throws IOException {
        return super.readNBytes(b, off, len);
    }

    @Override
    public byte[] readNBytes(int len) throws IOException {
        return super.readNBytes(len);
    }

    @Override
    public byte[] readAllBytes() throws IOException {
        return super.readAllBytes();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return super.read(b, off, len);
    }

    @Override
    public int read(byte[] b) throws IOException {
        return super.read(b);
    }
    
}
