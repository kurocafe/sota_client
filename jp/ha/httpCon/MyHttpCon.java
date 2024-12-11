package jp.ha.httpCon;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import com.jutil.Http.HttpWrapper;
import com.jutil.Logger.Logger;

import jp.ha.JSON.JSONMapper;
import jp.ha.httpCon.ApiCom.PostGenerateReq;
import jp.ha.httpCon.ApiCom.PostGenerateRes;
import jp.ha.utils.JsonIo;

class MyHTTPConfig {
    private String API_HOME; // これ変わるから注意
    private String DB_HOME;// これ変わるから注意
    private String TALK_HOME;

    public final String getAPI_HOME() {
        return API_HOME;
    }

    public final void setAPI_HOME(String aPI_HOME) {
        API_HOME = aPI_HOME;
    }

    public final String getDB_HOME() {
        return DB_HOME;
    }

    public final void setDB_HOME(String dB_HOME) {
        DB_HOME = dB_HOME;
    }

    public final String getTALK_HOME() {
        return TALK_HOME;
    }

    public final void setTALK_HOME(String tALK_HOME) {
        TALK_HOME = tALK_HOME;
    }
}

final public class MyHttpCon {
    private static final String LOG_TAG = "MyHttpCon";
    private static final Path MY_HTTP_CONFG_PATH = Paths.get("my_http_config.json");;
    private static MyHTTPConfig config;

    final public static void setAddress() {
        config = JsonIo.load(MY_HTTP_CONFG_PATH, MyHTTPConfig.class);
        if(config == null) {
            config = new MyHTTPConfig();
        }

        boolean isFin = false;

        Scanner scan = new Scanner(System.in);
        while (!isFin) {
            Logger.info(LOG_TAG, "setup API Address press 1: DB_HOME=", config.getDB_HOME());
            Logger.info(LOG_TAG, "setup API Address press 2: API_HOME=", config.getAPI_HOME());
            Logger.info(LOG_TAG, "setup API Address press 3: TALK_HOME=", config.getTALK_HOME());
            Logger.info(LOG_TAG, "press 4: FIN");
            int num1 = scan.nextInt();
            switch (num1) {
                case 1:
                    Logger.info(LOG_TAG, "update DB_HOME");
                    break;
                case 2:
                    Logger.info(LOG_TAG, "update API_HOME");
                    break;
                case 3:
                    Logger.info(LOG_TAG, "update TALK_HOME");
                    break;
                default:
                    isFin = true;
                    break;
            }
            if (isFin) {
                break;
            }
            int fragment1 = 0;
            int fragment2 = 0;
            int fragment3 = 0;
            int fragment4 = 0;
            int fragment5 = 0;

            Logger.info(LOG_TAG, "input part1 XXX.000.0.00:0000");
            fragment1 = scan.nextInt();
            Logger.info(LOG_TAG, "input part2 ", fragment1, ".XXX.0.00:0000");
            fragment2 = scan.nextInt();
            Logger.info(LOG_TAG, "input part3 ", fragment1, ".", fragment2, ".X.00:0000");
            fragment3 = scan.nextInt();
            Logger.info(LOG_TAG, "input part4 ", fragment1, ".", fragment2, ".", fragment3, ".XX:0000");
            fragment4 = scan.nextInt();
            Logger.info(LOG_TAG, "input part4 ", fragment1, ".", fragment2, ".", fragment3, ".", fragment4, ":XXXX");
            fragment5 = scan.nextInt();
            switch (num1) {
                case 1:
                    config.setDB_HOME("http://" + fragment1 + "." + fragment2 + "." + fragment3 + "." + fragment4 + ":"
                            + fragment5);
                    Logger.info(LOG_TAG, "update DB_HOME" + config.getDB_HOME());
                    break;
                case 2:
                    config.setAPI_HOME("http://" + fragment1 + "." + fragment2 + "." + fragment3 + "." + fragment4 + ":"
                            + fragment5);
                    Logger.info(LOG_TAG, "update API_HOME" + config.getAPI_HOME());
                    break;
                case 3:
                    config.setTALK_HOME("http://" + fragment1 + "." + fragment2 + "." + fragment3 + "." + fragment4 + ":"
                            + fragment5);
                    Logger.info(LOG_TAG, "update TALK_HOME" + config.getTALK_HOME());
                    break;
            }
        }

        JsonIo.dump(MY_HTTP_CONFG_PATH, config);
//        scan.close(); # クローズしたらだめ
    }

    /**
     * 音声合成
     *
     * @param filename
     * @param url
     * @return
     * @throws IOException
     */
    final public static byte[] tts(String text) throws IOException {
        byte[] response = null;
        String url = config.getAPI_HOME() + "/tts?&text=" + URLEncoder.encode(text, "UTF-8") + "&chara_id=0";
        response = HttpWrapper.createGetReqByByte(url);
        return response;
    }

    final public static PostGenerateRes postGenerate(PostGenerateReq req) throws IOException {
        String url = config.getTALK_HOME() + "/postGenerate";
        String response = HttpWrapper.sendJSON(JSONMapper.mapper.writeValueAsString(req), url);
        return JSONMapper.mapper.readValue(response, PostGenerateRes.class);
    }

}
