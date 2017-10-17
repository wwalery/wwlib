package org.wwlib.vkb.swing;

import org.wwlib.vkb.VirtualKeyboardData;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wwlib.vkb.VirtualKeyboardListener;
import org.wwlib.vkb.VirtualKeyboardResources;

/**
 *
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
@SuppressWarnings("serial")
public class VirtualKeyboard extends JPanel implements ActionListener {

// private final static int PREFERRED_WIDTH = 50;
// private final static int PREFERRED_HEIGHT = 50;
 private final static int PREFERRED_FONT_SIZE = 30;

 private static final Logger log = LoggerFactory.getLogger(VirtualKeyboard.class);

 private final List<VirtualKeyboardListener> listeners;
 private JButton[][] buttons;
 private String currentModifier;
// private int modificator;
// private int buttonWidth;
// private int buttonHeight;
 private Font buttonFont;
 private Dimension size;
// private final String keyboardType;
 private VirtualKeyboardData keyboard;

 public VirtualKeyboard(VirtualKeyboardData keyboard) {
  super();
  try {
   UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
  } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
   log.error("Error on set virtual keyboard look and feel", ex);
  }
//  buttonWidth = PREFERRED_WIDTH;
//  buttonHeight = PREFERRED_HEIGHT;
//  this.keyboardType = keyboardType;
//  modificator = 0;
  listeners = new ArrayList<>();
  this.keyboard = keyboard;
  size = keyboard.getSize();
  currentModifier = keyboard.getKeyboard().keySet().iterator().next();
  buttonFont = new Font(keyboard.getSetup().getButtonFontName(), 0, keyboard.getSetup().getButtonFontSize() > 0 ? keyboard.getSetup().getButtonFontSize() : PREFERRED_FONT_SIZE);
 }

 public void init() throws Exception {
  setFont(buttonFont);
  String[][] rows = keyboard.getKeyboard().get(currentModifier);
  setLayout(new GridLayout(rows.length, 1));
//  setLayout(new GridBagLayout()); //rows.length, 1));
// search max column
  int maxColumns = rows[0].length;
  for (int i = 1; i < rows.length; i++) {
   maxColumns = maxColumns > rows[i].length ? maxColumns : rows[i].length;
  }
//  GridBagConstraints c = new GridBagConstraints();
//  buttons = JButton[rows]
  buttons = new JButton[rows.length][maxColumns];
  for (int i = 0; i < rows.length; i++) {
//   c.gridy = i;
   String[] row = rows[i];
//   c.weightx = (double) row.length / maxColumns;
//   JPanel p = new JPanel(new GridLayout(1, row.length));
   JPanel p = new JPanel();
   p.setLayout(new GridLayout());
//  c.fill = GridBagConstraints.HORIZONTAL;
   for (int j = 0; j < row.length; j++) {
    String btn = row[j];
    if (btn.isEmpty()) {
     p.add(new JLabel(""));
//     add(new JLabel(""));
    } else {
     JButton b = new JButton();
     b.setFont(buttonFont);
//    b.setPreferredSize(new Dimension(100, 50));
//    if (STR_SHIFT.equals(btn) || STR_BACKSPACE.equals(btn)) {
//     b.setPreferredSize(new Dimension(100, PREFERRED_HEIGHT));
//    } else {
//     b.setPreferredSize(new Dimension(this.buttonWidth, this.buttonHeight));
//    }
     b.addActionListener(this);
//     c.gridwidth = btn.length();
     p.add(b);
//     add(b,c);
     buttons[i][j] = b;
    }
   }
   add(p);
  }
  fillButtons();
  setSize(size);
  setPreferredSize(size);
  setMinimumSize(size);
  setMaximumSize(size);
 }

 /**
  * Change language and shift state.
  */
 private void fillButtons() throws Exception {
  String[][] rows = keyboard.getKeyboard().get(currentModifier);
  if (buttons.length < rows.length) {
   throw new Exception(String.format("Created buttons rows [%s] less than in current keyboard: %s", buttons.length, rows.length));
  }
  for (int i = 0; i < rows.length; i++) {
   String[] row = rows[i];
   if (buttons[i].length < row.length) {
    throw new Exception(String.format("Created buttons in row %s - %s less than in current keyboard: %s", i + 1, buttons[i].length, row.length));
   }
   for (int j = 0; j < row.length; j++) {
    String btn = "";
    try {
     btn = row[j];
    } catch (Throwable ex) {
     log.error("Invalid virtual keyboard element: " + i + ", " + j);
    }
    if (!btn.isEmpty()) {
     buttons[i][j].setText(btn);
//     if (btn.length() >= 1) {
//     }
    }

//    Dimension dim = buttons[i][j].getPreferredSize();
//    if (dim.width < PREFERRED_WIDTH) {
//     dim.width = PREFERRED_WIDTH;
//    }
//    dim.height = PREFERRED_HEIGHT;
//    buttons[i][j].setSize(dim);
   }
  }
 }

 @Override
 public void actionPerformed(ActionEvent e) {
  if (e.getSource() instanceof JButton) {
   try {
    JButton b = (JButton) e.getSource();
    int keyCode = -1;
    String keys = null;
// process special keys
    if (keyboard.getSetup().getReplaceKey().containsKey(b.getText())) {
     switch (keyboard.getSetup().getReplaceKey().get(b.getText())) {
      case "\b":
//       buffer.addChar(KeyInputDeviceType.VIRTUAL_KEYBOARD, (char) KeyEvent.VK_BACK_SPACE);
       keyCode = KeyEvent.VK_BACK_SPACE;
       break;
      case "\n":
       keyCode = KeyEvent.VK_ENTER;
       break;
      case "^[":
       keyCode = KeyEvent.VK_ESCAPE;
//       buffer.clear();
       break;
// Shift
      case "^O":
       if (currentModifier.contains(b.getText())) {
        currentModifier = currentModifier.replace(b.getText(), "");
       } else {
        currentModifier = b.getText() + currentModifier;
       }
       fillButtons();
       for (VirtualKeyboardListener listener : listeners) {
        listener.modifierPressed(b.getText());
       }
       break;
      default:
       if (b.getText().length() == 1) {
        keyCode = keyboard.getSetup().getReplaceKey().get(b.getText()).charAt(0);
       } else {
        keys = keyboard.getSetup().getReplaceKey().get(b.getText());
       }
     }
    } else if (keyboard.getSetup().hasModifier(b.getText())) {
// change keyboard variant
     currentModifier = b.getText();
     fillButtons();
     for (VirtualKeyboardListener listener : listeners) {
      listener.modifierPressed(b.getText());
     }
    } else {
     if (b.getText().length() == 1) {
      keyCode = b.getText().charAt(0);
     } else {
      keys = b.getText();
      if (VirtualKeyboardResources.VKT_LIST.equals(keyboard.getType())) {
       keys = ((char) KeyEvent.VK_ESCAPE) + keys;
      }
     }
    }
    if (keyCode >= 0) {
     for (VirtualKeyboardListener listener : listeners) {
      listener.keyTyped(keyCode);
     }
    }
    if (keys != null) {
     for (VirtualKeyboardListener listener : listeners) {
      for (char ch : keys.toCharArray()) {
       listener.keyTyped(ch);
      }
     }
    }
   } catch (Exception ex) {
    log.error("Error on keyboard action", ex);
   }
  }
 }

 /**
  * @param buttonWidth the buttonWidth to set
  */
// public void setButtonWidth(int buttonWidth) {
//  this.buttonWidth = buttonWidth > 0 ? buttonWidth : PREFERRED_WIDTH;
// }
 /**
  * @param buttonHeight the buttonHeight to set
  */
// public void setButtonHeight(int buttonHeight) {
//  this.buttonHeight = buttonHeight > 0 ? buttonHeight : PREFERRED_HEIGHT;
// }
 /**
  * @param buttonFont the buttonFont to set
  */
 public void setButtonFont(String fontName, int fontSize) {
  buttonFont = new Font(fontName, 0, fontSize > 0 ? fontSize : PREFERRED_FONT_SIZE);
 }

 public void addListener(VirtualKeyboardListener listener) {
  listeners.add(listener);
 }

 public void removeListener(VirtualKeyboardListener listener) {
  listeners.remove(listener);
 }

 public void removeAllListeners() {
  listeners.clear();
 }

}
