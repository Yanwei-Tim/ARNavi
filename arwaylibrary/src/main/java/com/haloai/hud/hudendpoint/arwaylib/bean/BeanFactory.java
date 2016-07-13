package com.haloai.hud.hudendpoint.arwaylib.bean;

import com.haloai.hud.hudendpoint.arwaylib.bean.impl.CommonBean;
import com.haloai.hud.hudendpoint.arwaylib.bean.impl.CompassBean;
import com.haloai.hud.hudendpoint.arwaylib.bean.impl.MusicBean;
import com.haloai.hud.hudendpoint.arwaylib.bean.impl.NaviInfoBean;
import com.haloai.hud.hudendpoint.arwaylib.bean.impl.NetworkBean;
import com.haloai.hud.hudendpoint.arwaylib.bean.impl.RouteBean;
import com.haloai.hud.hudendpoint.arwaylib.bean.impl.SatelliteBean;
import com.haloai.hud.hudendpoint.arwaylib.bean.impl.SpeedBean;
import com.haloai.hud.hudendpoint.arwaylib.bean.impl_opengl.CameraBean;
import com.haloai.hud.hudendpoint.arwaylib.bean.impl_opengl.OpenglRouteBean;

/**
 * author       : 龙;
 * date         : 2016/5/5;
 * email        : helong@haloai.com;
 * package_name : com.haloai.hud.hudendpoint.arwaylib.bean;
 * project_name : hudlauncher;
 */
public class BeanFactory {
    public enum BeanType {
        COMMON,
        ROUTE,
        SATELLITE,
        MUSIC,
        NETWORK,
        NAVI_INFO,
        SPEED,
        COMPASS,
        GL_ROUTE,
        GL_CAMERA
    }

    /**
     * is or not inited.
     */
    private static boolean mIsInited = false;

    private static CommonBean      mCommonBean      = null;
    private static RouteBean       mRouteBean       = null;
    private static SatelliteBean   mSatelliteBean   = null;
    private static MusicBean       mMusicBean       = null;
    private static NetworkBean     mNetworkBean     = null;
    private static NaviInfoBean    mNaviInfoBean    = null;
    private static SpeedBean       mSpeedBean       = null;
    private static CompassBean     mCompassBean     = null;
    private static OpenglRouteBean mOpenglRouteBean = null;
    private static CameraBean      mCameraBean      = null;

    public static SuperBean getBean(BeanType beanType) {
        if (!mIsInited) {
            init();
        }
        SuperBean hudBean = null;
        switch (beanType) {
            case COMMON:
                hudBean = mCommonBean;
                break;
            case ROUTE:
                hudBean = mRouteBean;
                break;
            case SATELLITE:
                hudBean = mSatelliteBean;
                break;
            case MUSIC:
                hudBean = mMusicBean;
                break;
            case NETWORK:
                hudBean = mNetworkBean;
                break;
            case NAVI_INFO:
                hudBean = mNaviInfoBean;
                break;
            case SPEED:
                hudBean = mSpeedBean;
                break;
            case COMPASS:
                hudBean = mCompassBean;
                break;
            case GL_ROUTE:
                hudBean = mOpenglRouteBean;
                break;
            case GL_CAMERA:
                hudBean = mCameraBean;
                break;
            default:
                throw new RuntimeException("bean type is error or missing break.");
        }
        return hudBean;
    }

    private static void init() {
        synchronized (BeanFactory.class) {
            mRouteBean = new RouteBean();
            mSatelliteBean = new SatelliteBean();
            mMusicBean = new MusicBean();
            mNetworkBean = new NetworkBean();
            mNaviInfoBean = new NaviInfoBean();
            mSpeedBean = new SpeedBean();
            mCompassBean = new CompassBean();
            mCommonBean = new CommonBean();
            mOpenglRouteBean = new OpenglRouteBean();
            mCameraBean = new CameraBean();
            mIsInited = true;
        }
    }
}
