package org.mletkin.running.gui.main;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.mletkin.running.gui.main.SpeedGrouper.Line;
import org.mletkin.running.gui.prep.Normalizer;
import org.mletkin.running.model.Data;
import org.mletkin.running.model.Distance;
import org.mletkin.running.model.Lap;
import org.mletkin.running.model.Session;
import org.mletkin.running.model.Speed;
import org.mletkin.running.util.Format;

import javafx.scene.control.TextArea;

/**
 * Displays detail data for a session list.
 */
public class SessionListDetails extends TextArea {

    private Data data;
    private Function<Session, Session> convert = new Normalizer()::norm;
    private List<Session> sessions;
    private String text;

    /**
     * Creates a detail pane.
     *
     * @param data
     *                 Running data to select from
     */
    public SessionListDetails(Data data) {
        this.data = data;
    }

    /**
     * Sets the range to display.
     *
     * @param range
     *                  time range
     */
    public void setRange(Range range) {
        sessions = data.runs().filter(range::filter).collect(Collectors.toList());
        text = range.txt();
        render();
    }

    private void render() {
        var sum = new Summarizer();
        sessions.stream().map(convert).forEach(sum::add);

        clear();
        if (sum.runs() > 0) {
            add(text);
            if (sum.runs() == 1) {
                add(String.format("Start: %s", Format.time(sessions.getFirst().start())));
            } else {
                add(String.format("Runs: %d", sum.runs()));
            }
            add(String.format("Dist: %.2f km", sum.dist().km()));
            add(String.format("Time: %s", Format.time(sum.time())));
            add(String.format("Laps: %d", sum.laps()));
            add(String.format("Alt:  %.2f m", //
                    sessions.stream().flatMap(Session::laps) //
                            .map(Lap::deltaAlt) //
                            .reduce(Distance.ZERO, Distance::plus) //
                            .meter()));
            add("---");
            addRaw();
            add("---");
            addNorm();
        }
    }

    private void addRaw() {
        var sum = new Summarizer();
        sessions.stream() //
                .flatMap(Session::laps) //
                .flatMap(Lap::track) //
                .forEach(sum::add);

        add(String.format("Dist: %.3f km", sum.dist().km()));
        add(String.format("Time: %s", Format.time(sum.time())));
        add(String.format("Alt: %.2f/%.2f m", sum.altUp().meter(), sum.altDown().meter()));
        add(String.format("Pace: %s", Format.pace(new Speed(sum.time(), sum.dist()).pace())));
        add("---");
        var sg = new SpeedGrouper();
        sessions.stream() //
                .flatMap(Session::laps).flatMap(Lap::track) //
                .forEach(sg::process);

        sg.result().map(this::format).forEach(this::add);
    }

    private void addNorm() {
        var sum = new Summarizer();
        sessions.stream() //
                .map(convert) //
                .forEach(sum::add);

        add(String.format("Dist: %.3f km", sum.dist().km()));
        add(String.format("Time: %s", Format.time(sum.time())));
        add(String.format("Alt: %.2f/%.2f m", sum.altUp().meter(), sum.altDown().meter()));
        add(String.format("Pace: %s", Format.pace(new Speed(sum.time(), sum.dist()).pace())));
    }

    private void add(String line) {
        this.appendText("\n" + line);
    }

    private String format(Line line) {
        return String.format("%d %s, %d %%", //
                line.group(), Format.time(line.time()), (int) (line.percentage() * 100 + 0.5));
    }
}
