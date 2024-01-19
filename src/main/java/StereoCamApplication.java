// TODO - GUI prompts/mods if camera has no calibration (e.g. prevent rectification)
// TODO - lines on rectification images
// TODO - red/yellow/green indicator for calibration reproj error >1px, <1px, <0.5px
// TODO - display intermediate calibration info
// TODO - capture module - disable save button if no rectified image

import controller.view.GlobalViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.opencv.core.Core;

import java.io.IOException;

public class StereoCamApplication extends Application
{
    Stage stage;
    public GlobalViewController globalViewController;

    public static void main(String[] args)
    {
        // Load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException
    {
        // Create stage
        stage = primaryStage;

        // Disable LCD text rendering (revert to JavaFX "gray" implementation)
        System.setProperty("prism.lcdtext", "false");

        // Load fonts
        Font.loadFont(GlobalViewController.loadResourceAsStream("fonts/Carlito-Regular.ttf"),10);
        Font.loadFont(GlobalViewController.loadResourceAsStream("fonts/Carlito-Bold.ttf"),10);
        Font.loadFont(GlobalViewController.loadResourceAsStream("fonts/Carlito-Italic.ttf"),10);
        Font.loadFont(GlobalViewController.loadResourceAsStream("fonts/Carlito-BoldItalic.ttf"),10);

        // Load scene into stage
        FXMLLoader loader = new FXMLLoader(GlobalViewController.loadResource("fxml/mainScene.fxml"));
        this.globalViewController = loader.getController();
        Parent root = loader.load();

        stage.setScene(new Scene(root));
        stage.setTitle("stereoCam");
        stage.show();

        // set the proper behavior on closing the application
        stage.setOnCloseRequest(e ->
        {
            globalViewController.close();
            e.consume();
        });
    }
}
