package com.haloai.hud.hudendpoint.arwaylib.scene;

import android.content.Context;
import android.graphics.Color;

import com.haloai.hud.hudendpoint.arwaylib.R;
import com.haloai.hud.hudendpoint.arwaylib.rajawali.object3d.ARWayRoadBuffredObject;
import com.haloai.hud.hudendpoint.arwaylib.utils.ARWayConst;
import com.haloai.hud.hudendpoint.arwaylib.utils.MathUtils;
import com.haloai.hud.hudendpoint.arwaylib.utils.TimeRecorder;
import com.haloai.hud.utils.HaloLogger;

import org.rajawali3d.Object3D;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.NormalMapTexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.materials.textures.TextureManager;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Line3D;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.scene.Scene;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Created by wangshengxing on 16/9/22.
 */
public class ArwaySceneUpdater extends SuperArwaySceneUpdater implements IARwayRoadRender{

    private static final boolean IS_DEBUG_PATH_LINE    = false;
    private static final boolean IS_DEBUG_SHIPE_POINT  = false;
    private static final int     ROAD_OBJECT_SIZE      = 2;
    private static final boolean IS_DEBUG_MODE         = true;
    private static final boolean IS_DRAW_RFERENCE_LINT = true;

    private              List<RoadLayers> mRoadLayersList      = new LinkedList<>();
    private              List<RoadLayers> mCrossRoadLayersList      = new LinkedList<>();
    private              int              mRoadLayersIndex     = 0;


    //basic
    private Context mContext;

    //ROAD
    private static final float ROAD_WIDTH                 = 0.8f;
    private       double       REFERENCE_LINE_STEP_LENGTH = ARWayConst.REFERENCE_LINE_STEP_LENGTH* ROAD_WIDTH; //参考线间的长度
    private       Material     mRoadMaterial              = new Material();
    private       Material     mTestMaterial              = new Material();

    //render configuration
    private float mRoadScale = 20;
    private float mRoadWidth = ROAD_WIDTH;
    private float mCrossRoadWidth = ROAD_WIDTH;
    private float mRefLineHeight = ROAD_WIDTH;
    private float mRefLineWidth = ROAD_WIDTH;
    private float mRefLineStepLength = ROAD_WIDTH;

    private static ArwaySceneUpdater mArwaySceneUpdater = new ArwaySceneUpdater(null);
    private TimeRecorder mSceneUpdaterRecorder = new TimeRecorder();


    private class RoadLayers{
        private ARWayRoadBuffredObject white   = null;
        private ARWayRoadBuffredObject black   = null;
        private ARWayRoadBuffredObject refLine = null;

        public RoadLayers(ARWayRoadBuffredObject white, ARWayRoadBuffredObject black, ARWayRoadBuffredObject refLine) {
            this.white = white;
            this.black = black;
            this.refLine = refLine;
        }

        public RoadLayers(ARWayRoadBuffredObject white, ARWayRoadBuffredObject black) {
            this.white = white;
            this.black = black;
        }

    }

    public static ArwaySceneUpdater getInstance(){
        if (mArwaySceneUpdater == null) {
            mArwaySceneUpdater = new ArwaySceneUpdater(null);
        }
        return mArwaySceneUpdater;
    }

    private ArwaySceneUpdater() {
        this(null);
    }

    private ArwaySceneUpdater(Scene scene) {
        super(scene);
        mRoadMaterial.useVertexColors(true);
        mTestMaterial.setColor(Color.GREEN);
    }

    public void initScene() {
        float scale = ROAD_WIDTH;
        initRoadRender(1*scale,0.7f,0.4f*scale,0.12f*scale);
    }

