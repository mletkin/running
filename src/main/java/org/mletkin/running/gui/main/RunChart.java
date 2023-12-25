package org.mletkin.running.gui.main;

import static org.mletkin.running.gui.GuiUtil.max;
import static org.mletkin.running.gui.GuiUtil.min;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.mletkin.running.model.Activity;
import org.mletkin.running.model.Data;
import org.mletkin.running.model.Lap;
import org.mletkin.running.model.Trackpoint;

import eu.hansolo.fx.charts.Axis;
import eu.hansolo.fx.charts.AxisType;
import eu.hansolo.fx.charts.ChartType;
import eu.hansolo.fx.charts.Position;
import eu.hansolo.fx.charts.XYChart;
import eu.hansolo.fx.charts.XYPane;
import eu.hansolo.fx.charts.data.TYChartItem;
import eu.hansolo.fx.charts.series.XYSeries;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 * Plots data for an {@code Activity} in a line chart.
 */
public class RunChart extends Group {

    private Data data;

    RunChart(Data data) {
        this.data = data;
    }

    void setRange(Range range) {
        var runList = data.runs().filter(range::filter).collect(Collectors.toList());
        if (runList.size() == 1) {
            setChart(runList);
        }
    }

    void setChart(List<Activity> act) {
        var chart = mkChart(act.stream().flatMap(Activity::laps).flatMap(Lap::track).collect(Collectors.toList()));
        this.getChildren().clear();
        this.getChildren().add(chart);
    }

    void setChart(Activity act) {
        var chart = mkChart(act.laps().flatMap(Lap::track).collect(Collectors.toList()));
        this.getChildren().clear();
        this.getChildren().add(chart);
    }

    private Region mkChart(List<Trackpoint> trackPoints) {
        List<TYChartItem> items0 = stepItems(trackPoints);
        List<TYChartItem> items1 = paceItems(trackPoints);

        var series = List.of( //
                new XYSeries(items1, ChartType.RIDGE_LINE, "pace", Color.LIGHTSKYBLUE, Color.BLUE, true), //
                new XYSeries(items0, ChartType.RIDGE_LINE, "dist", Color.LIGHTSALMON, Color.RED, true) //
        );

        var pane = new XYPane(series);
        var xAxis = mkTimeAxis(trackPoints);
        var yAxis = mkYaxis(series);
        return new XYChart<>(pane, xAxis, yAxis);
    }

    private Axis mkTimeAxis(List<Trackpoint> items) {
        var axis = new Axis(Orientation.HORIZONTAL, AxisType.TIME, Position.BOTTOM);
        axis.setStart(items.stream().map(Trackpoint::time).min(LocalDateTime::compareTo).orElse(null));
        axis.setMinValue(axis.getStart());
        axis.setEnd(items.stream().map(Trackpoint::time).max(LocalDateTime::compareTo).orElse(null));
        axis.setMaxValue(axis.getEnd());
        return axis;
    }

    private Axis mkYaxis(List<XYSeries> series) {
        var axis = new Axis(Orientation.VERTICAL, Position.LEFT);
        axis.setMinValue(min(series.getFirst().getMinY(), series.stream().mapToDouble(XYSeries::getMinY).toArray()));
        axis.setMaxValue(max(series.getFirst().getMaxY(), series.stream().mapToDouble(XYSeries::getMaxY).toArray()));
        return axis;
    }

    private List<TYChartItem> paceItems(List<Trackpoint> tps) {
        return tps.stream() //
                // .filter(tp -> tp.pace().toSeconds() > 0) //
                .map(tp -> new TYChartItem(tp.time(), top(tp.pace()))) //
                .collect(Collectors.toList());
    }

    private long top(Duration pace) {
        var sec = pace.toSeconds();
        // System.out.println(sec + "s");
        return sec > 600 ? 600 : sec;
    }

    private List<TYChartItem> stepItems(List<Trackpoint> tps) {
        return tps.stream().map(tp -> new TYChartItem(tp.time(), tp.deltaDistance())).collect(Collectors.toList());
    }
}