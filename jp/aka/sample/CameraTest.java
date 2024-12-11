package jp.aka.sample;
import java.awt.Color;

import com.jutil.Http.HttpWrapper;
import com.jutil.Logger.Logger;

import jp.aka.sample.JSON.JSONMapper;
import jp.aka.sample.values.QRReadRes;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotMotion;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.camera.CameraCapture;
import jp.vstone.sotatalk.MotionAsSotaWish;

public class CameraTest {
	static String TAG = "MotionSample";
	static String API_HOME = "http://150.59.20.116:8000";
	static String QR_PATH = "./qr.jpg";
	static CRobotPose pose;
	static CRobotMotion motion;
	
	public static void main(String args[]) {
		CRobotUtil.Log(TAG, "Start " + TAG);
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
//		sotawish.StartIdling();
		 
		CameraCapture cap = null;
		pose = new CRobotPose();
		pose.setLED_Sota(Color.BLACK, Color.BLACK, 50, Color.BLACK);
		motion.play(pose, 1000);
    	   motion.waitEndinterpAll();
		cap = new CameraCapture(CameraCapture.CAP_IMAGE_SIZE_HD_1080, CameraCapture.CAP_FORMAT_3BYTE_BGR);
		try {
			int count = 0;
			while (count < 60) {
				cap.openDevice("/dev/video0");
				cap.snapGetFile(QR_PATH);
				String url = API_HOME + "/qr_read";
				QRReadRes response = null;
				cap.close();
				CRobotUtil.Log("main", "send");
				response = JSONMapper.mapper.readValue(HttpWrapper.uploadFile(QR_PATH, url), QRReadRes.class);
				CRobotUtil.Log(response.getResponse(), "wait end");
				
				if (response.getResponse() != null) {
					break;
				}
		       CRobotUtil.wait(1000);
		       count++; 
			}
					

//	        	cap.snap();
				cap.close();
//		    //. 画像読み込み
//	        	BufferedImage image = cap.RawtoBufferedImage();
//	        	LuminanceSource source = new BufferedImageLuminanceSource( image );
//	        	BinaryBitmap bitmap = new BinaryBitmap( new HybridBinarizer( source ) );
//
//	        	//. デコード
//	        	Reader reader = new MultiFormatReader();
//	        	Result result = reader.decode( bitmap );
//
//	        	//. バーコードフォーマット
//	        	BarcodeFormat format = result.getBarcodeFormat();
//
//	        	//. バーコードコンテンツ（読み取り結果）
//	        	String text = result.getText();
//	        	System.out.println("result2 " + text);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
