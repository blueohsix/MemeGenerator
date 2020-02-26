package com.projects.memegenerator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

public class Doodles {
	private BufferedImage image = null;
	private int imageHeight;
	private int imageWidth;
	private int textAreaWidth;
	private int stringWidth;
	int edgeDistance = (imageWidth - stringWidth) / 2;

	public static void main(String[] args) {
		Doodles doodle = new Doodles();
		doodle.readImage("/Users/caseyasher/Projects/images/profile.jpg");
		doodle.configureImage("This is branden's happy dog blah blah blah", "Top");
		doodle.configureImage("This is branden's happy dog", "Bottom");
		doodle.writeImage("/Users/caseyasher/Projects/images/Doodles/out.jpg");

	}

	public void readImage(String location) {
		System.out.println("Template Location: " + location);
		try {
			image = ImageIO.read(new File(location));
			System.out.println("Image successfully read");
			imageWidth = image.getWidth();
			imageHeight = image.getHeight();
			System.out.println("Image dimensions: " + imageHeight + " x " + imageWidth);
			textAreaWidth = (int) (imageWidth * .90);
			if ((imageWidth > 700 && imageHeight > 700) || (imageWidth < 700 && imageHeight < 700)) {
				resizeImage(image);
			}
		} catch (IOException e) {
			System.out.println("Image not found at " + location);
		}
	}

	private void resizeImage(BufferedImage toBeResized) {
		image = Scalr.resize(toBeResized, 700);
		imageWidth = image.getWidth();
		imageHeight = image.getHeight();
		textAreaWidth = (int) (imageWidth * .90);
		System.out.println("Resized dimensions: " + imageHeight + " x " + imageWidth);
	}

	public void writeImage(String location) {
		System.out.println("Save Location: " + location);
		File output = new File(location);
		try {
			ImageIO.write(image, "jpg", output);
			System.out.println("Image successfully saved");
		} catch (IOException e) {
			System.out.println("Image unable to be written to " + output);
		}

	}

	public void configureImage(String input, String position) {
		Graphics2D graphics = image.createGraphics();
		Font font = new Font("Impact", Font.PLAIN, 50);
		graphics.setFont(font);
		FontMetrics metrics = graphics.getFontMetrics(font);
		stringWidth = metrics.stringWidth(input);
		System.out.println("String width: " + stringWidth);
		System.out.println("Image width: " + imageWidth);

		if (stringWidth < textAreaWidth) {
			oneLiner(input, position, graphics);
		} else {
			System.out.println("String width is greater than text area width." + "\n" + "Wrapping text...");
			multiLiner(input, position, graphics);
		}

	}

	public void oneLiner(String input, String position, Graphics2D graphics) {
		System.out.println("Writing line to image");
		FontMetrics metrics = graphics.getFontMetrics(graphics.getFont());
		stringWidth = metrics.stringWidth(input);
		edgeDistance = (imageWidth - stringWidth) / 2;
		AffineTransform transform = graphics.getTransform();
		if (position.equalsIgnoreCase("top")) {
			transform.translate(edgeDistance, (int) (imageHeight * .12));
		} else {
			transform.translate(edgeDistance, (int) (imageHeight * .94));
		}

		graphics.transform(transform);
		graphics.setColor(Color.black);
		FontRenderContext frc = graphics.getFontRenderContext();
		TextLayout textLayout = new TextLayout(input, graphics.getFont(), frc);
		Shape outlinedLetters = textLayout.getOutline(null);
		graphics.setStroke(new BasicStroke(5.3f));
		graphics.draw(outlinedLetters);
		graphics.setColor(Color.white);
		graphics.fill(outlinedLetters);

	}

	public void multiLiner(String input, String position, Graphics2D graphics) {
		AffineTransform transform = graphics.getTransform();
		FontMetrics metrics = graphics.getFontMetrics(graphics.getFont());
		int fontHeight = metrics.getHeight();
		System.out.println("FontHeight: " + fontHeight);
		StringBuilder line;

		int lineCount = 1;
		int x = 0;
		while (x < input.length()) {
			stringWidth = 0;
			System.out.println("X: " + x);
			System.out.println("input length: " + input.length());
			line = new StringBuilder();
			for (;stringWidth < textAreaWidth; x++) {
				if(x >= input.length()) {
					break;
				}
				line.append(input.charAt(x));
				System.out.println(line.toString());
				stringWidth = metrics.stringWidth(line.toString());
			}
			System.out.println("Writing line to image");
			edgeDistance = (imageWidth - stringWidth) / 2;
			if (position.equalsIgnoreCase("top")) {
				transform.translate(edgeDistance, fontHeight ^ lineCount);
			} else {
				transform.translate(edgeDistance,(imageHeight - (fontHeight ^ lineCount)));
			}
			graphics.transform(transform);
			graphics.setColor(Color.black);
			FontRenderContext frc = graphics.getFontRenderContext();
			TextLayout textLayout = new TextLayout(line.toString(), graphics.getFont(), frc);
			Shape outlinedLetters = textLayout.getOutline(null);
			graphics.setStroke(new BasicStroke(5.3f));
			graphics.draw(outlinedLetters);
			graphics.setColor(Color.white);
			graphics.fill(outlinedLetters);
			lineCount++;
		}
	}
}