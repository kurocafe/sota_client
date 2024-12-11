package jp.ha.websocket;

import org.json.JSONException;
import org.json.JSONObject;

public class SpeechRecListener extends Listener {
    public static final String CHANNEL = "SPEECH_REC";
    private Boolean isStopRecord = false;
    private String result = null;
    private Boolean isEndRecord = false;
    private Boolean isDetect = false;

    public final Boolean getIsStopRecord() {
        return isStopRecord;
    }

    public final void setIsStopRecord(Boolean isStopRecord) {
        this.isStopRecord = isStopRecord;
    }

    public final String getResult() {
        return result;
    }

    public final void setResult(String result) {
        this.result = result;
    }

    public SpeechRecListener() {
        super();
        setChannel(CHANNEL);
        isStopRecord = false;
        result = null;
        isEndRecord = false;
    }

    @Override
    /**
     * メッセージを受け取ったら送信する
     */
    protected void callback(JSONObject data) {
        try {
            System.out.println("get message:" + data.get("payload").toString());
            JSONObject payload = data.getJSONObject("payload");
            String command = payload.getString("command");
            switch (command) {
                case "start_recognition":
//                    isStopRecord = false;
                    break;
                case "end_of_speech":
                    isStopRecord = true;
                    break;
                case "detect_speech":
                    setIsDetect(true);
                    break;
                case "send_result":
                    result = payload.getString("text");
                    isEndRecord = true;
                    break;
            }
//            WsClient.emit(CHANNEL, data.getString("payload"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Boolean getIsEndRecord() {
        return isEndRecord;
    }

    public void setIsEndRecord(Boolean isEndRecord) {
        this.isEndRecord = isEndRecord;
    }

    public Boolean getIsDetect() {
        return isDetect;
    }

    public void setIsDetect(Boolean isDetect) {
        this.isDetect = isDetect;
    }
}