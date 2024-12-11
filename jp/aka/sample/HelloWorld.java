package jp.aka.sample;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jutil.Http.HttpWrapper;
import com.jutil.Logger.Logger;

import jp.aka.sample.JSON.JSONMapper;
import jp.aka.sample.values.HelloWorldRes;
import jp.aka.sample.values.MessageReq;
import jp.vstone.RobotLib.CRobotUtil;

public class HelloWorld {
	private static String TAG = "HELLO_WORLD";
	public static void main(String[] args) {
		CRobotUtil.Log(TAG, "!");
		CRobotUtil.Err(TAG, "?");
		
		Logger.info(TAG, "!!!");

		try {
			HelloWorldRes response = getHelloWorld();
			String responseText = JSONMapper.mapper.writeValueAsString(response);
			Logger.info(TAG, "response", response.getMessage(), responseText);
			
			MessageReq request = new MessageReq();
			request.setMessage("heiこんにちは");
			request.setId(0);
			String url = "http://192.168.1.41:8000/message";
			String res = HttpWrapper.sendJSON(JSONMapper.mapper.writeValueAsString(request), url);
			Logger.info(TAG, res);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static HelloWorldRes getHelloWorld() throws IOException, JsonProcessingException, IOException {
		String url = "http://192.168.1.41:8000";
		HelloWorldRes response = JSONMapper.mapper.readValue(HttpWrapper.createGetReq(url), HelloWorldRes.class);
		return response;
	}
}
