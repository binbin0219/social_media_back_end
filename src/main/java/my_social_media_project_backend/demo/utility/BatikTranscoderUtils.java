package my_social_media_project_backend.demo.utility;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;

import java.io.*;

public class BatikTranscoderUtils {

    public static byte[] convertSvgToPng(String svgContent) throws IOException, TranscoderException {
        TranscoderInput input = new TranscoderInput(new StringReader(svgContent));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(outputStream);

        // Use Batik PNGTranscoder to convert SVG to PNG
        Transcoder transcoder = new PNGTranscoder();

        // Optional: Set image width/height if needed
        transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, 200f);
        transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, 200f);

        // Perform the conversion
        transcoder.transcode(input, output);

        // Close the stream
        outputStream.flush();
        outputStream.close();

        return outputStream.toByteArray();
    }
}

