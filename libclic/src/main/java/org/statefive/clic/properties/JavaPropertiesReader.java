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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.statefive.clic.ClcException;

/**
 * Java {@code java.util.Properties} implementation.
 *
 * @author rich
 */
public class JavaPropertiesReader extends AbstractPropertiesReader<Properties> {

    /**
     * Java properties, overridden by command line arguments (if present).
     */
    private final Properties properties = new Properties();

    /**
     * Get the properties that have been read by the reader.
     *
     * @return non-{@code null} properties.
     */
    @Override
    public Properties getProperties() {
        return properties;
    }

    /**
     * Read the properties from the specified source.
     *
     * @param source non-{@code null} source to read.
     *
     * @return non-{@code null} properties.
     *
     * @throws ClcException if the source is not a known source for
     * this reader; valid sources are {@link PropertiesStreamSource} and
     * {@link PropertiesFileSource}.
     *
     * @throws PropertiesLoadException if there is a problem reading from the
     * source stream or file.
     */
    @Override
    public Properties read(PropertiesSource source) throws ClcException, PropertiesLoadException {
        try {
            boolean supported = true;
            String invalidSource = null;
            if (source instanceof PropertiesStreamSource) {
                InputStream is = ((PropertiesStreamSource) source).getSource();
                properties.load(is);
                is.close();
            } else if (source instanceof PropertiesFileSource) {
                File file = ((PropertiesFileSource) source).getSource();
                FileInputStream fis = new FileInputStream(file);
                properties.load(fis);
                fis.close();
            } else if (source instanceof PropertiesCommandSource) {
                PropertiesCommandSource pcs = (PropertiesCommandSource) source;
                PropertiesSource ps = (PropertiesSource) pcs.getSource();
                if (ps instanceof PropertiesFileSource) {
                    File file = ((PropertiesFileSource) ps).getSource();
                    FileInputStream fis = new FileInputStream(file);
                    properties.load(fis);
                    fis.close();
                } else if (ps instanceof PropertiesStreamSource) {
                    InputStream is = ((PropertiesStreamSource) ps).getSource();
                    properties.load(is);
                    is.close();
                } else {
                    supported = false;
                    invalidSource = ps.getClass().getSimpleName();
                }
            } else {
                supported = false;
                invalidSource = source.getClass().getSimpleName();
            }
            if (!supported) {
                throw new ClcException("Cannot load properties using "
                        + invalidSource);
            }
        } catch (IOException ex) {
            throw new PropertiesLoadException(ex.getMessage(), ex);
        }
        return properties;
    }

}
