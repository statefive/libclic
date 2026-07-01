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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.statefive.clic.ClcException;

/**
 * Implementation of the implementation of the
 * {@code org.apache.commons.configuration2.PropertiesConfiguration} property
 * reader.
 *
 * @author rich
 */
public class PropertiesConfigurationReader extends AbstractPropertiesReader<Configuration> {

    /**
     * Property configuration.
     */
    private PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();

    /**
     * Get the properties that have been read by the reader.
     *
     * @return non-{@code null} properties.
     */
    @Override
    public Configuration getProperties() {
        propertiesConfiguration.setListDelimiterHandler(new DefaultListDelimiterHandler(
                PropertiesConfigurationBuilder.DEFAULT_LIST_DELIMITER));
        return propertiesConfiguration;
    }

    /**
     * Read the configuration from the specified source.
     *
     * @param source non-{@code null} source to read.
     *
     * @return non-{@code null} configuration.
     *
     * @throws ClcException if the source is not a known source for
     * this reader; valid sources are {@link PropertiesStreamSource} and
     * {@link PropertiesFileSource}.
     *
     * @throws PropertiesLoadException if there is a problem reading from the
     * source stream or file.
     */
    @Override
    public Configuration read(PropertiesSource source)
            throws ClcException, PropertiesLoadException {
        try {
            boolean supported = true;
            String invalidSource = null;
            if (source instanceof PropertiesStreamSource) {
                InputStream is = ((PropertiesStreamSource) source).getSource();
                Reader reader = new InputStreamReader(is);
                ((PropertiesConfiguration) propertiesConfiguration).read(reader);
                reader.close();
                is.close();
            } else if (source instanceof PropertiesFileSource) {
                File file = ((PropertiesFileSource) source).getSource();
                if (!file.exists()) {
                    throw new PropertiesLoadException(file.getPath()
                            + " (No such file or directory)");
                }
                Reader reader = getReader(file);
                ((PropertiesConfiguration) propertiesConfiguration).read(reader);
                reader.close();
            } else if (source instanceof PropertiesCommandSource) {
                PropertiesCommandSource pcs = (PropertiesCommandSource) source;
                PropertiesSource ps = (PropertiesSource) pcs.getSource();
                if (ps instanceof PropertiesFileSource) {
                    File file = ((PropertiesFileSource) ps).getSource();
                    Reader reader = getReader(file);
                    ((PropertiesConfiguration) propertiesConfiguration).read(reader);
                    reader.close();
                } else if (ps instanceof PropertiesStreamSource) {
                    InputStream is = ((PropertiesStreamSource) ps).getSource();
                    Reader reader = new InputStreamReader(is);
                    ((PropertiesConfiguration) propertiesConfiguration).read(reader);
                    reader.close();
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
        } catch (ConfigurationException ex) {
            throw new ClcException("Failed to read properties: "
                    + ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new PropertiesLoadException(ex.getMessage(), ex);
        }
        return propertiesConfiguration;
    }

    /**
     * Inspired by
     * https://stackoverflow.com/questions/39573880/apache-commons-configuration2-how-to-read-data-from-inputstream
     * this section enables 'include' to load other properties files when
     * present in a properties configuration:
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    private Reader getReader(File file) throws FileNotFoundException, ConfigurationException {
        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<PropertiesConfiguration> fileBuilder
                = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                        .configure(params.fileBased().setFileName(file.getAbsolutePath()));
        propertiesConfiguration = fileBuilder.getConfiguration();
        return new FileReader(file);
    }

//    /**
//     *
//     * @param comments
//     * @return
//     */
//    private List<String> getComments(String comments) {
//        List<String> lines = new ArrayList<>();
//        String[] lineArray = null;
//        if (comments != null) {
//            lineArray = comments.split(System.getProperty("line.separator"));
//            for (String line : lineArray) {
//                lines.add(line);
//            }
//        }
//        return lines;
//    }
}
