package com.structurizr.cli;

import com.structurizr.Workspace;
import com.structurizr.model.Person;
import com.structurizr.util.WorkspaceUtils;
import com.structurizr.view.ElementView;
import com.structurizr.view.SystemLandscapeView;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class MergeCommandTests {

    @Test
    public void run() throws Exception {
        File tmpDir = Files.createTempDirectory("structurizr").toFile();

        String[] args = {
                "merge",
                "-workspace", "src/test/mergeLayout/workspace.dsl",
                "-layout", "src/test/mergeLayout/workspace.json",
                "-output", new File(tmpDir, "merged.json").getCanonicalPath()
        };
        new MergeCommand().run(args);

        assertTrue(new File(tmpDir, "merged.json").exists());

        Workspace workspace = WorkspaceUtils.loadWorkspaceFromJson(new File(tmpDir, "merged.json"));
        Person user = workspace.getModel().getPersonWithName("User");
        SystemLandscapeView view = workspace.getViews().getSystemLandscapeViews().iterator().next();
        ElementView elementView = view.getElementView(user);
        assertEquals(123, elementView.getX());
        assertEquals(456, elementView.getY());
    }

}