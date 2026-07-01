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
package org.statefive.clic.valuetype;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Value type representing a directory; by default, no checks are made as to the
 * existence of the directory, although users can specify the file type property
 * that will perform necessary checks.
 *
 * <p>
 * <strong>Properties</strong>
 * <ul>
 *
 * <li><strong>dirType</strong>=<i>[type]</i> (optional): If not supplied, no
 * checks are performed whether the file exists or if it's a directory or file.
 * One of:
 *
 * <ul>
 * <li><strong>exists</strong>: the created directory must exist as a directory
 * (not a file) on the file system. If this property is specified, any of the
 * other properties (recursive, suffixes etc.) may also be specified according
 * to the constraints set out for each property. Registered
 * {@link DirUpdateListener}s will be updated once all arguments have been
 * processed;</li>
 * <li><strong>!exists</strong>: the created type must not exist as a directory
 * <i>or</i> a file on the file system. If this property is set the directory
 * type cannot have any other properties set on it;</li>
 * <li><strong>mkdirs</strong>: if the directory does not exist, create it. If
 * this property is set the directory type cannot have any other properties set
 * on it;</li>
 * </ul>
 * </li>
 *
 * <li><strong>recursive</strong>=<i>[true|false]</i> (optional): recursively
 * traverse all directories within the specified directory if {@code true},
 * otherwise just scan the directory specified for this directory type. Callers
 * should ensure they have implemented a {@link DirUpdateListener} to receive
 * updates as the directory/directories are scanned. If using recursion,
 * directories will be scanned using a breadth-first strategy unless a different
 * strategy is employed (see below). If this property is set as {@code true} or
 * {@code false}, callers are also required to set the {@code listener-id}
 * (again, see below);</li>
 * <li><strong>listener-id</strong>=<i>[id]</i> (optional, unless
 * {@code recursive} is set in which case it is mandatory): the listener ID.
 * When updates to a directory are called by one of the updates specified in
 * {@link DirUpdateListener}, listeners can check to see if the ID from the
 * update matches their registered ID;</li>
 * <li><strong>suffixes</strong>=<i>[suffix1 suffix2 ... suffixn]</i>
 * (optional): space-separated list of suffixes that any files must match . Only
 * files that match the specified suffixes (ignoring case) will be included in
 * any updates to
 * {@link DirUpdateListener#directoryTraversed(java.io.File, java.io.File[], java.lang.String)};</li>
 * <li><strong>matches</strong>=<i>[regex1 regex2 ... regexn]</i>
 * (optional): space-separated list of regular expressions that any files must
 * match before being considered for inclusion to the
 * {@link DirUpdateListener#directoryTraversed(java.io.File, java.io.File[], java.lang.String)}
 * call. If specified with {@code suffixes} property, only files that match the
 * regular expression and has one of the specified suffixes will be
 * considered;</li>
 * <li><strong>strategy</strong>=<i>[depth|breadth]</i> (optional): employ
 * either a depth-first or breadth-first strategy when searching directories,
 * respectively. Only used if {@code recursive} is set to {@code true}.</li>
 *
 * </ul>
 */
public class DirType extends AbstractValueType<File, String> {

    /**
     * Type name.
     */
    public static final String DIRECTORY = "dir";

    /**
     * Directory type to determine if the directory should exist or not.
     */
    private static final String DIR_TYPE = "dirType";

    /**
     * Create the directory if it doesn't exist.
     */
    private static final String MKDIRS = "mkdirs";

    /**
     * Property to define if a listing should be obtained for a directory or if
     * it should be recursively searched.
     */
    private static final String RECURSIVE = "recursive";

    /**
     * Property to define the type's listener ID that callers will use to
     * identify if they are registered as a {@link DirUpdateListener}.
     */
    private static final String LISTENER_ID = "listener-id";

    /**
     * List of file suffixes (case insensitive) to consider as matches when
     * getting a list of files from a given directory.
     */
    private static final String SUFFIXES = "suffixes";

    /**
     * List of file matches to consider as matches when getting a list of files
     * from a given directory.
     */
    private static final String MATCHES = "matches";

    /**
     * List of directories to ignore when considering directories.
     */
    private static final String DIR_BLACKLIST = "dir-blacklist";

    /**
     * Strategy - either breadth or depth first search.
     */
    private static final String STRATEGY = "strategy";

    /**
     * Breadth-first search strategy for recursive search directories.
     */
    private static final String BREADTH_FIRST = "breadth";

    /**
     * Depth-first search strategy for recursive search directories.
     */
    private static final String DEPTH_FIRST = "depth";

    /**
     * The directory constructed from this type.
     */
    private File directory;

    /**
     * By default, this type does not check for the existence (or non-existence)
     * of any created file/directory; by setting this to {@code true}, callers
     * imply any directory created must exist (and must not be a file), or an
     * exception will be raised; conversely, setting it to {@code false} implies
     * the caller wants to ensure that the specified directory does not already
     * exist.
     */
    private Boolean exists = null;

    /**
     *
     */
    private boolean mkdirs = false;

    /**
     * If {@code null}, no listeners will be updated; if {@code false} the
     * listener will receive an update of just the directory listing; if
     * {@code true} listeners will receive an update for the directory and all
     * sub-directories within the directory.
     */
    private Boolean recursive;

    /**
     * Recursive search strategy.
     */
    private boolean depthFirst;

    /**
     * If non-{@code null}, the ID used to update any
     * {@link DirUpdateListener}s.
     */
    private String listenerId;

    /**
     *
     */
    private final List<String> suffixes = new ArrayList<>();

    /**
     *
     */
    private final List<String> matches = new ArrayList<>();

    /**
     *
     */
    private final List<String> dirBlacklist = new ArrayList<>();

    /**
     *
     */
    private final FileFilter dirFilter = new DirectoryFileFilter();

    /**
     *
     */
    private final FileFilter fileFilter = new DirTypeFileFilter();
    
    /**
     * 
     */
    public DirType() {
        setPackageName(File.class.getPackageName());
        setJavaClassName(File.class.getSimpleName());
        setJavaPrimitiveName(null);
    }

    /**
     * Get the file from the specified data. The data is converted to a Java
     * {@code java.io.File} object and then necessary checks made against any
     * properties supplied.
     *
     * @param data non-{@code null} data representing a file.
     *
     * @return the file it it could be constructed.
     *
     * @throws ValueTypeCreationException if any of the specified properties
     * conflict with the created file, or the directory is an existing file
     * (which conflicts with the type being a directory type)
     */
    @Override
    public File getValue(String data) throws ValueTypeCreationException {
        directory = new File(data);
        if (directory.isFile()) {
            throw new ValueTypeCreationException("Directory " + data
                    + " is a file.");
        }
        if (exists != null) {
            if (exists && !directory.exists()) {
                throw new ValueTypeCreationException("Specified directory " + data
                        + " does not exist.");
            } else if (!exists && directory.exists()) {
                throw new ValueTypeCreationException("Specified directory " + data
                        + " already exists.");
            }
        }
        if (!directory.exists() && mkdirs) {
            if (!directory.mkdirs()) {
                throw new ValueTypeCreationException("Could not create directory"
                        + data);
            }
        }
        return directory;
    }

    /**
     * As {@link #getValue(java.lang.String)}.
     *
     * @param data non-{@code null} data representing a file.
     *
     * @throws ValueTypeCreationException as
     * {@link #getValue(java.lang.String)}.
     */
    @Override
    public void setDefault(String data) throws ValueTypeCreationException {
        getValue(data);
    }

    /**
     * Get the directory for this directory type.
     *
     * @return the directory, if it has been set; {@code null} otherwise. If the
     * directory type has not had {@link #getValue(java.lang.String)} invoked,
     * the directory will always be {@code null}.
     */
    public File getDirectory() {
        return directory;
    }

    /**
     * If {@code null}, implies this directory type is not interested in
     * receiving updates; if {@code true} any registered listeners will receive
     * updates for all sub-directories scanned; if {@code false}, only scan the
     * base directory.
     *
     * @return {@code true} to recursively scan directories, {@code false} to
     * scan only the base directory, {@code null} to not perform any scanning.
     */
    public Boolean isRecursive() {
        return recursive;
    }

    /**
     * Listener ID for this directory type; multiple listeners can use this to
     * identify which directory is being updated. This is the listener ID that
     * will be passed to
     * {@link DirUpdateListener#directoryTraversed(java.io.File, java.io.File[], java.lang.String)}.
     *
     * @return the listener ID; if {@code null}, implies no updates will be
     * made.
     */
    public String getListenerId() {
        return listenerId;
    }

    /**
     * Get the file filter for this directory type.
     *
     * @return the non-{@code null} file filter. Note that by default the file
     * filter accepts all files (not directories) unless suffixes or text
     * matches have been associated with the filter.
     */
    public FileFilter getFileFilter() {
        return fileFilter;
    }

    /**
     * Get the directory filter for this directory type.
     *
     * @return the non-{@code null} directory filter.
     */
    public FileFilter getDirFilter() {
        return dirFilter;
    }

    /**
     * Construct the file according to the specified properties.
     *
     * @param properties valid file type properties or {@code null} if no
     * properties are used.
     *
     * @throws ValueTypeCreationException if the properties cannot be parsed, or
     * if the parsed properties do not conform to the specified file (for
     * example, a directory that exists but the file type was {@code !exists}.
     */
    @Override
    public void setProperties(String properties) throws ValueTypeCreationException {
        List<String> props = new ArrayList<>();
        if (properties.contains(",")) {
            props.addAll(Arrays.asList(properties.split(",")));
        } else {
            props.add(properties.trim());
        }
        for (String property : props) {
            if (!property.contains("=")) {
                throw new ValueTypeCreationException("Invalid properties: "
                        + properties);
            }
            String[] data = property.split("=");
            switch (data[0].trim()) {
                case DIR_TYPE:
                    switch (data[1].trim()) {
                        case FileType.EXISTS:
                            exists = true;
                            break;
                        case FileType.NOT_EXISTS:
                            exists = false;
                            break;
                        case MKDIRS:
                            mkdirs = true;
                            break;
                        default:
                            throw new ValueTypeCreationException(
                                    "Unknown directory type: " + data[1]
                                    + ". Expected " + FileType.EXISTS + " or "
                                    + FileType.NOT_EXISTS);
                    }
                    break;
                case RECURSIVE:
                    recursive = Boolean.valueOf(data[1].trim());
                    break;
                case LISTENER_ID:
                    listenerId = data[1].trim();
                    break;
                case SUFFIXES:
                    final String[] exts = data[1].trim().split(" ");
                    suffixes.addAll(Arrays.asList(exts));
                    ((DirTypeFileFilter) fileFilter).addSuffixes(suffixes);
                    break;
                case MATCHES: {
                    final String[] regexes = data[1].trim().split(" ");
                    matches.addAll(Arrays.asList(regexes));
                    ((DirTypeFileFilter) fileFilter).addMatches(matches);
                    break;
                }
                case DIR_BLACKLIST: {
                    final String[] regexes = data[1].trim().split(" ");
                    dirBlacklist.addAll(Arrays.asList(regexes));
                    ((DirectoryFileFilter) dirFilter).addDirectoryBlacklist(
                            dirBlacklist);
                    break;
                }
                case STRATEGY:
                    if (BREADTH_FIRST.equals(data[1].trim())) {
                        depthFirst = false;
                    } else if (DEPTH_FIRST.equals(data[1].trim())) {
                        depthFirst = true;
                    } else {
                        throw new ValueTypeCreationException(
                                "Unknown recursion strategy: " + data[1]
                                + ". Expected " + BREADTH_FIRST + " or "
                                + DEPTH_FIRST);
                    }
                    break;
                default:
                    throw new ValueTypeCreationException("Unknown property: " + property);
            }
        }
        if (recursive != null && listenerId == null) {
            throw new ValueTypeCreationException(RECURSIVE + " property set but"
                    + " there's no " + LISTENER_ID + " property specified. You"
                    + " must specify a " + LISTENER_ID);
        }
        if (recursive == null && listenerId != null) {
            throw new ValueTypeCreationException(LISTENER_ID + " property set but"
                    + " there's no " + RECURSIVE + " property specified. You"
                    + " must specify " + RECURSIVE + " as either true or false.");
        }
        if (exists != null && !exists && props.size() > 1) {
            throw new ValueTypeCreationException(FileType.NOT_EXISTS + " cannot be"
                    + " specified with any other properties.");
        }
        if (mkdirs && props.size() > 1) {
            throw new ValueTypeCreationException(MKDIRS + " cannot be"
                    + " specified with any other properties.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValueTypeName() {
        return DIRECTORY;
    }

    /**
     * Return a string representation of the directory.
     *
     * @return the file as a string as absolute path.
     */
    @Override
    public String toString() {
        return directory.getAbsolutePath();
    }

}
