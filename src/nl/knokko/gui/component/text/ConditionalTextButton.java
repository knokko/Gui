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
package nl.knokko.gui.component.text;

import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.util.Condition;
import nl.knokko.gui.util.TextBuilder.Properties;

public class ConditionalTextButton extends TextButton {
	
	protected Condition condition;

	public ConditionalTextButton(String text, Properties properties, Properties hoverProperties, Runnable action, Condition condition) {
		super(text, properties, hoverProperties, action);
		this.condition = condition;
	}
	
	@Override
	public void click(float x, float y, int button){
		if(condition.isTrue())
			super.click(x, y, button);
	}
	
	@Override
	public void render(GuiRenderer renderer){
		if(condition.isTrue())
			super.render(renderer);
	}
}