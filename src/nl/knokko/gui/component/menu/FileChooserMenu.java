package nl.knokko.gui.component.menu;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;

import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.text.ConditionalTextButton;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.util.TextBuilder.Properties;
import sun.awt.shell.ShellFolder;

public class FileChooserMenu extends GuiMenu {
	
	public static final Properties CANCEL_PROPERTIES = Properties.createButton(new Color(200, 200, 200), new Color(150, 150, 250));
	public static final Properties CANCEL_HOVER_PROPERTIES = Properties.createButton(Color.WHITE, new Color(200, 200, 255));
	
	public static final Properties SELECT_PROPERTIES = Properties.createButton(new Color(150, 150, 200), new Color(120, 120, 250));
	public static final Properties SELECT_HOVER_PROPERTIES = Properties.createButton(new Color(100, 100, 255), Color.BLUE);
	
	protected final FileListener listener;
	protected final FileFilter filter;
	protected final GuiComponent returnMenu;
	protected FileList list;
	
	protected File selectedFile;
	protected File directory;
	protected File parentDirectory;

	public FileChooserMenu(GuiComponent returnMenu, FileListener listener, FileFilter filter) {
		this.returnMenu = returnMenu;
		this.listener = listener;
		this.filter = filter;
		this.directory = new File("").getAbsoluteFile();
		this.parentDirectory = directory.getParentFile();
	}

	@Override
	protected void addComponents() {
		addComponent(new TextButton("Cancel", CANCEL_PROPERTIES, CANCEL_HOVER_PROPERTIES, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.2f, 0.05f, 0.35f, 0.15f);
		addComponent(new ConditionalTextButton("Select", SELECT_PROPERTIES, SELECT_HOVER_PROPERTIES, () -> {
			listener.onChoose(selectedFile);
			state.getWindow().setMainComponent(returnMenu);
		}, () -> {
			return selectedFile != null;
		}), 0.6f, 0.05f, 0.75f, 0.15f);
		addComponent(new ConditionalTextButton("Go up", CANCEL_PROPERTIES, CANCEL_HOVER_PROPERTIES, () -> {
			setDirectory(parentDirectory);
		}, () -> {
			return parentDirectory != null;
		}), 0.25f, 0.85f, 0.35f, 0.95f);
		list = new FileList();
		addComponent(list, 0.05f, 0.2f, 0.95f, 0.8f);
	}
	
	protected void setDirectory(File newDirectory) {
		directory = newDirectory;
		parentDirectory = directory.getParentFile();
		list.setDirectory();
	}
	
	public static final Properties FILE_NAME_PROPERTIES = Properties.createLabel(Color.BLACK, Color.WHITE, 512, 128);
	public static final Properties FILE_NAME_HOVER_PROPERTIES = Properties.createLabel(new Color(50, 50, 50), new Color(150, 150, 255), 512, 128);
	public static final Properties FOLDER_NAME_PROPERTIES = Properties.createLabel(Color.BLACK, Color.WHITE, 512, 128);
	public static final Properties FOLDER_NAME_HOVER_PROPERTIES = Properties.createLabel(new Color(50, 50, 50), new Color(150, 150, 255), 512, 128);
	
	protected class FileList extends GuiMenu {

		@Override
		protected void addComponents() {
			setDirectory();
		}
		
		protected void setDirectory() {
			clearComponents();
			File[] files = directory.listFiles();
			int index = 0;
			for(File file : files) {
				try {
					if(file.isDirectory() || filter.accept(file)) {
						Image icon = ShellFolder.getShellFolder(file).getIcon(true);
						BufferedImage image = new BufferedImage(icon.getWidth(null), icon.getHeight(null), BufferedImage.TYPE_INT_ARGB);
						Graphics2D g = image.createGraphics();
						g.drawImage(icon, 0, 0, null);
						g.dispose();
						addComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(image)), 0f, 0.9f - index * 0.1f, 0.1f, 1f - index * 0.1f);
						if(file.isDirectory()) {
							addComponent(new TextButton(file.getName(), FILE_NAME_PROPERTIES, FILE_NAME_HOVER_PROPERTIES, () -> {
								FileChooserMenu.this.setDirectory(file);
							}), 0.15f, 0.9f - index * 0.1f, 1f, 1f - index * 0.1f);
						} else {
							addComponent(new TextButton(file.getName(), FOLDER_NAME_PROPERTIES, FOLDER_NAME_HOVER_PROPERTIES, () -> {
								selectedFile = file;
							}), 0.15f, 0.9f - index * 0.1f, 1f, 1f - index * 0.1f);
						}
						index++;
					}
				} catch(FileNotFoundException fnfe) {
					System.out.println("Couldn't 'find'" + file + ": " + fnfe.getMessage());
				}
			}
		}
	}
	
	public static interface FileListener {
		
		void onChoose(File file);
	}
	
	public static interface FileFilter {
		
		boolean accept(File file);
	}
}