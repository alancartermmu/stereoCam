package controller.view;

import controller.main.MainController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.camera.StereoCamera;
import org.controlsfx.control.ToggleSwitch;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class GlobalViewController implements Initializable
{
    /*---------
     * FIELDS *
     ---------*/
    
    /* FXML FIELDS */

    @FXML
    public AnchorPane sidebar;
    public AnchorPane header;
    public AnchorPane title;
    public AnchorPane pageContent;
    
    @FXML
    public Button sidebar_btn_camera;
    public Button sidebar_btn_calibrate;
    public Button sidebar_btn_capture;
    public Button sidebar_btn_exit;
    public Button swapSwitch;
    public ToggleSwitch cameraSwitch;

    
    /* OTHER FIELDS */
    private ViewController pageController;
    Object cameraPageNodes;
    Object calibrationPageNodes;
    Object capturePageNodes;
    ViewController cameraPageController;
    ViewController calibrationPageController;
    ViewController capturePageController;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        cameraSwitch.selectedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                StereoCamera cam = MainController.getInstance().getCamera();

                if (cameraSwitch.selectedProperty().get())
                {
                    boolean status = cam.start();
                    MainController.getInstance().status_CameraStarted.set(status);
                    cameraSwitch.selectedProperty().set(status);
                    swapSwitch.setDisable(false);
                }
                else
                {
                    cam.stop();
                    MainController.getInstance().status_CameraStarted.set(false);
                    cameraSwitch.selectedProperty().set(false);
                    swapSwitch.setDisable(true);
                }
            }
        });

        // Load FXML nodes and controller for each page
        FXMLLoader cameraLoader = new FXMLLoader(loadResource("fxml/adjustmentsPage.fxml"));
        FXMLLoader calibrationLoader = new FXMLLoader(loadResource("fxml/calibrationPage.fxml"));
        FXMLLoader captureLoader = new FXMLLoader(loadResource("fxml/capturePage.fxml"));
        try
        {
            cameraPageNodes = cameraLoader.load();
            calibrationPageNodes = calibrationLoader.load();
            capturePageNodes = captureLoader.load();

            cameraPageController = cameraLoader.getController();
            calibrationPageController = calibrationLoader.getController();
            capturePageController = captureLoader.getController();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void handleCameraButtonAction(ActionEvent event)
    {
        changePage("ADJUSTMENTS");
    }

    @FXML
    protected void handleCalibrateButtonAction(ActionEvent event)
    {
        changePage("CALIBRATION");
    }

    @FXML
    protected void handleCaptureButtonAction(ActionEvent event)
    {
        changePage("CAPTURE");
    }

    @FXML
    protected void handleExitButtonAction(ActionEvent event)
    {
        close();
    }
    
    @FXML
    public void handleSwapButton(ActionEvent event)
    {
        MainController.getInstance().getCamera().swapCameras();
    }
    
    @FXML
    public void handleScrollPaneClick(MouseEvent mouseEvent)
    {
        ScrollPane p = (ScrollPane) mouseEvent.getSource();
        p.getParent().requestFocus();
        mouseEvent.consume();
    }

    /* OTHER */
    public static URL loadResource(String resourcePath)
    {
        return ClassLoader.getSystemClassLoader().getResource(resourcePath);
    }

    public static InputStream loadResourceAsStream(String resourcePath)
    {
        return ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);
    }

    public void close()
    {
        calibrationPageController.close();

        MainController.getInstance().exit();
    }

    private void changePage(String page)
    {
        Node pageNodes;
        boolean cameraButton = false;
        boolean calibrationButton = false;
        boolean captureButton = false;

        if (pageController != null) pageController.hide();
        switch (page)
        {
            case "ADJUSTMENTS":
                pageNodes = (Node) cameraPageNodes;
                pageController = cameraPageController;
                cameraButton = true;
                break;
            case "CALIBRATION":
                pageNodes = (Node) calibrationPageNodes;
                pageController = calibrationPageController;
                calibrationButton = true;
                break;
            case "CAPTURE":
                pageNodes = (Node) capturePageNodes;
                pageController = capturePageController;
                captureButton = true;
                break;
            default:
                pageNodes = null;
                pageController = null;
                break;
        }

        pageContent.getChildren().clear();
        pageContent.getChildren().add(pageNodes);
        if (pageController != null) pageController.show();

        sidebar_btn_camera.setDefaultButton(cameraButton);
        sidebar_btn_calibrate.setDefaultButton(calibrationButton);
        sidebar_btn_capture.setDefaultButton(captureButton);
    }
}
