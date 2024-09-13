package com.mentor.taskProcessing;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class BackgroundTaskProcessing implements IBackgroundTaskProcessing
{
    private static BackgroundTaskProcessing scriptExecutor = null;
    private static String CIS_OUTPUT_STORE = "";
    private static final String FOLDER_NAME = "CUSTOM";

    private BackgroundTaskProcessing()
    {
        // Default protected constructor
    }

    public static synchronized BackgroundTaskProcessing getInstance()
    {
        if(scriptExecutor == null){
            scriptExecutor = new BackgroundTaskProcessing();
        }
        return scriptExecutor;
    }
    @Override
    public String getOutputPath(String outputPath, String taskProcessOutput)
    {
        CIS_OUTPUT_STORE = outputPath;
        return alternateOutputPath(taskProcessOutput);
    }

    @Override
    public int postProcessingTask(String source, String scriptToPath)
    {
        // post-processing for custom task triggered from Hook.
        int statusCode = runPythonScriptToCopyFiles(source, CIS_OUTPUT_STORE, "PUSH", scriptToPath);
        deleteTargetDirectory(source);
        return statusCode;
    }

    @Override
    public int preProcessingTask(String source, String destination, String scriptToPath)
    {
        // pre-processing for custom task triggered from Hook to download cis_config properties file for custom Task.
        System.out.println("Destination for preProcessing: " + destination);
        int statusCode = runPythonScriptToCopyFiles(source, destination, "PULL", scriptToPath);
        return statusCode;
    }

    private void deleteTargetDirectory(String pathForDeletion)
    {
        deleteRecursively(Paths.get(pathForDeletion).toFile(), child -> throwException(child));
    }

    private int runPythonScriptToCopyFiles(String source, String destination, String cmd, String scriptToPath)
    {
        final int MAX_RETRIES = 2;
        int retryCount = 0;
        boolean success = false;
        int exitCode = -1;  // initialize exitCode with error Value
        while (retryCount < MAX_RETRIES && !success) {
            try {
                String osName = System.getProperty("os.name").toLowerCase();
                String pythonCommand = "";
                if (osName.contains("windows")) {
                    pythonCommand = "python";
                } else if (osName.contains("linux")) {
                    pythonCommand = "python3";
                } else {
                    System.err.println("Unsupported operating system.");
                    return exitCode; // return exit-Code
                }
                String[] command = {pythonCommand, scriptToPath, source, destination, cmd};

                ProcessBuilder processBuilder = new ProcessBuilder(command);
                Process process = processBuilder.start();

                // Create threads to read standard output and error output streams concurrently
                Thread outputThread = new Thread(() -> readStream(process.getInputStream(), "output"));
                Thread errorThread = new Thread(() -> readStream(process.getErrorStream(), "error"));

                outputThread.start();
                errorThread.start();

                exitCode = process.waitFor(); // If process fails, exitCode is 2.
                if (exitCode == 0) {
                    success = true;
                } else {
                    retryCount++;
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                retryCount++;
            }
        }
        if (!success) {
            System.err.println("Failed to execute Python script after " + MAX_RETRIES + " retries.");
        }
        return exitCode;
    }

    private String alternateOutputPath(String taskProcessOutput)
    {
        // Get current date and time
        LocalDateTime now = LocalDateTime.now();

        // Format the date-time as a string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String formattedDateTime = now.format(formatter);

        // Construct the path with the formatted date-time appended
        Path pathToCustomFEMTask = Paths.get(createDirectoryForAlternateOutput(taskProcessOutput), "output_" + formattedDateTime);

        // Try to create the directory and return the path if successful
        if (createDirectory(pathToCustomFEMTask.toFile())) {
            return pathToCustomFEMTask.toString();
        } else {
            System.out.println("Failed to create directory: " + pathToCustomFEMTask);
            return "";
        }
    }

    private String getCapitalTempEnv()
    {
        String chsTemp = System.getenv("CHS_TEMP");
        String capitalTemp = System.getenv("CAPITAL_TEMP");

        if (chsTemp != null && capitalTemp != null) {
            if (chsTemp.equals(capitalTemp)) {
                return chsTemp;
            }
        } else if (chsTemp != null) {
            return chsTemp;
        } else if (capitalTemp != null) {
            return capitalTemp;
        }

        return null;
    }

    private String createDirectoryForAlternateOutput(String taskProcessOutput)
    {
        String modifiedOutputPath = getCapitalTempEnv() + File.separator + taskProcessOutput + File.separator + FOLDER_NAME;
        System.out.println("Modified Output Path: " + modifiedOutputPath);
        return modifiedOutputPath;
    }

    /**
     * Create a directory, recursively creating parent dirs if required
     *
     * @param dir the directory
     * @return true if created the directory.
     */
    public boolean createDirectory(File dir)
    {
        File parent = dir.getParentFile();
        if (parent == null) {
            return true;
        }

        boolean result = true;
        if (!parent.exists() && parent.getPath().length() < dir.getPath().length()) {
            result = createDirectory(parent);
        }
        if (!dir.exists()) {
            result &= dir.mkdir();
        }
        return result;
    }

    public static boolean deleteRecursively(File file, Function<File, Boolean> statusProvider)
    {
        if (file.isDirectory()) {
            for (File child : listFiles(file)) {
                if (!deleteRecursively(child)) {
                    return statusProvider.apply(child);
                }
            }
        }

        return file.delete();
    }

    public static boolean deleteRecursively(File file)
    {
        return deleteRecursively(file, child -> false);
    }

    private static Boolean throwException(File child)
    {
        throw new IllegalStateException((child.isDirectory() ? "could not delete folder :" :
                "could not delete file :") + child.getPath());
    }

    public static List<File> listFiles(File file)
    {
        assert file.isDirectory();
        File[] files = file.listFiles();
        return files == null ? Collections.emptyList() : Arrays.asList(files);
    }

    private synchronized void readStream(InputStream inputStream, String streamName)
    {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)))
        {
            String line = reader.readLine();
            while (line != null)
            {
                if("error".equals(streamName))
                {
                    System.err.println(line);
                }
                System.out.println(line);
                line = reader.readLine();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
