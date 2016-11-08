package com.haloai.hud.hudendpoint.arwaylib.utils;

/**
 * Created by wangshengxing on 16/7/12.
 */
public class ARWayConst {

    public static boolean IS_DARW_ARWAY = true; //是否偏航时清空显示数据
    //disiplay
    public static final boolean IS_YAW_CLEAR_DISPLAY = false; //是否偏航时清空显示数据
    public static final boolean IS_NEW_ROADOBJECT = true; //是否偏航时清空显示数据

    //data
    public static final boolean IS_FILTER_PATH_LITTLE_DISTANCE = false; //
    public static final boolean IS_CAT_MULL_ROM = false; //

    // debug log
    public static final String  INDICATE_LOG_TAG = "arway_indication";
    public static final String  ERROR_LOG_TAG = "arway_error";
    public static final String  SPECIAL_LOG_TAG = "arway_special";
    public static final boolean ENABLE_LOG_OUT = true;//ARWAY 输出log
    public static final boolean ENABLE_FAST_LOG = false;//ARWAY 输出log
    public static final boolean ENABLE_SPECIAL_LOG = true;//ARWAY 输出紧要log
    public static final boolean ENABLE_TEST_LOG = false;//ARWAY 输出测试log
    public static final boolean ENABLE_PERFORM_TEST = false;//ARWAY 输出测试log


    //camera
    public static final double CAMERA_NEAR_PLANE = 0.5;
    public static final double CAMERA_FAR_PLANE  = 25;

    //open gl
    public static final int     FRAME_RATE             = 30;
    public static final double DEFAULT_CAMERA_Z           = 15;
    public static final float  REAL_TO_AMAP_GL_RATE       = 9027.669311696285f;//61.720116/0.0068367719141013686
    public static final float  AMAP_TO_ARWAY_GL_RATE      = 1170.1428832954605F;
    public static final float  ROAD_WIDTH                 = 0.4f;
    public static final float  REFERENCE_LINE_STEP_LENGTH = ROAD_WIDTH*10;

    public static final boolean IS_DEBUG_SCENE = true;

    //NAVING
    public static final boolean IS_AMAP_VIEW = false;//
    public static final int NAVI_CAR_START_DISTANCE = 50;

    public static final boolean NAVI_ENABLE_RESTRICT_DISTANCE  = false;
    public static final int     NAVI_MAX_RESTRICT_POINT_NUMBER = 5000;

    //road obect
    public static final boolean IS_USE_ROAD_TEXTURE      = true;

