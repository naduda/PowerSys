package pr.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;

import javafx.scene.image.Image;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.wmf.tosvg.WMFTranscoder;

public class WMF2PNG {
	public static Image convert(String wmfFile) {
		try {
			String svgURI = new File(wmfFile).toURI().toURL().toString();
			ByteArrayOutputStream svg = getSVG_OutputStream(new TranscoderInput(svgURI));

			ImageTranscoder it = new PNGTranscoder();
			it.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, new Float(1000));
			ByteArrayOutputStream png = new ByteArrayOutputStream();
			it.transcode(new TranscoderInput(new ByteArrayInputStream(svg.toByteArray())), new TranscoderOutput(png));
			String pngFile = "d:/1.png";
			FileOutputStream jpgOut = new FileOutputStream(pngFile);
			jpgOut.write(png.toByteArray());
			jpgOut.close();
			jpgOut.flush();

			String pngURL = "file:" + pngFile;
			return new Image(pngURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Image convertJPG(String wmfFile, double quality, double width) {
		String svgURI = "";
		String pngFile = "d:/1.jpg";
		try {
			svgURI = new File(wmfFile).toURI().toURL().toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try (ByteArrayOutputStream svg = getSVG_OutputStream(new TranscoderInput(svgURI));
				ByteArrayOutputStream png = new ByteArrayOutputStream();
				FileOutputStream jpgOut = new FileOutputStream(pngFile);) {
			
			ImageTranscoder it = new JPEGTranscoder();
			it.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(quality));
			it.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, new Float(width));
			it.transcode(new TranscoderInput(new ByteArrayInputStream(svg.toByteArray())), new TranscoderOutput(png));

			jpgOut.write(png.toByteArray());
			jpgOut.close();
			jpgOut.flush();

			String pngURL = "file:" + pngFile;
			return new Image(pngURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static InputStream convert(InputStream in, double width) {
		try (ByteArrayOutputStream svg = getSVG_OutputStream(new TranscoderInput(in));
				ByteArrayOutputStream png = new ByteArrayOutputStream();) {
			
			ImageTranscoder it = new PNGTranscoder();
			it.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, new Float(width));
			it.transcode(new TranscoderInput(new ByteArrayInputStream(svg.toByteArray())), new TranscoderOutput(png));

			return new ByteArrayInputStream(png.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static InputStream convertJPG(InputStream in, double quality, double width) {
		try (ByteArrayOutputStream svg = getSVG_OutputStream(new TranscoderInput(in));
				ByteArrayOutputStream png = new ByteArrayOutputStream();) {

			ImageTranscoder it = new JPEGTranscoder();
			it.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(quality));
			it.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, new Float(width));
			it.transcode(new TranscoderInput(new ByteArrayInputStream(svg.toByteArray())), new TranscoderOutput(png));

			return new ByteArrayInputStream(png.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static ByteArrayOutputStream getSVG_OutputStream(TranscoderInput input) throws TranscoderException {
		TranscoderOutput output = null;
		try (ByteArrayOutputStream svg = new ByteArrayOutputStream();) {
			output = new TranscoderOutput(new OutputStreamWriter(svg, "UTF-8"));
			WMFTranscoder transcoder = new WMFTranscoder();
			transcoder.transcode(input, output);
			return svg;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static InputStream convertWMF2SVG(InputStream in) {
		try (ByteArrayOutputStream output = new ByteArrayOutputStream();) {
			TranscoderInput input = new TranscoderInput(in);

			WMFTranscoder transcoder = new WMFTranscoder();
			transcoder.transcode(input, new TranscoderOutput(output));
			return new ByteArrayInputStream(output.toByteArray());
		} catch (IOException | TranscoderException e) {
			e.printStackTrace();
		}
		return null;
	}
}