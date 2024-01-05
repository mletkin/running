package org.mletkin.running.gui.main;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.mletkin.running.model.Data;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * Main window of the application.
 */
public class Running extends Application {
    private Data data;
    private SessionListDetails details;
    private RunChart runChart;
    private Stage primary;

    /**
     * Launches the application.
     *
     * @param args
     *                 first argument is the data directory
     */
    public static void launch(String... args) {
        Application.launch(args);
    }

    private Optional<Path> firstParameter() {
        return Optional.ofNullable(getParameters()) //
                .map(Parameters::getRaw) //
                .filter(Predicate.not(List::isEmpty)) //
                .map(List::getFirst) //
                .map(Paths::get);
    }

    @Override
    public void start(Stage stage) {
        data = firstParameter().map(Data::new).orElseGet(Data::new);
        details = new SessionListDetails(data);
        runChart = new RunChart(data);

        stage.setTitle("Running");

        var root = new BorderPane();
        root.setTop(menu());
        root.setCenter(new VBox( //
                new MonthCalendar(new BoxFactory(data, this::rangeChanged)), //
                runChart) //
        );

        root.setRight(details);

        Scene scene = new Scene(root, 900, 500);

        stage.setScene(scene);
        stage.show();
        primary = stage;
    }

    private void rangeChanged(Range range) {
        details.setRange(range);
        runChart.setRange(range);
    };

    private MenuBar menu() {
        MenuItem[] items = { //
                item("Read Data", a -> readData()) //
        };

        var menuBar = new MenuBar();
        var menu = new Menu("File");

        menuBar.getMenus().addAll(menu);
        menu.getItems().addAll(items);

        return menuBar;
    }

    private MenuItem item(String text, EventHandler<ActionEvent> action) {
        var it = new MenuItem(text);
        it.setOnAction(action);
        return it;
    }

    /**
     * Select directory and read the data.
     */
    private void readData() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(primary);
        if (selectedDirectory != null) {
            data = new Data(selectedDirectory.toPath());
        }
    }

}
