package jp.vstone.sotatest;

import java.awt.image.BufferedImage;

import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.camera.CameraCapture;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
//import com.jutil.Http.HttpWrapper;
//
//import jp.aka.sample.JSON.JSONMapper;
//import jp.aka.sample.values.SpReqRes;


public class QRCodeTest {
    public static void main(String[] args) {
        CameraCapture cap = null;
        cap = new CameraCapture(CameraCapture.CAP_IMAGE_SIZE_VGA, CameraCapture.CAP_FORMAT_3BYTE_BGR);
        try {
            cap.openDevice("/dev/video0");
            
            String text = null;
            while (text == null) {
                try {
                    cap.snap();
                    CRobotUtil.wait(1000);
                    cap.snap();

                    BufferedImage image = cap.RawtoBufferedImage();
                    LuminanceSource source = new BufferedImageLuminanceSource(image);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                    Reader reader = new MultiFormatReader();
                    Result result = reader.decode(bitmap);

                    BarcodeFormat format = result.getBarcodeFormat();
                    text = result.getText();
                    System.out.println("QRコード検出: " + text);
                    System.out.println("フォーマット: " + format);
                } catch (NotFoundException e) {
                    System.out.println("QRコードが見つかりません。再試行します...");
                    CRobotUtil.wait(2000); // 2秒待機してから再試行
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cap != null) {
                cap.close();
            }
        }
    }
}
