package jp.aka.sample;
import javax.sound.sampled.*;

public class MicCheck {
    public static void main(String[] args) {
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        System.out.println("Available mixers:");
        for (Mixer.Info info : mixerInfos) {
            Mixer mixer = AudioSystem.getMixer(info);
            Line.Info[] lineInfos = mixer.getSourceLineInfo();
            for (Line.Info lineInfo : lineInfos) {
                if (lineInfo instanceof DataLine.Info) {
                    DataLine.Info dataLineInfo = (DataLine.Info) lineInfo;
                    if (dataLineInfo.getLineClass().equals(TargetDataLine.class)) {
                        System.out.println("Microphone found: " + info.getName());
                    }
                }
            }
        }
    }
}
