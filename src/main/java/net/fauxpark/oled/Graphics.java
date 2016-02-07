package net.fauxpark.oled;

import net.fauxpark.oled.font.Font;

/**
 * A wrapper around the SSD1306 with some basic graphics methods.
 *
 * @author fauxpark
 */
public class Graphics {
	/**
	 * The SSD1306 OLED display.
	 */
	private SSD1306 ssd1306;

	/**
	 * Display constructor.
	 *
	 * @param ssd1306 The SSD1306 object to use.
	 * @param width The width of the display in pixels.
	 * @param height The height of the display in pixels.
	 * @param channel The SPI channel to use.
	 * @param rstPin The GPIO pin to use for the RST line.
	 * @param dcPin The GPIO pin to use for the D/C line.
	 */
	public Graphics(SSD1306 ssd1306) {
		this.ssd1306 = ssd1306;
	}

	/**
	 * Draw text onto the display.
	 *
	 * @param x The X position to start drawing at.
	 * @param y The Y position to start drawing at.
	 * @param font The font to use.
	 * @param text The text to draw.
	 */
	public void text(int x, int y, Font font, String text) {
		int rows = font.getRows();
		int cols = font.getColumns();
		int[] glyphs = font.getGlyphs();

		for(int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			int p = c * cols;

			for(int col = 0; col < cols; col++) {
				int mask = glyphs[p++];

				for(int row = 0; row < rows; row++) {
					ssd1306.setPixel(x, y + row, (mask & 1) == 1);
					mask >>= 1;
				}

				x++;
			}
		}
	}

	/**
	 * Draw an image onto the display.
	 */
	public void image() {
		// TODO
	}

	/**
	 * Draw a line from one point to another.
	 *
	 * @param x0 The X position of the first point.
	 * @param y0 The Y position of the first point.
	 * @param x1 The X position of the second point.
	 * @param y1 The Y position of the second point.
	 */
	public void line(int x0, int y0, int x1, int y1) {
		int dx = (x1 - x0);
		int dy = (y1 - y0);

		if(dx == 0 && dy == 0) {
			ssd1306.setPixel(x0, y0, true);

			return;
		}

		if(dx == 0) {
			for(int y = Math.min(y0,  y1); y <= Math.max(y1, y0); y++) {
				ssd1306.setPixel(x0, y, true);
			}
		} else if(dy == 0) {
			for(int x = Math.min(x0, x1); x <= Math.max(x1, x0); x++) {
				ssd1306.setPixel(x, y0, true);
			}
		} else if(Math.abs(dx) > Math.abs(dy)) {
			if(dx < 0) {
				int ox = x0;
				int oy = y0;
				x0 = x1;
				y0 = y1;
				x1 = ox;
				y1 = oy;
				dx = (x1 - x0);
				dx = (y1 - y0);
			}

			double coeff = (double) dy / (double) dx;

			for(int x = 0; x <= dx; x++) {
				ssd1306.setPixel(x + x0, y0 + (int) Math.round(x + coeff), true);
			}
		} else if(Math.abs(dx) < Math.abs(dy)) {
			if(dy < 0) {
				int ox = x0;
				int oy = y0;
				x0 = x1;
				y0 = y1;
				x1 = ox;
				y1 = oy;
				dx = (x1 - x0);
				dx = (y1 - y0);
			}

			double coeff = (double) dx / (double) dy;

			for(int y = 0; y <= dy; y++) {
				ssd1306.setPixel(x0 + (int) Math.round(y + coeff), y + y0, true);
			}
		}
	}

	/**
	 * Draw a rectangle.
	 *
	 * @param x The X position of the rectangle.
	 * @param y The Y position of the rectangle.
	 * @param width The width of the rectangle in pixels.
	 * @param height The height of the rectangle in pixels.
	 * @param fill Whether to draw a filled rectangle.
	 */
	public void rectangle(int x, int y, int width, int height, boolean fill) {
		if(fill) {
			for(int i = x; i < width; i++) {
				for(int j = y; j < height; j++) {
					ssd1306.setPixel(i, j, true);
				}
			}
		} else {
			line(x, y, x, y + height);
			line(x, y + height, x + width, y + height);
			line(x + width, y + height, x + width, y);
			line(x + width, y, x, y);
		}
	}

	/**
	 * Draw an arc.
	 *
	 * @param x The X position of the center of the arc.
	 * @param y The Y position of the center of the arc.
	 * @param radius The radius of the arc in pixels.
	 * @param startAngle The starting angle of the arc in degrees.
	 * @param endAngle The ending angle of the arc in degrees.
	 */
	public void arc(int x, int y, int radius, int startAngle, int endAngle) {
		for(int i = startAngle; i <= endAngle; i++) {
			ssd1306.setPixel(x + (int) Math.round(radius * Math.sin(Math.toRadians(i))), y + (int) Math.round(radius * Math.cos(Math.toRadians(i))), true);
		}
	}

	/**
	 * Draw a circle.
	 *
	 * @param x The X position of the center of the circle.
	 * @param y The Y position of the center of the circle.
	 * @param radius The radius of the circle in pixels.
	 */
	public void circle(int x, int y, int radius) {
		arc(x, y, radius, 0, 360);
	}
}