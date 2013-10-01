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

    private final String version;
    private final Sys sys;
    private Path leinScript;
    private Path leinJar;
    private Map<String, String> environment = new HashMap<>();

    public lein() {
        this(new DefaultSys(new Log.SystemLog()));
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

    public void useEnvironment(Map<String, String> environment) {
        this.environment.putAll(environment);
    }

    Path getUberJar() throws IOException {
        Path installDir = Paths.get(".", "self-installs");
        sys.makeDir(installDir);
        return sys.download(installDir.resolve(Paths.get("leiningen-" + version + "-standalone.jar")), "https://leiningen.s3.amazonaws.com/downloads/leiningen-" + version + "-standalone.jar");
    }

    Path getScript() throws IOException {
        Path scriptPath = Paths.get(".","lein" + (sys.isWindows() ? ".bat" : ""));

        scriptPath = sys.download(scriptPath, "https://raw.github.com/technomancy/leiningen/stable/bin/lein" + (sys.isWindows() ? ".bat" : ""));

        sys.makeExecutable(scriptPath);

        return scriptPath;
    }

    public void init() {
        try {
            leinScript = getScript();
            leinJar = getUberJar();

            Map<String, String> environment = new HashMap<>();
            environment.put("LEIN_JAR", leinJar.toAbsolutePath().toString());
            environment.put("LEIN_HOME", sys.currentDirectory());
            useEnvironment(environment);
            
        } catch (IOException e) {
            throw new leinException(e);
        }
    }

    public void run(String target) {
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
