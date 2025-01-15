package jp.aka.client;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.jutil.Http.HttpWrapper;
import com.jutil.Logger.Logger;

import jp.aka.sample.JSON.JSONMapper;
import jp.aka.sample.values.GenerateReq;
import jp.aka.sample.values.GenerateRes;


public class Search {
	static final String API_HOME = "http://150.59.20.116:8000"; // サーバーPC
	private static final String TAG = "Search";
	
	public static String keyword(long userId) {
		GenerateReq requestGen = new GenerateReq();
//		requestGen.setUser_message(inputText);
		requestGen.setUser_id(userId);
		
		String url = API_HOME + "/search";
		GenerateRes responseGen = null;
		
		try {
			responseGen = JSONMapper.mapper.readValue(HttpWrapper.sendJSON(JSONMapper.mapper.writeValueAsString(requestGen), url), GenerateRes.class);
			Logger.info(TAG, "gen=", responseGen.getResponse());
			return responseGen.getResponse();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
}
