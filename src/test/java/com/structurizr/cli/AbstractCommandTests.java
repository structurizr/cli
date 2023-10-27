package com.structurizr.cli;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class AbstractCommandTests {

    @Test
    public void loadWorkspace_ThrowsAnException_WhenTheWorkspacePathIsADirectory() {
        try {
            new ValidateCommand().loadWorkspace("src/test/dsl");
            fail();
        } catch (Exception e) {
            assertTrue(e.getMessage().endsWith("/src/test/dsl is not a JSON or DSL file"));
        }
    }


}