    @Override
    public void setScene(Scene scene) {
        super.setScene(scene);
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public float getRoadScale() {
        return mRoadScale;
    }

    public void setRoadScale(float roadScale) {
        mRoadScale = roadScale;
    }

    private RoadLayers createRoadLayer(float roadWidth, float roadRate, float refLineHegiht, float refLineWidth, Material material){
        /*Material rMaterial = new Material();
        try {
            rMaterial.addTexture(new Texture("road_white", R.drawable.road_white));
            HaloLogger.logE(ARWayConst.ERROR_LOG_TAG,"renderVisiblePath,add texture ok");
        } catch (ATexture.TextureException e) {
            HaloLogger.logE(ARWayConst.ERROR_LOG_TAG,"renderVisiblePath,add texture error");
            e.printStackTrace();
        }
        rMaterial.setColorInfluence(0);*/

//        rMaterial.setColor(0);
//        rMaterial.enableTime(true);
        /*rMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
        rMaterial.enableLighting(true);*/

        RoadLayers roadLayers = new RoadLayers(new ARWayRoadBuffredObject(roadWidth, Color.WHITE,material),
                new  ARWayRoadBuffredObject(roadWidth*roadRate, Color.BLACK,material),
                new ARWayRoadBuffredObject(refLineHegiht,refLineWidth, Color.WHITE,material));
        return roadLayers;
    }

    private RoadLayers createCrossRoadLayer(float roadWidth, float roadRate,Material material){
        RoadLayers roadLayers = new RoadLayers(new ARWayRoadBuffredObject(roadWidth, Color.WHITE,material),
                new  ARWayRoadBuffredObject(roadWidth*roadRate, Color.BLACK,material));
        return roadLayers;
    }

    /**
     * 初始化道路显示配置
     * 道路的GPU消耗为：为1000个形状点2.536MB
     * @param roadWidth
     * @param refLineHegiht
     * @param refLineWidth
     */
    private boolean initRoadRender(float roadWidth,float roadRate,float refLineHegiht,float refLineWidth){
        boolean result = true;
        mRoadLayersList.clear();
        for (int i = 0; i < ROAD_OBJECT_SIZE; i++) {
            RoadLayers roadLayers = new RoadLayers(new ARWayRoadBuffredObject(roadWidth, Color.WHITE,mRoadMaterial),
                    new  ARWayRoadBuffredObject(roadWidth*roadRate, Color.BLACK,mRoadMaterial),
                    new ARWayRoadBuffredObject(refLineHegiht,refLineWidth, Color.WHITE,mRoadMaterial));
            mRoadLayersList.add(roadLayers);
        }
        for(RoadLayers roadLayers:mRoadLayersList){
            result &= addObject(roadLayers.white);
        }
        for(RoadLayers roadLayers:mRoadLayersList){
            result &= addObject(roadLayers.black);
        }
        for(RoadLayers roadLayers:mRoadLayersList){
            result &= addObject(roadLayers.refLine);
        }
        if(IS_DEBUG_MODE){
            HaloLogger.logE(ARWayConst.ERROR_LOG_TAG, String.format("initRoadRender, scene child size is %s",mScene.getNumChildren()));
        }
        mRoadLayersIndex = 0;
        return result;
    }

    private void onRoadRender(){
        if(++mRoadLayersIndex>1){
            mRoadLayersIndex=0;
        }
    }

    /**
     * 渲染当前显示的道路
     * @param path
     * @return
     */
    public boolean renderVisiblePath(List<Vector3> path) {
        if (path == null) {
            return false;
        }
        boolean result = true;
        if(IS_DEBUG_MODE){
            HaloLogger.logE(ARWayConst.ERROR_LOG_TAG, String.format("renderVisiblePath,path size is %s",path.size()));
        }
        if (mSceneUpdaterRecorder != null) {
            mSceneUpdaterRecorder.start();
        }
        RoadLayers roadLayers = null;
        if(true){
            float roadscale = 0.8f;
            Material rMaterial = new Material();
            /*try {
                rMaterial.addTexture(new Texture("route_new_line", R.drawable.route_new_line));
            } catch (ATexture.TextureException e) {
                HaloLogger.logE(ARWayConst.ERROR_LOG_TAG,"renderVisiblePath,add texture error");
                e.printStackTrace();
            }*/
//            rMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
//            rMaterial.enableLighting(true);
//            rMaterial.setColorInfluence(0);
//            rMaterial.addTexture(TextureManager.getInstance().addTexture(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.route_new_line), ATexture.TextureType.NORMAL));
            rMaterial.useVertexColors(true);
            roadLayers = createRoadLayer(1*roadscale,0.7f,0.4f*roadscale,0.12f*roadscale,mRoadMaterial);
            mRoadLayersList.clear();
            mRoadLayersList.add(roadLayers);
        }else {
            roadLayers = mRoadLayersList.get(mRoadLayersIndex);
            //            removeObject(new Object3D[]{roadLayers.white,roadLayers.black, roadLayers.refLine});
            setVisible(new Object3D[]{roadLayers.white,roadLayers.black, roadLayers.refLine},false);
            onRoadRender();
            roadLayers = mRoadLayersList.get(mRoadLayersIndex);
            setVisible(new Object3D[]{roadLayers.white,roadLayers.black, roadLayers.refLine},true);
//            addObject(new Object3D[]{roadLayers.white,roadLayers.black, roadLayers.refLine});
        }
        /*if (roadLayers != null) {
            mScene.clearChildren();
            result &= roadLayers.white.updateBufferedRoad(path);
            result &= roadLayers.black.updateBufferedRoad(path);
            double distStep = REFERENCE_LINE_STEP_LENGTH;
            List<Vector3> points = new ArrayList<>();
            List<Float> directions = new ArrayList<>();

            int cnt = path.size();
            if(cnt>=2){
                Vector3 v1 = path.get(0);
                Vector3 v2 = path.get(1);
                Float direction = new Float((float) Math.atan2(v2.y-v1.y,v2.x-v1.x));
                points.add(v1);
                directions.add(direction);
                for (int i = 0; i < cnt - 1; i++) {
                    v2 = path.get(i + 1);
                    double temp = MathUtils.calculateDistance(v1.x, v1.y, v2.x, v2.y);
                    if (temp >= distStep) {
                        double scale = distStep / temp;
                        Vector3 v = new Vector3();
                        v.x = v1.x + (v2.x - v1.x) * scale;
                        v.y = v1.y + (v2.y - v1.y) * scale;
                        v.z = 0;
                        v1 = new Vector3(v);
                        i--;
                        direction = new Float((float) Math.atan2(v2.y-v1.y,v2.x-v1.x));
                        directions.add(direction);
                        points.add(v);
                        distStep = REFERENCE_LINE_STEP_LENGTH;
                    } else if (temp < distStep) {
                        distStep -= temp;
                        v1 = path.get(i+1);
                    }
                }
                if(IS_DRAW_RFERENCE_LINT){
                    result &= roadLayers.refLine.updateReferenceLine(points,directions);
                }
            }
            addObject(new Object3D[]{roadLayers.white,roadLayers.black, roadLayers.refLine});
            return result;
        }*/

        Vector3 postion = new Vector3(0,0,0);
        roadLayers.white.setPosition(postion);
        roadLayers.black.setPosition(postion);
        roadLayers.black.setPosition(postion);
        result &= roadLayers.white.updateBufferedRoad(path);
        result &= roadLayers.black.updateBufferedRoad(path);
        double distStep = REFERENCE_LINE_STEP_LENGTH;
        List<Vector3> points = new ArrayList<>();
        List<Float> directions = new ArrayList<>();

        int cnt = path.size();
        if(cnt>=2){
            Vector3 v1 = path.get(0);
            Vector3 v2 = path.get(1);
            Float direction = new Float((float) Math.atan2(v2.y-v1.y,v2.x-v1.x));
            points.add(v1);
            directions.add(direction);
            for (int i = 0; i < cnt - 1; i++) {
                v2 = path.get(i + 1);
                double temp = MathUtils.calculateDistance(v1.x, v1.y, v2.x, v2.y);
                if (temp >= distStep) {
                    double scale = distStep / temp;
                    Vector3 v = new Vector3();
                    v.x = v1.x + (v2.x - v1.x) * scale;
                    v.y = v1.y + (v2.y - v1.y) * scale;
                    v.z = 0;
                    v1 = new Vector3(v);
                    i--;
                    direction = new Float((float) Math.atan2(v2.y-v1.y,v2.x-v1.x));
                    directions.add(direction);
                    points.add(v);
                    distStep = REFERENCE_LINE_STEP_LENGTH;
                } else if (temp < distStep) {
                    distStep -= temp;
                    v1 = path.get(i+1);
                }
            }
            if(IS_DRAW_RFERENCE_LINT){
                result &= roadLayers.refLine.updateReferenceLine(points,directions);
            }
        }
        if(IS_DEBUG_SHIPE_POINT){
            for(Vector3 p :path){
                Plane plane = new Plane(0.1f,0.1f,10,10, Vector3.Axis.Z,false,true);
                plane.setMaterial(mTestMaterial);
                plane.setPosition(p);
                mScene.addChild(plane);
            }
        }
        if(IS_DEBUG_PATH_LINE){
            Stack lineStack = new Stack();
            lineStack.addAll(path);
            Line3D line3D = new Line3D(lineStack,2);
            Material material = new Material();
            material.setColor(Color.RED);
            line3D.setMaterial(material);
            mScene.addChild(line3D);
        }
        if(ARWayConst.ENABLE_PERFORM_TEST){
            if (mSceneUpdaterRecorder != null) {
                mSceneUpdaterRecorder.recordeAndLog("performance","renderVisiblePath");
            }
        }
        /*if(true) {
            List<List<Vector3>> cross = new LinkedList<>();
            List<Vector3> road = new LinkedList<>();
            for (int i = 0; i < 10; i++) {
                road.add(new Vector3(path.get(0).x + i, path.get(0).y, 0));
            }
            cross.add(road);
            renderCrossRoad(cross);
        }*/
        reloadAllRoadLayer();
        return result;
    }

    @Override
    public boolean renderIndicationLine(List<Vector3> path) {
        return false;
    }

    @Override
    public boolean renderCrossRoad(List<List<Vector3>> cross) {
        boolean result = true;
        Material rMaterial = new Material();
        rMaterial.useVertexColors(true);
        mCrossRoadLayersList.clear();
        for (List<Vector3> road:cross) {
            RoadLayers roadLayers = createCrossRoadLayer(mCrossRoadWidth,0.7f,mRoadMaterial);
            mCrossRoadLayersList.add(roadLayers);
            roadLayers.black.updateBufferedRoad(road);
            roadLayers.white.updateBufferedRoad(road);
        }

        for(RoadLayers roadLayers:mCrossRoadLayersList){
            result &= addObject(roadLayers.white);
        }
        for(RoadLayers roadLayers:mCrossRoadLayersList){
            result &= addObject(roadLayers.black);
        }

//        reloadAllRoadLayer();
        return false;
    }

    /**
     * 重新按图层显示顺序加载需要显示的图层
     * @return
     */
    public boolean reloadAllRoadLayer(){
        boolean result = true;
        mScene.clearChildren();

        for(RoadLayers roadLayers:mRoadLayersList){
            result &= addObject(roadLayers.white);
        }
        for(RoadLayers roadLayers:mCrossRoadLayersList){
            result &= addObject(roadLayers.white);
        }

        for(RoadLayers roadLayers:mRoadLayersList){
            result &= addObject(roadLayers.black);
        }
        for(RoadLayers roadLayers:mCrossRoadLayersList){
            result &= addObject(roadLayers.black);
        }

        for(RoadLayers roadLayers:mRoadLayersList){
            result &= addObject(roadLayers.refLine);
        }
        return result;
    }
    /**
     * 清除显示的道路
     * @return
     */
    public boolean clearRenderedRoad(){
        boolean result = true;
        for (RoadLayers roadLayers:mRoadLayersList) {
            result &= removeObject(new Object3D[]{roadLayers.white,roadLayers.black, roadLayers.refLine});
        }
        return result;
    }

}

