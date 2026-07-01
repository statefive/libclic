/**
 * Copyright 2019 www.statefive.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.typesafe.clic.properties;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.statefive.clic.ClcException;
import org.statefive.clic.properties.AbstractPropertiesReader;
import org.statefive.clic.properties.PropertiesCommandSource;
import org.statefive.clic.properties.PropertiesFileSource;
import org.statefive.clic.properties.PropertiesLoadException;
import org.statefive.clic.properties.PropertiesSource;

/**
 *
 * @author rich
 */
public class TypesafeConfigReader extends AbstractPropertiesReader<Config> {

    /**
     *
     */
    private Config config;

    /**
     *
     * @param source
     * @return
     * @throws ClcException
     */
    @Override
    public Config read(PropertiesSource source) throws ClcException, PropertiesLoadException {
        File file = null;
        if (source instanceof PropertiesCommandSource) {
            PropertiesCommandSource pcs = (PropertiesCommandSource) source;
            PropertiesSource ps = (PropertiesSource) pcs.getSource();
            try {
                if (ps instanceof PropertiesFileSource) {
                    file = ((PropertiesFileSource) ps).getSource();
                    FileInputStream fis = new FileInputStream(file);
                    config = ConfigFactory.parseFile(file);
                    fis.close();
                } else {
                    throw new ClcException("Cannot load command properties using "
                            + source.getClass().getName());
                }
            } catch (IOException ex) {
                throw new PropertiesLoadException(ex.getMessage(), ex);
            }
        } else if (source instanceof PropertiesFileSource) {
            file = ((PropertiesFileSource) source).getSource();
            if (file == null) {
                config = ConfigFactory.load();
            } else {
                config = ConfigFactory.parseFile(file);
            }
        } else if (source instanceof TypesafePropertiesStreamResource) {
            try {
                config = ConfigFactory.parseString(getResourceData(
                        (TypesafePropertiesStreamResource) source));
            } catch (IOException ex) {
                throw new ClcException(ex.getMessage());
            }
        } else {
            throw new ClcException("Cannot load properties using "
                    + source.getClass().getName());
        }
        return config;
    }

    /**
     *
     * @return
     */
    @Override
    public Config getProperties() {
        return config;
    }

    /**
     * Get the data from the specified resource stream.
     * 
     * @param source non-{@code null} properties resource.
     * 
     * @return data from the stream.
     * 
     * @throws IOException if the resource does not exist or cannot be read.
     */
    private String getResourceData(TypesafePropertiesStreamResource source) throws IOException {
        String resource = ((TypesafePropertiesStreamResource) source).getSource();
        InputStream is = TypesafeConfigReader.class.getResourceAsStream(resource);
        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        for (int result = bis.read(); result != -1; result = bis.read()) {
            buf.write((byte) result);
        }
        bis.close();
        is.close();
        return buf.toString();
    }

}
