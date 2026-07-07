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
package org.statefive.clic.examples.example2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import org.statefive.clic.Clc;
import org.statefive.clic.ClcException;
import org.statefive.clic.properties.JavaPropertiesBuilder;
import org.statefive.clic.properties.PropertiesBuilder;
import org.statefive.clic.properties.PropertiesLoadException;
import org.statefive.clic.properties.PropertiesSource;
import org.statefive.clic.properties.PropertiesStreamSource;
import org.statefive.clic.properties.TypeInferralConfigBuilder;

/**
 *
 * @author irch
 */
public class JavaPropertiesDemo {

    public static void main(String[] args) throws Exception {
        try {
            new JavaPropertiesDemo(args);
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
    public JavaPropertiesDemo(String[] args) throws IOException, ClcException,
            PropertiesLoadException {
        InputStream clcStream = JavaPropertiesDemo.class.getResourceAsStream(
                "/overrides.clc");
        PropertiesSource source = new PropertiesStreamSource(
                JavaPropertiesDemo.class.getResourceAsStream(
                        "/cliapp.props"));
        PropertiesBuilder<Properties> builder = new JavaPropertiesBuilder()
                .withClc(clcStream)
                .withTypeInferralConfig(
                        new TypeInferralConfigBuilder()
                                .withInferTypes()
                                .withFalseAsUnarySwitch()
                                .build())
                .withVersion()
                .addPropertiesSource(source);
        Properties props = builder.build(args);

        // parseArgs will be false if help was invoked; in which case we don't
        // want to do any more processing and can quit:
        if (!Clc.getInstance().isParseArgs()) {
            System.exit(0);
        } else {
            System.out.println("IP address   : " + props.getProperty("host.ip"));
            System.out.println("Port         : " + (int) props.get("host.port"));
            System.out.println("Protocol     : " + props.getProperty("host.protocol"));
            System.out.println("MIME Types   : " + props.get("file.mimeTypes"));
            List<String> list = (List<String>) props.get("file.mimeTypes");
            for (int i = 0; i < list.size(); i++) {
                System.out.println("MIME type " + i + "  : " + list.get(i));
            }
            System.out.println("Extensions   : " + (List<String>) props.get("file.extensions"));
            List<String> list2 = (List<String>) props.get("file.extensions");
            for (int i = 0; i < list2.size(); i++) {
                System.out.println("Extension " + i + "  : " + list2.get(i));
            }
            System.out.println("Strip EXIF   : " + (boolean) props.get("strip.exif"));

            File inDir = (File) Clc.getInstance().getArgsValueTypes().get(0);
            System.out.println("Input Dir    : " + inDir.getAbsolutePath());
            File outDir = (File) Clc.getInstance().getArgsValueTypes().get(1);
            System.out.println("Output Dir   : " + outDir.getAbsolutePath());
        }
    }
}
