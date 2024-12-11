package jp.ha;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import com.jutil.Logger.Logger;

import jp.ha.utils.JsonIo;
import jp.ha.websocket.SpeechRecListener;
import jp.ha.websocket.WsClient;


class SpRecConfig {
    private String SERVER_HOME;

    public String getSERVER_HOME() {
        return SERVER_HOME;
    }

    public void setSERVER_HOME(String sERVER_HOME) {
        SERVER_HOME = sERVER_HOME;
    }
}

public class SpeechRec {
    private static final String TAG = "SpeechRec";
    TargetDataLine line;
    WsClient client;
    SpeechRecListener listener = null;

    private static final Path CONFIG_PATH = Paths.get("sp_rec_config.json");
    private static SpRecConfig config;

    public static void setUpConfigs() {
        config = JsonIo.load(CONFIG_PATH, SpRecConfig.class);
        if (config == null) {
            config = new SpRecConfig();
        }

        boolean isFin = false;

        Scanner scan = new Scanner(System.in);
        while (!isFin) {
            Logger.info(TAG, "setup API Address press 1: SP_REC_HOME=", config.getSERVER_HOME());
            Logger.info(TAG, "press 4: FIN");
            int num1 = scan.nextInt();
            switch (num1) {
                case 1:
                    Logger.info(TAG, "update DB_HOME");
                    break;
                case 2:
                    Logger.info(TAG, "update API_HOME");
                    break;
                case 3:
                    Logger.info(TAG, "update TALK_HOME");
                    break;
                default:
                    isFin = true;
                    break;
            }
            if (isFin) {
                break;
            }
            int fragment1 = 0;
            int fragment2 = 0;
            int fragment3 = 0;
            int fragment4 = 0;
            int fragment5 = 0;

            Logger.info(TAG, "input part1 XXX.000.0.00:0000");
            fragment1 = scan.nextInt();
            Logger.info(TAG, "input part2 ", fragment1, ".XXX.0.00:0000");
            fragment2 = scan.nextInt();
            Logger.info(TAG, "input part3 ", fragment1, ".", fragment2, ".X.00:0000");
            fragment3 = scan.nextInt();
            Logger.info(TAG, "input part4 ", fragment1, ".", fragment2, ".", fragment3, ".XX:0000");
            fragment4 = scan.nextInt();
            Logger.info(TAG, "input part4 ", fragment1, ".", fragment2, ".", fragment3, ".", fragment4, ":XXXX");
            fragment5 = scan.nextInt();
            switch (num1) {
                case 1:
                    config.setSERVER_HOME("ws://" + fragment1 + "." + fragment2 + "." + fragment3 + "." + fragment4 + ":"
                            + fragment5);
                    Logger.info(TAG, "update DB_HOME" + config.getSERVER_HOME());
                    break;
            }
        }

        JsonIo.dump(CONFIG_PATH, config);
        // scan.close(); # クローズしたらだめ
    }

    // 録音開始メソッド
    public String start() {
        try {
            listener = new SpeechRecListener();
            WsClient.on(listener);
            client = new WsClient(new URI(config.getSERVER_HOME()));
            client.connect();
        } catch (URISyntaxException e1) {
            // TODO 自動生成された catch ブロック
            e1.printStackTrace();
        }
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            // 録音ラインの取得
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            Logger.info(TAG, "録音を開始しました");

            // 別スレッドで録音を実行
            Thread thread = new Thread(() -> {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[8192]; // NOTE: 小さなノイズに対処するためにbufferは大きめに取っておく
                    while (!Thread.currentThread().isInterrupted()) {
                        if (listener.getIsEndRecord()) {
                            break;
                        }
                        if (listener.getIsStopRecord()) {
                            continue;
                        }
                        int bytesRead = line.read(buffer, 0, buffer.length);
                        if (bytesRead > 0) {
                            out.write(buffer, 0, bytesRead);
                            WsClient.emitByte(Arrays.copyOf(buffer, bytesRead));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            thread.start();

            // Record終了フラグが来るまで待つ
            while (!listener.getIsEndRecord()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO 自動生成された catch ブロック
                    e.printStackTrace();
                }
            }

            finish();
            client.disconnect();
            WsClient.off(listener);

            return listener.getResult();

        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // 録音停止メソッド
    void finish() {
        line.stop();
        line.close();
        Logger.info(TAG, "録音を停止しました。");
    }

    // オーディオフォーマットの設定
    AudioFormat getAudioFormat() {
        float sampleRate = 16000; // サンプルレート
        int sampleSizeInBits = 16; // ビット深度
        int channels = 2; // チャンネル数（ステレオ）
        boolean signed = true; // 符号付き
        boolean bigEndian = false; // エンディアン

        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    // メインメソッド
    public static void main(String[] args) {
        SpeechRec.setUpConfigs();

        Logger.info(TAG, new SpeechRec().start());

        Logger.info(TAG, new SpeechRec().start());
    }
}
