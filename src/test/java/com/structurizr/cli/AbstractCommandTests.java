package com.structurizr.cli;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractCommandTests {

    @Test
    public void loadWorkspace_ThrowsAnException_WhenTheWorkspacePathIsADirectory() {
        File file = new File("src/test/dsl");
        try {

            new ValidateCommand().loadWorkspace(file.getAbsolutePath());
            fail();
        } catch (Exception e) {
            assertTrue(e.getMessage().endsWith(file.getAbsolutePath() + " is not a JSON or DSL file"));
        }
    }

    @Test
    public void loadWorkspace_ThrowsAnException_WhenWorkspaceScopeValidationFails() {
        try {
            new ValidateCommand().loadWorkspace("src/test/dsl/workspace-scope.dsl");
            fail();
        } catch (Exception e) {
            assertEquals("Workspace is software system scoped, but multiple software systems have containers and/or documentation defined.", e.getMessage());
        }
    }

}