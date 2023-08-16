package com.photo.editor.snapstudio.PhEditor.Editor;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.core.view.ViewCompat;

import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.PhEditor.constants.Constants;
import com.photo.editor.snapstudio.PhEditor.helper.BeautyHelper;
import com.photo.editor.snapstudio.PhEditor.views.FaceDetect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class MakeUpView extends View {
    public Paint BpaintPath;
    public Paint BrectPaint;
    public Bitmap baseBitmap;
    public Canvas baseCanvas;
    public Paint baseDrawPaint;
    public Path baseEyePathLeft;
    public Path baseEyePathRight;
    public Path baseMouthPath;
    public Paint basePaint;
    public int[] basePix;
    public Paint blushDrawPaint;
    public Bitmap blushLeftBitmap;
    public Canvas blushLeftCanvas;
    public Path blushLeftPath;
    public int[] blushLeftPix;
    public Rect blushLeftRect;
    public Paint blushPaint;
    public Bitmap blushRightBitmap;
    public Canvas blushRightCanvas;
    public Path blushRightPath;
    public int[] blushRightPix;
    public Rect blushRightRect;
    public Path border;
    public Path border1;
    public Path border2;
    public Path border3;
    public Rect brightLeftRect;
    public Rect brightRightRect;
    public Rect enlargeLeftRect;
    public Rect enlargeRightRect;
    public Bitmap eyeColorLeftBp;
    public Bitmap eyeColorRightBp;
    public List<Landmark> eyeLeftLandmarks;
    public Path eyeLeftPath;
    public Bitmap eyeLens;
    public Canvas eyeLensCanvas;
    public Bitmap eyeLensMask;
    public int[] eyeLensMaskPix;
    public int eyeRadius;
    public List<Landmark> eyeRightLandmarks;
    public Path eyeRightPath;
    public FaceDetect face;
    public Rect faceRect;
    public Bitmap filterBitmap;
    public Canvas filterCanvas;
    public boolean isFirstTime = true;
    public boolean isInitBase = false;
    public boolean isInitBlush = false;
    public boolean isInitBrighten = false;
    public boolean isInitDarkCircle;
    public boolean isInitEnlarge = false;
    public boolean isInitEyeColor = false;
    public boolean isInitEyeLash;
    public boolean isInitGoogles;
    public boolean isInitLip = false;
    public boolean isInitearRings;
    public Bitmap leftEyeBag;
    public Path leftEyeBagPath;
    public Rect leftMaskRect;
    public Rect leftRectBag;
    public Canvas lipCanvas;
    public Paint lipDrawPaint;
    public Path lipInPath;
    public Paint lipInnerPaint;
    public Bitmap lipMaskBitmap;
    public Path lipOutPath;
    public Paint lipOuterPaint;
    public int[] lipPixels;
    public Rect lipRect;
    public Matrix matrixBitmap;
    public List<Landmark> mouthInLandmarks;
    public List<Landmark> mouthOutLandmarks;
    public Paint paintBtm = new Paint();
    public int progress;
    public android.graphics.Point pupilLeft;
    public Paint pupilPaint;
    public android.graphics.Point pupilRight;
    public Bitmap rightEyeBag;
    public Path rightEyeBagPath;
    public Rect rightMaskRect;
    public Rect rightRectBag;
    public Bitmap savedSessionBitmap;
    public Bitmap sessionBitmap;
    public Canvas sessionCanvas;
    public List<Landmark> slimLeftLandmarks;
    public List<Landmark> slimRightLandmarks;
    public Canvas tmpCanvas = new Canvas();
    Path basePath;
    private Context context;
    private Matrix inverseMatrix;
    private Rect leftDrawRect;
    private Rect leftRectColor;
    private Paint mCirclePaint;
    private HashSet<CircleArea> mCircles;
    private HashSet<CircleArea> mLandmarks;
    private int magnifyArea;
    private RectF magnifyDstRect;
    private RectF magnifyRect;
    private List<Landmark> noseLandmarks;
    private MakeUpParams parameters = new MakeUpParams();
    private Rect rightDrawRect;
    private Rect rightRectColor;
    private Bitmap sourceBitmap;
    private Paint strokePaint;
    private Rect tmpRect;
    private File isfile;
    private String imagePath = "";

    public MakeUpView(Context context2, Bitmap bitmap) {
        super(context2);
        this.context = context2;
        this.sourceBitmap = bitmap;
        this.paintBtm.setFilterBitmap(true);
        this.BpaintPath = new Paint();
        this.BrectPaint = new Paint();
        Paint paint = new Paint();
        this.strokePaint = paint;
        paint.setAntiAlias(true);
        this.strokePaint.setStyle(Style.STROKE);
        this.strokePaint.setStrokeWidth(1.0f);
        this.strokePaint.setColor(ViewCompat.MEASURED_STATE_MASK);
        Paint paint2 = new Paint(1);
        this.pupilPaint = paint2;
        paint2.setColor(2147450704);
        this.pupilPaint.setStyle(Style.FILL);
        Paint paint3 = new Paint();
        this.mCirclePaint = paint3;
        paint3.setColor(-1);
        this.mCirclePaint.setStrokeWidth(3.0f);
        this.mCirclePaint.setStyle(Style.FILL);
        this.mCirclePaint.setAntiAlias(true);
        this.eyeLeftLandmarks = new ArrayList();
        this.eyeRightLandmarks = new ArrayList();
        this.noseLandmarks = new ArrayList();
        this.slimLeftLandmarks = new ArrayList();
        this.slimRightLandmarks = new ArrayList();
        this.mouthInLandmarks = new ArrayList();
        this.mouthOutLandmarks = new ArrayList();
        this.isInitEyeLash = false;
        this.isInitDarkCircle = false;
        this.filterBitmap = this.sourceBitmap.copy(Config.ARGB_8888, true);
        this.filterCanvas = new Canvas(this.filterBitmap);
        this.savedSessionBitmap = this.sourceBitmap.copy(Config.ARGB_8888, true);
        this.sessionBitmap = this.sourceBitmap.copy(Config.ARGB_8888, true);
        this.sessionCanvas = new Canvas(this.sessionBitmap);
    }

    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        setViewMatrix(i, i2);
    }

    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(this.filterBitmap, this.matrixBitmap, this.paintBtm);
        HashSet<CircleArea> hashSet = this.mLandmarks;
        if (hashSet != null) {
            Iterator it = hashSet.iterator();
            while (it.hasNext()) {
                CircleArea circleArea = (CircleArea) it.next();
                float[] fArr = {(float) circleArea.centerX, (float) circleArea.centerY};
                this.matrixBitmap.mapPoints(fArr);
                this.mCirclePaint.setColor(circleArea.paintColor);
                if (circleArea.f227id == -1) {
                    canvas.drawCircle(fArr[0], fArr[1], this.matrixBitmap.mapRadius((float) (this.eyeRadius / 2)), this.pupilPaint);
                    canvas.drawCircle(fArr[0], fArr[1], this.matrixBitmap.mapRadius((float) (this.eyeRadius / 10)), this.mCirclePaint);
                } else {
                    canvas.drawCircle(fArr[0], fArr[1], this.matrixBitmap.mapRadius((float) circleArea.radius), this.mCirclePaint);
                    canvas.drawCircle(fArr[0], fArr[1], this.matrixBitmap.mapRadius((float) circleArea.radius), this.strokePaint);
                }
            }
        }
        HashSet<CircleArea> hashSet2 = this.mCircles;
        if (hashSet2 != null) {
            Iterator it2 = hashSet2.iterator();
            while (it2.hasNext()) {
                CircleArea circleArea2 = (CircleArea) it2.next();
                drawMagnifiedRect(canvas, circleArea2.xPos, circleArea2.yPos);
            }
        }
    }

    public void DarkCirclesInit() {
        this.isInitDarkCircle = true;
        this.BrectPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        List<Landmark> list = this.eyeLeftLandmarks;
        if (list != null && list.size() != 0) {
            Path drawPathForDarkCircle = BeautyHelper.drawPathForDarkCircle(Landmark.generateExtendedLandmarksForDarkPath(this.eyeLeftLandmarks, 2, 20));
            this.leftEyeBagPath = drawPathForDarkCircle;
            this.leftRectBag = BeautyHelper.computeBounds(drawPathForDarkCircle, this.savedSessionBitmap);
            List<Landmark> list2 = this.eyeRightLandmarks;
            if (list2 != null && list2.size() != 0) {
                Path drawPathForDarkCircle2 = BeautyHelper.drawPathForDarkCircle(Landmark.generateExtendedLandmarksForDarkPath(this.eyeRightLandmarks, 2, 20));
                this.rightEyeBagPath = drawPathForDarkCircle2;
                this.rightRectBag = BeautyHelper.computeBounds(drawPathForDarkCircle2, this.savedSessionBitmap);
                this.BpaintPath.setColor(BeautyHelper.getAverageColor(this.savedSessionBitmap, this.leftRectBag));
                Bitmap createBitmap = Bitmap.createBitmap(this.leftRectBag.width(), this.leftRectBag.height(), Config.ARGB_8888);
                this.leftEyeBag = createBitmap;
                BeautyHelper.featherBitmap(this.tmpCanvas, this.leftEyeBagPath, this.leftRectBag, this.savedSessionBitmap, createBitmap, this.BpaintPath);
                this.BpaintPath.setColor(BeautyHelper.getAverageColor(this.savedSessionBitmap, this.rightRectBag));
                Bitmap createBitmap2 = Bitmap.createBitmap(this.rightRectBag.width(), this.rightRectBag.height(), Config.ARGB_8888);
                this.rightEyeBag = createBitmap2;
                BeautyHelper.featherBitmap(this.tmpCanvas, this.rightEyeBagPath, this.rightRectBag, this.savedSessionBitmap, createBitmap2, this.BpaintPath);
                this.leftEyeBagPath.offset((float) (-this.leftRectBag.left), (float) (-this.leftRectBag.top));
                this.rightEyeBagPath.offset((float) (-this.rightRectBag.left), (float) (-this.rightRectBag.top));
            }
        }
    }

    private void setViewMatrix(int i, int i2) {
        if (i > 0 && i2 > 0) {
            float width = (float) this.sourceBitmap.getWidth();
            float height = (float) this.sourceBitmap.getHeight();
            float f = (float) i;
            float f2 = (float) i2;
            float min = Math.min(f / width, f2 / height);
            Matrix matrix = new Matrix();
            this.matrixBitmap = matrix;
            matrix.reset();
            this.matrixBitmap.postScale(min, min);
            this.matrixBitmap.postTranslate((f - (width * min)) / 2.0f, (f2 - (height * min)) / 2.0f);
            invalidate();
        }
    }

    public void EyeColorInit() {
        List<Landmark> list = this.eyeLeftLandmarks;
        if (list != null && list.size() != 0) {
            List<Landmark> list2 = this.eyeRightLandmarks;
            if (list2 != null && list2.size() != 0) {
                this.eyeLeftPath = BeautyHelper.drawPath(this.eyeLeftLandmarks);
                this.eyeRightPath = BeautyHelper.drawPath(this.eyeRightLandmarks);
                this.leftRectColor = BeautyHelper.computeBounds(this.eyeLeftPath, this.savedSessionBitmap);
                this.rightRectColor = BeautyHelper.computeBounds(this.eyeRightPath, this.savedSessionBitmap);
                if (this.isFirstTime) {
                    this.isFirstTime = false;
                    this.eyeRadius = (int) (((float) this.leftRectColor.height()) * 1.5f);
                }
                if (this.pupilLeft == null && this.leftRectColor != null) {
                    this.pupilLeft = new android.graphics.Point(this.leftRectColor.centerX(), this.leftRectColor.centerY());
                }
                Rect rect = new Rect();
                this.leftMaskRect = rect;
                rect.left = this.leftRectColor.left > this.pupilLeft.x - (this.eyeRadius / 2) ? this.pupilLeft.x - (this.eyeRadius / 2) : this.leftRectColor.left;
                this.leftMaskRect.top = this.leftRectColor.top > this.pupilLeft.y - (this.eyeRadius / 2) ? this.pupilLeft.y - (this.eyeRadius / 2) : this.leftRectColor.top;
                this.leftMaskRect.right = this.leftRectColor.right > this.pupilLeft.x + (this.eyeRadius / 2) ? this.leftRectColor.right : this.pupilLeft.x + (this.eyeRadius / 2);
                this.leftMaskRect.bottom = this.leftRectColor.bottom > this.pupilLeft.y + (this.eyeRadius / 2) ? this.leftRectColor.bottom : this.pupilLeft.y + (this.eyeRadius / 2);
                if (this.pupilRight == null && this.rightRectColor != null) {
                    this.pupilRight = new android.graphics.Point(this.rightRectColor.centerX(), this.rightRectColor.centerY());
                }
                Rect rect2 = new Rect();
                this.rightMaskRect = rect2;
                rect2.left = this.rightRectColor.left > this.pupilRight.x - (this.eyeRadius / 2) ? this.pupilRight.x - (this.eyeRadius / 2) : this.rightRectColor.left;
                this.rightMaskRect.top = this.rightRectColor.top > this.pupilRight.y - (this.eyeRadius / 2) ? this.pupilRight.y - (this.eyeRadius / 2) : this.rightRectColor.top;
                this.rightMaskRect.right = this.rightRectColor.right > this.pupilRight.x + (this.eyeRadius / 2) ? this.rightRectColor.right : this.pupilRight.x + (this.eyeRadius / 2);
                this.rightMaskRect.bottom = this.rightRectColor.bottom > this.pupilRight.y + (this.eyeRadius / 2) ? this.rightRectColor.bottom : this.pupilRight.y + (this.eyeRadius / 2);
                Options options = new Options();
                options.inMutable = true;
                Bitmap bitmap = this.eyeLensMask;
                if (bitmap != null && !bitmap.isRecycled()) {
                    this.eyeLensMask.recycle();
                }
                Bitmap decodeResource = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.eyelensmask, options);
                this.eyeLensMask = decodeResource;
                int i = this.eyeRadius;
                Bitmap createScaledBitmap = Bitmap.createScaledBitmap(decodeResource, i, i, false);
                this.eyeLensMask = createScaledBitmap;
                int[] iArr = new int[(createScaledBitmap.getWidth() * this.eyeLensMask.getHeight())];
                this.eyeLensMaskPix = iArr;
                Bitmap bitmap2 = this.eyeLensMask;
                bitmap2.getPixels(iArr, 0, bitmap2.getWidth(), 0, 0, this.eyeLensMask.getWidth(), this.eyeLensMask.getHeight());
                if (this.eyeLens != null && !this.eyeLensMask.isRecycled()) {
                    this.eyeLens.recycle();
                }
                Bitmap decodeResource2 = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.eye_lens, options);
                this.eyeLens = decodeResource2;
                int i2 = this.eyeRadius;
                this.eyeLens = Bitmap.createScaledBitmap(decodeResource2, i2, i2, false);
                Bitmap bitmap3 = this.eyeColorLeftBp;
                if (bitmap3 != null && !bitmap3.isRecycled()) {
                    this.eyeColorLeftBp.recycle();
                }
                this.eyeColorLeftBp = Bitmap.createBitmap(this.leftMaskRect.width(), this.leftMaskRect.height(), Config.ARGB_8888);
                Bitmap bitmap4 = this.eyeColorRightBp;
                if (bitmap4 != null && !bitmap4.isRecycled()) {
                    this.eyeColorRightBp.recycle();
                }
                this.eyeColorRightBp = Bitmap.createBitmap(this.rightMaskRect.width(), this.rightMaskRect.height(), Config.ARGB_8888);
                this.isInitEyeColor = true;
            }
        }
    }

    public void BrightenEyesInit() {
        this.isInitBrighten = true;
        this.BrectPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        this.BpaintPath.setColor(-1);
        this.BpaintPath.setStyle(Style.FILL_AND_STROKE);
        this.BpaintPath.setAntiAlias(true);
        List<Landmark> list = this.eyeLeftLandmarks;
        if (list != null && list.size() != 0) {
            Path drawPath = BeautyHelper.drawPath(Landmark.generateExtendedLandmarks(this.eyeLeftLandmarks, 2, 20, false));
            this.eyeLeftPath = drawPath;
            Rect computeBoundsWithRadius = BeautyHelper.computeBoundsWithRadius(drawPath, this.savedSessionBitmap, 5);
            this.brightLeftRect = computeBoundsWithRadius;
            this.eyeLeftPath.offset((float) (-computeBoundsWithRadius.left), (float) (-this.brightLeftRect.top));
            List<Landmark> list2 = this.eyeRightLandmarks;
            if (list2 != null && list2.size() != 0) {
                Path drawPath2 = BeautyHelper.drawPath(Landmark.generateExtendedLandmarks(this.eyeRightLandmarks, 2, 20, false));
                this.eyeRightPath = drawPath2;
                Rect computeBoundsWithRadius2 = BeautyHelper.computeBoundsWithRadius(drawPath2, this.savedSessionBitmap, 5);
                this.brightRightRect = computeBoundsWithRadius2;
                this.eyeRightPath.offset((float) (-computeBoundsWithRadius2.left), (float) (-this.brightRightRect.top));
            }
        }
    }

    public void EnlargeEyesInit() {
        this.enlargeLeftRect = BeautyHelper.getEyeRectanglePositions(this.eyeLeftLandmarks, this.noseLandmarks, this.sourceBitmap);
        this.enlargeRightRect = BeautyHelper.getEyeRectanglePositions(this.eyeRightLandmarks, this.noseLandmarks, this.sourceBitmap);
        this.isInitEnlarge = true;
    }

    public void applyFilter() {
        this.filterCanvas.drawBitmap(this.sessionBitmap, 0.0f, 0.0f, null);
    }

    private void drawMagnifiedRect(Canvas canvas, int i, int i2) {
        int i3 = 0;
        if (this.leftDrawRect.contains(i, i2) && this.parameters.isDrawedLeft) {
            this.parameters.isDrawedLeft = false;
            this.parameters.isDrawedRight = true;
        } else if (this.rightDrawRect.contains(i, i2) && this.parameters.isDrawedRight) {
            this.parameters.isDrawedLeft = true;
            this.parameters.isDrawedRight = false;
        } else if (this.parameters.isDrawedLeft) {
            this.parameters.isDrawedLeft = true;
            this.parameters.isDrawedRight = false;
        } else if (this.parameters.isDrawedRight) {
            this.parameters.isDrawedLeft = false;
            this.parameters.isDrawedRight = true;
        } else {
            this.parameters.isDrawedLeft = true;
            this.parameters.isDrawedRight = false;
        }
        int i4 = this.magnifyArea;
        int i5 = i < i4 ? 0 : i - i4;
        int i6 = this.magnifyArea;
        if (i2 >= i6) {
            i3 = i2 - i6;
        }
        int i7 = this.magnifyArea;
        int i8 = (i7 * 2) + i5;
        int i9 = (i7 * 2) + i3;
        this.magnifyRect.left = (float) i5;
        this.magnifyRect.top = (float) i3;
        this.magnifyRect.right = (float) i8;
        this.magnifyRect.bottom = (float) i9;
        this.matrixBitmap.invert(this.inverseMatrix);
        this.inverseMatrix.mapRect(this.magnifyDstRect, this.magnifyRect);
        if (this.magnifyDstRect.left < 0.0f) {
            this.magnifyDstRect.right -= this.magnifyDstRect.left;
            this.magnifyDstRect.left = 0.0f;
        }
        if (this.magnifyDstRect.top < 0.0f) {
            this.magnifyDstRect.bottom -= this.magnifyDstRect.top;
            this.magnifyDstRect.top = 0.0f;
        }
        if (this.magnifyDstRect.right > ((float) this.sourceBitmap.getWidth())) {
            this.magnifyDstRect.left -= this.magnifyDstRect.right - ((float) this.sourceBitmap.getWidth());
            this.magnifyDstRect.right = (float) this.sourceBitmap.getWidth();
        }
        if (this.magnifyDstRect.bottom > ((float) this.sourceBitmap.getHeight())) {
            this.magnifyDstRect.top -= this.magnifyDstRect.bottom - ((float) this.sourceBitmap.getHeight());
            this.magnifyDstRect.bottom = (float) this.sourceBitmap.getHeight();
        }
        this.magnifyDstRect.round(this.tmpRect);
        if (this.parameters.isDrawedLeft) {
            canvas.drawBitmap(this.sessionBitmap, this.tmpRect, this.leftDrawRect, this.paintBtm);
        } else {
            canvas.drawBitmap(this.sessionBitmap, this.tmpRect, this.rightDrawRect, this.paintBtm);
        }
    }

   /* public String saveBitmap() {
        File storageDir = new File(Environment.getExternalStorageDirectory(), "Android/data/" + BuildConfig.APPLICATION_ID + "/PicShot/");
        storageDir.mkdirs();
        File image = null;
        try {
            image = new File(storageDir, getResources().getString(R.string.app_name));
            if (image.exists())
                image.delete();
            image.createNewFile();

            imagePath = image.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return String.valueOf(image);
    }
    */
    public String saveBitmap() {

        File file = null;
        String folder = Constants.TEMP_FOLDER_NAME;
        ContextWrapper cw = new ContextWrapper(context);
        File pictureFileDir = cw.getDir(folder, Context.MODE_PRIVATE);

        try {
            StringBuilder sb = new StringBuilder();
            sb.append(folder);
            sb.append(".jpg");
            imagePath = sb.toString();
            file = new File(pictureFileDir, imagePath);
            this.isfile = file;
            if (file.exists()) {
                this.isfile.delete();
                this.isfile.createNewFile();
            } else {
                this.isfile.createNewFile();
            }

            FileOutputStream oStream = new FileOutputStream(file);
            filterBitmap.compress(CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();


        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        scanGallery(context, pictureFileDir.getAbsolutePath());
        return String.valueOf(file);
    }

    private void scanGallery(Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                    // Toast.makeText(cntx, "Save Image Successfully..", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue scanning gallery.");
        }
    }


    public void LipInit() {
        List<Landmark> list = this.mouthOutLandmarks;
        if (list != null && list.size() != 0) {
            this.lipOutPath = BeautyHelper.drawPath(this.mouthOutLandmarks);
            List<Landmark> list2 = this.mouthInLandmarks;
            if (list2 != null && list2.size() != 0) {
                this.lipInPath = BeautyHelper.drawPath(this.mouthInLandmarks);
                for (int size = this.mouthOutLandmarks.size() - 2; size < this.mouthOutLandmarks.size(); size++) {
                    if (size >= 0) {
                        PointLip pointLip = new PointLip();
                        pointLip.f482x = ((Landmark) this.mouthOutLandmarks.get(size)).getPosition().x;
                        pointLip.f483y = ((Landmark) this.mouthOutLandmarks.get(size)).getPosition().y;
                        if (size == 0) {
                            PointLip pointLip2 = new PointLip();
                            int i = size + 1;
                            pointLip2.f482x = ((Landmark) this.mouthOutLandmarks.get(i)).getPosition().x;
                            pointLip2.f483y = ((Landmark) this.mouthOutLandmarks.get(i)).getPosition().y;
                            pointLip.f230dx = (pointLip2.f482x - pointLip.f482x) / 3.0f;
                            pointLip.f231dy = (pointLip2.f483y - pointLip.f483y) / 3.0f;
                        } else if (size == this.mouthOutLandmarks.size() - 1) {
                            PointLip pointLip3 = new PointLip();
                            int i2 = size - 1;
                            pointLip3.f482x = ((Landmark) this.mouthOutLandmarks.get(i2)).getPosition().x;
                            pointLip3.f483y = ((Landmark) this.mouthOutLandmarks.get(i2)).getPosition().y;
                            pointLip.f230dx = (pointLip.f482x - pointLip3.f482x) / 3.0f;
                            pointLip.f231dy = (pointLip.f483y - pointLip3.f483y) / 3.0f;
                        } else {
                            PointLip pointLip4 = new PointLip();
                            PointLip pointLip5 = new PointLip();
                            int i3 = size + 1;
                            pointLip4.f482x = ((Landmark) this.mouthOutLandmarks.get(i3)).getPosition().x;
                            pointLip4.f483y = ((Landmark) this.mouthOutLandmarks.get(i3)).getPosition().y;
                            int i4 = size - 1;
                            pointLip5.f482x = ((Landmark) this.mouthOutLandmarks.get(i4)).getPosition().x;
                            pointLip5.f483y = ((Landmark) this.mouthOutLandmarks.get(i4)).getPosition().y;
                            pointLip.f230dx = (pointLip4.f482x - pointLip5.f482x) / 3.0f;
                            pointLip.f231dy = (pointLip4.f483y - pointLip5.f483y) / 3.0f;
                        }
                    }
                }
                Integer valueOf = Integer.valueOf(1);
                for (int i5 = 0; i5 < this.mouthOutLandmarks.size(); i5++) {
                    PointLip pointLip6 = new PointLip();
                    pointLip6.f482x = ((Landmark) this.mouthOutLandmarks.get(i5)).getPosition().x;
                    pointLip6.f483y = ((Landmark) this.mouthOutLandmarks.get(i5)).getPosition().y;
                    if (valueOf != null) {
                        this.lipOutPath.moveTo(pointLip6.f482x, pointLip6.f483y);
                        valueOf = null;
                    } else {
                        PointLip pointLip7 = new PointLip();
                        int i6 = i5 - 1;
                        pointLip7.f482x = ((Landmark) this.mouthOutLandmarks.get(i6)).getPosition().x;
                        pointLip7.f483y = ((Landmark) this.mouthOutLandmarks.get(i6)).getPosition().y;
                        this.lipOutPath.cubicTo(pointLip7.f482x + pointLip7.f230dx, pointLip7.f483y + pointLip7.f231dy, pointLip6.f482x - pointLip6.f230dx, pointLip6.f483y - pointLip6.f231dy, pointLip6.f482x, pointLip6.f483y);
                    }
                }
                Rect computeBoundsWithRadius = BeautyHelper.computeBoundsWithRadius(this.lipOutPath, this.savedSessionBitmap, 5);
                this.lipRect = computeBoundsWithRadius;
                this.lipOutPath.offset((float) (-computeBoundsWithRadius.left), (float) (-this.lipRect.top));
                this.lipInPath.offset((float) (-this.lipRect.left), (float) (-this.lipRect.top));
                Paint paint = new Paint(1);
                this.lipOuterPaint = paint;
                paint.setStyle(Style.FILL_AND_STROKE);
                this.lipOuterPaint.setMaskFilter(new BlurMaskFilter(5.0f, Blur.NORMAL));
                Paint paint2 = new Paint(1);
                this.lipInnerPaint = paint2;
                paint2.setColor(0);
                this.lipInnerPaint.setStyle(Style.FILL_AND_STROKE);
                this.lipInnerPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
                this.lipInnerPaint.setMaskFilter(new BlurMaskFilter(2.0f, Blur.NORMAL));
                if (this.lipRect.width() * this.lipRect.height() > 0) {
                    int[] iArr = new int[(this.lipRect.width() * this.lipRect.height())];
                    this.lipPixels = iArr;
                    this.savedSessionBitmap.getPixels(iArr, 0, this.lipRect.width(), this.lipRect.left, this.lipRect.top, this.lipRect.width(), this.lipRect.height());
                    Bitmap bitmap = this.lipMaskBitmap;
                    if (bitmap != null && !bitmap.isRecycled()) {
                        this.lipMaskBitmap.recycle();
                    }
                    Bitmap createBitmap = Bitmap.createBitmap(this.lipRect.width(), this.lipRect.height(), Config.ARGB_8888);
                    this.lipMaskBitmap = createBitmap;
                    if (createBitmap != null && !createBitmap.isRecycled()) {
                        this.lipCanvas = new Canvas(this.lipMaskBitmap);
                        this.lipDrawPaint = new Paint();
                        this.isInitLip = true;
                    }
                }
            }
        }
    }

    public void baseInit() {
        Bitmap bitmap = this.baseBitmap;
        if (bitmap != null) {
            bitmap.recycle();
        }
        Rect rect = this.faceRect;
        if (rect != null && rect.width() != 0 && this.faceRect.height() != 0 && this.faceRect.width() * this.faceRect.height() > 0) {
            Bitmap createBitmap = Bitmap.createBitmap(this.faceRect.width(), this.faceRect.height(), Config.ARGB_8888);
            this.baseBitmap = createBitmap;
            if (createBitmap != null && !createBitmap.isRecycled()) {
                this.baseCanvas = new Canvas(this.baseBitmap);
                Path path = new Path();
                this.basePath = path;
                path.moveTo(((float) this.faceRect.width()) * 0.0f, ((float) this.faceRect.height()) * 0.0f);
                this.basePath.lineTo(((float) this.faceRect.width()) * 1.0f, ((float) this.faceRect.height()) * 0.0f);
                this.basePath.lineTo(((float) this.faceRect.width()) * 1.0f, ((float) this.faceRect.height()) * 1.0f);
                this.basePath.lineTo(((float) this.faceRect.width()) * 0.0f, ((float) this.faceRect.height()) * 1.0f);
                this.basePath.lineTo(((float) this.faceRect.width()) * 0.0f, ((float) this.faceRect.height()) * 0.0f);
                this.basePath.close();
                Paint paint = new Paint();
                this.basePaint = paint;
                paint.setMaskFilter(new BlurMaskFilter((float) (this.faceRect.width() / 6), Blur.NORMAL));
                if (this.basePix == null) {
                    this.basePix = new int[(this.faceRect.width() * this.faceRect.height())];
                }
                this.baseDrawPaint = new Paint();
                List<Landmark> list = this.eyeLeftLandmarks;
                if (list != null && list.size() != 0) {
                    this.baseEyePathLeft = BeautyHelper.drawPath(this.eyeLeftLandmarks);
                    List<Landmark> list2 = this.eyeRightLandmarks;
                    if (list2 != null && list2.size() != 0) {
                        this.baseEyePathRight = BeautyHelper.drawPath(this.eyeRightLandmarks);
                        List<Landmark> list3 = this.mouthOutLandmarks;
                        if (list3 != null && list3.size() != 0) {
                            this.baseMouthPath = BeautyHelper.drawPath(this.mouthOutLandmarks);
                            Path path2 = new Path();
                            this.border = path2;
                            path2.moveTo(0.0f, 0.0f);
                            this.border.lineTo((float) (this.faceRect.width() / 3), 0.0f);
                            this.border.lineTo(0.0f, (float) (this.faceRect.height() / 2));
                            this.border.lineTo(0.0f, 0.0f);
                            this.border.close();
                            Path path3 = new Path();
                            this.border1 = path3;
                            path3.moveTo(((float) this.faceRect.width()) * 0.66f, 0.0f);
                            this.border1.lineTo((float) this.faceRect.width(), 0.0f);
                            this.border1.lineTo((float) this.faceRect.width(), (float) (this.faceRect.height() / 2));
                            this.border1.lineTo(((float) this.faceRect.width()) * 0.66f, 0.0f);
                            this.border1.close();
                            Path path4 = new Path();
                            this.border2 = path4;
                            path4.moveTo((float) this.faceRect.width(), (float) this.faceRect.height());
                            this.border2.lineTo((float) (this.faceRect.width() / 2), (float) this.faceRect.height());
                            this.border2.lineTo((float) this.faceRect.width(), (float) (this.faceRect.height() / 2));
                            this.border2.lineTo((float) this.faceRect.width(), (float) this.faceRect.height());
                            this.border2.close();
                            Path path5 = new Path();
                            this.border3 = path5;
                            path5.moveTo(0.0f, (float) this.faceRect.height());
                            this.border3.lineTo((float) (this.faceRect.width() / 2), (float) this.faceRect.height());
                            this.border3.lineTo(0.0f, (float) (this.faceRect.height() / 2));
                            this.border3.lineTo(0.0f, (float) this.faceRect.height());
                            this.border3.close();
                            this.baseEyePathLeft.offset((float) (-this.faceRect.left), (float) (-this.faceRect.top));
                            this.baseEyePathRight.offset((float) (-this.faceRect.left), (float) (-this.faceRect.top));
                            this.baseMouthPath.offset((float) (-this.faceRect.left), (float) (-this.faceRect.top));
                            this.isInitBase = true;
                        }
                    }
                }
            }
        }
    }

    public void BlushInit(int i) {
        int i2 = i;
        Log.e("BlushInit", "===========()");
        FaceDetect face2 = this.face;
        int width = face2 != null ? face2.getFaceRect().width() / 12 : 50;
        List<Landmark> list = this.slimLeftLandmarks;
        if (list != null && list.size() != 0) {
            List<Landmark> list2 = this.slimRightLandmarks;
            if (list2 != null && list2.size() != 0) {
                ArrayList arrayList = new ArrayList();
                arrayList.addAll(this.slimLeftLandmarks);
                ArrayList arrayList2 = new ArrayList();
                arrayList2.addAll(this.slimRightLandmarks);
                if (arrayList.size() == 0) {
                    FaceDetect face3 = this.face;
                    if (!(face3 == null || face3.getLeftSlimLandmarks().size() == 0)) {
                        arrayList.addAll(this.face.getLeftSlimLandmarks());
                    }
                }
                if (arrayList.size() == 0) {
                    this.blushLeftPath = new Path();
                } else if (i2 == 3 || i2 == 4) {
                    this.blushLeftPath = BeautyHelper.drawPath(arrayList);
                } else if (i2 == 2) {
                    arrayList.remove(0);
                    this.blushLeftPath = BeautyHelper.drawPath(arrayList);
                } else if (i2 == 1) {
                    arrayList.remove(0);
                    arrayList.remove(arrayList.size() - 1);
                    this.blushLeftPath = BeautyHelper.drawPath(arrayList);
                }
                Rect computeBoundsWithRadius = BeautyHelper.computeBoundsWithRadius(this.blushLeftPath, this.savedSessionBitmap, width);
                this.blushLeftRect = computeBoundsWithRadius;
                this.blushLeftPath.offset((float) (-computeBoundsWithRadius.left), (float) (-this.blushLeftRect.top));
                Bitmap bitmap = this.blushLeftBitmap;
                if (bitmap != null) {
                    bitmap.recycle();
                }
                Bitmap createBitmap = Bitmap.createBitmap(this.blushLeftRect.width(), this.blushLeftRect.height(), Config.ARGB_8888);
                this.blushLeftBitmap = createBitmap;
                if (createBitmap != null && !createBitmap.isRecycled()) {
                    this.blushLeftCanvas = new Canvas(this.blushLeftBitmap);
                    Paint paint = new Paint();
                    this.blushPaint = paint;
                    paint.setMaskFilter(new BlurMaskFilter((float) width, Blur.NORMAL));
                    Matrix matrix = new Matrix();
                    if (arrayList.size() != 0) {
                        if (i2 == 4) {
                            matrix.setScale(0.45f, 0.65f, (((((Landmark) arrayList.get(3)).getPosition().x - ((Landmark) arrayList.get(1)).getPosition().x) / 8.0f) + ((Landmark) arrayList.get(1)).getPosition().x) - ((float) this.blushLeftRect.left), (((((Landmark) arrayList.get(1)).getPosition().y - ((Landmark) arrayList.get(3)).getPosition().y) / 4.0f) + ((Landmark) arrayList.get(3)).getPosition().y) - ((float) this.blushLeftRect.top));
                        } else if (i2 == 2) {
                            matrix.setScale(0.45f, 0.55f, (((((Landmark) arrayList.get(3)).getPosition().x - ((Landmark) arrayList.get(1)).getPosition().x) / 4.0f) + ((Landmark) arrayList.get(1)).getPosition().x) - ((float) this.blushLeftRect.left), (((((Landmark) arrayList.get(1)).getPosition().y - ((Landmark) arrayList.get(3)).getPosition().y) / 2.0f) + ((Landmark) arrayList.get(3)).getPosition().y) - ((float) this.blushLeftRect.top));
                        } else if (i2 == 1) {
                            matrix.setScale(0.65f, 0.5f, (((((Landmark) arrayList.get(2)).getPosition().x - ((Landmark) arrayList.get(0)).getPosition().x) / 2.0f) + ((Landmark) arrayList.get(0)).getPosition().x) - ((float) this.blushLeftRect.left), ((Landmark) arrayList.get(0)).getPosition().y - ((float) this.blushLeftRect.top));
                        } else if (i2 == 3) {
                            matrix.setScale(0.45f, 0.55f, (((((Landmark) arrayList.get(4)).getPosition().x - ((Landmark) arrayList.get(1)).getPosition().x) / 3.0f) + ((Landmark) arrayList.get(1)).getPosition().x) - ((float) this.blushLeftRect.left), ((Landmark) arrayList.get(1)).getPosition().y - ((float) this.blushLeftRect.top));
                        }
                    }
                    this.blushLeftPath.transform(matrix);
                    this.blushDrawPaint = new Paint();
                    this.blushLeftPix = new int[(this.blushLeftRect.width() * this.blushLeftRect.height())];
                    if (arrayList2.size() == 0) {
                        FaceDetect face4 = this.face;
                        if (!(face4 == null || face4.getRightSlimLandmarks().size() == 0)) {
                            arrayList2.addAll(this.face.getRightSlimLandmarks());
                        }
                    }
                    if (arrayList2.size() == 0) {
                        this.blushRightPath = new Path();
                    } else if (i2 == 3 || i2 == 4) {
                        this.blushRightPath = BeautyHelper.drawPath(arrayList2);
                    } else if (i2 == 2) {
                        arrayList2.remove(0);
                        this.blushRightPath = BeautyHelper.drawPath(arrayList2);
                    } else if (i2 == 1) {
                        arrayList2.remove(0);
                        arrayList2.remove(arrayList2.size() - 1);
                        this.blushRightPath = BeautyHelper.drawPath(arrayList2);
                    }
                    Rect computeBoundsWithRadius2 = BeautyHelper.computeBoundsWithRadius(this.blushRightPath, this.savedSessionBitmap, width);
                    this.blushRightRect = computeBoundsWithRadius2;
                    this.blushRightPath.offset((float) (-computeBoundsWithRadius2.left), (float) (-this.blushRightRect.top));
                    Bitmap createBitmap2 = Bitmap.createBitmap(this.blushRightRect.width(), this.blushRightRect.height(), Config.ARGB_8888);
                    this.blushRightBitmap = createBitmap2;
                    if (createBitmap2 != null && !createBitmap2.isRecycled()) {
                        this.blushRightCanvas = new Canvas(this.blushRightBitmap);
                        Matrix matrix2 = new Matrix();
                        if (arrayList2.size() != 0) {
                            if (i2 == 4) {
                                matrix2.setScale(0.45f, 0.65f, (((Landmark) arrayList2.get(1)).getPosition().x - ((((Landmark) arrayList2.get(1)).getPosition().x - ((Landmark) arrayList2.get(3)).getPosition().x) / 8.0f)) - ((float) this.blushRightRect.left), (((((Landmark) arrayList2.get(1)).getPosition().y - ((Landmark) arrayList2.get(3)).getPosition().y) / 4.0f) + ((Landmark) arrayList2.get(3)).getPosition().y) - ((float) this.blushRightRect.top));
                            } else if (i2 == 2) {
                                matrix2.setScale(0.45f, 0.55f, (((Landmark) arrayList2.get(1)).getPosition().x - ((((Landmark) arrayList2.get(1)).getPosition().x - ((Landmark) arrayList2.get(3)).getPosition().x) / 4.0f)) - ((float) this.blushRightRect.left), (((((Landmark) arrayList2.get(1)).getPosition().y - ((Landmark) arrayList2.get(3)).getPosition().y) / 2.0f) + ((Landmark) arrayList2.get(3)).getPosition().y) - ((float) this.blushRightRect.top));
                            } else if (i2 == 1) {
                                matrix2.setScale(0.65f, 0.5f, (((Landmark) arrayList2.get(0)).getPosition().x - ((((Landmark) arrayList2.get(0)).getPosition().x - ((Landmark) arrayList2.get(2)).getPosition().x) / 2.0f)) - ((float) this.blushRightRect.left), ((Landmark) arrayList2.get(0)).getPosition().y - ((float) this.blushRightRect.top));
                            } else if (i2 == 3) {
                                matrix2.setScale(0.45f, 0.55f, (((Landmark) arrayList2.get(1)).getPosition().x - ((((Landmark) arrayList2.get(1)).getPosition().x - ((Landmark) arrayList2.get(4)).getPosition().x) / 3.0f)) - ((float) this.blushRightRect.left), ((Landmark) arrayList2.get(1)).getPosition().y - ((float) this.blushRightRect.top));
                            }
                        }
                        this.blushRightPath.transform(matrix2);
                        this.blushRightPix = new int[(this.blushRightRect.width() * this.blushRightRect.height())];
                        this.isInitBlush = true;
                    }
                }
            }
        }
    }

    public enum BEAUTY_MODE {
        NONE,
        APPLY,
        CANCEL
    }

    private class CircleArea {
        int centerX;
        int centerY;


        int f227id;
        int paintColor;
        int radius;
        int xPos;
        int yPos;

        CircleArea(int i, int i2, int i3, int i4, int i5, int i6, int i7) {
            this.radius = i3;
            this.centerX = i;
            this.centerY = i2;
            this.xPos = i4;
            this.yPos = i5;
            this.paintColor = i6;
            this.f227id = i7;
        }
    }

    private class MakeUpParams {
        public List<Landmark> eyeLeftLandmarks;
        public List<Landmark> eyeRightLandmarks;
        public boolean isDrawedLeft;
        public boolean isDrawedRight;
        public android.graphics.Point leftEyePupil;
        public List<Landmark> mouthInLandmarks;
        public List<Landmark> mouthOutLandmarks;
        public int progressAutoAcne;
        public int progressBrighten;
        public int progressEnlarge;
        public int progressEyeBag;
        public int progressSlim;
        public int progressSmooth;
        public android.graphics.Point rightEyePupil;
        public int savedFilterIndex;

        private MakeUpParams() {
            this.eyeLeftLandmarks = new ArrayList();
            this.eyeRightLandmarks = new ArrayList();
            this.isDrawedLeft = false;
            this.isDrawedRight = false;
            this.leftEyePupil = new android.graphics.Point();
            this.mouthInLandmarks = new ArrayList();
            this.mouthOutLandmarks = new ArrayList();
            this.progressAutoAcne = 0;
            this.progressBrighten = 0;
            this.progressEnlarge = 0;
            this.progressEyeBag = 0;
            this.progressSlim = 0;
            this.progressSmooth = 0;
            this.rightEyePupil = new android.graphics.Point();
            this.savedFilterIndex = 0;
        }
    }

    public class Point {


        float f228x;


        float f229y;

        Point(float f, float f2) {
            this.f228x = f;
            this.f229y = f2;
        }
    }

    class PointLip {


        float f230dx;


        float f231dy;
        float f482x;
        float f483y;

        PointLip() {
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.f482x);
            sb.append(", ");
            sb.append(this.f483y);
            return sb.toString();
        }
    }
}
