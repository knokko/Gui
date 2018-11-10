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
package nl.knokko.gui.component.simple;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.util.Condition;

public class ConditionalColorComponent extends SimpleColorComponent {
	
	protected Condition condition;

	public ConditionalColorComponent(GuiColor color, Condition condition) {
		super(color);
		this.condition = condition;
	}
	
	@Override
	public void render(GuiRenderer renderer){
		if(condition.isTrue())
			super.render(renderer);
	}
}