package nl.knokko.gui.component.text.dynamic;

import nl.knokko.gui.component.AbstractGuiComponent;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.util.TextBuilder.Properties;

public class DynamicTextComponent extends AbstractGuiComponent {
	
	private String text;
	private Properties props;
	
	private GuiTexture[] textures;

	public DynamicTextComponent(String text, Properties props) {
		this.text = text;
		this.props = props;
	}
	
	protected void updateTextures() {
		textures = new GuiTexture[text.length()];
		for (int index = 0; index < textures.length; index++)
			textures[index] = state.getWindow().getCharBuilder().getTexture(text.charAt(index), props.textColor, props.font);
	}

	@Override
	public void init() {
		updateTextures();
	}

	@Override
	public void update() {}

	@Override
	public void render(GuiRenderer renderer) {
		float charWidth = 1f / textures.length;
		for (int index = 0; index < textures.length; index++)
			renderer.renderTexture(textures[index], index * charWidth, 0, (index + 1) * charWidth, 1);
	}

	@Override
	public void click(float x, float y, int button) {}

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