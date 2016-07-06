package com.haloai.hud.hudendpoint.arwaylib.framedata.impl;

import android.graphics.AvoidXfermode;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.PointF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.amap.api.navi.model.AMapNaviLocation;
import com.haloai.hud.hudendpoint.arwaylib.calculator.result.RouteResult;
import com.haloai.hud.hudendpoint.arwaylib.calculator.result.SuperResult;
import com.haloai.hud.hudendpoint.arwaylib.framedata.SuperFrameData;
import com.haloai.hud.hudendpoint.arwaylib.utils.DrawUtils;
import com.haloai.hud.hudendpoint.arwaylib.utils.MathUtils;
import com.haloai.hud.utils.HaloLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * author       : 龙;
 * date         : 2016/5/5;
 * email        : helong@haloai.com;
 * package_name : com.haloai.hud.hudendpoint.arwaylib.framedata.impl;
 * project_name : hudlauncher;
 */
public class RouteFrameData extends SuperFrameData {
    private static final boolean ROUTE_FRAME_DEBUG = false;

    private static final int    X                     = 100;
    private static final int    Y                     = 100;
    private static final int    MAGNIFIED_TIME        = 4;
    //路线错乱的容忍值当点的Y坐标大于起始点的Y坐标+TOLERATE_VALUE,代表绘制该点可能会出现错乱的情况
    private static final int    TOLERATE_VALUE        = 70;
    //在一个点的左右两侧多少距离生成两个点与当前点组成一个贝塞尔曲线
    private static final double ADD_POINT_INTERVAL    = 100f;
    private static final String NOT_DRAW_TEXT_CONTENT = "正在检测行驶方向";//，请继续行驶...

    private static int ROAD_TURN_DRAFT_ANGLE = 15;

    private int   IMAGE_WIDTH                  = 0;
    private int   IMAGE_HEIGHT                 = 0;
    private int   OUTSIDE_LINE_WIDTH           = 0;
    private int   MIDDLE_LINE_WIDTH            = 0;
    private int   INSIDE_LINE_WIDTH            = 0;
    private float NOT_DRAW_TEXT_X              = 0;//50;
    private float NOT_DRAW_TEXT_Y              = 0;//270;
    private float NOT_DRAW_TEXT_SIZE           = 0;//50
    private float NOT_DRAW_SUB_TEXT_SIZE           = 0;//50
    private float CIRCLE_INTERVAL              = 0;//500f;
    private float CIRCLE_RADIUS                = 0;//15f;
    private int   NEXT_ROAD_X                  = 0;//500;
    private int   NEXT_ROAD_Y                  = 0;//500;
    private int   NEXT_ROAD_TEXT_OFFSET_HEIGHT = 0;//300;


    private List<PointF> mTempPoints             = new ArrayList<PointF>();
    private List<PointF> mOffsetPoints           = new ArrayList<PointF>();
    //此paint可以将Route中的黑色去掉变为透明
    private Paint        mPaintBitmapColorFilter = new Paint();
    private TextPaint    mTextPaint              = new TextPaint();

    private static RouteFrameData   mRouteFrameData   = new RouteFrameData();
    private        List<Point>      mLastPoints       = new ArrayList<Point>();
    private        int              mLastDrawIndex    = 0;
    private        double           mLastOffsetHeight = 0;
    private        double           mLastOffsetDistance = 0;
    private        AMapNaviLocation mPrePreLocation   = null;
    private        float            mLastDistance     = 0f;
    private        AMapNaviLocation mFakeLocation     = null;
    private        Picture          mPictureOne       = new Picture();
    private        Picture          mPictureTwo       = new Picture();
    private        boolean          mChooseOne        = true;

    private RouteFrameData() {
        setPosition(X, Y);

        this.mPaintBitmapColorFilter.setDither(true);
        this.mPaintBitmapColorFilter.setFilterBitmap(true);
        this.mPaintBitmapColorFilter.setARGB(0, 0, 0, 0);
        this.mPaintBitmapColorFilter.setXfermode(new AvoidXfermode(0x000000, 10, AvoidXfermode.Mode.TARGET));

    }

    @Override
    public void reset() {
        super.reset();
        mTempPoints.clear();
        mOffsetPoints.clear();
        mLastDrawIndex = 0;
        mLastOffsetHeight = 0;
        mLastOffsetDistance = 0;
        mPrePreLocation = null;
        mLastDistance = 0f;
        mFakeLocation = null;
        mChooseOne = true;
    }

    public static RouteFrameData getInstance() {
        return mRouteFrameData;
    }

    @Override
    public void animOver() {
        this.mLastDistance = 0f;
        this.mFakeLocation = null;
        this.mLastDrawIndex = 0;
        this.mLastOffsetHeight = 0;
        this.mChooseOne = true;
    }

    public void initDrawLine(int bitmap_width, int bitmap_height) {
        this.IMAGE_WIDTH = MathUtils.formatAsEvenNumber(bitmap_width);
        this.IMAGE_HEIGHT = MathUtils.formatAsEvenNumber(bitmap_height);
        this.OUTSIDE_LINE_WIDTH = MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_WIDTH * 0.437f));
        this.MIDDLE_LINE_WIDTH = MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_WIDTH * 0.375f));
        this.OUTSIDE_LINE_WIDTH = MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_WIDTH * 0.237f));
        this.MIDDLE_LINE_WIDTH = MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_WIDTH * 0.185f));
        this.INSIDE_LINE_WIDTH = MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_WIDTH * 0.353f));
