package org.mletkin.running.gui.main;

import static org.mletkin.running.gui.GuiUtil.max;
import static org.mletkin.running.gui.GuiUtil.min;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.mletkin.running.gui.prep.Smoother;
import org.mletkin.running.model.Data;
import org.mletkin.running.model.Lap;
import org.mletkin.running.model.Session;
import org.mletkin.running.model.Trackpoint;
import org.mletkin.running.util.Format;

import eu.hansolo.fx.charts.Axis;
import eu.hansolo.fx.charts.AxisType;
import eu.hansolo.fx.charts.ChartType;
import eu.hansolo.fx.charts.Position;
import eu.hansolo.fx.charts.XYChart;
import eu.hansolo.fx.charts.XYPane;
import eu.hansolo.fx.charts.data.TYChartItem;
import eu.hansolo.fx.charts.series.XYSeries;
import javafx.geometry.Orientation;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

/**
 * Plots data for an {@code Activity} in a line chart.
 */
public class RunChart extends Region {

    private Data data;
    private Region chart;

    RunChart(Data data) {
        this.data = data;
        setPrefWidth(900);
        setMaxWidth(900);
    }

    void setRange(Range range) {
        var runList = data.runs().filter(range::filter).collect(Collectors.toList());
        if (runList.size() == 1) {
            setChart(runList);
        }
    }

    void setChart(List<Session> act) {
        chart = mkChart(act.stream().flatMap(Session::laps).flatMap(Lap::track).collect(Collectors.toList()));
        this.getChildren().clear();
        this.getChildren().add(chart);
    }

    void setChart(Session act) {
        chart = mkChart(act.laps().flatMap(Lap::track).collect(Collectors.toList()));
        this.getChildren().clear();
        this.getChildren().add(chart);
    }

    private Region mkChart(List<Trackpoint> trackPoints) {
        List<TYChartItem> items0 = stepItems(trackPoints);
        List<TYChartItem> items1 = paceItems(trackPoints);
        List<TYChartItem> items2 = hiteItems(trackPoints);

        var stepSeries = new XYSeries(items0, ChartType.RIDGE_LINE, "dist", Color.LIGHTSALMON, Color.RED, true);
        var paceSeries = new XYSeries(items1, ChartType.RIDGE_LINE, "pace", Color.LIGHTSKYBLUE, Color.BLUE, true);
        var hiteSeries = new XYSeries(items2, ChartType.RIDGE_LINE, "hite", Color.LIGHTGREEN, Color.GREEN, true);

        XYPane<TYChartItem> pacePane = new XYPane(List.of(paceSeries));
        XYPane<TYChartItem> stepPane = new XYPane(List.of(stepSeries));
        XYPane<TYChartItem> hitePane = new XYPane(List.of(hiteSeries));

        var xAxis = mkTimeAxis(trackPoints);
        var yAxisLeft = mkYaxisPace(List.of( //
                paceSeries, //
                stepSeries, //
                hiteSeries //
        ));
        var yAxisRight = mkYaxisMeter(List.of( //
                paceSeries, //
                stepSeries, //
                hiteSeries //
        ));

        List<XYPane<TYChartItem>> paneList = List.of(pacePane, stepPane, hitePane);
        var chart = new XYChart<TYChartItem>(paneList, xAxis, yAxisLeft, yAxisRight);
        chart.setMaxWidth(900);
        chart.setPrefWidth(900);
        return chart;
    }

    private Axis mkTimeAxis(List<Trackpoint> items) {
        var axis = new Axis(Orientation.HORIZONTAL, AxisType.TIME, Position.BOTTOM);
        axis.setStart(items.stream().map(Trackpoint::time).min(LocalDateTime::compareTo).orElse(null));
        axis.setMinValue(axis.getStart());
        axis.setEnd(items.stream().map(Trackpoint::time).max(LocalDateTime::compareTo).orElse(null));
        axis.setMaxValue(axis.getEnd());
        axis.setDateTimeFormatPattern("HH:mm:ss");
        return axis;
    }

    private Axis mkYaxisPace(List<XYSeries> series) {
        var axis = new Axis(Orientation.VERTICAL, Position.LEFT);
        axis.setMinValue(min(series.getFirst().getMinY(), series.stream().mapToDouble(XYSeries::getMinY).toArray()));
        axis.setMaxValue(max(series.getFirst().getMaxY(), series.stream().mapToDouble(XYSeries::getMaxY).toArray()));
        axis.setNumberFormatter(new PaceConverter());
        return axis;
    }

    private Axis mkYaxisMeter(List<XYSeries> series) {
        var axis = new Axis(Orientation.VERTICAL, Position.RIGHT);
        axis.setMinValue(min(series.getFirst().getMinY(), series.stream().mapToDouble(XYSeries::getMinY).toArray()));
        axis.setMaxValue(max(series.getFirst().getMaxY(), series.stream().mapToDouble(XYSeries::getMaxY).toArray()));

        return axis;
    }

    private class PaceConverter extends StringConverter<Number> {

        @Override
        public String toString(Number object) {
            if (object == null) {
                return "";
            }
            return Format.pace(Duration.ofSeconds(object.longValue()));
        }

        @Override
        public Number fromString(String string) {
            return null;
        }

    }

    private List<TYChartItem> paceItems(List<Trackpoint> tps) {
        var sm = new Smoother(5);

        return tps.stream() //
                .map(tp -> new TYChartItem(tp.time(), Math.min(sm.add(tp).value().secPerKm(), 1_200))) //
                .collect(Collectors.toList());
    }

    private List<TYChartItem> stepItems(List<Trackpoint> tps) {
        return tps.stream() //
                .map(tp -> new TYChartItem(tp.time(), tp.deltaDistance().meter() * 100)) //
                .collect(Collectors.toList());
    }

    private List<TYChartItem> hiteItems(List<Trackpoint> tps) {
        return tps.stream() //
                .map(tp -> new TYChartItem(tp.time(), tp.deltaAltitude().meter() * 200)) //
                .collect(Collectors.toList());
    }

}
