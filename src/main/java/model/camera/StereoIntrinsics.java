package model.camera;

import model.camera.intrinsics.*;
import org.opencv.core.Size;

// TODO quality information? number of views? view errors?
// TODO timestamp?  something to identify when calibration was made...
public class StereoIntrinsics
{
    /*---------
     * FIELDS *
     ---------*/

    private final Size imageSize;
    private final IntrinsicMatrix K1;
    private final IntrinsicMatrix K2;
    private final DistortionVector D1;
    private final DistortionVector D2;
    private final EssentialMatrix E;
    private final FundamentalMatrix F;
    private final RotationMatrix R;
    private final TranslationVector T;
    private final double rmsReprojectionError;

    /*---------------
     * CONSTRUCTORS *
     ---------------*/

    public StereoIntrinsics(
            Size imageSize,
            IntrinsicMatrix K1,
            IntrinsicMatrix K2,
            DistortionVector D1,
            DistortionVector D2,
            EssentialMatrix E,
            FundamentalMatrix F,
            RotationMatrix R,
            TranslationVector T,
            double rmsReprojectionError
    )
    {
        this.imageSize = imageSize;
        this.K1 = K1;
        this.K2 = K2;
        this.D1 = D1;
        this.D2 = D2;
        this.E = E;
        this.F = F;
        this.R = R;
        this.T = T;
        this.rmsReprojectionError = rmsReprojectionError;
    }

    /*--------------------
     * GETTERS & SETTERS *
     --------------------*/

    public Size ImageSize() { return imageSize; }
    public IntrinsicMatrix IntrinsicMatrixK1() { return K1; }
    public IntrinsicMatrix IntrinsicMatrixK2() { return K2; }
    public DistortionVector DistortionCoefficients1() { return D1; }
    public DistortionVector DistortionCoefficients2() { return D2; }
    public EssentialMatrix EssentialMatrixE() { return E; }
    public FundamentalMatrix FundamentalMatrixF() { return F; }
    public RotationMatrix RotationMatrixR() { return R; }
    public TranslationVector TranslationVectorT() { return T; }
    public double RmsReprojectionError() { return rmsReprojectionError; }
}
