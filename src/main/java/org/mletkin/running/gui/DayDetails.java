package org.mletkin.running.gui;

import org.mletkin.running.model.Activity;
import org.mletkin.running.util.Format;

import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

/**
 * Pane with detailed data for an Activity.
 */
public class DayDetails extends VBox {

    private TextArea ta = new TextArea();

    public DayDetails() {
        getChildren().add(ta);
    }

    public void add(String line) {
        ta.appendText("\n" + line);
    }

    public void clear() {
        ta.clear();
    }

    public void setRun(Activity run) {
        clear();
        if (run != null) {
            add(Format.date(run.start()));
            add(String.format("Start: %s" , Format.time(run.start())));
            add(String.format("Dist: %.2f km", run.dist() / 1000));
            add(String.format("Time: %s" , Format.time(run.time())));
            add(String.format("Laps: %d", run.laps().count()));
        }
    }
}
