package org.mletkin.running.gui;

import java.time.Duration;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import org.mletkin.running.model.Activity;
import org.mletkin.running.model.Data;
import org.mletkin.running.util.Format;

import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

/**
 * Pane with detailed data for an Activity.
 */
public class MonthSummary extends VBox {

    private Data data;
    private TextArea ta = new TextArea();

    public MonthSummary(Data data) {
        this.data = data;
        getChildren().add(ta);
    }

    public void add(String line) {
        ta.appendText("\n" + line);
    }

    public void clear() {
        ta.clear();
    }

    public void setMonth(YearMonth month) {
        clear();
        List<Activity> runs = data.runs() //
                .filter(a -> YearMonth.from(a.start()).equals(month)) //
                .collect(Collectors.toList());

        var dist = runs.stream().mapToDouble(Activity::dist).sum();
        var duration = runs.stream().map(Activity::time).reduce(Duration.ZERO, Duration::plus);
        var laps = runs.stream().flatMap(Activity::laps).count();

        add(String.format("%s %s", month.getMonth(), month.getYear()));
        add(String.format("Dist: %.2f km", dist / 1000));
        add(String.format("Time: %s", Format.time(duration)));
        add(String.format("Laps: %d", laps));
    }
}
