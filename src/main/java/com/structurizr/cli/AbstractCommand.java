package com.structurizr.cli;

import com.structurizr.Workspace;
import com.structurizr.dsl.Features;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.util.WorkspaceUtils;
import com.structurizr.validation.WorkspaceScopeValidatorFactory;
import com.structurizr.view.Styles;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;

public abstract class AbstractCommand {

    private static final Log log = LogFactory.getLog(AbstractCommand.class);

    private static final String PLUGINS_DIRECTORY_NAME = "plugins";
    private static final int HTTP_OK_STATUS = 200;

    protected AbstractCommand() {
    }

    public abstract void run(String... args) throws Exception;

    String getAgent() {
        return "structurizr-cli/" + getClass().getPackage().getImplementationVersion();

    }

    protected void addDefaultViewsAndStyles(Workspace workspace) {
        if (workspace.getViews().isEmpty()) {
            log.info(" - no views defined; creating default views");
            workspace.getViews().createDefaultViews();
        }

        Styles styles = workspace.getViews().getConfiguration().getStyles();
        if (styles.getElements().isEmpty() && styles.getRelationships().isEmpty() && workspace.getViews().getConfiguration().getThemes() == null) {
            log.info(" - no styles or themes defined; use the \"default\" theme to add some default styles");
        }
    }

    protected Workspace loadWorkspace(String workspacePathAsString) throws Exception {
        Workspace workspace;

        if (workspacePathAsString.endsWith(".json")) {
            if (workspacePathAsString.startsWith("http://") || workspacePathAsString.startsWith("https")) {
                String json = readFromUrl(workspacePathAsString);
                workspace = WorkspaceUtils.fromJson(json);
            } else {
                File workspaceFile = new File(workspacePathAsString);
                if (!workspaceFile.exists()) {
                    throw new StructurizrCliException(workspaceFile.getAbsolutePath() + " does not exist");
                }

                if (!workspaceFile.isFile()) {
                    throw new StructurizrCliException(workspaceFile.getAbsolutePath() + " is not a JSON or DSL file");
                }

                workspace = WorkspaceUtils.loadWorkspaceFromJson(workspaceFile);
            }

        } else {
            StructurizrDslParser structurizrDslParser = new StructurizrDslParser();
            structurizrDslParser.getFeatures().configure(Features.ARCHETYPES, Configuration.PREVIEW_FEATURES);
            structurizrDslParser.setCharacterEncoding(Charset.defaultCharset());

            if (workspacePathAsString.startsWith("http://") || workspacePathAsString.startsWith("https://")) {
                String dsl = readFromUrl(workspacePathAsString);
                structurizrDslParser.parse(dsl);
            } else {
                File workspaceFile = new File(workspacePathAsString);
                if (!workspaceFile.exists()) {
                    throw new StructurizrCliException(workspaceFile.getAbsolutePath() + " does not exist");
                }

                if (!workspaceFile.isFile()) {
                    throw new StructurizrCliException(workspaceFile.getAbsolutePath() + " is not a JSON or DSL file");
                }

                structurizrDslParser.parse(workspaceFile);
            }

            workspace = structurizrDslParser.getWorkspace();

            if (workspace == null) {
                throw new StructurizrCliException("No workspace definition was found - please check your DSL");
            }
        }

        // validate workspace scope
        WorkspaceScopeValidatorFactory.getValidator(workspace).validate(workspace);

        return workspace;
    }

    protected String readFromUrl(String url) {
        try (CloseableHttpClient httpClient = HttpClients.createSystem()) {
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(httpGet);

            if (response.getCode() == HTTP_OK_STATUS) {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (Exception ioe) {
            log.error(ioe);
        }

        return "";
    }

    protected Class loadClass(String fqn, File workspaceFile) throws Exception {
        File pluginsDirectory = new File(workspaceFile.getParent(), PLUGINS_DIRECTORY_NAME);
        URL[] urls = new URL[0];

        if (pluginsDirectory.exists()) {
            File[] jarFiles = pluginsDirectory.listFiles((dir, name) -> name.endsWith(".jar"));
            if (jarFiles != null) {
                urls = new URL[jarFiles.length];
                for (int i = 0; i < jarFiles.length; i++) {
                    System.out.println(jarFiles[i].getAbsolutePath());
                    try {
                        urls[i] = jarFiles[i].toURI().toURL();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        URLClassLoader childClassLoader = new URLClassLoader(urls, getClass().getClassLoader());
        return childClassLoader.loadClass(fqn);
    }

    protected void configureDebugLogging() {
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();

        builder.add(
            builder.newAppender("stdout", "Console")
                .add(
                    builder.newLayout(PatternLayout.class.getSimpleName())
                        .addAttribute(
                            "pattern",
                            "%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"
                        )
                )
        );

        builder.add(builder.newLogger("com.structurizr", Level.DEBUG));

        Configurator.reconfigure(builder.build());
    }

}