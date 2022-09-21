package com.structurizr.cli;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StructurizrCliApplicationTests {

    @Test
    public void validateWorkspace() {
        String[] args = {
                "validate",
                "-workspace", "src/test/dsl/workspace.dsl"
        };
        StructurizrCliApplication.main(args);
    }

    @Test
    public void exportWorkspace() throws Exception {
        File tmpDir = Files.createTempDirectory("structurizr").toFile();

        String[] args = {
                "export",
                "-workspace", "src/test/dsl/workspace.dsl",
                "-output", tmpDir.getCanonicalPath(),
                "-format", "plantuml"
        };
        StructurizrCliApplication.main(args);

        assertTrue(new File(tmpDir, "structurizr-SystemLandscape.puml").exists());
    }

}