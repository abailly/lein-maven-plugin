A maven plugin that wraps [leiningen](https://github.com/technomancy/leiningen) build tool. 

# Getting Started

1. Download & build source code (assume `git` and `mvn` are in your `PATH`):

    ```
    git clone https://github.com/abailly/lein-maven-plugin.git
    cd lein-maven-plugin
    mvn install
    ```

2. Configure your project to use the plugin. 

    ```xml
    <plugin>
        <groupId>foldlabs</groupId>
        <artifactId>lein-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <executions>
          <execution>
            <goals>
              <goal>lein</goal>
            </goals>
            <configuration>
              <environment>
                <PORT>${server.port}</PORT>
              </environment>
              <targets>
                <target>deps</target>
                <target>run</target>
              </targets>
            </configuration>
          </execution>
        </executions>
      </plugin>
    ```
    
3. Run maven in your project with suitable target:
     
     ```
     mvn compile
     ...
     [INFO]
     [INFO] --- lein-maven-plugin:1.0-SNAPSHOT:lein (default) @ colonel-train ---
     [INFO] using default system utilities provider
     [INFO] downloading https://raw.github.com/technomancy/leiningen/stable/bin/lein.bat to lein.bat
     [INFO] lein.bat exists, skipping download
     [INFO] downloading https://leiningen.s3.amazonaws.com/downloads/leiningen-2.3.2-standalone.jar to leiningen-2.3.2-standalone.jar
     [INFO] leiningen-2.3.2-standalone.jar exists, skipping download
     [INFO] lein deps...
     [INFO] done
     [INFO] lein run...
     INFO  org.eclipse.jetty.server.Server - jetty-7.6.1.v20120215
     INFO  org.eclipse.jetty.server.AbstractConnector - Started SelectChannelConnector@0.0.0.0:3000
     ```

# Public repository access

If you don't want or cannot build the plugin yourself, a binary version is available from Dropbox. 
Add the following repository definition to your `pom.xml` or `settings.xml` file:

```
<pluginRepository>
  <id>foldlabs</id>
  <url>https://dl.dropboxusercontent.com/u/2060057/maven/</url>
  <snapshots>
    <enabled>true</enabled>
  </snapshots>
</pluginRepository>
```

# Configuration details

This simple mojo wraps leiningen execution with maven. It is extremely dumb,
simple and dirty:

- download leiningen uber-jar,
- download platform-dependent script,
- run lein <args> repeatedly for each given do element.

Current help is always available inline with the following command (assuming plugin group *foldlabs* is configured in settings...):

```
mvn lein:help -Ddetail
```

## Available parameters:

### environment

Sets environment variables to pass to underlying processes.
Each leiningen execution will be run in an environment with all this
configured variables set. Of course the variables content may use maven
properties.

### leinVersion (Default: 2.3.2)

Defines which leiningen's version to use.
Required: Yes

### targets (Default: compile)

Sets leiningen's tasks to execute.
Each task will be executed in sequence. Default task is compile. For
example:

```xml
<targets>
  <target>deps</target>
  <target>run</target>
</targets>
```

This instructs plugin to run `deps` task followed by `run` task.

**CAVEAT**: Note each task is run in a sub-process of the current maven
process. If the current process is killed while sub-processes are still
alive, this will create orphan processes that must be killed manually.
Required: Yes