    //测试岔路模拟数据
    public static final String BRANCH_LINES =
            "cross starts==========\n" +
                    "cross start==========\n" +
                    "-10.0,-4.4375\n" +
                    "-10.0525,-5.28\n" +
                    "-10.10,-6.27\n" +
                    "-10.158,-8.98\n" +
                    "-10.192,-15.523\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "-10.158,-8.98\n" +
                    "-11.5529,-8.527\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "-10.158,-8.98\n" +
                    "-8.5529,-8.527\n" +
                    "cross end==========\n" +
                    "cross ends==========\n" +
                    "\n" +
                    "cross starts==========\n" +
                    "cross start==========\n" +
                    "-16.0,-3.5625\n" +
                    "-17.00,-6.548\n" +
                    "-17.62,-8.202\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "-16.0,-3.5625\n" +
                    "-16.23,-5.2412\n" +
                    "-16.863,-7.142\n" +
                    "-17.37,-8.58\n" +
                    "cross end==========\n" +
                    "cross ends==========\n" +
                    "\n" +
                    "cross starts==========\n" +
                    "cross start==========\n" +
                    "-14.625,0.0\n" +
                    "-13.375,3.63\n" +
                    "-12.252,6.8399\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "-13.375,3.63\n" +
                    "-12.006,5.8499\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "-15.25,0.375\n" +
                    "-12.6402,7.8129\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "-15.4744,0.07309\n" +
                    "-15.7232,-0.5424\n" +
                    "-16.5005,-1.0882\n" +
                    "-17.714,-1.549\n" +
                    "cross end==========\n" +
                    "cross ends==========\n" +
                    "\n" +
                    "cross starts==========\n" +
                    "cross start==========\n" +
                    "-16.75,-3.5625\n" +
                    "-17.694,-4.1887\n" +
                    "-18.739,-6.48\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "-22.6815,-17.24\n" +
                    "-23.1855,-17.7664\n" +
                    "-26.6897,-17.0285\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "-20.5125,-12.8514\n" +
                    "-20.632,-15.5115\n" +
                    "-23.6628,-25.5208\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "-28.1559,-39.1129\n" +
                    "-28.8832,-40.603\n" +
                    "-32.079,-43.335\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "-26.5817,-31.7337\n" +
                    "-26.3182,-43.6211\n" +
                    "-25.7271,-57.4707\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "-26.3182,-43.6211\n" +
                    "-23.7703,-50.748\n" +
                    "cross end==========\n"+
                    "cross start==========\n" +
                    "-29.4413,-60.7756\n" +
                    "-29.7574,-61.3819\n" +
                    "-30.2349,-62.325\n" +
                    "-30.557,-64.688\n" +
                    "-30.577,-67.26\n" +
                    "-30.29,-69.425\n" +
                    "-29.594,-71.073\n" +
                    "cross end==========\n" +
                    "cross ends==========\n" +
                    "\n" +
                    "cross starts==========\n" +
                    "cross start==========\n" +
                    "-29.75,-93.3125\n" +
                    "-29.978,-110.042\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "-29.75,-93.3125\n" +
                    "-35.9765,-93.2174\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "-32.9358,-93.2397\n" +
                    "-32.9358,-78.2233\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "-32.9358,-93.2397\n" +
                    "-33.1358,-110.2233\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "-29.815,-97.557\n" +
                    "-27.115,-97.354\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "-24.973,-93.596\n" +
                    "-24.2294,-92.707\n" +
                    "-23.010,-86.6145\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "-21.6772,-93.89\n" +
                    "-21.6993,-105.25\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "-10.2888,-93.59\n" +
                    "-10.2828,-76.57\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "-10.2888,-93.59\n" +
                    "-10.2828,-112.62\n" +
                    "cross end==========\n" +
                    "cross ends==========\n" +
                    "\n" +
                    "cross starts==========\n" +
                    "cross start==========\n" +
                    "-0.75,-93.4375\n" +
                    "12.35168,-93.46\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "2.35168,-93.46\n" +
                    "2.3303,-84.859\n" +
                    "2.33388,-74.752\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "2.3303,-84.859\n" +
                    "0.59577,-75.039\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "2.3303,-84.859\n" +
                    "4.45577,-75.039\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "2.008,-115.64\n" +
                    "3.5421,-115.77\n" +
                    "8.8908,-115.38\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "2.03092,-137.459\n" +
                    "-3.097,-138.034\n" +
                    "cross end==========\n" +
                    "cross ends==========\n" +
                    "\n" +
                    "cross starts==========\n" +
                    "cross start==========\n" +
                    "2.125,-157.8125\n" +
                    "2.0322,-169.66\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "2.125,-157.8125\n" +
                    "-1.357,-158.112\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "2.125,-157.8125\n" +
                    "-0.8455,-167.849\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "8.749,-158.629\n" +
                    "8.377,-158.145\n" +
                    "8.034,-156.606\n" +
                    "7.871,-149.656\n" +
                    "cross end==========\n" +
                    "cross ends==========\n" +
                    "\n" +
                    "cross starts==========\n" +
                    "cross start==========\n" +
                    "16.625,-161.0625\n" +
                    "18.199,-144.076\n" +
                    "cross end==========\n" +
                    "cross ends==========\n" +
                    "\n" +
                    "cross starts==========\n" +
                    "cross start==========\n" +
                    "15.875,-181.0\n" +
                    "15.9805,-183.68\n" +
                    "16.292,-192.24\n" +
                    "16.452,-198.76\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "15.9805,-183.68\n" +
                    "15.3905,-185.56\n" +
                    "12.2576,-187.28\n" +
                    "8.9231,-188.07\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "20.407,-180.67\n" +
                    "25.3883,-180.019\n" +
                    "cross end==========\n" +
                    "cross ends==========\n" +
                    "\n" +
                    "cross starts==========\n" +
                    "cross start==========\n" +
                    "19.75,-159.9375\n" +
                    "19.864,-154.32\n" +
                    "19.963,-150.055\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "19.864,-154.32\n" +
                    "18.769,-154.32\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "26.0205,-158.55\n" +
                    "26.6649,-157.745\n" +
                    "27.861,-155.6\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "33.230,-158.59\n" +
                    "33.467,-159.465\n" +
                    "33.767,-166.625\n" +
                    "cross end==========\n" +
                    "cross ends==========\n" +
                    "\n" +
                    "cross starts==========\n" +
                    "cross start==========\n" +
                    "39.5,-158.3125\n" +
                    "39.5,-170.83\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "39.225,-158.533\n" +
                    "39.3508,-159.605\n" +
                    "39.492,-161.45\n" +
                    "cross end==========\n" +
                    "cross ends==========\n" +
                    "\n" +
                    "cross starts==========\n" +
                    "cross start==========\n" +
                    "39.875,-147.875\n" +
                    "39.849,-138.64\n" +
                    "cross end==========\n" +
                    "cross ends==========\n" +
                    "cross starts==========\n" +
                    "cross start==========\n" +
                    "43.5,-147.75\n" +
                    "43.5103,-142.042\n" +
                    "43.516,-126.061\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "43.5103,-142.042\n" +
                    "42.181,-132.859\n" +
                    "cross end==========\n" +
                    "cross start==========\n" +
                    "43.5103,-142.042\n" +
                    "45.451,-142.859\n" +
                    "cross end==========\n" +
                    "cross ends==========";
    //测试岔路模拟数据
    public static final String BRANCH_LINES_BJ =
                   "crossLink cross start\n" +
                           "39.989883841395454,116.49602472782135\n" +
                           "39.98963621396658,116.49602472782135\n" +
                           "39.9890039982176,116.49602472782135\n" +
                           "crossLink cross end\n" +
                           "crossLink cross start\n" +
                           "39.989883841395454,116.49602472782135\n" +
                           "39.98989411637311,116.49614408612251\n" +
                           "39.989864318933655,116.49676367640495\n" +
                           "crossLink cross end\n" +
                           "crossLink cross start\n" +
                           "39.99180934521249,116.49637207388878\n" +
                           "39.99178982330102,116.49640157818794\n" +
                           "39.99140965864956,116.49673148989677\n" +
                           "39.99120965864956,116.49693148989677\n" +
                           "crossLink cross end\n" +
                           "crossLink cross start\n" +
                           "39.99169940595408,116.49590134620667\n" +
                           "39.99180934521249,116.49583160877228\n" +
                           "39.99187921306043,116.49577125906944\n" +
                           "39.992057992228325,116.49552717804909\n" +
                           "39.992157992228325,116.49512717804909\n" +
                           "crossLink cross end\n" +
                           "crossLink cross start\n" +
                           "39.993078328019506,116.49460449814796\n" +
                           "39.993718355708694,116.49528443813324\n" +
                           "crossLink cross end\n" +
                           "crossLink cross start\n" +
                           "39.98880085008429,116.48991331458092\n" +
                           "39.988530613332756,116.48977383971214\n" +
                           "39.98832099929879,116.48970410227776\n" +
                           "39.98804048539401,116.48970410227776\n" +
                           "39.98777472372584,116.48980693048477\n" +
                           "crossLink cross end\n" +
                           "crossLink cross start\n" +
                           "39.98880085008429,116.48991331458092\n" +
                           "39.98869090598359,116.48965314030647\n" +
                           "39.988680630824945,116.48954316973686\n" +
                           "39.98872070393489,116.48945331573486\n" +
                           "39.988811125224856,116.48937419056892\n" +
                           "39.98892106913209,116.4893339574337\n" +
                           "crossLink cross end\n" +
                           "crossLink cross start\n" +
                           "39.98831483417038,116.48918643593788\n" +
                           "39.98803534776596,116.48955658078194\n" +
                           "39.98797472372584,116.48961693048477\n" +
                           "39.98777472372584,116.48980693048477\n" +
                           "39.98757472372584,116.48999693048477\n" +
                           "crossLink cross end\n" +
                           "crossLink cross start\n" +
                           "39.993954666536695,116.48058995604515\n" +
                           "39.993874526434496,116.48049876093864\n" +
                           "39.99379541367725,116.48041293025017\n" +
                           "39.99329541367725,116.48001293025017\n" +
                           "crossLink cross end\n" +
                           "crossLink cross start\n" +
                           "39.993954666536695,116.48058995604515\n" +
                           "39.99407487651371,116.48071870207787\n" +
                           "39.99443447774587,116.48109957575798\n" +
                           "39.99467489579922,116.48132890462875\n" +
                           "39.99479510450857,116.48144960403442\n" +
                           "crossLink cross end\n" +
                           "crossLink cross start\n" +
                           "39.995863,116.477722\n" +
                           "39.996763,116.476422\n" +
                           "crossLink cross end\n" +
                           "crossLink cross start\n" +
                           "39.995863,116.477722\n" +
                           "39.994863,116.477022\n" +
                           "crossLink cross end\n" +
                           "crossLink cross start\n" +
                           "39.99849270324384,116.47890418767929\n" +
                           "39.99849270324384,116.47877410054207\n" +
                           "39.99849270324384,116.47807410054207\n" +
                           "crossLink cross end\n" +
                           "crossLink cross start\n" +
                           "39.99849270324384,116.47890418767929\n" +
                           "39.998642698855775,116.47889479994774\n" +
                           "39.999342698855775,116.47889479994774\n" +
                           "crossLink cross end\n" +
                           "crossLink cross start\n" +
                           "39.99705848198937,116.48296102881432\n" +
                           "39.99695882509528,116.48305088281631\n" +
                           "39.996887935051205,116.48310050368309\n" +
                           "39.99664855218455,116.48326009511948\n" +
                           "39.996498552192406,116.48334056138992\n" +
                           "39.99633827786408,116.48341029882431\n" +
                           "39.99621807187103,116.48347064852715\n" +
                           "39.996038276187306,116.48355111479759\n" +
                           "39.99583793186787,116.48364096879959\n" +
                           "39.99573827319264,116.4836610853672\n" +
                           "crossLink cross end\n" +
                           "crossLink cross start\n" +
                           "39.99735334225721,116.48064091801643\n" +
                           "39.997512586820136,116.48064091801643\n" +
                           "39.99778278803538,116.48057118058205\n" +
                           "39.99796257912615,116.48047059774399\n" +
                           "39.99809202841837,116.48034051060677\n" +
                           "crossLink cross end\n" +
                           "crossLink cross start\n" +
                           "39.99735334225721,116.48064091801643\n" +
                           "39.99720334381314,116.48064091801643\n" +
                           "39.99689307201339,116.48064091801643\n" +
                           "39.99664341520398,116.48070126771927\n" +
                           "crossLink cross end\n" +
                           "crossLink cross start\n" +
                           "39.9972927264873,116.48092120885849\n" +
                           "39.997193069935065,116.48119077086449\n" +
                           "39.997102659741245,116.48134097456932\n" +
                           "39.99695266074656,116.48155018687248\n" +
                           "crossLink cross end\n" +
                           "crossLink cross start\n" +
                           "39.9972927264873,116.48092120885849\n" +
                           "39.9980725083027,116.48092120885849\n" +
                           "crossLink cross end\n" +
                           "crossLink cross start\n" +
                           "39.996685538433354,116.48007094860077\n" +
                           "39.99663622343053,116.47994086146355\n" +
                           "39.99661567550214,116.47976115345955\n" +
                           "39.99664649739236,116.47965118288994\n" +
                           "crossLink cross end";
}
