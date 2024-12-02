package com.structurizr.cli.util;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Version {

    private static final String BUILD_VERSION_KEY = "build.number";
    private static final String BUILD_TIMESTAMP_KEY = "build.timestamp";
    private static final String GIT_COMMIT_KEY = "git.commit";

    private static String version = "";
    private static Date buildTimestamp = new Date();
    private static String gitCommit;

    static {
        try {
            Properties buildProperties = new Properties();
            InputStream in = Version.class.getClassLoader().getResourceAsStream("build.properties");
            DateFormat format = new SimpleDateFormat(DateUtils.ISO_DATE_TIME_FORMAT);
            if (in != null) {
                buildProperties.load(in);
                version = buildProperties.getProperty(BUILD_VERSION_KEY);
                buildTimestamp = format.parse(buildProperties.getProperty(BUILD_TIMESTAMP_KEY));
                gitCommit = buildProperties.getProperty(GIT_COMMIT_KEY);
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getBuildNumber() {
        return version;
    }

    public Date getBuildTimestamp() {
        return buildTimestamp;
    }

    public String getGitCommit() {
        return gitCommit;
    }

}