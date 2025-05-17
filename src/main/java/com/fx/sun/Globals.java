package com.fx.sun;

import java.util.Locale;

public class Globals {

    public static final String BUNDLE_PATH = "com.fx.sun.bundle.sun";
    public static final String LOG4J_CONFIG_PATH = System.getProperty("user.dir") + "/config/log4j.xml";
    public static final String LOG4J2_CONFIG_PATH = System.getProperty("user.dir") + "/config/log4j2.xml";
    public static final String XML_CONFIG_PATH = System.getProperty("user.dir") + "/config/config.xml";
    public static final String POV_BLUEPRINT_CONFIG_PATH = System.getProperty("user.dir") + "/config/blueprint.pov";
    
    public static final Locale DEFAULT_LOCALE = Locale.US;

    public static XMLPropertyManager propman;

    static {
        propman = new XMLPropertyManager(XML_CONFIG_PATH);
    }

    //Images
    public static final String APP_LOGO_PATH = System.getProperty("user.dir") + "/images/kdf.png";
    public static final String SIDE_IMAGE_PATH = System.getProperty("user.dir") + "/images/side.png";
    public static final String CSS_PATH = "/com/fx/sun/style/hec.css";
    public static final String MOON_IMAGE_PATH = System.getProperty("user.dir") + "/images/mond_small.png";

    //FXML
    public static final String FXML_PATH = "/com/fx/sun/fxml/";
    public static final String FXML_MAIN_PATH = FXML_PATH + "main_app.fxml";
    public static final String FXML_MAP_PATH = FXML_PATH + "map.fxml";
    public static final String FXML_MAP_HEIGHT_PATH = FXML_PATH + "map_height.fxml";
    public static final String FXML_SRTM_PATH = FXML_PATH + "srtm.fxml";
    public static final String FXML_SRTM_3D_PATH = FXML_PATH + "srtm_3d.fxml";
    public static final String FXML_PROFILE_PATH = FXML_PATH + "profile.fxml";
    public static final String FXML_LOCATIONS_PATH = FXML_PATH + "locations.fxml";
    public static final String FXML_HCM_CODE_PATH = FXML_PATH + "hcm_code.fxml";
    public static final String FXML_HCM_HEAT_PATH = FXML_PATH + "hcm_heat.fxml";
    public static final String FXML_HCM_3D_PATH = FXML_PATH + "hcm_3d.fxml";
    public static final String FXML_POLYGON_PATH = FXML_PATH + "polygon.fxml";
    public static final String FXML_NOMINATIM_SEARCH_PATH = FXML_PATH + "nominatim_search.fxml";
    public static final String FXML_TILE_PATH = FXML_PATH + "tile.fxml";
    public static final String FXML_CAL_PATH = FXML_PATH + "calendar.fxml";
    public static final String FXML_GRAPH_PATH = FXML_PATH + "graph.fxml";
    public static final String FXML_POLAR_PATH = FXML_PATH + "polar.fxml";
    public static final String FXML_MOON_PATH = FXML_PATH + "moon.fxml";
    public static final String FXML_MOON_PARAMS_PATH = FXML_PATH + "moon_params.fxml";
    public static final String FXML_MOON_TABLE_PATH = FXML_PATH + "moon_table.fxml";
    public static final String FXML_SUN_PARAMS_PATH = FXML_PATH + "sun_params.fxml";
    public static final String FXML_SUN_PARAMS_SPREAD_PATH = FXML_PATH + "sun_params_spread.fxml";
    public static final String FXML_INFO_PATH = FXML_PATH + "info.fxml";
    public static final String FXML_GRAPH_ANCHOR_PATH = FXML_PATH + "graph_anchor.fxml";
    public static final String FXML_BIG_DATA_PATH = FXML_PATH + "big_data.fxml";
    public static final String FXML_ELE_TIME_PATH = FXML_PATH + "ele_time.fxml";
    public static final String FXML_ELE_AZI_PATH = FXML_PATH + "ele_azi.fxml";
    public static final String FXML_ANCHOR_TEST_PATH = FXML_PATH + "anchor_test.fxml";
    public static final String FXML_DISTANCE_PATH = FXML_PATH + "distance.fxml";
    public static final String FXML_SUN_CHART_PATH = FXML_PATH + "sun_chart.fxml";
    public static final String FXML_SUN_CHART_LINES_PATH = FXML_PATH + "sun_chart_lines.fxml";
    public static final String FXML_SUN_CHART_POLAR_PATH = FXML_PATH + "sun_chart_polar.fxml";

    public static final String FXML_TEST_PATH = FXML_PATH + "test.fxml";

    //FXML-Dialoge
    public static final String DLG_PROGRESS_PATH = FXML_PATH + "progress.fxml";
    public static final String DLG_TILE_PATH = FXML_PATH + "tile_dlg.fxml";
    public static final String DLG_WGS_PATH = FXML_PATH + "dlg_wgs.fxml";
    public static final String DLG_OSM_PATH = FXML_PATH + "dlg_osm.fxml";

    //Custom-Path's
    public static final String BORDER_DIR = "BORDER_DIR";
    public static final String TOPO_DIR = "TOPO_DIR";
    public static final String MORPHO_DIR = "MORPHO_DIR";
    public static final String SRTM_DIR = "SRTM_DIR";
    public static final String POV_DIR = "POV_DIR";
    public static final String CSV_DIR = "CSV_DIR";
    public static final String COORD_LAT = "COORD_LAT";
    public static final String COORD_LON = "COORD_LON";

    //Default Path's
    public static final String TOPO_PATH = "c:/geo/topo";
    public static final String MORPHO_PATH = "c:/geo/morpho";

    //Control-Flags
    public static enum MODE {
        E, M
    };

    public static enum LOAD_MODE {
        FILE_CHOOSER, GERMANY_TOPO, GERMANY_MORPHO
    };

    public static enum TEXTURE_MODE {
        NORMAL, CHROME
    };

    public static boolean DEBUG = true;

    //Show Test-UI
    public static boolean SHOW_TEST_UI = false;

    public static final double HEIGHT = 720;//900.0f;
    public static final double WIDTH = HEIGHT * 18.0f / 9.0f;
}
