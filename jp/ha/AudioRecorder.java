package jp.ha;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class AudioRecorder {

    TargetDataLine line;

    // 録音開始メソッド
    void start() {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            // 録音ラインの取得
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            System.out.println("録音を開始しました。停止するにはEnterキーを押してください。");

            // 別スレッドで録音を実行
            Thread thread = new Thread(() -> {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[4096];
                    while (!Thread.currentThread().isInterrupted()) {
                        int bytesRead = line.read(buffer, 0, buffer.length);
                        if (bytesRead > 0) {
                            out.write(buffer, 0, bytesRead);
                        }
                    }
                    // バイトデータの取得
                    byte[] audioData = out.toByteArray();
                    // 必要に応じてバイトデータを処理
                    processAudioData(audioData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            thread.start();

            // Enterキーが押されるまで待機
            System.in.read();
            thread.interrupt();

            finish();

        } catch (LineUnavailableException | IOException ex) {
            ex.printStackTrace();
        }
    }

    // 録音停止メソッド
    void finish() {
        line.stop();
        line.close();
        System.out.println("録音を停止しました。");
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

    // バイトデータを処理するメソッド
    void processAudioData(byte[] audioData) {
        // 例として、取得したバイトデータをファイルに書き込む
        try (FileOutputStream fos = new FileOutputStream("record.raw")) {
            fos.write(audioData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // その他の処理をここに追加
        // バイト配列をAudioInputStreamに変換
        ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
        AudioInputStream ais = new AudioInputStream(bais, getAudioFormat(), audioData.length / getAudioFormat().getFrameSize());
        // WAVファイルとして保存
        try {
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File("record.wav"));
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
    }

    // メインメソッド
    public static void main(String[] args) {
        new AudioRecorder().start();
    }
}
