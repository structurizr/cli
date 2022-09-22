package com.structurizr.cli;

import com.structurizr.cli.export.ExportCommand;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.util.*;

public class StructurizrCliApplication {

	private static Log log;

	private static final String PUSH_COMMAND = "push";
	private static final String PULL_COMMAND = "pull";
	private static final String LOCK_COMMAND = "lock";
	private static final String UNLOCK_COMMAND = "unlock";
	private static final String EXPORT_COMMAND = "export";
	private static final String VALIDATE_COMMAND = "validate";
	private static final String LIST_COMMAND = "list";
	private static final String HELP_COMMAND = "help";

	private static final Map<String,AbstractCommand> COMMANDS = new HashMap<>();

	static {
		ConfigurationBuilder<BuiltConfiguration> builder =
				ConfigurationBuilderFactory.newConfigurationBuilder();

		// configure a console appender
		builder.add(
				builder.newAppender("stdout", "Console")
						.add(
								builder.newLayout(PatternLayout.class.getSimpleName())
										.addAttribute(
												"pattern",
												"%msg%n"
										)
						)
		);

		// configure the root logger
		builder.add(
				builder.newRootLogger(Level.INFO)
						.add(builder.newAppenderRef("stdout"))
		);

		// apply the configuration
		Configurator.initialize(builder.build());

		log = LogFactory.getLog(StructurizrCliApplication.class);

		COMMANDS.put(PUSH_COMMAND, new PushCommand());
		COMMANDS.put(PULL_COMMAND, new PullCommand());
		COMMANDS.put(LOCK_COMMAND, new LockCommand());
		COMMANDS.put(UNLOCK_COMMAND, new UnlockCommand());
		COMMANDS.put(EXPORT_COMMAND, new ExportCommand());
		COMMANDS.put(VALIDATE_COMMAND, new ValidateCommand());
		COMMANDS.put(LIST_COMMAND, new ListCommand());
		COMMANDS.put(HELP_COMMAND, new HelpCommand());
	}

	public void run(String... args) {
		try {
			checkJavaVersion();

			if (args == null || args.length == 0) {
				printUsageMessageAndExit();
			}

			AbstractCommand command = COMMANDS.get(args[0]);
			if (command != null) {
				command.run(Arrays.copyOfRange(args, 1, args.length));
			} else {
				printUsageMessageAndExit();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void printUsageMessageAndExit() {
		String version = getClass().getPackage().getImplementationVersion();
		log.info("structurizr-cli: " + version);
		log.info("Usage: structurizr push|pull|lock|unlock|export|validate|list|help [options]");
		System.exit(1);
	}

	private void checkJavaVersion() {
		Set<String> versions = new HashSet<>();
		versions.add("11");
		versions.add("11.0.0");
		versions.add("11.0.1");
		versions.add("11.0.2");
		versions.add("11.0.3");

		String version = System.getProperty("java.version");

		if (versions.contains(version)) {
			log.error("The Structurizr CLI does not work with Java versions 11.0.0-11.0.3 - please upgrade your Java installation");
			System.exit(1);
		}

	}

	public static void main(String[] args) {
		new StructurizrCliApplication().run(args);
	}

}