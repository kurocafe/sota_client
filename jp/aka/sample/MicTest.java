package jp.aka.sample;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.jutil.Http.HttpWrapper;
import com.jutil.Logger.Logger;

import jp.aka.sample.JSON.JSONMapper;
import jp.aka.sample.values.GenerateReq;
import jp.aka.sample.values.GenerateRes;
import jp.aka.sample.values.SpReqRes;
import jp.vstone.RobotLib.*;
import jp.vstone.camera.CameraCapture;
import jp.vstone.sotatalk.MotionAsSotaWish;

/**
 * VSMDを使用し、モーション再生・音声再生するサンプル
 * 
 * @author Vstone
 *
 */
public class MicTest {
	static final String TAG = "MotionSample";
	static final String RECPATH = "./test_rec.wav";
	static final String API_HOME2 = "http://192.168.1.41:8000"; // ローカルPC
	static final String API_HOME = "http://150.59.20.116:8800"; // サーバーPC

	static final String TTS_PATH = "./tts.wav";

	public static void main(String args[]) {
		CRobotUtil.Log(TAG, "Start " + TAG);

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

		CRobotUtil.Log(TAG, "Mic Recording Test");
		CPlayWave.PlayWave_wait("sound/start_rec_test_ex2.wav");

		while (true) {
			// 音声ファイル録音
			CRecordMic mic = new CRecordMic();
			mic.startRecording(RECPATH, 5000);
			CRobotUtil.Log(TAG, "wait end");
			mic.waitend();

			String url = API_HOME + "/sp_rec";
			SpReqRes response = null;
			try {
				response = JSONMapper.mapper.readValue(HttpWrapper.uploadFile(RECPATH, url), SpReqRes.class);

				Logger.info(TAG, "rec=", response.getResponse());

				GenerateReq request_gen = new GenerateReq();
				request_gen.setUser_message(response.getResponse());

				String url2 = API_HOME + "/generate";
				GenerateRes response_gen = null;
				response_gen = JSONMapper.mapper.readValue(
						HttpWrapper.sendJSON(JSONMapper.mapper.writeValueAsString(request_gen), url2),
						GenerateRes.class);
				Logger.info(TAG, "gen=", response_gen.getResponse());

				int speakerId = 0;
				tts(response_gen.getResponse(), speakerId, sotawish, MotionAsSotaWish.MOTION_TYPE_TALK);

				Logger.info(TAG, response_gen);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// CRobotUtil.Log(TAG, "Spk Play Test");
			// 音声ファイル再生
			// CPlayWav0e.PlayWave_wait("./test_rec.wav");

			// 音声ファイル再生
			// raw Waveファイルのみ対応
		}

//		CPlayWave.PlayWave("sound/end_test_ex.wav");			
//		CRobotUtil.wait(2000);
	}

	public static byte[] tts_req(String text, int speakerId) throws IOException {
		@SuppressWarnings("deprecation")
		String ttsUrl = API_HOME + "/tts?text=" + URLEncoder.encode(text) + "&speaker_id=" + speakerId;
		return HttpWrapper.createGetReqByByte(ttsUrl);
	}

	public static void tts(String text, int speakerId, MotionAsSotaWish sotawish, String motion) throws IOException {
		byte[] response = tts_req(text, speakerId);

		// ファイルに一旦保存
		BufferedOutputStream bf = new BufferedOutputStream(new FileOutputStream(TTS_PATH));
		bf.write(response);
		bf.flush();
		bf.close();
//		CPlayWave.PlayWave_wait(TTS_PATH);
		sotawish.SayFile(TTS_PATH, motion);
	}
}
