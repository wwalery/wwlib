package org.wwlib.vkb;

/**
 *
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public interface VirtualKeyboardListener {

 void keyTyped(int keyCode);

// void keysTyped(String typed);

 void modifierPressed(String modifier);


}
