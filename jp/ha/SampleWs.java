package jp.ha;

import java.net.URI;
import java.net.URISyntaxException;

import jp.ha.websocket.MessageListener;
import jp.ha.websocket.WsClient;

public class SampleWs {

    public static void main(String[] args) {
        // TODO 自動生成されたメソッド・スタブ
        try {
            WsClient.on(new MessageListener());
            WsClient client = new WsClient(new URI("ws://192.168.1.36:8080"));
            client.connect();
        } catch (URISyntaxException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
    }

}
