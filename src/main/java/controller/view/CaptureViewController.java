package controller.view;

import controller.main.MainController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import model.camera.StereoCamera;
import model.image.ImageUtilities;
import model.rectification.StereoRemapping;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CaptureViewController implements ViewController
{
    public VBox pageArea;
    public VBox previewPane;
    public AnchorPane previewContainer;
    public ImageView previewImageView;
    public VBox saveImagePanel;
    public DirectoryChooser directoryChooser = new DirectoryChooser();
    public Button chooseDirectoryButton;
    public Button saveImageButton;
    public Label directoryLabel;
    
    private File directory;
    
    MainController repo = MainController.getInstance();;
    StereoCamera cam = repo.getCamera();
    ScheduledExecutorService feedExecutor;
    StereoRemapping remap;
    
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        double pictureAspectRatio = (double) (2 * cam.Config().Width()) / (cam.Config().Height());
        
        // Add bindings
        previewContainer.minHeightProperty().bind(previewContainer.widthProperty().divide(pictureAspectRatio));
        previewImageView.fitWidthProperty().bind(previewContainer.widthProperty());
    }
    
    @Override
    public void show()
    {
        feedExecutor = Executors.newSingleThreadScheduledExecutor();
        feedExecutor.scheduleAtFixedRate(() -> showRectifiedImage(previewImageView), 0, 1000 / cam.Config().Fps(), TimeUnit.MILLISECONDS);
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
        previewContainer.minHeightProperty().unbind();
        previewImageView.fitWidthProperty().unbind();
    }
    
    private void showRectifiedImage(ImageView imView)
    {
        Image image;
        
        if (cam.isStarted() && cam.isCalibrated())
        {
            image = ImageUtilities.MatToFxImage(
                    ImageUtilities.drawLinesOnImage(
                            cam.captureRectifiedStereoImage().toSingleImage().Data(),
                            10,new Scalar(0,255,0),0,2
                    )
            );
        }
        else
        {
            image = null;
        }
        
        Platform.runLater(() -> imView.setImage(image));
    }
    
    public void handleChooseDirectoryButton(ActionEvent event)
    {
        directoryChooser.setTitle("Choose directory");
        this.directory = directoryChooser.showDialog(((Node) event.getSource()).getScene().getWindow());
        directoryLabel.setText(directory.getAbsolutePath());
    }
    
    
    public void handleSaveImageButton(ActionEvent event)
    {
        if (cam.isStarted() && cam.isCalibrated() && directory != null)
        {
            Mat img = cam.captureRectifiedStereoImage().toSingleImage().Data();
            String folder = directory.getAbsolutePath()+"\\";
            String format = "png";
            ImageUtilities.exportImage(img, folder, format);
        }
    }
    
    
}
