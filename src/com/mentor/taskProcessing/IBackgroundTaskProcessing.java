package com.mentor.taskProcessing;

public interface IBackgroundTaskProcessing
{
    String getOutputPath(String outputPath, String taskProcessOutput);

    int postProcessingTask(String source, String scriptToPath);

    int preProcessingTask(String source, String destination, String scriptToPath);
}
