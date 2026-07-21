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
    private String ip;
    private List<String> mimetypes;
    private String protocol;
    private List<String> extensions;
    private Integer port;
    private boolean stripExif;

    @Override
    public void option(String option, Object value) {
        switch (option) {
            case "h":
            case "help":
                System.exit(0);
            case "ip":
                ip = (String) value;
                break;
            case "mimeTypes":
                mimetypes = (List<String>) value;
                break;
            case "P":
            case "protocol":
                protocol = (String) value;
                break;
            case "extensions":
                extensions = (List<String>) value;
                break;
            case "p":
            case "port":
                port = (int) value;
                break;
            case "strip-exif":
                stripExif = true;
                break;
            case "v":
            case "version":
                System.exit(0);
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

    public List<String> getExtensions() {
        return extensions;
    }

    public String getIp() {
        return ip;
    }

    public List<String> getMimetypes() {
        return mimetypes;
    }

    public Integer getPort() {
        return port;
    }

    public String getProtocol() {
        return protocol;
    }

    public boolean isStripExif() {
        return stripExif;
    }

}
