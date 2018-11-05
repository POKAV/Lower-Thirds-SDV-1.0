package se.pingstteknik.propresenter.stagedisplayviewer.config;

public class applicationSettings {

    private static final String applicationName = "Lower Thirds SDV";
    private static double versionNumber = 1.0;

    public static String getApplicationName(){
        return applicationName + " - " + versionNumber;
    }

}
