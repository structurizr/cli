package com.structurizr.cli;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class StructurizrCliApplication implements CommandLineRunner {

	private static final String PUSH_COMMAND = "push";
	private static final String PULL_COMMAND = "pull";
	private static final String LOCK_COMMAND = "lock";
	private static final String UNLOCK_COMMAND = "unlock";
	private static final String EXPORT_COMMAND = "export";
	private static final String VALIDATE_COMMAND = "validate";

	@Autowired
	private ApplicationContext context;

	@Override
	public void run(String... args) {
		try {
			String version = context.getBean(BuildProperties.class).getVersion();
			System.out.println("Structurizr CLI v" + version);

			checkJavaVersion();

			if (args == null || args.length == 0) {
				printUsageMessageAndExit();
			}

			if (PUSH_COMMAND.equalsIgnoreCase(args[0])) {
				new PushCommand(version).run(Arrays.copyOfRange(args, 1, args.length));
			} else if (PULL_COMMAND.equalsIgnoreCase(args[0])) {
				new PullCommand(version).run(Arrays.copyOfRange(args, 1, args.length));
			} else if (LOCK_COMMAND.equalsIgnoreCase(args[0])) {
				new LockCommand(version).run(Arrays.copyOfRange(args, 1, args.length));
			} else if (UNLOCK_COMMAND.equalsIgnoreCase(args[0])) {
				new UnlockCommand(version).run(Arrays.copyOfRange(args, 1, args.length));
			} else if (EXPORT_COMMAND.equalsIgnoreCase(args[0])) {
				new ExportCommand(version).run(Arrays.copyOfRange(args, 1, args.length));
			} else if (VALIDATE_COMMAND.equalsIgnoreCase(args[0])) {
				new ValidateCommand(version).run(Arrays.copyOfRange(args, 1, args.length));
			} else {
				printUsageMessageAndExit();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void printUsageMessageAndExit() {
		System.out.println("Usage: structurizr push|pull|lock|unlock|export|validate [options]");
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
		SpringApplication.run(StructurizrCliApplication.class, args);
	}

}