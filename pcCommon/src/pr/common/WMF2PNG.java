package pr.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

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
		try {
        	String svgURI = new File(wmfFile).toURI().toURL().toString();
        	
        	ByteArrayOutputStream svg = getSVG_OutputStream(new TranscoderInput(svgURI));
        	
        	ImageTranscoder it = new JPEGTranscoder();
    		it.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(quality));
    		it.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, new Float(width));
        	
        	ByteArrayOutputStream png = new ByteArrayOutputStream();
        	it.transcode(new TranscoderInput(new ByteArrayInputStream(svg.toByteArray())), new TranscoderOutput(png));
        	String pngFile = "d:/1.jpg";
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
	
	public static InputStream convert(InputStream in, double width) {
		try {
        	ByteArrayOutputStream svg = getSVG_OutputStream(new TranscoderInput(in));
        	
        	ImageTranscoder it = new PNGTranscoder();
        	it.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, new Float(width));
        	ByteArrayOutputStream png = new ByteArrayOutputStream();
        	it.transcode(new TranscoderInput(new ByteArrayInputStream(svg.toByteArray())), new TranscoderOutput(png));
        	
    		return new ByteArrayInputStream(png.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static InputStream convertJPG(InputStream in, double quality, double width) {
		try {
			ByteArrayOutputStream svg = getSVG_OutputStream(new TranscoderInput(in));
        	
        	ImageTranscoder it = new JPEGTranscoder();
    		it.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(quality));
    		it.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, new Float(width));
    		
        	ByteArrayOutputStream png = new ByteArrayOutputStream();
        	it.transcode(new TranscoderInput(new ByteArrayInputStream(svg.toByteArray())), new TranscoderOutput(png));
        	
    		return new ByteArrayInputStream(png.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void svg2png(ByteArrayInputStream svg, float width, String outFile) {
		try {
        	ImageTranscoder it = new PNGTranscoder();
        	it.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, width);
        	ByteArrayOutputStream png = new ByteArrayOutputStream();
        	it.transcode(new TranscoderInput(svg), new TranscoderOutput(png));
        	
        	FileOutputStream outFileOS = new FileOutputStream(outFile);
        	outFileOS.write(png.toByteArray());
        	outFileOS.close();
        	outFileOS.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static ByteArrayOutputStream getSVG_OutputStream(TranscoderInput input) throws TranscoderException {
    	ByteArrayOutputStream svg = new ByteArrayOutputStream();
    	TranscoderOutput output = null;
		try {
			output = new TranscoderOutput(new OutputStreamWriter(svg, "UTF-8"));
			WMFTranscoder transcoder = new WMFTranscoder();
	    	transcoder.transcode(input, output);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	return svg;
	}
}
