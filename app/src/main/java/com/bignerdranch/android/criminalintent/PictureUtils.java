package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.widget.ImageView;

/**
 * Created by Luwei on 2015/11/4.
 */
//Í¼Æ¬³ß´çËõ·Åº¯Êý
public class PictureUtils {
    /**
     * Get a BitmapDrawable from a local file that is scaled down
     * to fit the current Window size.
     */
    @SuppressWarnings("deprecation")
    public static BitmapDrawable getScaleDrawable(Activity a, String path) {
        Display display = a.getWindowManager().getDefaultDisplay();
        float desWidth = display.getWidth();
        float desHeight = display.getHeight();
        // read in the dimensions of the ipmage on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        int inSampleSize = 1;
        if (srcHeight>desHeight||srcWidth>desWidth){
            if (srcWidth>srcHeight) {
                inSampleSize = Math.round((float)srcHeight / (float)desHeight);
            }else {
                inSampleSize = Math.round((float)srcWidth/(float)desWidth);
            }
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        Bitmap bitmap = BitmapFactory.decodeFile(path,options);
        return new BitmapDrawable(a.getResources(),bitmap);
    }

    public static void cleanImageView(ImageView imageView){
        if (!(imageView.getDrawable()instanceof BitmapDrawable))
            return;
        //clean up the view's image for the sake of memory
        BitmapDrawable b = (BitmapDrawable) imageView.getDrawable();
        b.getBitmap().recycle();
        imageView.setImageDrawable(null);
    }

}
