package jp.ha;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import com.jutil.Http.HttpWrapper;
import com.jutil.Logger.Logger;

import jp.ha.utils.JsonIo;
import jp.vstone.RobotLib.CPlayWave;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.sotatalk.MotionAsSotaWish;

class TTSConfig {
    private String API_HOME; // これ変わるから注意
    private int id;

    public final String getAPI_HOME() {
        return API_HOME;
    }

    public final void setAPI_HOME(String aPI_HOME) {
        API_HOME = aPI_HOME;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

final public class TextToSpeech {
    private static final String TAG = "TextToSpeech";
    private static final String FILE_PATH = "tts.wav";
    private static final Path CONFIG_PATH = Paths.get("tts_config.json");
    private static TTSConfig config;

    final public static void setAddress() {
        config = JsonIo.load(CONFIG_PATH, TTSConfig.class);
        if (config == null) {
            config = new TTSConfig();
        }

        boolean isFin = false;

        Scanner scan = new Scanner(System.in);
        while (!isFin) {
            Logger.info(TAG, "setup API Address press 1: API_HOME=", config.getAPI_HOME());
            Logger.info(TAG, "press 4: FIN");
            int num1 = scan.nextInt();
            switch (num1) {
                case 1:
                    Logger.info(TAG, "update API_HOME");
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

            Logger.info(TAG, "input part1 XXX.000.0.00:0000");
            fragment1 = scan.nextInt();
            Logger.info(TAG, "input part2 ", fragment1, ".XXX.0.00:0000");
            fragment2 = scan.nextInt();
            Logger.info(TAG, "input part3 ", fragment1, ".", fragment2, ".X.00:0000");
            fragment3 = scan.nextInt();
            Logger.info(TAG, "input part4 ", fragment1, ".", fragment2, ".", fragment3, ".XX:0000");
            fragment4 = scan.nextInt();
            Logger.info(TAG, "input part4 ", fragment1, ".", fragment2, ".", fragment3, ".", fragment4, ":XXXX");
            fragment5 = scan.nextInt();
            switch (num1) {
                case 1:
                    config.setAPI_HOME("http://" + fragment1 + "." + fragment2 + "." + fragment3 + "." + fragment4 + ":"
                            + fragment5);
                    Logger.info(TAG, "update API_HOME" + config.getAPI_HOME());
                    break;
            }
        }

        JsonIo.dump(CONFIG_PATH, config);
//        scan.close(); # クローズしたらだめ
    }

    public static void setUpConfigs() {
        setAddress();
        config = JsonIo.load(CONFIG_PATH, TTSConfig.class);
        if (config == null) {
            config = new TTSConfig();
        }

        boolean isFin = false;
        Scanner scan = new Scanner(System.in);
        while (!isFin) {
            Logger.info(TAG, "setup CHARA_ID 0: CHARA_ID=", config.getId());
            Logger.info(TAG, " press 4: FIN");
            int num1 = scan.nextInt();
            switch (num1) {
                case 0:
                    Logger.info(TAG, "update CHARA_ID");
                    break;
                default:
                    isFin = true;
                    break;
            }
            if (isFin) {
                break;
            }
            int fragment1 = 0;

            Logger.info(TAG, "input number");
            fragment1 = scan.nextInt();
            switch (num1) {
                case 0:
                    config.setId(fragment1);
                    Logger.info(TAG, "update CHARA_ID=", config.getId());
                    break;
            }
        }

        JsonIo.dump(CONFIG_PATH, config);
        // scan.close(); # クローズしたらだめ
    }

    /**
     * 音声合成
     *
     * @param filename
     * @param url
     * @return
     * @throws IOException
     */
    final private static byte[] tts(String text) throws IOException {
        byte[] response = null;
        String url = config.getAPI_HOME() + "/tts?&text=" + URLEncoder.encode(text, "UTF-8") + "&chara_id="
                + config.getId();
        response = HttpWrapper.createGetReqByByte(url);
        return response;
    }

    final private static void saveFile(byte[] data) throws IOException {
        BufferedOutputStream bf = new BufferedOutputStream(new FileOutputStream(FILE_PATH));
        bf.write(data);
        bf.flush();
        bf.close();
    }

    /**
     * 音声合成を行い再生する
     *
     * @param text
     * @param sotawish
     * @param scene    モーションの種類
     */
    final public static void speechWithMotion(String text, MotionAsSotaWish sotawish, String scene) {
        try {
            // 音声合成
            byte[] response = tts(text);
            // ファイルに書き込む
            saveFile(response);
            sotawish.SayFile(FILE_PATH, scene);
        } catch (Exception e) {
            CRobotUtil.Log(TAG, e.toString());
            e.printStackTrace();
        }
    }

    /**
     * 音声合成を行い再生する
     *
     * @param text
     */
    final public static void speech(String text) {
        try {
            // 音声合成
            byte[] response = tts(text);
            // ファイルに書き込む
            saveFile(response);
            CPlayWave.PlayWave(FILE_PATH, true);
        } catch (Exception e) {
            CRobotUtil.Log(TAG, e.toString());
            e.printStackTrace();
        }
    }

 // メインメソッド
    public static void main(String[] args) {
        TextToSpeech.setUpConfigs();

        TextToSpeech.speech("こんにちは");

        // VSMDと通信ソケット・メモリアクセス用クラス
        CRobotMem mem = new CRobotMem();
        // Sota用モーション制御クラス
        CSotaMotion motion = new CSotaMotion(mem);
        MotionAsSotaWish sotawish = new MotionAsSotaWish(motion);

        if (!mem.Connect()) {
            Logger.error(TAG, "CRobotMem is not connect...");
        }
        // Sota仕様にVSMDを初期化
        motion.InitRobot_Sota();
        motion.ServoOn();

        TextToSpeech.speechWithMotion("こんにちは", sotawish, MotionAsSotaWish.MOTION_TYPE_CALL);
    }
}
