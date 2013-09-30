package foldlabs;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple wrapper over leiningen script files.
 */
public class lein {

    public static final String DEFAULT_VERSION = "2.3.2";
    static String port = System.getProperty("coloneltrain.port", "3000");

    private final String version;
    private final Sys sys;
    private Path leinScript;
    private Path leinjar;

    public lein() {
        this(new Sys(new Log.SystemLog()));
    }

    public lein(Sys sys) {
        this(sys, DEFAULT_VERSION);
    }

    public lein(Sys sys, String leinVersion) {
        this.version = leinVersion;
        this.sys = sys;
    }

    public static void main(String[] args) throws IOException {
        new lein().build();
    }

    public void build() {
        try {
            init();
            run("deps");
            run("run");
        } catch (Exception e) {
            throw new leinException(e);
        }
    }

    Path getUberjar() throws IOException {
        return sys.download(Paths.get("leiningen-" + version + "-standalone.jar"), "https://leiningen.s3.amazonaws.com/downloads/leiningen-" + version + "-standalone.jar");
    }

    Path getScript() throws IOException {
        Path scriptPath = Paths.get("lein" + (sys.isWindows() ? ".bat" : ""));

        scriptPath = sys.download(scriptPath, "https://raw.github.com/technomancy/leiningen/stable/bin/lein" + (sys.isWindows() ? ".bat" : ""));

        sys.makeExecutable(scriptPath);

        return scriptPath;
    }

    public void init() {
        try {
            leinScript = getScript();
            leinjar = getUberjar();
        } catch (IOException e) {
            throw new leinException(e);
        }
    }

    public void run(String target) {
        Map<String, String> environment = new HashMap<String, String>();
        environment.put("LEIN_JAR", leinjar.toAbsolutePath().toString());
        environment.put("PORT", port);

        try {
            sys.run(leinScript, target, "lein " + target + "...", environment);
        } catch (Exception e) {
            throw new leinException(e);
        }
    }

    class leinException extends RuntimeException {
        public leinException(Throwable throwable) {
            super(throwable);
        }
    }
}