//        this.NOT_DRAW_TEXT_X = MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_WIDTH * 0.102f)); //helong
        this.NOT_DRAW_TEXT_X = MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_WIDTH * 0.522f));
        this.NOT_DRAW_TEXT_Y = MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_HEIGHT * 0.574f));
        this.NOT_DRAW_TEXT_SIZE = MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_WIDTH * .0550f));
        this.NOT_DRAW_SUB_TEXT_SIZE = MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_WIDTH * .0475f));
        this.CIRCLE_INTERVAL = MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_WIDTH * 1.064f));
        this.CIRCLE_RADIUS = MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_WIDTH * 0.022f));
        this.CIRCLE_RADIUS = MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_WIDTH * 0.012f));
        this.NEXT_ROAD_X = MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_WIDTH * 1.064f));
        this.NEXT_ROAD_Y = MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_WIDTH * 1.064f));
        this.NEXT_ROAD_TEXT_OFFSET_HEIGHT = MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_WIDTH * 0.438f));
    }

    public void update(SuperResult result) throws Exception{
        RouteResult routeResult = null;
        if (result instanceof RouteResult) {
            routeResult = (RouteResult)result;
        }else {
            throw new Exception("SuperResult 的实例类型不可用");
        }
        long performanceLogTime;
        performanceLogTime = System.currentTimeMillis();
        Picture picture = mChooseOne ? this.mPictureOne : this.mPictureTwo;
        Canvas canvas = picture.beginRecording(IMAGE_WIDTH, IMAGE_HEIGHT);
        if (routeResult.mCanDraw) {
            //          this.mImage = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);
            //          Canvas canvas = new Canvas(this.mImage);
            this.mPaint.reset();
            mPaint.setColor(Color.BLACK);
            canvas.drawPaint(mPaint);
            //if the location point may be a error point ,do not to draw path and to draw text to warning user.
            //显示指示性文字
            {
                PointF titlePos = new PointF(NOT_DRAW_TEXT_X, MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_HEIGHT * 0.374f)));
                boolean drawSub = false;
                String title = null;
                String text = null;
                if(routeResult.mNaviEnd){
                    HaloLogger.logE("sen_debug_arway"," call route update :本次导航结束");
                    title = "本次导航结束";
                }else if(routeResult.mIsYaw){
                    HaloLogger.logE("sen_debug_arway"," call route update :正在偏航");
                    title = "重新计算偏航路径";
                }else if (routeResult.mMayBeErrorLocation){
                    HaloLogger.logE("sen_debug_arway"," call update :行驶未超出50米");
                    title = NOT_DRAW_TEXT_CONTENT;
                    drawSub = true;
                    if((!ROUTE_FRAME_DEBUG) && routeResult.mGpsNumber<1){
                        HaloLogger.logE("sen_debug_arway"," call update :GPS 信号弱，请开往空旷处");
                        title = "GPS 信号弱";
                    }
                }if (!routeResult.mIsMatchNaviPath) {
                    HaloLogger.logE("sen_debug_error", "route update ：location 不在path 上");
                    title = NOT_DRAW_TEXT_CONTENT;
                } else if (routeResult.mCurrentLatLngs == null || routeResult.mCurrentLatLngs.size() <= 1 || routeResult.mCurrentLocation == null
                        || routeResult.mProjection == null || routeResult.mCurrentPoints == null) {
                    HaloLogger.logE("sen_debug_error", "route update ：绘制条件不足");
                    title = NOT_DRAW_TEXT_CONTENT;
                }
                if (title != null) {
                    mTextPaint.setTextSize(NOT_DRAW_TEXT_SIZE);
                    mTextPaint.setColor(Color.RED);
                    canvas.drawText(title, titlePos.x, titlePos.y, mTextPaint);
                    if (drawSub && routeResult.mNaviText != null) {
                        if (routeResult.mNaviText != null){
                            text = " "+routeResult.mNaviText;
                        }
                        mTextPaint.setAntiAlias(true);
                        mTextPaint.setColor(Color.RED);
                        mTextPaint.setTextSize(NOT_DRAW_SUB_TEXT_SIZE);
                        StaticLayout layout = new StaticLayout(text,mTextPaint,(int)(IMAGE_WIDTH/2-NOT_DRAW_SUB_TEXT_SIZE), Layout.Alignment.ALIGN_NORMAL, (float) 1.0,(float) 0.0, false);
                        canvas.translate(NOT_DRAW_TEXT_X, MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_HEIGHT * 0.474f)));
                        layout.draw(canvas);
                    }
                    picture.endRecording();
                    return;
                }

            }
            /*if(routeResult.mNaviEnd){
                HaloLogger.logE("sen_debug_arway"," call route update :本次导航结束");
                mTextPaint.setTextSize(NOT_DRAW_TEXT_SIZE);
                mTextPaint.setColor(Color.RED);
                canvas.drawText("本次导航结束", MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_WIDTH * 0.522f)), NOT_DRAW_TEXT_Y, mTextPaint);
                picture.endRecording();
                return;
            }else if(routeResult.mIsYaw){
                HaloLogger.logE("sen_debug_arway"," call route update :正在偏航");
                mTextPaint.setTextSize(NOT_DRAW_TEXT_SIZE);
                mTextPaint.setColor(Color.RED);
                canvas.drawText("重新计算偏航路径", MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_WIDTH * 0.522f)), NOT_DRAW_TEXT_Y, mTextPaint);
                picture.endRecording();
                return;
            }
            if (routeResult.mMayBeErrorLocation) {
                mTextPaint.setTextSize(NOT_DRAW_TEXT_SIZE);
                mTextPaint.setAntiAlias(true);
                mTextPaint.setColor(Color.RED);
                if(true){//语音内容
                    if (routeResult.mNaviText != null){
                        String text = " "+routeResult.mNaviText;
                        mTextPaint.setTextSize(NOT_DRAW_TEXT_SIZE);
                        canvas.drawText(NOT_DRAW_TEXT_CONTENT, NOT_DRAW_TEXT_X, MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_HEIGHT * 0.374f)), mTextPaint);

                        mTextPaint.setTextSize(NOT_DRAW_SUB_TEXT_SIZE);
                        StaticLayout layout = new StaticLayout(text,mTextPaint,(int)(IMAGE_WIDTH/2-NOT_DRAW_SUB_TEXT_SIZE), Layout.Alignment.ALIGN_NORMAL, (float) 1.0,(float) 0.0, false);
                        canvas.translate(NOT_DRAW_TEXT_X, MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_HEIGHT * 0.474f)));
                        layout.draw(canvas);
//                        canvas.drawText(routeResult.mNaviText, NOT_DRAW_TEXT_X, MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_HEIGHT * 0.674f)), mTextPaint);
                    }else {
                        canvas.drawText(NOT_DRAW_TEXT_CONTENT, NOT_DRAW_TEXT_X, NOT_DRAW_TEXT_Y, mTextPaint);
                    }


                }else {//下一条路名
                    if (routeResult.mNextRoadName != null  && (!routeResult.mNextRoadName.equals("")) && (!routeResult.mNextRoadName.contains("无名道路"))){
                        mTextPaint.setTextSize(NOT_DRAW_TEXT_SIZE);
                        canvas.drawText("请行驶到"+routeResult.mNextRoadName, NOT_DRAW_TEXT_X, MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_HEIGHT * 0.374f)), mTextPaint);
                        mTextPaint.setTextSize(NOT_DRAW_TEXT_SIZE);
                        canvas.drawText(NOT_DRAW_TEXT_CONTENT, NOT_DRAW_TEXT_X, MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_HEIGHT * 0.674f)), mTextPaint);
                    }else {
                        canvas.drawText(NOT_DRAW_TEXT_CONTENT, NOT_DRAW_TEXT_X, NOT_DRAW_TEXT_Y, mTextPaint);
                    }

                }


                picture.endRecording();
                return;
            }else if((!ROUTE_FRAME_DEBUG) && routeResult.mGpsNumber<3){
                HaloLogger.logE("sen_debug_arway"," call update :GPS 信号弱，请开往空旷处");
                mTextPaint.setTextSize(NOT_DRAW_TEXT_SIZE);
                mTextPaint.setColor(Color.RED);
                canvas.drawText("GPS 信号弱", NOT_DRAW_TEXT_X, NOT_DRAW_TEXT_Y, mTextPaint);
                picture.endRecording();
                return;
            }else if (!routeResult.mIsMatchNaviPath){//(routeResult.mCurrentLocation != null && (!routeResult.mCurrentLocation.isMatchNaviPath())
                HaloLogger.logE("sen_debug_arway"," call route update :定位点不在规划路径上");
                mTextPaint.setTextSize(NOT_DRAW_TEXT_SIZE);
                mTextPaint.setColor(Color.RED);
                canvas.drawText("正在检测行驶方向", MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_WIDTH * 0.522f)), NOT_DRAW_TEXT_Y, mTextPaint);
                picture.endRecording();
                return;
            }

            //形状点为空时的防护处理
            if (routeResult.mCurrentLatLngs == null || routeResult.mCurrentLatLngs.size() <= 1 || routeResult.mCurrentLocation == null
                    || routeResult.mProjection ==null || routeResult.mCurrentPoints==null) {
                HaloLogger.logE("sen_debug_error","route update ：绘制条件不足");
                mTextPaint.setTextSize(NOT_DRAW_TEXT_SIZE);
                mTextPaint.setColor(Color.RED);
                canvas.drawText("重新计算偏航路径", MathUtils.formatAsEvenNumber(Math.round(this.IMAGE_WIDTH * 0.522f)), NOT_DRAW_TEXT_Y, mTextPaint);
                // FIXME: 16/6/30 先不切换PICTURE
//                this.mChooseOne = !this.mChooseOne;
                picture.endRecording();
                return;
            }*/

            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setAntiAlias(true);
            mPaint.setStrokeJoin(Paint.Join.ROUND);


            //mFakerPointX 已经经过二次转换
            PointF fakePoint = new PointF((float)routeResult.mFakerPointX,(float)routeResult.mFakerPointY);
            PointF originPoint0 = routeResult.mCurrentPoints.get(0);
            PointF originRef = originPoint0;
            PointF originPoint1 = routeResult.mCurrentPoints.get(1);

            double k_fakey = (fakePoint.y-originPoint1.y)*1.0/(originPoint0.y-originPoint1.y);
            double originFakeX = originPoint1.x+(originPoint0.x-originPoint1.x)*k_fakey;
            PointF originfakePoint = new PointF((float)originFakeX,fakePoint.y);

            double driftAngle = MathUtils.pointDegree(originPoint0,originPoint1);
            double rotateRadian = (driftAngle-270)*Math.PI/180;
            PointF rotatePoint1 = MathUtils.pointRotate(originPoint1,originRef,rotateRadian);
            PointF rotateFakePoint = MathUtils.pointRotate(originfakePoint,originRef,rotateRadian);


            double tOffsetDistance = 0;
            double offsetHeight = routeResult.mFakerPointY -originPoint0.y;
            double offsetWidth = routeResult.mFakerPointX - originPoint0.x;

            HaloLogger.logE("route_log_info", "1-0 driftAngle: "+driftAngle+",rotateRadian: "+rotateRadian+",originPoint0:"+originPoint0
                    +",rotatePoint1:"+rotatePoint1+",rotateFakePoint:"+rotateFakePoint+",originfakePoint:"+originfakePoint);

            // FIXME: 16/6/15 排除计算间隔，造成地图旋转mProjection转换误差
            HaloLogger.logI("route_log_info_test_performance","=========performance_log=========== update frame time = "+ (System.currentTimeMillis()-performanceLogTime));
            if (false){//经过摆正
                offsetHeight = rotateFakePoint.y - originPoint0.y;
                offsetWidth = rotateFakePoint.x -originPoint0.x;
                tOffsetDistance = offsetHeight*offsetHeight+offsetWidth*offsetWidth;
                HaloLogger.logE("route_log_info", " real faker : " + routeResult.mFakeLocation.getCoord().getLatitude() + "," + routeResult.mFakeLocation.getCoord().getLongitude()
                        + ",  fake point:"+fakePoint);
            }else {//helong 直接处理
                offsetHeight = fakePoint.y - originPoint0.y;
                offsetWidth = fakePoint.x -originPoint0.x;
            }
            double tempOffsetHeight = originPoint1.y - originPoint0.y;


            HaloLogger.logE("route_log_info","1-0:"+tempOffsetHeight+" , f-0:"+offsetHeight);
            if (tempOffsetHeight > offsetHeight) {
                HaloLogger.logE("route_log_info", "range error");
                offsetHeight = tempOffsetHeight;
            } else {
                HaloLogger.logE("route_log_info", "normal error");
            }
            // FIXME: 16/6/15 给回退加一个阀值
            //靠上的位置Y轴小
            if ((driftAngle<=(270+ROAD_TURN_DRAFT_ANGLE) && driftAngle>=(270-ROAD_TURN_DRAFT_ANGLE)) && this.mLastOffsetHeight - offsetHeight < 0 && this.mLastDrawIndex == routeResult.mDrawIndex) {
//            if ((this.mLastOffsetDistance - tOffsetDistance < 0) && (this.mLastOffsetHeight - offsetHeight < 0 ) && this.mLastDrawIndex == routeResult.mDrawIndex) {
                HaloLogger.logE("route_log_info_test__error", "????????? error : offset_height back off!!, error offsetHeight : " + offsetHeight+",last offsetHeight :"+this.mLastOffsetHeight
                        +",  1-0:"+tempOffsetHeight+",  currentIndex:" + routeResult.mCurrentIndex+"   ,darwIndex:" + routeResult.mDrawIndex+", FakeOver :"+routeResult.mFakeOver);
                offsetHeight = this.mLastOffsetHeight;
            } else {
                this.mLastOffsetDistance = tOffsetDistance;
                this.mLastOffsetHeight = offsetHeight;
                this.mLastDrawIndex = routeResult.mDrawIndex;
            }

            this.mTempPoints.clear();
            for (int i = 0; i < routeResult.mCurrentPoints.size(); i++) {
//                PointF point = new PointF(routeResult.mProjection.toScreenLocation(DrawUtils.naviLatLng2LatLng(routeResult.mCurrentLatLngs.get(i))));
                PointF pointF = routeResult.mCurrentPoints.get(i);
                PointF point = new PointF(pointF.x,pointF.y);
                if (i != 0) {// FIXME: 16/6/20  给后续点设定一个下移极限值
                    point.x -= offsetWidth;
                    point.y -= offsetHeight;
                }
                this.mTempPoints.add(point);
            }

            /*HaloLogger.logE("route_log_info", "currentIndex:" + routeResult.mCurrentIndex);
            HaloLogger.logE("route_log_info", "darwIndex:" + routeResult.mDrawIndex);
            HaloLogger.logE("route_log_info", "offset_height:" + offsetHeight);
            HaloLogger.logE("route_log_info", "points size : " + mTempPoints.size() + ",points:" + mTempPoints + "");*/

            HaloLogger.logE("route_log_info", "currentIndex:" + routeResult.mCurrentIndex);
            HaloLogger.logE("route_log_info", "darwIndex:" + routeResult.mDrawIndex);
            HaloLogger.logE("route_log_info", "offset_height:" + offsetHeight);
            HaloLogger.logE("route_log_info", "offsetWidth:" + offsetWidth);
            HaloLogger.logE("route_log_info", "points size : " + mTempPoints.size() + ",points:" + mTempPoints + "");
            if(mTempPoints != null &&mTempPoints.size()>=2 && mTempPoints.get(1).y>mTempPoints.get(0).y){
                HaloLogger.logE("route_log_info", "points path black");
            }
            HaloLogger.logE("route_log_info", "==============================================***************   end   end  end ***************===========================================================================================================");

            /**
             * 根据fakerLocation计算出应该移动多少米,就向下一个形状点移动多少米:
             * 1.如何计算出应该移动多少米:
             * 使用高德utils得到当前faker与上一个形状点的距离即可;
             * 2.怎么移动,不再是单一的从一个点移动到另一个点,而是应该有策略的:
             *      TODO 这种情况是不应该出现的,但是可能出现.此处暂时先做简单处理.
             *      1.如果移动的米数大于等于当前形状点到下一个形状点的距离,说明faker已经超过了下一个形状点,那暂且移动到下一个形状点即可;
             *      2.如果移动的米数小于当前形状点到下一个形状点的距离,那么计算移动到线上的哪个位置,也就是说应该移动的x和y方向的量为多少,计算出来后,移动整个path;
             */

            //            float start2FakerDist = AMapUtils.calculateLineDistance(
            //                    DrawUtils.naviLatLng2LatLng(routeResult.mCurrentLatLngs.get(0)),
            //                    DrawUtils.naviLatLng2LatLng(routeResult.mFakeLocation.getCoord()));
            //            float start2NextDist = AMapUtils.calculateLineDistance(
            //                    DrawUtils.naviLatLng2LatLng(routeResult.mCurrentLatLngs.get(0)),
            //                    DrawUtils.naviLatLng2LatLng(routeResult.mCurrentLatLngs.get(1)));
            //            if (start2FakerDist >= start2NextDist) {
            //                mTempPoints.remove(0);
            //            } else {
            //                double offsetHeight = routeResult.mCurrentPoints.get(0).y - routeResult.mFakerPointY;
            //                if (mLastDrawIndex != routeResult.mLastDrawIndex) {
            //                    mLastDrawIndex = routeResult.mLastDrawIndex;
            //                    mLastOffsetHeight = 0f;
            //                }
            //                if (offsetHeight >= mLastOffsetHeight) {
            //                    mLastOffsetHeight = offsetHeight;
            //                }
            //                for (int i = 1; i < routeResult.mCurrentPoints.size(); i++) {
            //                    routeResult.mCurrentPoints.get(i).y += mLastOffsetHeight;
            //                }
            ////                for (int i = 1; i < routeResult.mCurrentPoints.size(); i++) {
            ////                    routeResult.mCurrentPoints.get(i).y += offsetHeight;
            ////                }
            //            }

            // move to screen center
            float offsetX = this.IMAGE_WIDTH / 2 - this.mTempPoints.get(0).x+(int)(this.IMAGE_WIDTH/4.5);
            float offsetY = this.IMAGE_HEIGHT - this.mTempPoints.get(0).y;
            for (int i = 0; i < this.mTempPoints.size(); i++) {
                PointF temp_point = this.mTempPoints.get(i);
                temp_point.x += offsetX;
                temp_point.y += offsetY;
            }

            // get offset with current and next point.
            mOffsetPoints.clear();
            for (int i = 0; i < this.mTempPoints.size() - 1; i++) {
                PointF offset_point = new PointF();
                offset_point.x = this.mTempPoints.get(i + 1).x - this.mTempPoints.get(i).x;
                offset_point.y = this.mTempPoints.get(i + 1).y - this.mTempPoints.get(i).y;
                mOffsetPoints.add(offset_point);
            }

            // Magnified N times
            // start point is constant，another points will change
            for (int i = 1; i < this.mTempPoints.size(); i++) {
                PointF temp_point = this.mTempPoints.get(i);
                PointF pre_point = this.mTempPoints.get(i - 1);
                PointF offset_point = mOffsetPoints.get(i - 1);
                temp_point.x = pre_point.x + offset_point.x * MAGNIFIED_TIME;
                temp_point.y = pre_point.y + offset_point.y * MAGNIFIED_TIME;
            }
            HaloLogger.logE("sen_gl","scale points is "+mTempPoints);
            //remove the point in list if it may be error to draw
            PointF first_point = this.mTempPoints.get(0);
            for (int i = 1; i < this.mTempPoints.size(); i++) {
                PointF tempPoint = this.mTempPoints.get(i);
                if (tempPoint.y > first_point.y + TOLERATE_VALUE) {
                    for (int j = i; j < this.mTempPoints.size(); j++) {
                        this.mTempPoints.remove(j);
                        j--;
                    }
                    if (this.mTempPoints.size() <= 1) {
                        picture.endRecording();
                        this.mChooseOne = !this.mChooseOne;
                        return;
                    }
                    i--;
                    PointF lastPoint = this.mTempPoints.get(i - 1);
                    PointF makePoint = new PointF();
                    makePoint.y = first_point.y + 20;
                    makePoint.x = (float) ((1.0 * makePoint.y - lastPoint.y) / (tempPoint.y - lastPoint.y) * (tempPoint.x - lastPoint.x) + lastPoint.x);
                    this.mTempPoints.add(makePoint);
                    break;
                }
            }

            if (mTempPoints.size() <= 1) {
                HaloLogger.logE("route_log_info___", "error return : mTempPoints.size() <= 1 , \n" + mTempPoints);
                picture.endRecording();
                this.mChooseOne = !this.mChooseOne;
                return;
            }

            //            //如果第二个点的横向移动幅度很小,则判断为抖动情况,则不予移动.
            //            if (mLastPoints == null || mLastPoints.size() <= 0) {
            //                mLastPoints.addAll(mTempPoints);
            //            } else {
            //                for (int i = 1; i < mLastPoints.size() && i < mTempPoints.size(); i++) {
            //                    Point last_point = mLastPoints.get(i);
            //                    Point temp_point = mTempPoints.get(i);
            //                    if (Math.abs(last_point.x - temp_point.x) < 15) {
            //                        temp_point.x = last_point.x;
            //                    } else {
            //                        last_point.x = temp_point.x;
            //                    }
            //                }
            //            }
            //TODO log points
            if (this.mPrePreLocation == null) {
                //HaloLogger.logE("route_log_info", "=================================");
                this.mPrePreLocation = routeResult.mPreLocation;
            } else if (this.mPrePreLocation != routeResult.mPreLocation) {
                //HaloLogger.logE("route_log_info", "=================================");
                this.mPrePreLocation = routeResult.mPreLocation;
            }

            // here we must be 3D turn around first ,and rotate the path second(Now we do not to rotate);
            // first:3D turn around and set matrix;
            first_point = mTempPoints.get(0);
            DrawUtils.setRotateMatrix4Canvas(first_point.x, first_point.y, -100f, 60f, canvas);

            /*// second:Calculate degrees and rotate path with it.
            float degrees = 0f;
            // if the line is vertical
            first_point = mTempPoints.get(0);
            Point second_point = mTempPoints.get(1);
            if (second_point.x == first_point.x) {
                if (second_point.y <= first_point.x) {
                    degrees = 0;
                } else {
                    degrees = 180;
                }
                // if the line is horizontal
            } else if (second_point.y == first_point.y) {
                if (second_point.x <= first_point.x) {
                    degrees = 90;
                } else {
                    degrees = 270;
                }
            } else {
                // if the line is not a vertical or horizontal,we should be to
                // calculate the degrees
                // cosA = (c*c + b*b - a*a)/(2*b*c)
                // A = acos(A)/2/PI*360
                double c = Math.sqrt(Math.pow(Math.abs(first_point.x - second_point.x),
                                              2.0) + Math.pow(Math.abs(first_point.y - second_point.y), 2.0));
                double b = Math.abs(first_point.y - second_point.y);
                double a = Math.abs(first_point.x - second_point.x);
                degrees = (float) (Math.acos((c * c + b * b - a * a) / (2 * b * c))
                        / 2 / Math.PI * 360);
                if (second_point.x >= first_point.x && second_point.y >= first_point.y) {
                    degrees += 180;
                } else if (second_point.x <= first_point.x && second_point.y >= first_point.y) {
                    degrees = (90 - degrees) + 90;
                } else if (second_point.x <= first_point.x && second_point.y <= first_point.y) {
                    degrees += 0;
                } else if (second_point.x >= first_point.x && second_point.y <= first_point.y) {

                    degrees = 270 + (90 - degrees);
                }
            }

            //rotate the path because the fakeLocation is created by our caculate , so it may be has a little error.
            canvas.rotate(degrees, first_point.x, first_point.y);*/

            // draw the path(outside,inside,circle)
            Path basePath = new Path();
            basePath.moveTo(first_point.x, first_point.y);
            Point test_point = null;
            for (int i = 1; i < mTempPoints.size(); i++) {
                //例如刚开始时从p1,到p2,到p3
                PointF temp_point = mTempPoints.get(i);
                //move
                //                temp_point.y -= offsetHeight;
                /*if(temp_point.y >= first_point.y + TOLERATE_VALUE){
                    break;
                }
                if (i != mTempPoints.size() - 1) {
                    //不是最后一个点,创建该点左右两个点作为lineTo点,当前点作为queato的控制点
                    //TODO 关键在于以下两个点如何获取,通过当前点和其前一个后一个三个点
                    Point pre_point = null;
                    if(test_point!=null){
                        pre_point = test_point;
                        test_point = null;
                    }else {
                        if (i != 1) {
                            pre_point = mTempPoints.get(i - 1);
                        } else {
                            pre_point = first_point;
                        }
                    }
                    Point next_point = mTempPoints.get(i + 1);
                    Point left_point = new Point();
                    Point right_point = new Point();
                    double pre_temp_distance = MathUtils.calculateDistance(pre_point, temp_point);
                    if (pre_temp_distance <= ADD_POINT_INTERVAL) {
                        left_point = pre_point;
                    } else {
                        double div = ADD_POINT_INTERVAL / pre_temp_distance;
                        left_point.x = (int) (temp_point.x + div * (pre_point.x - temp_point.x));
                        left_point.y = (int) (temp_point.y + div * (pre_point.y - temp_point.y));
                    }
                    double next_temp_distance = MathUtils.calculateDistance(next_point, temp_point);
                    if (next_temp_distance <= ADD_POINT_INTERVAL) {
                        right_point = next_point;
                    } else {
                        double div = ADD_POINT_INTERVAL / next_temp_distance;
                        right_point.x = (int) (temp_point.x + div * (next_point.x - temp_point.x));
                        right_point.y = (int) (temp_point.y + div * (next_point.y - temp_point.y));
                    }
                    basePath.lineTo(left_point.x, left_point.y);
                    basePath.quadTo(temp_point.x, temp_point.y, right_point.x, right_point.y);
                    //right_point == next_point or right_point != next_point
                    if(right_point == next_point){
                        //如果当前贝塞尔曲线的尾点就是下一个点,那么直接跳过下一个点即可.
                        i++;
                    }else{
                        //如果当前贝塞尔曲线的尾点还没到下一个点的位置,此时下一个点的贝塞尔绘制不能简单的执行
                        //而是受限于right点.
                        test_point = right_point;
                    }
                } else {
                    //到达最后一个点
                    basePath.lineTo(temp_point.x, temp_point.y);
                }*/
                basePath.lineTo(temp_point.x, temp_point.y);
            }

            //draw circle path
            Path circlePath = new Path();
            //上两个点之间间隔的剩余值,如果小于circle_interval,那么就是该值,如果大于,就是与circle_interval的差值
            float offsetDistance = 0f;
            //不使用最后一个点,使用一个点代替最后一个点
            int tempPointsSize = mTempPoints.size();
            //value为最后两个点之间的距离
            PointF last_pre_point = mTempPoints.get(tempPointsSize - 2);
            PointF last_point = mTempPoints.get(tempPointsSize - 1);
            double value = MathUtils.calculateDistance(last_point, last_pre_point);
            PointF remove_point = null;
            PointF add_point = null;
            if (value % CIRCLE_INTERVAL == 0) {
                //1.最后两个点之间的距离是circle_interval的整数倍,则不需要处理
            } else {
                //2.如果不是整数倍:
                value = value / CIRCLE_INTERVAL;
                if (value >= 1.0) {
                    //如果大于1.0,则在N.0处取一个点;
                    double decimal = value - (int) (value);
                    double scala = (value - decimal) / value;
                    float distX = last_pre_point.x - last_point.x;
                    float distY = last_pre_point.y - last_point.y;
                    add_point = new PointF((float) (last_pre_point.x + distX * scala), (float) (last_pre_point.y + distY * scala));
                } else {
                    //如果小于1.0,则舍弃该点
                    remove_point = mTempPoints.remove(tempPointsSize - 1);
                }
            }
            //从最后面开始到正数第二个为止计算参考点的位置并添加到path中.
            for (int i = mTempPoints.size() - 2; i >= 1; i--) {
                PointF curPoint = mTempPoints.get(i);
                PointF targetPoint = mTempPoints.get(i - 1);
                float distance = (float) MathUtils.calculateDistance(curPoint, targetPoint);
                if (distance + offsetDistance >= CIRCLE_INTERVAL) {
                    float scala = (CIRCLE_INTERVAL - offsetDistance) / distance;
                    float distX = targetPoint.x - curPoint.x;
                    float distY = targetPoint.y - curPoint.y;
                    float circle_x = curPoint.x + (distX * scala);
                    float circle_y = curPoint.y + (distY * scala);
                    circlePath.addCircle(circle_x, circle_y, CIRCLE_RADIUS, Path.Direction.CW);
                    offsetDistance += distance - CIRCLE_INTERVAL;

                    while (offsetDistance >= CIRCLE_INTERVAL) {
                        scala = CIRCLE_INTERVAL / offsetDistance;
                        distX = targetPoint.x - circle_x;
                        distY = targetPoint.y - circle_y;
                        circle_x += distX * scala;
                        circle_y += distY * scala;
                        circlePath.addCircle(circle_x, circle_y, CIRCLE_RADIUS, Path.Direction.CW);

                        offsetDistance -= CIRCLE_INTERVAL;
                    }
                } else {
                    offsetDistance += distance;
                }
            }
            if (add_point != null) {
                mTempPoints.remove(add_point);
            }
            if (remove_point != null) {
                mTempPoints.add(remove_point);
            }

            // set corner path for right angle(直角) 参数代表-平滑程度
            //CornerPathEffect cornerPathEffect = new CornerPathEffect(200);
            //mPaint.setPathEffect(cornerPathEffect);

            // draw outside line
            mPaint.setStrokeWidth(OUTSIDE_LINE_WIDTH);
            mPaint.setColor(Color.WHITE);
            canvas.drawPath(basePath, mPaint);

            // draw inside line
            mPaint.setStrokeWidth(MIDDLE_LINE_WIDTH);
            mPaint.setColor(Color.BLACK);
            canvas.drawPath(basePath, mPaint);

            // draw white circle path
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.WHITE);
            //canvas.drawPath(circlePath, mPaint);

            //============================================================
            //TODO helong debug
            mPaint.setColor(Color.RED);
            for (int i = 0; i < mTempPoints.size(); i++) {
                canvas.drawCircle(mTempPoints.get(i).x, mTempPoints.get(i).y, CIRCLE_RADIUS, mPaint);
            }
            /*mPaint.setColor(Color.BLUE);
            Point testPoint = routeResult.mProjection.toScreenLocation(DrawUtils.naviLatLng2LatLng(routeResult.mFakeLocation.getCoord()));
            canvas.drawCircle(testPoint.x, testPoint.y, 5, mPaint);
            mPaint.setColor(Color.GREEN);
            canvas.drawCircle(testPoint.x, testPoint.y, 7, mPaint);
            mPaint.setColor(Color.YELLOW);
            canvas.drawCircle(testPoint.x, testPoint.y, 5, mPaint);*/
            //============================================================

            //delete black color background
            //TODO Too long to run this.
            //canvas.drawPaint(mPaintBitmapColorFilter);

            //            //TODO 问题:由于此时的mTempPoints是经过一系列操作处理的,但是mCurrentLatLngs是未经过处理的,因此两者并不同步,不能直接从后者得到nextRoadIndex用于前者
            //            //draw next road name
            //            Matrix final_matrix = canvas.getMatrix();
            //            int nextRoadIndex = -1;
            //            for (int i = 0; i < mTempPoints.size() - 1 && i < routeResult.mCurrentLatLngs.size() - 1; i++) {
            //                //may be index out of bound, because mCurrentLatLngs.size is less one than mCurrentPoints.size.
            //                if (routeResult.mCurrentLatLngs.get(i).equals(routeResult.mNextRoadNamePosition)) {
            //                    nextRoadIndex = i;
            //                    break;
            //                }
            //            }
            //            if (routeResult.mHasNextRoadName && routeResult.mNextRoadNamePosition != null && nextRoadIndex != -1) {
            //                Paint paint = new Paint();
            //                paint.setColor(Color.rgb(0x00, 0x7a, 0xff));
            //                PointF nextRoadPoint1 = mTempPoints.get(nextRoadIndex);
            //                PointF nextRoadPoint2 = mTempPoints.get(nextRoadIndex + 1);
            //                double distance_1_2 = MathUtils.calculateDistance(nextRoadPoint1, nextRoadPoint2);
            //                float dist_x = nextRoadPoint2.x - nextRoadPoint1.x;
            //                float dist_y = nextRoadPoint2.y - nextRoadPoint1.y;
            //                nextRoadPoint2.x = (float) (NEXT_ROAD_X / distance_1_2 * dist_x + nextRoadPoint1.x);
            //                nextRoadPoint2.y = (float) (NEXT_ROAD_Y / distance_1_2 * dist_y + nextRoadPoint1.y);
            //
            //                Matrix init_matrix = new Matrix();
            //                canvas.setMatrix(init_matrix);
            //                float[] xy_1 = new float[2];
            //                float[] xy_2 = new float[2];
            //                //-500将文字向上移动
            //                final_matrix.mapPoints(xy_1, new float[]{nextRoadPoint1.x, nextRoadPoint1.y - NEXT_ROAD_TEXT_OFFSET_HEIGHT});
            //                final_matrix.mapPoints(xy_2, new float[]{nextRoadPoint2.x, nextRoadPoint2.y - NEXT_ROAD_TEXT_OFFSET_HEIGHT});
            //                Path text_path = new Path();
            //                //if text order is horizontal
            //                if (Math.abs(xy_1[0] - xy_2[0]) > Math.abs(xy_1[1] - xy_2[1])) {
            //                    if (xy_1[0] >= xy_2[0]) {
            //                        text_path.moveTo(xy_2[0], xy_2[1]);
            //                        text_path.lineTo(xy_1[0], xy_1[1]);
            //                    } else {
            //                        text_path.moveTo(xy_1[0], xy_1[1]);
            //                        text_path.lineTo(xy_2[0], xy_2[1]);
            //                    }
            //                    //if text order is vertical(竖向还没有很好的方法可以显示的比较好看)
            //                } /*else {
            //                    if (xy_1[1] >= xy_2[1]) {
            //                        text_path.moveTo(xy_1[0], xy_1[1]);
            //                        text_path.lineTo(xy_2[0], xy_2[1]);
            //                    } else {
            //                        text_path.moveTo(xy_2[0], xy_2[1]);
            //                        text_path.lineTo(xy_1[0], xy_1[1]);
            //                    }
            //                }*/
            //                paint.setTextSize(15 + (xy_1[1] / IMAGE_WIDTH * 400));
            //                String road_name = "";
            //                if (routeResult.mNextRoadType == RouteBean.NextRoadType.LEFT) {
            //                    road_name = "<<" + routeResult.mNextRoadName;
            //                } else if (routeResult.mNextRoadType == RouteBean.NextRoadType.RIGHT) {
            //                    road_name = routeResult.mNextRoadName + ">>";
            //                } else {
            //                    road_name = "∩" + routeResult.mNextRoadName;
            //                }
            //                //canvas.drawTextOnPath(road_name, text_path, 0, 0, paint);
            //            }

            Matrix matrix = new Matrix();
            canvas.setMatrix(matrix);
            mPaint.setColor(Color.BLACK);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(0,0,(float) (this.IMAGE_WIDTH*0.439),this.IMAGE_HEIGHT*(1.1f),mPaint);
            mPaint.reset();
            drawLeftBlack(canvas);
            picture.endRecording();
        } else {
            picture.endRecording();
            animOver();
        }
    }

    private void drawLeftBlack(Canvas canvas){
        Matrix matrix = new Matrix();
        canvas.setMatrix(matrix);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0,0,(float) (this.IMAGE_WIDTH*0.439),this.IMAGE_HEIGHT*(1.1f),mPaint);
        mPaint.reset();
    }

    public Picture getPicture() {
        Picture picture = this.mChooseOne ? mPictureOne : mPictureTwo;
        return picture;
    }
}
