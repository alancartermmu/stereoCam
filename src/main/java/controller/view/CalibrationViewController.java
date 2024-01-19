package controller.view;

import controller.main.MainController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import model.calibration.StereoCalibration;
import model.camera.StereoCamera;
import model.correspondence.Checkerboard;
import model.correspondence.CheckerboardUtilities;
import model.correspondence.StereoProjection;
import model.image.ImageUtilities;
import model.image.StereoImage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static model.correspondence.CheckerboardUtilities.findCornersStereo;
import static model.image.ImageUtilities.drawPointsOnImage;

public class CalibrationViewController implements ViewController
{
    /*---------
     * FIELDS *
     ---------*/
    
    public VBox graphPanelContent;
    public AnchorPane checkerboardSketchContainer;
    public Spinner<Integer> checkerboardRowsSpinner;
    public Spinner<Integer> checkerboardColsSpinner;
    public Spinner<Integer> checkerboardSqSizeSpinner;
    public ImageView calibrationImageView;
    public VBox controlPanel;
    public VBox controlPanelContent;
    public VBox checkerboardPanel;
    public Label checkerBoardDetectedLabel;
    public Button addToCalibrationButton;
    public VBox graphPanel;
    public BarChart<String,Double> calibrationBarChart;
    public VBox calibrationDataPanel;
    public VBox calibrationDataPanelContent;
    public Button saveCalibrationButton;
    public Button newCalibrationButton;
    public Label calibrationAcceptableLabel;
    public VBox previewPane;
    public AnchorPane previewContainer;
    public ImageView previewImageView;
    public FontIcon icon_viewsCheck;
    public FontIcon icon_pointsCheck;
    public FontIcon icon_ErrorCheck;
    
    MainController mainController = MainController.getInstance();
    StereoCamera cam = mainController.getCamera();
    Checkerboard cb = mainController.getCheckerboard();
    ScheduledExecutorService feedExecutor;
    private final String icon_pass = "bi-check";
    private final String icon_fail = "bi-x";
    private final String icon_unknown = "bi-dash";
    private final Color colour_pass = Color.GREEN;
    private final Color colour_fail = Color.RED;
    private final Color colour_neutral = Color.GRAY;

    // Listeners
    ChangeListener<Integer> checkerboardChangeListener = (observable, oldValue, newValue) -> handleCheckerBoardChange();
    ChangeListener<Boolean> checkerboardDetectedListener = (observable, oldValue, newValue) -> handleCheckerBoardDetectedChange();
    ChangeListener<Boolean> cameraStartedListener = (observable, oldValue, newValue) -> handleCameraStartedChange();
    ChangeListener<Boolean> hasCalibrationDataListener = (observable, oldValue, newValue) -> handleHasCalibrationDataChange();

    /********************
     * OVERRIDE METHODS *
     ********************/

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // Anything properties which could not be set in FXML
        checkerboardRowsSpinner.setValueFactory(new IntegerSpinnerValueFactory(4,20, cb.Rows()));
        checkerboardColsSpinner.setValueFactory(new IntegerSpinnerValueFactory(4,20, cb.Columns()));
        checkerboardSqSizeSpinner.setValueFactory(new IntegerSpinnerValueFactory(5,50, (int) cb.SquareSize()));

        // Add listeners
        checkerboardRowsSpinner.valueProperty().addListener(checkerboardChangeListener);
        checkerboardColsSpinner.valueProperty().addListener(checkerboardChangeListener);
        checkerboardSqSizeSpinner.valueProperty().addListener(checkerboardChangeListener);
        mainController.status_CheckerboardDetected.addListener(checkerboardDetectedListener);
        mainController.status_CameraStarted.addListener(cameraStartedListener);
        mainController.status_CalibrationStarted.addListener(hasCalibrationDataListener);

        double pictureAspectRatio = (double) (2 * cam.Config().Width()) / (cam.Config().Height());

