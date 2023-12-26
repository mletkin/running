package org.mletkin.running.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.mletkin.garmin.ActivityLapT;
import org.mletkin.garmin.ActivityListT;
import org.mletkin.garmin.ActivityT;
import org.mletkin.garmin.TrainingCenterDatabaseT;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;

public class XmlRead {

    public Optional<TrainingCenterDatabaseT> readFile(Path file) {
        try {
            var context = JAXBContext.newInstance(TrainingCenterDatabaseT.class);
            var unmarshaller = context.createUnmarshaller();
            var result = (JAXBElement<TrainingCenterDatabaseT>) unmarshaller.unmarshal(file.toFile());
            return result == null ? Optional.empty() : Optional.of(result.getValue());
        } catch (JAXBException e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    public TrainingCenterDatabaseT data(Path file) {
        return readFile(file).orElse(null);
    }

    public Stream<ActivityT> runs(TrainingCenterDatabaseT data) {
        return Stream.of(data) //
                .map(TrainingCenterDatabaseT::getActivities) //
                .map(ActivityListT::getActivity) //
                .flatMap(List::stream);
    }

    public void printLaps(TrainingCenterDatabaseT data) {
        var act = data.getActivities().getActivity().get(0);
        act.getLap().stream().forEach(this::print);
    }

    private void print(ActivityLapT lap) {
        System.out.println(lap.getDistanceMeters());
    }

}
