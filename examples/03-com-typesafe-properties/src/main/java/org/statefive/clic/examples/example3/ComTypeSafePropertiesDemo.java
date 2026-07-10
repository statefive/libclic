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
package org.statefive.clic.examples.example3;

import com.typesafe.clic.properties.ConfigListType;
import com.typesafe.clic.properties.TypesafeConfigBuilder;
import com.typesafe.clic.properties.TypesafePropertiesStreamResource;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigList;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.statefive.clic.Clc;
import org.statefive.clic.ClcException;
import org.statefive.clic.properties.PropertiesBuilder;
import org.statefive.clic.properties.PropertiesLoadException;
import org.statefive.clic.properties.PropertiesSource;
import org.statefive.clic.properties.TypeInferralConfigBuilder;
import org.statefive.clic.valuetype.ListType;
import org.statefive.clic.valuetype.ValueTypeBuilder;
import org.statefive.clic.valuetype.ValueTypeFactory;

/**
 *
 * @author irch
 */
public class ComTypeSafePropertiesDemo {

    public static void main(String[] args) throws Exception {
        try {
            new ComTypeSafePropertiesDemo(args);
        } catch (ClcException | IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

    /**
     *
     * @param args
     * @throws IOException
     * @throws ClcException
     * @throws PropertiesLoadException
     */
    public ComTypeSafePropertiesDemo(String[] args) throws IOException, ClcException,
            PropertiesLoadException {
        ValueTypeFactory.getInstance().removeRegisteredValueType(ListType.LIST);
        ValueTypeBuilder<ConfigListType> configList
                = new ValueTypeBuilder<>(ConfigListType.class);
        ValueTypeFactory.getInstance().registerValueTypeBuilder(ListType.LIST, configList);
        
        InputStream clcStream = ComTypeSafePropertiesDemo.class.getResourceAsStream(
                "/overrides.clc");
        PropertiesSource source = new TypesafePropertiesStreamResource(
                "/cliapp.conf");
        PropertiesBuilder<Config> builder = new TypesafeConfigBuilder()
                .withClc(clcStream)
                .withTypeInferralConfig(
                        new TypeInferralConfigBuilder()
                                .withInferTypes()
                                .withFalseAsUnarySwitch()
                                .build())
                .withVersion()
                .addPropertiesSource(source);
        Config config = builder.build(args);

        // parseArgs will be false if help was invoked; in which case we don't
        // want to do any more processing and can quit:
        if (!Clc.getInstance().isParseArgs()) {
            System.exit(0);
        } else {
            System.out.println("IP address   : " + config.getString("host.ip"));
            System.out.println("Port         : " + config.getInt("host.port"));
            System.out.println("Protocol     : " + config.getString("host.protocol"));
            System.out.println("MIME types   : " + config.getList("file.mimeTypes").unwrapped());
            ConfigList list = config.getList("file.mimeTypes");
            for (int i = 0 ; i < list.size(); i++) {
                System.out.println("MIME type " + i + "  : " + list.get(i).render());
            }
            System.out.println("Extensions   : " + config.getList("file.extensions").unwrapped());
            ConfigList list2 = config.getList("file.extensions");
            for (int i = 0 ; i < list2.size(); i++) {
                System.out.println("Extension " + i + "  : " + list2.get(i).render());
            }
            System.out.println("Strip EXIF   : " + config.getString("strip.exif"));
            
            File inDir = (File) Clc.getInstance().getArgsValueTypes().get(0);
            System.out.println("Input Dir    : " + inDir.getAbsolutePath());
            File outDir = (File) Clc.getInstance().getArgsValueTypes().get(1);
            System.out.println("Output Dir   : " + outDir.getAbsolutePath());
        }
    }
}
