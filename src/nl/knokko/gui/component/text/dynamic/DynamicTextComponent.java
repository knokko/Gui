package nl.knokko.gui.component.text.dynamic;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.AbstractGuiComponent;
import nl.knokko.gui.component.state.GuiComponentState;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.util.CharBuilder;
import nl.knokko.gui.util.TextBuilder.Properties;

public class DynamicTextComponent extends AbstractGuiComponent {
	
	protected SingleText text;

	public DynamicTextComponent(String text, Properties props) {
		this.text = new SingleText(text, props);
	}
	
	public void setText(String newText) {
		text.setText(newText);
	}
	
	public void setProperties(Properties newProps) {
		text.props = newProps;
		text.updateTextures();
	}
	
	@Override
	public void init() {
		text.init(state);
	}

	@Override
	public void update() {}

	@Override
	public void render(GuiRenderer renderer) {
		text.render(renderer);
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
	
	protected static class SingleText {
		
		private CharBuilder charBuilder;
		
		private String text;
		private Properties props;
		
		private GuiColor borderColor;
		private GuiColor backgroundColor;
		
		private GuiTexture[] textures;
		private float[] xCoords;
		private float minTextY;
		private float maxTextY;
		
		protected SingleText(String text, Properties props) {
			this.text = text;
			this.props = props;
		}
		
		protected void setText(String newText) {
			text = newText;
			updateTextures();
		}
		
		protected void setProperties(Properties newProps) {
			props = newProps;
			updateTextures();
		}
		
		protected void init(GuiComponentState state) {
			charBuilder = state.getWindow().getCharBuilder();
			updateTextures();
		}
		
		protected void updateTextures() {
			textures = new GuiTexture[text.length()];
			int totalWidth = 0;
			for (int index = 0; index < textures.length; index++) {
				textures[index] = charBuilder.getTexture(text.charAt(index), props.textColor, props.font);
				totalWidth += textures[index].getWidth();
			}
			if (totalWidth != 0) {
				xCoords = new float[textures.length + 1];
				float x = props.borderX + props.marginX;
				float widthFactor = 1 - 2 * props.borderX - 2 * props.marginX;
				for (int index = 0; index < textures.length; index++) {
					xCoords[index] = x;
					x += ((float) textures[index].getWidth() / totalWidth) * widthFactor;
					xCoords[index + 1] = x; 
				}
			} else {
				xCoords = null;
			}
			backgroundColor = new SimpleGuiColor(props.backgroundColor.getRGB());
			borderColor = new SimpleGuiColor(props.borderColor.getRGB());
			minTextY = props.borderY + props.marginY;
			maxTextY = 1 - minTextY;
		}
		
		protected void render(GuiRenderer renderer) {
			renderer.clear(backgroundColor);
			renderer.fill(borderColor, 0, 0, 1, props.borderY);
			renderer.fill(borderColor, 0, 0, props.borderX, 1);
			renderer.fill(borderColor, 0, 1 - props.borderY, 1, 1);
			renderer.fill(borderColor, 1 - props.borderX, 0, 1, 1);
			if (xCoords != null) {
				for (int index = 0; index < textures.length; index++)
					renderer.renderTexture(textures[index], xCoords[index], minTextY, xCoords[index + 1], maxTextY);
			}
		}
	}
}