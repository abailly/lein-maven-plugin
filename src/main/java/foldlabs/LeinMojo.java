package foldlabs;

import com.google.common.annotations.VisibleForTesting;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Wraps clojure's leiningen build tool in maven.
 * <p>
 * This simple mojo wraps leiningen execution with maven. It is extremely dumb, simple and dirty:
 * </p>
 * <ul>
 * <li>download leiningen uber-jar,</li>
 * <li>download platform-dependent script,</li>
 * <li>run <tt>lein &lt;args&gt;</tt> repeatedly for each given <tt>do</tt> element.</li>
 * </ul>
 */
@Mojo(name = "lein", defaultPhase = LifecyclePhase.COMPILE, threadSafe = false)
public class LeinMojo extends AbstractMojo {

    @Parameter(property = "lein.targets")
    private String[] targets = new String[0];

    @Parameter(property = "lein.version", defaultValue = "2.3.2", required = true)
    private String leinVersion = "2.3.2";

    private Sys sys;

    @VisibleForTesting
    LeinMojo(Sys sys) {
        this.sys = sys;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (sys == null) {
            sys = new Sys(new Log() {
                @Override
                public void log(String message) {
                    getLog().info(message);
                }
            });
        }
        
        lein lein = new lein(sys, leinVersion);

        lein.init();

        for (String target : targets) {
            lein.run(target);
        }

    }

    public void setLeinVersion(String leinVersion) {
        this.leinVersion = leinVersion;
    }

    public void setTargets(String[] targets) {
        this.targets = targets;
    }
}
