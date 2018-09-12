/* 
 * The MIT License
 *
 * Copyright 2018 20182191.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package nl.knokko.gui.texture;

import java.awt.Image;

public class ImageGuiTexture implements GuiTexture {
	
	private final Image image;
	
	public ImageGuiTexture(Image image){
		this.image = image;
	}

	public int getTextureID() {
		throw new UnsupportedOperationException("An ImageGuiTexture is no GLGuiTexture");
	}

	public float getMinU() {
		throw new UnsupportedOperationException("An ImageGuiTexture is no GLGuiTexture");
	}

	public float getMinV() {
		throw new UnsupportedOperationException("An ImageGuiTexture is no GLGuiTexture");
	}

	public float getMaxU() {
		throw new UnsupportedOperationException("An ImageGuiTexture is no GLGuiTexture");
	}

	public float getMaxV() {
		throw new UnsupportedOperationException("An ImageGuiTexture is no GLGuiTexture");
	}

	public Image getImage() {
		return image;
	}

	public int getMinX() {
		return 0;
	}

	public int getMinY() {
		return 0;
	}

	public int getMaxX() {
		return image.getWidth(null) - 1;
	}

	public int getMaxY() {
		return image.getHeight(null) - 1;
	}
}