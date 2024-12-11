package jp.ha;

import com.jutil.Logger.Logger;

import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.sotatalk.MotionAsSotaWish;

public class Sample2 {
    private static String TAG = "Sample";

    public static void main(String[] args) {
        // TODO 自動生成されたメソッド・スタブ
        SpeechRecWithLED.setUpConfigs();
        TextToSpeech.setUpConfigs();

        Logger.info(TAG, "With LED");
        CRobotPose pose = null;
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

        while(true) {
            String spRecResult = new SpeechRecWithLED().start(motion, pose);
            Logger.info(TAG, spRecResult);
            if(spRecResult == "") {
                continue;
            }
            TextToSpeech.speechWithMotion(spRecResult, sotawish, MotionAsSotaWish.MOTION_TYPE_CALL);
        }

    }

}
