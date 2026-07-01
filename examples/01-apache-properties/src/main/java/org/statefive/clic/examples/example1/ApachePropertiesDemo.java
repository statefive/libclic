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
package org.statefive.clic.examples.example1;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.configuration2.Configuration;
import org.statefive.clic.Clc;
import org.statefive.clic.ClcException;
import org.statefive.clic.properties.PropertiesBuilder;
import org.statefive.clic.properties.PropertiesConfigurationBuilder;
import org.statefive.clic.properties.PropertiesLoadException;
import org.statefive.clic.properties.PropertiesSource;
import org.statefive.clic.properties.PropertiesStreamSource;
import org.statefive.clic.properties.TypeInferralConfigBuilder;

/**
 *
 * @author irch
 */
public class ApachePropertiesDemo {

    public static void main(String[] args) throws Exception {
        try {
            new ApachePropertiesDemo(args);
        } catch (ClcException | IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     *
     * @param args
     * @throws IOException
     * @throws ClcException
     * @throws PropertiesLoadException
     */
    public ApachePropertiesDemo(String[] args) throws IOException, ClcException,
            PropertiesLoadException {
        InputStream clcStream = ApachePropertiesDemo.class.getResourceAsStream(
                "/overrides.clc");
        PropertiesSource source = new PropertiesStreamSource(
                ApachePropertiesDemo.class.getResourceAsStream(
                        "/cliapp.props"));
        PropertiesBuilder<Configuration> builder = new PropertiesConfigurationBuilder()
                .withClc(clcStream)
                .withTypeInferralConfig(
                        new TypeInferralConfigBuilder()
                                .withInferTypes()
                                .withFalseAsUnarySwitch()
                                .build())
                .addPropertiesSource(source);
        Configuration props = builder.build(args);

        // parseArgs will be false if help was invoked; in which case we don't
        // want to do any more processing and can quit:
        if (!Clc.getInstance().isParseArgs()) {
            System.exit(0);
        } else {
            System.out.println("IP address   : " + props.getString("host.ip"));
            System.out.println("Port         : " + props.getInt("host.port"));
            System.out.println("Protocol     : " + props.getString("host.protocol"));
            System.out.println("MIME types   : " + props.getList("file.mimeTypes"));
            List<String> list = props.getList(String.class, "file.mimeTypes");
            for (int i = 0; i < list.size(); i++) {
                System.out.println("MIME type " + i + "  : " + list.get(i));
            }
            System.out.println("Extensions   : " + props.getList("file.extensions"));
            List<String> list2 = props.getList(String.class, "file.extensions");
            for (int i = 0; i < list2.size(); i++) {
                System.out.println("Extension " + i + "  : " + list2.get(i));
            }
            System.out.println("Strip EXIF   : " + props.getString("strip.exif"));

            File inDir = (File) Clc.getInstance().getArgsValueTypes().get(0);
            System.out.println("Input Dir    : " + inDir.getAbsolutePath());
            File outDir = (File) Clc.getInstance().getArgsValueTypes().get(1);
            System.out.println("Output Dir   : " + outDir.getAbsolutePath());
        }
    }
}
