package jp.aka.client;

import com.jutil.Logger.Logger;

import jp.ha.SpeechRec;
import jp.ha.SpeechRecWithLED;
import jp.ha.TextToSpeech;
import jp.ha.httpCon.MyHttpCon;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.sotatalk.MotionAsSotaWish;

public class TestApp {
	private static final String TAG = "Test1";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CRobotPose pose = null;
		CRobotMem mem = new CRobotMem();
		CSotaMotion motion = new CSotaMotion(mem);
		MotionAsSotaWish sotawish = new MotionAsSotaWish(motion);
		if (!mem.Connect()) {
			return;
		}
		motion.InitRobot_Sota();
		motion.ServoOn();
		sotawish.StartIdling();

		MyHttpCon.setAddress();
		SpeechRecWithLED.setUpConfigs();
		TextToSpeech.setUpConfigs();
		
		while (true) {
			
			String recResult = new SpeechRecWithLED().start(motion, pose);
			Logger.info(TAG, recResult);
			String genResult = Chat.simpleChat(recResult);
			TextToSpeech.speechWithMotion(genResult, sotawish, MotionAsSotaWish.MOTION_TYPE_TALK);
		}
	}
}
