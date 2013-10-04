package foldlabs;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * Interface to underlying system-level functions.
 */
public interface Sys {

    /**
     * Download {@code uri} to given {@code path}.
     *
     * @param path target path for downloaded resource. Must point to a file within an existing directory hierarchy (eg. parent directories
     *             are not created).
     * @param uri  download source URI.
     * @return the {@code path}.
     * @throws IOException
     */
    Path download(Path path, String uri) throws IOException;

    /**
     * Runs {@code lein} executable script with given target in current directory.
     *
     * @param lein   the location of lein script to run. Must be a valid path pointing to an executable file.
     * @param target the lein target to run
     * @param env    a possibly empty map containing environment variables to pass to process. This will extend the current
     *               (default) environment.
     * @throws IOException
     * @throws InterruptedException
     */
    void run(Path lein, String target, Map<String, String> env) throws IOException, InterruptedException;

    /**
     * @return true if underlying system is any flavor of Windows&reg;.
     */
    boolean isWindows();

    /**
     * Request underlying system to make given path executable.
     *
     * @param scriptPath points to file to make executable. Must exist.
     * @throws IOException 
     */
    void makeExecutable(Path scriptPath) throws IOException;

    /**
     * @return the current directory (eg. pwd for current process).
     */
    Path currentDirectory();

    /**
     * @param installDir directory to create.
     * @return true if directory was created. Hierarchy is created if it does not exist.
     */
    boolean makeDir(Path installDir);

    /**
     * Request changing the current directory.
     *
     * @param directory path to a valid existing directory.
     */
    void cd(Path directory);
}
