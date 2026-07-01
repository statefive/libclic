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
import java.lang.Integer;
import java.lang.String;
import java.util.List;
import org.statefive.clic.ArgsListener;
import org.statefive.clic.OptionListener;

public class OptionHelper implements OptionListener, ArgsListener {

    private File args0InDir;
    private File args1OutDir;
    private String hostIp;
    private List<String> fileMimetypes;
    private String hostProtocol;
    private List<String> fileExtensions;
    private Integer hostPort;
    private boolean stripExif;

    @Override
    public void option(String option, Object value) {
        switch (option) {
            case "help":
                System.exit(0);
            case "host-ip":
                hostIp = (String) value;
                break;
            case "file-mimeTypes":
                fileMimetypes = (List<String>) value;
                break;
            case "host-protocol":
                hostProtocol = (String) value;
                break;
            case "file-extensions":
                fileExtensions = (List<String>) value;
                break;
            case "host-port":
                hostPort = (int) value;
                break;
            case "strip-exif":
                stripExif = true;
                break;
        }

    }

    @Override
    public void argument(String name, int index, Object value) {
        switch (name) {
            case "in-dir":
                switch (index) {
                    case 0:
                        args0InDir = (File) value;
                        break;
                }
                break;
            case "out-dir":
                switch (index) {
                    case 1:
                        args1OutDir = (File) value;
                        break;
                }
                break;
        }

    }

    public File getArgs0InDir() {
        return args0InDir;
    }

    public File getArgs1OutDir() {
        return args1OutDir;
    }

    public List<String> getFileExtensions() {
        return fileExtensions;
    }

    public List<String> getFileMimetypes() {
        return fileMimetypes;
    }

    public String getHostIp() {
        return hostIp;
    }

    public Integer getHostPort() {
        return hostPort;
    }

    public String getHostProtocol() {
        return hostProtocol;
    }

    public boolean isStripExif() {
        return stripExif;
    }

}
