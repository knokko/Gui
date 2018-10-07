package nl.knokko.gui.component.image;

import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.util.Condition;

public class ConditionalImageButton extends ImageButton {
	
	protected Condition condition;

	public ConditionalImageButton(GuiTexture texture, GuiTexture hoverTexture, Runnable clickAction, Condition condition) {
		super(texture, hoverTexture, clickAction);
		this.condition = condition;
	}
	
	@Override
	public void render(GuiRenderer renderer) {
		if(condition.isTrue())
			super.render(renderer);
	}
	
	@Override
	public void click(float x, float y, int button) {
		if(condition.isTrue())
			super.click(x, y, button);
	}
}