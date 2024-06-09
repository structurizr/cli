package com.structurizr.cli;

import com.structurizr.cli.export.ExportCommand;
import com.structurizr.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class StructurizrCliApplication {

	private static final Log log = LogFactory.getLog(StructurizrCliApplication.class);

	private static final String PUSH_COMMAND = "push";
	private static final String PULL_COMMAND = "pull";
	private static final String LOCK_COMMAND = "lock";
	private static final String UNLOCK_COMMAND = "unlock";
	private static final String EXPORT_COMMAND = "export";
	private static final String MERGE_COMMAND = "merge";
	private static final String VALIDATE_COMMAND = "validate";
	private static final String INSPECT_COMMAND = "inspect";
	private static final String LIST_COMMAND = "list";
	private static final String VERSION_COMMAND = "version";
	private static final String HELP_COMMAND = "help";

	private static final Map<String,AbstractCommand> COMMANDS = new HashMap<>();

	static {
		COMMANDS.put(PUSH_COMMAND, new PushCommand());
		COMMANDS.put(PULL_COMMAND, new PullCommand());
		COMMANDS.put(LOCK_COMMAND, new LockCommand());
		COMMANDS.put(UNLOCK_COMMAND, new UnlockCommand());
		COMMANDS.put(EXPORT_COMMAND, new ExportCommand());
		COMMANDS.put(MERGE_COMMAND, new MergeCommand());
		COMMANDS.put(VALIDATE_COMMAND, new ValidateCommand());
		COMMANDS.put(INSPECT_COMMAND, new InspectCommand());
		COMMANDS.put(LIST_COMMAND, new ListCommand());
		COMMANDS.put(VERSION_COMMAND, new VersionCommand());
		COMMANDS.put(HELP_COMMAND, new HelpCommand());
	}

	public void run(String... args) {
		try {
			if (args == null || args.length == 0) {
				printUsageMessageAndExit(null);
			}

			String commandName = args[0];
			AbstractCommand command = COMMANDS.get(commandName);
			if (command != null) {
				command.run(Arrays.copyOfRange(args, 1, args.length));
				System.exit(0);
			} else {
				printUsageMessageAndExit(commandName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void printUsageMessageAndExit(String commandName) throws Exception {
		if (!StringUtils.isNullOrEmpty(commandName)) {
			log.error("Error: " + commandName + " not recognised");
		}

		new HelpCommand().run();
		System.exit(1);
	}

	public static void main(String[] args) {
		new StructurizrCliApplication().run(args);
	}

}