        // Add bindings
        previewContainer.minHeightProperty().bind(previewContainer.widthProperty().divide(pictureAspectRatio));
        previewImageView.fitWidthProperty().bind(previewContainer.widthProperty());
        
        icon_viewsCheck.setIconLiteral(icon_unknown);
        icon_pointsCheck.setIconLiteral(icon_unknown);
        icon_ErrorCheck.setIconLiteral(icon_unknown);
        icon_viewsCheck.setIconColor(colour_neutral);
        icon_pointsCheck.setIconColor(colour_neutral);
        icon_ErrorCheck.setIconColor(colour_neutral);
        calibrationAcceptableLabel.setTextFill(colour_neutral);
    }

    @Override
    public void show()
    {
        showFeed();
        handleCameraStartedChange();
        handleCheckerBoardChange();
        handleCheckerBoardDetectedChange();
        drawRpeGraph();
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
        checkerboardRowsSpinner.valueProperty().removeListener(checkerboardChangeListener);
        checkerboardColsSpinner.valueProperty().removeListener(checkerboardChangeListener);
        checkerboardSqSizeSpinner.valueProperty().removeListener(checkerboardChangeListener);
        mainController.status_CheckerboardDetected.removeListener(checkerboardDetectedListener);
        mainController.status_CameraStarted.removeListener(cameraStartedListener);
        mainController.status_CalibrationStarted.removeListener(hasCalibrationDataListener);

        // Unbind
        previewContainer.minHeightProperty().unbind();
        previewImageView.fitWidthProperty().unbind();
    }

    /******************************
     * FXML EVENT HANDLER METHODS *
     ******************************/

    @FXML
    public void handleAddToCalibrationButton(ActionEvent event)
    {
        StereoImage image = cam.captureStereoImage();
        StereoProjection proj = CheckerboardUtilities.findCornersStereo(image, cb);
        if (proj != null) mainController.calibrationProjections.add(proj);
        mainController.recalibrate();
        drawRpeGraph();
        updateStatus();
        mainController.status_CalibrationStarted.set(!mainController.calibrationProjections.isEmpty());
    }

    @FXML
    public void handleSaveCalibrationButton(ActionEvent event)
    {
        mainController.getCamera().updateIntrinsics(mainController.calibration.StereoCameraIntrinsics());
    }

    @FXML
    public void handleNewCalibrationButton(ActionEvent event)
    {
        mainController.calibration = null;
        mainController.calibrationProjections.clear();
        mainController.status_CalibrationStarted.set(false);
    }

    @FXML
    public void handleScrollPaneClick(MouseEvent mouseEvent)
    {
        ScrollPane p = (ScrollPane) mouseEvent.getSource();
        p.getParent().requestFocus();
        mouseEvent.consume();
    }

    /*******************************
     * OTHER EVENT HANDLER METHODS *
     *******************************/

    public void handleCheckerBoardDetectedChange()
    {
        boolean detected = mainController.status_CheckerboardDetected.get();
        boolean started = mainController.status_CameraStarted.get();

        if (started)
        {
            checkerBoardDetectedLabel.setText(detected ? "CHECKERBOARD DETECTED" : "CHECKERBOARD NOT DETECTED");
            checkerBoardDetectedLabel.setTextFill(detected ? Color.GREEN : Color.RED);
        }
        addToCalibrationButton.setDisable(!detected);
    }

    public void handleCameraStartedChange()
    {
        boolean started = mainController.status_CameraStarted.get();
        if (!started)
        {
            checkerBoardDetectedLabel.setText("CAMERA NOT STARTED");
            checkerBoardDetectedLabel.setTextFill(Color.BLACK);
        }
        else
        {
            handleCheckerBoardDetectedChange();
        }
    }

    public void handleCheckerBoardChange()
    {
        mainController.getCheckerboard().setnRows(checkerboardRowsSpinner.getValue());
        mainController.getCheckerboard().setnColumns(checkerboardColsSpinner.getValue());
        mainController.getCheckerboard().setSquareSize(checkerboardSqSizeSpinner.getValue());

        drawCheckerboard(cb.Rows(), cb.Columns(), checkerboardSketchContainer);
    }

    private void handleHasCalibrationDataChange()
    {
        updateStatus();
    }

    /*************************
     * OTHER UTILITY METHODS *
     *************************/
    
    private void updateStatus()
    {
        if (mainController.status_CalibrationStarted.get())
        {
            boolean viewsCheck = mainController.calibrationViewsCheck();
            boolean pointsCheck = mainController.calibrationPointsCheck();
            boolean errorCheck = mainController.calibrationErrorCheck();
            boolean overallCheck = mainController.calibrationOverallCheck();
            
            saveCalibrationButton.setDisable(!overallCheck);
            
            icon_viewsCheck.setIconLiteral(viewsCheck ? icon_pass : icon_fail);
            icon_pointsCheck.setIconLiteral(pointsCheck ? icon_pass : icon_fail);
            icon_ErrorCheck.setIconLiteral(errorCheck ? icon_pass : icon_fail);
            
            icon_viewsCheck.setIconColor(viewsCheck ? colour_pass : colour_fail);
            icon_pointsCheck.setIconColor(pointsCheck ? colour_pass : colour_fail);
            icon_ErrorCheck.setIconColor(errorCheck ? colour_pass : colour_fail);
            
            calibrationAcceptableLabel.setText(overallCheck ? "CALIBRATION ACCEPTABLE" : "CALIBRATION UNACCEPTABLE");
            calibrationAcceptableLabel.setTextFill(overallCheck ? colour_pass : colour_fail);
        }
        else
        {
            icon_viewsCheck.setIconLiteral(icon_unknown);
            icon_pointsCheck.setIconLiteral(icon_unknown);
            icon_ErrorCheck.setIconLiteral(icon_unknown);
            
            icon_viewsCheck.setIconColor(colour_neutral);
            icon_pointsCheck.setIconColor(colour_neutral);
            icon_ErrorCheck.setIconColor(colour_neutral);
            
            calibrationBarChart.getData().clear();
            saveCalibrationButton.setDisable(true);
            calibrationAcceptableLabel.setText("NO CALIBRATION DATA");
            calibrationAcceptableLabel.setTextFill(colour_neutral);
        }
    }

    private void showFeed()
    {
        feedExecutor = Executors.newSingleThreadScheduledExecutor();
        feedExecutor.scheduleAtFixedRate(this::showImageWithCheckerboard, 0, 1000 / cam.Config().Fps(), TimeUnit.MILLISECONDS);
    }

    private void showNormalImage()
    {
        Image image = cam.isStarted() ? ImageUtilities.MatToFxImage(cam.capture()) : null;
        Platform.runLater(() -> calibrationImageView.setImage(image));
    }

    private void showImageWithCheckerboard()
    {
        Image image;
        boolean detected;

        if (cam.isStarted())
        {
            StereoImage stImg = cam.captureStereoImage();
            StereoProjection proj = findCornersStereo(stImg, cb);

            Mat imgLeft = stImg.Left().Data();
            Mat imgRight = stImg.Right().Data();

            if (proj != null)
            {
                imgLeft = drawPointsOnImage(imgLeft, proj.ImagePointsLeft(), new Scalar(0,255,0), Imgproc.MARKER_DIAMOND, 25, 5);
                imgRight = drawPointsOnImage(imgRight, proj.ImagePointsRight(), new Scalar(0,255,0), Imgproc.MARKER_DIAMOND, 25, 5);
                detected = true;
            }
            else
            {
                detected = false;
            }

            Mat compositeImage = ImageUtilities.hConcat(imgLeft, imgRight);

            image = ImageUtilities.MatToFxImage(compositeImage);
        }
        else
        {
            detected = false;
            image = null;
        }
        Platform.runLater(() -> previewImageView.setImage(image));
        Platform.runLater(() -> mainController.status_CheckerboardDetected.set(detected));
    }

    private void drawCheckerboard(int rows, int cols, AnchorPane container)
    {
        // Get container dimensions
        double paddingLeft = container.getPadding().getLeft();
        double paddingRight = container.getPadding().getRight();
        double paddingTop = container.getPadding().getTop();
        double paddingBottom = container.getPadding().getBottom();
        DoubleBinding containerWidth = container.widthProperty().subtract(paddingLeft).subtract(paddingRight);
        DoubleBinding containerHeight = container.heightProperty().subtract(paddingTop).subtract(paddingBottom);

        // Clear container
        container.getChildren().clear();

        // Create frame to display checkerboard
        Rectangle frame = new Rectangle();
        frame.setManaged(false);

        // Calculate desired aspect ratio of frame (allowing one square margin all around)
        double aspectRatioFrame = (double) (2 + rows) / (2 + cols);

        // Compute dimensions and location of frame (fit checkerboard to container)
        DoubleBinding frameWidth = (DoubleBinding) Bindings.min(container.widthProperty(), container.heightProperty().divide(aspectRatioFrame));
        DoubleBinding frameHeight = (DoubleBinding) Bindings.min(container.heightProperty(), container.widthProperty().multiply(aspectRatioFrame));
        DoubleBinding frameX = container.widthProperty().divide(2).subtract(frameWidth.divide(2));
        DoubleBinding frameY = container.heightProperty().divide(2).subtract(frameHeight.divide(2));

        // Bind dimensions and location of frame
        frame.widthProperty().bind(frameWidth);
        frame.heightProperty().bind(frameHeight);
        frame.xProperty().bind(frameX);
        frame.yProperty().bind(frameY);

        // Set frame appearance
        frame.setFill(Color.WHITE);
        frame.setStroke(Color.BLACK);
        frame.setStrokeWidth(1);

        // Add frame to container
        container.getChildren().add(frame);

        // Squares
        DoubleBinding squareWidth = frameWidth.divide(2 + cols);
        DoubleBinding squareHeight = frame.heightProperty().divide(2 + rows);
        for (int row = 0; row < rows; row++)
        {
            for (int col = 0; col < cols; col++)
            {
                Rectangle square = new Rectangle();
                Color color;
                if ((row + col) % 2 == 0) color = Color.BLACK;
                else color = Color.WHITE;
                square.setFill(color);
                square.widthProperty().bind(squareWidth);
                square.heightProperty().bind(squareHeight);
                square.xProperty().bind(squareWidth.multiply(col).add(frame.xProperty()).add(squareWidth));
                square.yProperty().bind(squareHeight.multiply(row).add(frame.yProperty()).add(squareHeight));
                square.setManaged(false);
                container.getChildren().add(square);
            }
        }
    }

    private void drawRpeGraph()
    {
        XYChart.Series<String,Double> left = new XYChart.Series<>();
        left.setName("Left");

        XYChart.Series<String,Double> right = new XYChart.Series<>();
        right.setName("Right");

        List<Double> leftErrors = new ArrayList<>();
        List<Double> rightErrors = new ArrayList<>();

        StereoCalibration calibration = mainController.calibration;
        if (calibration != null)
        {
            leftErrors = mainController.calibration.LeftViewReprojectionErrors();
            rightErrors = mainController.calibration.RightViewReprojectionErrors();
        }

        for (int i = 0; i < leftErrors.size(); i++)
        {
            left.getData().add(new XYChart.Data<String, Double>(String.valueOf(i + 1), leftErrors.get(i)));
            right.getData().add(new XYChart.Data<String, Double>(String.valueOf(i + 1), rightErrors.get(i)));
        }

        calibrationBarChart.getData().clear();
        calibrationBarChart.getData().addAll(left, right);
    }
}
