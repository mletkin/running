package org.mletkin.running.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mletkin.garmin.ActivityListT;
import org.mletkin.garmin.TrainingCenterDatabaseT;

/**
 * Container that provides access to all the data.
 */
public class Data {

    private List<Activity> content = List.of();

    public Data() {
        // creates an empty container
    }

    /**
     * Creates a container with the data from the file system.
     *
     * @param source
     *                   entry point for reading
     */
    public Data(Path source) {
        content = files(source) //
                .map(new XmlRead()::data) //
                .map(TrainingCenterDatabaseT::getActivities) //
                .map(ActivityListT::getActivity) //
                .flatMap(List::stream) //
                .map(Activity::new) //
                .collect(Collectors.toList());
    }

    private Stream<Path> files(Path path) {
        try {
            if (Files.isDirectory(path)) {
                return Files.find(path, 2, this::match);
            }
            if (Files.isRegularFile(path)) {
                return Stream.of(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading Files", e);
        }
        return Stream.of();
    }

    private boolean match(Path path, BasicFileAttributes attr) {
        if (attr.isRegularFile()) {
            return true;
        }
        return false;
    }

    /**
     * Returns all activities in the container.
     *
     * @return Stream of all activities
     */
    public Stream<Activity> runs() {
        return content.stream();
    }

    /**
     * Returns all laps of all activities in the container.
     *
     * @return Stream of all laps
     */
    public Stream<Lap> laps() {
        return content.stream().flatMap(Activity::laps);
    }

}
