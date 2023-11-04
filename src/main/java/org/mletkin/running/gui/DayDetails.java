package org.mletkin.running.gui;

import org.mletkin.running.model.Activity;

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
            add("Start: " + run.start());
            add("Dist: " + run.dist());
            add("Time: " + run.time());
            add("Laps: " + run.laps().count());
        }
    }
}
