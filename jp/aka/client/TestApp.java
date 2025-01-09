package jp.aka.client;

import com.jutil.Http.HttpWrapper;
import com.jutil.Logger.Logger;

import jp.aka.sample.values.QRReadRes;
import jp.ha.SpeechRec;
import jp.ha.SpeechRecWithLED;
import jp.ha.TextToSpeech;
import jp.ha.JSON.JSONMapper;
import jp.ha.httpCon.MyHttpCon;
import jp.vstone.RobotLib.CPlayWave;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.camera.CameraCapture;
import jp.vstone.sotatalk.MotionAsSotaWish;

public class TestApp {
	private static final String TAG = "Test1";
	private static final String API_HOME = "http://150.59.20.116:8000";
	private static final String QR_PATH = "./qr.jpg";
	private static long userId = -1;
	
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
//		sotawish.StartIdling();

		MyHttpCon.setAddress();
		SpeechRecWithLED.setUpConfigs();
		TextToSpeech.setUpConfigs();
		
		CPlayWave.PlayWave_wait("./sound/show_me QRcode.wav");
		// QRコード読み取りロジック
		CameraCapture cap = null;
		try {
			cap = new CameraCapture(CameraCapture.CAP_IMAGE_SIZE_HD_1080, CameraCapture.CAP_FORMAT_3BYTE_BGR);
			cap.openDevice("/dev/video0");
			int count = 0;
			while (count < 60) {
				cap.snapGetFile(QR_PATH);
				String url = API_HOME + "/qr_read";
				QRReadRes response = null;
//				cap.close();
				Logger.info(TAG, "QRコード読み取りリクエストを送信します");
				response = JSONMapper.mapper.readValue(HttpWrapper.uploadFile(QR_PATH, url), QRReadRes.class);
				
				if (response.getResponse() != null) {
					Logger.info(TAG, "QRコード読み取り成功: " + response.getResponse());
//					CPlayWave.PlayWave_wait("./sound/i_see_QRcode.wav");
					TextToSpeech.speechWithMotion("QRコード、見えたよ。", sotawish, MotionAsSotaWish.MOTION_TYPE_TALK);
					String getUserName = response.getResponse() + "さん、よろしくね。";
					TextToSpeech.speechWithMotion(getUserName, sotawish, MotionAsSotaWish.MOTION_TYPE_TALK);
					userId = response.getUser_id();
					break;
				}
				
				Thread.sleep(1000);
				count++;
			}		
		} catch (Exception e) {
			Logger.error(TAG, "QRコード読み取りエラー", e);
		} finally {
			if (cap != null) {
				cap.close();
			}
		}


//		対話スタート
		while (true) {
			
			String recResult = new SpeechRecWithLED().start(motion, pose);
			Logger.info(TAG, recResult);
			String genResult = Chat.simpleChat(recResult, userId);
			TextToSpeech.speechWithMotion(genResult, sotawish, MotionAsSotaWish.MOTION_TYPE_TALK);
		}
	}
}
