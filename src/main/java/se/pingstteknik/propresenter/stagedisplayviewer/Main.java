//Made by TreyCarey
//Credit to Daniel Kihlgren For Base Code
//Credit to Kevin Blenman for Graphics

package se.pingstteknik.propresenter.stagedisplayviewer;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import se.pingstteknik.propresenter.stagedisplayviewer.config.Property;
import se.pingstteknik.propresenter.stagedisplayviewer.eventhandler.SceneKeyTypedHandler;
import se.pingstteknik.propresenter.stagedisplayviewer.runner.LowerKeyHandler;
import se.pingstteknik.propresenter.stagedisplayviewer.util.FxUtils;
import se.pingstteknik.propresenter.stagedisplayviewer.util.Logger;
import se.pingstteknik.propresenter.stagedisplayviewer.util.LoggerFactory;
import se.pingstteknik.propresenter.stagedisplayviewer.config.applicationSettings;
import se.pingstteknik.propresenter.stagedisplayviewer.util.MidiModule;

import java.io.IOException;

public class Main extends Application {

    public static final int textboxWidth = 200;

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static LowerKeyHandler lowerKeyHandler;
    private static Thread thread;
    private MidiModule midiModule;

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws IOException {

        Stage loginScreen = new Stage();
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #4b4b4b;");

        VBox topVBox = new VBox();
        borderPane.setTop(topVBox);

        VBox centerVBox = new VBox(10);
        centerVBox.setAlignment(Pos.CENTER);
        borderPane.setCenter(centerVBox);

        Label titleLabel = new Label(applicationSettings.getApplicationName());
        titleLabel.setPadding(new Insets(10, 0, 0, 270));
        System.out.println(Font.getFamilies());
        titleLabel.setFont(Font.font("MarkPro-Book", 25));
        titleLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-text-alignment: center;");

        Label errorLabel = new Label("ERROR: INCORRECT USERNAME OR PASSWORD");
        errorLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #f43d01;");
        errorLabel.setVisible(false);

        TextField usernameField = new TextField();
        usernameField.setMaxWidth(textboxWidth);
        usernameField.setText("USERNAME");

        PasswordField passwordField = new PasswordField();
        passwordField.setMaxWidth(textboxWidth);
        passwordField.setText("PASSWORD");

        Button loginButton = new Button("LOGIN");

        topVBox.getChildren().addAll(titleLabel);
        centerVBox.getChildren().addAll(errorLabel, usernameField, passwordField, loginButton);

        loginScreen.setScene(new Scene(borderPane, 800, 600));
        loginScreen.setTitle(applicationSettings.getApplicationName());
        loginScreen.show();

        loginButton.setOnAction(e ->{
            if (usernameField.getText().equals("POKAV") && passwordField.getText().equals("AudioVideo1")) {
                errorLabel.setVisible(false);
                log.info("Starting program");
                final FxUtils fxUtils = new FxUtils();

                Property.loadProperties();

                Text lowerKey = fxUtils.createLowerKey();
                midiModule = new MidiModule();
                try {
                    lowerKeyHandler = new LowerKeyHandler(lowerKey, midiModule);
                } catch (IOException f) {
                    f.printStackTrace();
                }
                thread = new Thread(lowerKeyHandler);

                primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
                primaryStage.setTitle(applicationSettings.getApplicationName());
                Scene scene = fxUtils.createScene(lowerKey);
                scene.setOnKeyTyped(new SceneKeyTypedHandler(primaryStage));
                primaryStage.setScene(scene);
                fxUtils.startOnCorrectScreen(primaryStage);
                primaryStage.setOnCloseRequest(getEventHandler());
                primaryStage.setFullScreen(Property.START_IN_FULLSCREEN.isTrue());
                primaryStage.show();
                thread.start();
            }else{
                System.out.println("Incorrect Login");
                errorLabel.setVisible(true);
            }
        });

    }

    private EventHandler<WindowEvent> getEventHandler() {
        return new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                midiModule.terminate();
                lowerKeyHandler.terminate();
            }
        };
    }
}
