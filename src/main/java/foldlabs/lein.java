package foldlabs;


import com.google.common.collect.ImmutableSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.attribute.PosixFilePermission.*;

/**
 * Simple wrapper over leiningen script files.
 */
public class lein {

    public static final String DEFAULT_VERSION = "2.3.2";
    static String port = System.getProperty("coloneltrain.port", "3000");

    private final Log log;
    private final String version;
    private final Sys sys;

    public lein() {
        this(new Log.SystemLog());
    }

    public lein(Log log) {
        this(log, new Sys(log), DEFAULT_VERSION);
    }

    public lein(Log log, Sys sys) {
        this(log, sys, DEFAULT_VERSION);
    }

    public lein(Log log, Sys sys, String leinVersion) {
        this.log = log;
        this.version = leinVersion;
        this.sys = sys;
    }

    public static void main(String[] args) throws IOException {
        new lein().build();
    }

    public void build() {
        try {

            Path leinScript = getScript();

            Path leinjar = getUberjar();

            Map<String, String> environment = new HashMap<String, String>();
            environment.put("LEIN_JAR", leinjar.toAbsolutePath().toString());
            environment.put("PORT", port);

            sys.run(leinScript, "deps", "updating dependencies ...", environment);

            sys.run(leinScript, "run", "running app on port " + port + "...", environment);
        } catch (Exception e) {
            throw new leinException(e);
        }
    }

    Path getUberjar() throws IOException {
        return sys.download(Paths.get("leiningen-" + version + "-standalone.jar"), "https://leiningen.s3.amazonaws.com/downloads/leiningen-" + version + "-standalone.jar");
    }

    Path getScript() throws IOException {
        Path scriptPath = Paths.get("lein" + (isWindows() ? ".bat" : ""));

        scriptPath = sys.download(scriptPath, "https://raw.github.com/technomancy/leiningen/stable/bin/lein" + (isWindows() ? ".bat" : ""));

        if (!isWindows())
            Files.setPosixFilePermissions(scriptPath, ImmutableSet.of(OWNER_EXECUTE, OWNER_READ, OWNER_WRITE));

        return scriptPath;
    }

    boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    private class leinException extends RuntimeException {
        public leinException(Throwable throwable) {
            super(throwable);
        }
    }
}
