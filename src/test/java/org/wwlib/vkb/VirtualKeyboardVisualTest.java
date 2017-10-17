package org.wwlib.vkb;

import java.util.ArrayList;
import java.util.Arrays;
import org.wwlib.vkb.swing.VirtualKeyboard;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.wwlib.CommonUtils;

/**
 *
 * @author walery
 */
@SuppressWarnings("serial")
public class VirtualKeyboardVisualTest extends JFrame implements VirtualKeyboardListener {

 private final VirtualKeyboard panel;

 public static void main(String[] args) {
  SwingUtilities.invokeLater(new Runnable() {
   @Override
   public void run() {
    try {
//     VirtualKeyboardVisualTest test = new VirtualKeyboardVisualTest(VirtualKeyboardResources.VKT_FULL);
//     VirtualKeyboardVisualTest test = new VirtualKeyboardVisualTest(VirtualKeyboardResources.VKT_NUM);
//     VirtualKeyboardVisualTest test = new VirtualKeyboardVisualTest(VirtualKeyboardResources.VKT_NUM_INT);
//     VirtualKeyboardVisualTest test = new VirtualKeyboardVisualTest(VirtualKeyboardResources.VKT_PINPAD);
     VirtualKeyboardVisualTest test = new VirtualKeyboardVisualTest(VirtualKeyboardResources.VKT_LIST);
    } catch (Exception ex) {
     System.out.println(CommonUtils.getStackTrace(ex));
    }
   }
  });
 }


 public VirtualKeyboardVisualTest(String type) throws Exception {
  super();
  setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
  getContentPane().setLayout(null);
  VirtualKeyboardResources vkbData = VirtualKeyboardResources.load("resources/keyboard.template");
  VirtualKeyboardData data = vkbData.getKeyboards().get(type);
  if (type.equals(VirtualKeyboardResources.VKT_LIST)) {
//    data.setKeyboard("!", Arrays.asList(new String[]{"test1","test2","test3","test4","test5"}));
    data.setKeyboard("!", Arrays.asList(new String[]{"11111111111111","22222222222222","33333333333333"}));
  }
  panel = new VirtualKeyboard(data);
  panel.addListener(this);
  panel.init();
  getContentPane().add(panel);
  setVisible(true);
  setLocationRelativeTo(null);
  setSize(1_000, 400);
 }

 @Override
 public void keyTyped(int keyCode) {
  System.out.println("Char: " + (char) keyCode);
 }

 @Override
 public void modifierPressed(String modifier) {
  System.out.println("Modifier: " + modifier);
 }

}
