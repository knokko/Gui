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

import nl.knokko.gui.mousecode.MouseCode;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.texture.GuiTexture;

public class ImageButton extends SimpleImageComponent {
	
	protected GuiTexture hoverTexture;
	protected Runnable clickAction;

	public ImageButton(GuiTexture texture, GuiTexture hoverTexture, Runnable clickAction) {
		super(texture);
		this.hoverTexture = hoverTexture;
		this.clickAction = clickAction;
	}
	
	@Override
	public void click(float x, float y, int button) {
		if(button == MouseCode.BUTTON_LEFT)
			clickAction.run();
	}
	
	@Override
	public void render(GuiRenderer renderer) {
		if(state.isMouseOver())
			renderer.renderTexture(hoverTexture, 0, 0, 1, 1);
		else
			super.render(renderer);
	}
}