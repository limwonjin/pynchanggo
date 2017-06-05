package pus.pynchanggo;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by 임원진 on 2017-05-03.
 */
public class Rotate3DAnimation extends Animation{
    private final float mfromdegress;
    private final float mtodegress;
    private final float mcenterx;
    private final float mcentery;
    private final float mdepthz;
    private final boolean mreverse;
    private Camera mcamera;

   public Rotate3DAnimation(float fromdegress,float todegress, float centerx, float centery, float depthz, boolean reverse){
       mfromdegress=fromdegress;
       mtodegress=todegress;
       mcenterx=centerx;
       mcentery=centery;
       mdepthz=depthz;
       mreverse=reverse;
   }
    @Override
    public void initialize(int width, int height, int parentwidth, int parentheight){
        super.initialize(width,height,parentwidth,parentheight);
        mcamera= new Camera();
    }
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t){

        Camera cam = mcamera;
        //cam.rotateX(360 * interpolatedTime);
        cam.rotateY(360 * interpolatedTime);
        //cam.rotateZ(360 * interpolatedTime);
        Matrix matrix = t.getMatrix();
        cam.getMatrix(matrix);
        // 회전 중심을 이미지 중심으로 하기 위해 카메라를 회전하기 전 중심을
        matrix.preTranslate(-mcenterx, -mcentery);    // 원점으로 옮기고
        matrix.postTranslate(mcenterx, mcentery);    // 회전 후 다시 원래 위치
/*
        final float fromdegress =mfromdegress;
        float degress = fromdegress + ((mtodegress -fromdegress) * interpolatedTime);
        final float centerx = mcenterx;
        final float centery =mcentery;
        final Camera camera= mcamera;
        final Matrix matrix = t.getMatrix();

        camera.save();

        if(mreverse){
            camera.translate(0.0f,0.0f, mdepthz*interpolatedTime);
        }else{
            camera.translate(0.0f,0.0f,mdepthz*(1.0f -interpolatedTime));
        }

        camera.rotateY(degress);
        camera.getMatrix(matrix);
        camera.restore();

        matrix.preTranslate(-centerx, -centery);
        matrix.postTranslate(centerx,centery);*/
    }

}
