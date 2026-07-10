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
package org.statefive.clic.examples.example4;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.statefive.clic.Clc;
import org.statefive.clic.ClcException;
import org.statefive.clic.properties.PropertiesLoadException;

/**
 *
 * @author irch
 */
public class ClcDemo {

    public static void main(String[] args) throws Exception {
        try {
            new ClcDemo(args);
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
    public ClcDemo(String[] args) throws IOException, ClcException,
            PropertiesLoadException {
        InputStream is = ClcDemo.class.getResourceAsStream(
                "/app.clc");
        Clc clc = Clc.getInstance();
        OptionHelper optionHelper = new OptionHelper();
        clc.addOptionListener(optionHelper);
        clc.addArgsListener(optionHelper);
        clc.process(is, "UTF-8", args);

        System.out.println("IP address   : " + optionHelper.getHostIp());
        System.out.println("Port         : " + optionHelper.getHostPort());
        System.out.println("Protocol     : " + optionHelper.getHostProtocol());
        System.out.println("MIME types   : " + optionHelper.getFileMimetypes());
        System.out.println("Extensions   : " + optionHelper.getFileExtensions());
        System.out.println("Strip EXIF   : " + optionHelper.isStripExif());

        File inDir = (File) optionHelper.getArgs0InDir();
        System.out.println("Input Dir    : " + inDir.getAbsolutePath());
        File outDir = (File) optionHelper.getArgs1OutDir();
        System.out.println("Output Dir   : " + outDir.getAbsolutePath());
    }
}
