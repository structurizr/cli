package com.structurizr.cli;

import com.structurizr.dsl.StructurizrDslParser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StructurizrCliApplication {

	private static final String PUSH_COMMAND = "push";
	private static final String PULL_COMMAND = "pull";
	private static final String LOCK_COMMAND = "lock";
	private static final String UNLOCK_COMMAND = "unlock";
	private static final String EXPORT_COMMAND = "export";
	private static final String VALIDATE_COMMAND = "validate";
	private static final String LIST_COMMAND = "list";

	public void run(String... args) {
		try {
			checkJavaVersion();

			if (args == null || args.length == 0) {
				printUsageMessageAndExit();
			}

			if (PUSH_COMMAND.equalsIgnoreCase(args[0])) {
				new PushCommand().run(Arrays.copyOfRange(args, 1, args.length));
			} else if (PULL_COMMAND.equalsIgnoreCase(args[0])) {
				new PullCommand().run(Arrays.copyOfRange(args, 1, args.length));
			} else if (LOCK_COMMAND.equalsIgnoreCase(args[0])) {
				new LockCommand().run(Arrays.copyOfRange(args, 1, args.length));
			} else if (UNLOCK_COMMAND.equalsIgnoreCase(args[0])) {
				new UnlockCommand().run(Arrays.copyOfRange(args, 1, args.length));
			} else if (EXPORT_COMMAND.equalsIgnoreCase(args[0])) {
				new ExportCommand().run(Arrays.copyOfRange(args, 1, args.length));
			} else if (VALIDATE_COMMAND.equalsIgnoreCase(args[0])) {
				new ValidateCommand().run(Arrays.copyOfRange(args, 1, args.length));
			} else if (LIST_COMMAND.equalsIgnoreCase(args[0])) {
				new ListCommand().run(Arrays.copyOfRange(args, 1, args.length));
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
		System.out.println("Structurizr CLI v" + version);
		try {
			System.out.println("Structurizr DSL v" + Class.forName(StructurizrDslParser.class.getCanonicalName()).getPackage().getImplementationVersion());
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Usage: structurizr push|pull|lock|unlock|export|validate|list [options]");
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
			System.out.println("Error: the Structurizr CLI does not work with Java versions 11.0.0-11.0.3 - please upgrade your Java installation");
			System.exit(1);
		}

	}

	public static void main(String[] args) {
		new StructurizrCliApplication().run(args);
	}

}