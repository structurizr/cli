package com.structurizr.cli;

import org.junit.jupiter.api.Test;

public class ValidateCommandTests {

    @Test
    public void run() throws Exception {
        String[] args = {
                "validate",
                "-workspace", "src/test/dsl/workspace.dsl"
        };
        new ValidateCommand().run(args);
    }

}