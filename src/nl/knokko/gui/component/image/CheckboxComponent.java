/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
 *  This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package nl.knokko.gui.component.image;

import nl.knokko.gui.component.AbstractGuiComponent;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.texture.loader.GuiTextureLoader;

public class CheckboxComponent extends AbstractGuiComponent {
	
	protected static GuiTexture baseImage;
	protected static GuiTexture hoverImage;
	protected static GuiTexture checkedImage;
	protected static GuiTexture checkedHoverImage;
	
	protected boolean checked;

	public CheckboxComponent(boolean startChecked) {
		checked = startChecked;
	}
	
	@Override
	public void init() {
		if (baseImage == null) {
			GuiTextureLoader loader = state.getWindow().getTextureLoader();
			baseImage = loader.loadTexture("nl/knokko/gui/images/icons/checkbox_base.png");
			hoverImage = loader.loadTexture("nl/knokko/gui/images/icons/checkbox_hover.png");
			checkedImage = loader.loadTexture("nl/knokko/gui/images/icons/checkbox_checked.png");
			checkedHoverImage = loader.loadTexture("nl/knokko/gui/images/icons/checkbox_checked_hover.png");
		}
	}
	
	@Override
	public void click(float x, float y, int button) {
		checked = !checked;
	}
	
	@Override
	public void render(GuiRenderer renderer) {
		if (state.isMouseOver()) {
			if (checked)
				renderer.renderTexture(checkedHoverImage, 0, 0, 1, 1);
			else
				renderer.renderTexture(hoverImage, 0, 0, 1, 1);
		} else {
			if (checked)
				renderer.renderTexture(checkedImage, 0, 0, 1, 1);
			else
				renderer.renderTexture(baseImage, 0, 0, 1, 1);
		}
	}
	
	public boolean isChecked() {
		return checked;
	}
	
	public void check() {
		checked = true;
	}
	
	public void uncheck() {
		checked = false;
	}
	
	public void check(boolean value) {
		checked = value;
	}

	@Override
	public void update() {}

	@Override
	public void clickOut(int button) {}

	@Override
	public boolean scroll(float amount) {
		return false;
	}

	@Override
	public void keyPressed(int keyCode) {}

	@Override
	public void keyPressed(char character) {}

	@Override
	public void keyReleased(int keyCode) {}
}