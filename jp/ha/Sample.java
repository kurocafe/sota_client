package jp.ha;

import java.awt.Color;

import com.jutil.Logger.Logger;

import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.sotatalk.MotionAsSotaWish;

public class Sample {
    private static String TAG = "Sample";

    private static void up_L(CSotaMotion motion, CRobotPose pose) {

        pose = new CRobotPose();
        pose.SetPose(new Byte[] { CSotaMotion.SV_HEAD_R, CSotaMotion.SV_L_SHOULDER, CSotaMotion.SV_L_ELBOW } // id
                , new Short[] { 200, 700, -200 } // target pos
        );
        pose.setLED_Sota(Color.GREEN, Color.GREEN, 255, Color.GREEN);
        motion.play(pose, 1000);
        motion.waitEndinterpAll();
    }

    private static void down_L(CSotaMotion motion, CRobotPose pose) {
        pose = new CRobotPose();
        pose.SetPose(new Byte[] { CSotaMotion.SV_HEAD_R, CSotaMotion.SV_L_SHOULDER, CSotaMotion.SV_L_ELBOW } // id
                , new Short[] { 200, -900, 0 } // target pos
        );
        pose.setLED_Sota(Color.GREEN, Color.GREEN, 255, Color.GREEN);
        motion.play(pose, 1000);
        motion.waitEndinterpAll();
    }

    private static void up_R(CSotaMotion motion, CRobotPose pose) {

        pose = new CRobotPose();
        pose.SetPose(new Byte[] { CSotaMotion.SV_HEAD_R, CSotaMotion.SV_R_SHOULDER, CSotaMotion.SV_R_ELBOW } // id
                , new Short[] { 200, -700, 200 } // target pos
        );
        pose.setLED_Sota(Color.GREEN, Color.GREEN, 255, Color.GREEN);
        motion.play(pose, 1000);
        motion.waitEndinterpAll();
    }

    private static void down_R(CSotaMotion motion, CRobotPose pose) {
        pose = new CRobotPose();
        pose.SetPose(new Byte[] { CSotaMotion.SV_HEAD_R, CSotaMotion.SV_R_SHOULDER, CSotaMotion.SV_R_ELBOW } // id
                , new Short[] { 200, 900, 0 } // target pos
        );
        pose.setLED_Sota(Color.GREEN, Color.GREEN, 255, Color.GREEN);
        motion.play(pose, 1000);
        motion.waitEndinterpAll();
    }

    private static void reset(CSotaMotion motion, CRobotPose pose) {
        // すべての軸を動作
        pose = new CRobotPose();
        pose.SetPose(new Byte[] { 1, 2, 3, 4, 5, 6, 7, 8 } // id
                , new Short[] { 0, -900, 0, 900, 0, 0, 0, 0 } // target pos
        );
        // LEDを点灯（左目：赤、右目：赤、口：Max、電源ボタン：赤）
        pose.setLED_Sota(Color.RED, Color.RED, 255, Color.RED);

        // 遷移時間1000msecで動作開始。
        CRobotUtil.Log(TAG, "play:" + motion.play(pose, 1000));

        // 補間完了まで待つ
        motion.waitEndinterpAll();
    }

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

        reset(motion, pose);

        while (true) {
            String spRecResult = new SpeechRecWithLED().start(motion, pose);
            Logger.info(TAG, spRecResult);
            if (spRecResult == "") {
                continue;
            }

            TextToSpeech.speech("はい!");

            if (spRecResult.contains("右手上げて")) {
                up_R(motion, pose);
            }

            if (spRecResult.contains("右手下げて")) {
                down_R(motion, pose);
            }

            if (spRecResult.contains("左手上げて")) {
                up_L(motion, pose);
            }

            if (spRecResult.contains("左手下げて")) {
                down_L(motion, pose);
            }
        }

    }

}
