package controller.view;

import controller.main.MainController;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.camera.CameraProperty;
import model.camera.StereoCamera;
import model.image.ImageUtilities;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.opencv.videoio.Videoio.*;

public class AdjustmentViewController implements ViewController
{
    /*---------
     * FIELDS *
     ---------*/
    
    @FXML
    public VBox mainCameraArea;
    public VBox imageControlPanel;
    public VBox imageControlPanelContent;
    public Slider slider_focus;
    public Slider slider_exposure;
    public Slider slider_brightness;
    public Slider slider_contrast;
    public Slider slider_saturation;
    public Slider slider_sharpness;
    public Slider slider_gain;
    public Slider slider_backlight;
    public Slider slider_gamma;
    public ImageView previewImageView;
    public AnchorPane previewContainer;
    public VBox previewPane;
    
    MainController repo = MainController.getInstance();;
    StereoCamera cam = repo.getCamera();
    ScheduledExecutorService feedExecutor;
    
    List<Slider> sliders = new ArrayList<>();

    /********************
     * OVERRIDE METHODS *
     ********************/

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // Any properties which could not be set in FXML
        
        double pictureAspectRatio = (double) (2 * cam.Config().Width()) / (cam.Config().Height());

        // Add listeners
        initialiseSlider(slider_exposure, cam, CAP_PROP_EXPOSURE, false);
        initialiseSlider(slider_focus, cam, CAP_PROP_FOCUS, true);
        initialiseSlider(slider_brightness, cam, CAP_PROP_BRIGHTNESS, false);
        initialiseSlider(slider_contrast, cam, CAP_PROP_CONTRAST, false);
        initialiseSlider(slider_saturation, cam, CAP_PROP_SATURATION, false);
        initialiseSlider(slider_sharpness, cam, CAP_PROP_SHARPNESS, false);
        initialiseSlider(slider_gain, cam, CAP_PROP_GAIN, false);
        initialiseSlider(slider_backlight, cam, CAP_PROP_BACKLIGHT, false);
        initialiseSlider(slider_gamma, cam, CAP_PROP_GAMMA, false);
        
        sliders.add(slider_exposure);
        sliders.add(slider_focus);
        sliders.add(slider_brightness);
        sliders.add(slider_contrast);
        sliders.add(slider_saturation);
        sliders.add(slider_sharpness);
        sliders.add(slider_gain);
        sliders.add(slider_backlight);
        sliders.add(slider_gamma);

        // Add bindings
        for (Slider s : sliders) s.disableProperty().bind(repo.status_CameraStarted.not());
        previewContainer.minHeightProperty().bind(previewContainer.widthProperty().divide(pictureAspectRatio));
        previewImageView.fitWidthProperty().bind(previewContainer.widthProperty());
    }

    @Override
    public void show()
    {
        feedExecutor = Executors.newSingleThreadScheduledExecutor();
        feedExecutor.scheduleAtFixedRate(
                () -> showNormalImage(previewImageView),
                0,
                1000 / cam.Config().Fps(),
                TimeUnit.MILLISECONDS
        );
    }

    @Override
    public void hide()
    {
        // Stop executor service
        if (feedExecutor != null)
        {
            try
            {
                feedExecutor.shutdown();
                feedExecutor.awaitTermination(50, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e)
            {
                feedExecutor.shutdownNow();
            }
        }
    }

    @Override
    public void close()
    {
        hide();

        // Remove listeners

        // Unbind
        for (Slider s : sliders) s.disableProperty().unbind();
        previewContainer.minHeightProperty().unbind();
        previewImageView.fitWidthProperty().unbind();
    }

    /******************************
     * FXML EVENT HANDLER METHODS *
     ******************************/


    /*******************************
     * OTHER EVENT HANDLER METHODS *
     *******************************/


    /*************************
     * OTHER UTILITY METHODS *
     *************************/

    private void initialiseSlider(Slider s, StereoCamera c, int propertyId, boolean reverse)
    {
        CameraProperty p = c.Properties().get(propertyId);
        s.setMax(p.MaxValue());
        s.setMin(p.MinValue());
        s.setValue(reverse? p.MaxValue() - p.Value() : p.Value());
        ChangeListener<Number> ch = (observable, oldValue, newValue) -> c.setProperty(propertyId, reverse ? s.getMax() - s.getValue() : s.getValue());
        s.valueProperty().addListener(ch);
    }
    
    private void showNormalImage(ImageView imView)
    {
        Image image = cam.isStarted() ? ImageUtilities.MatToFxImage(cam.capture()) : null;
        Platform.runLater(() -> imView.setImage(image));
    }
    
}
