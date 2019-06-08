# Gui
A java library that helps building menus that will behave almost equally in AWT and LWJGL and can quickly switch

I created this library to help with building Graphical User Interfaces. This library is made to do (almost) the same on AWT windows as LWJGL windows.
Applications that use this library can switch between them by only changing 1 or 2 lines in the code (1 line for the import and the other 1 for the right constructor call).

To use this library/framework:

GuiWindow window = new AWTGuiWindow();

//or new GLGuiWindow() if you would rather use LWJGL and you added my project GLGui as well

window.setMainComponent(new YourCoreComponent());

// YourCoreComponent should be a class that extends GuiMenu and override addComponents()

window.open("A fancy title", 800, 600, true);//title, width, height, showBorder

window.run(60);//preferred frames per second
