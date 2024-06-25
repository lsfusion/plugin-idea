package com.lsfusion.design;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.EventObject;

public class KeyStrokes {

    public static boolean isKeyEvent(EventObject event, int keyCode) {
        return (event instanceof KeyEvent && ((KeyEvent) event).getKeyCode() == keyCode);
    }

    public static boolean isEnterEvent(EventObject event) {
        return isKeyEvent(event, KeyEvent.VK_ENTER);
    }
    
    public static KeyStroke getEnter(int modifiers) {
        return KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, modifiers);
    }

    //---- form buttons keystrokes
    public static KeyStroke getApplyKeyStroke() {
        return getEnter(InputEvent.ALT_DOWN_MASK);
    }

    public static KeyStroke getCancelKeyStroke() {
        return getEscape(InputEvent.SHIFT_DOWN_MASK);
    }

    public static KeyStroke getCloseKeyStroke() {
        return getEscape(0);
    }

    public static KeyStroke getEditKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK);
    }

    public static KeyStroke getNullKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.ALT_DOWN_MASK);
    }

    public static KeyStroke getOkKeyStroke() {
        return getEnter(InputEvent.CTRL_DOWN_MASK);
    }

    public static KeyStroke getRefreshKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK);
    }

    public static KeyStroke getEscape(int modifier) {
        return KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, modifier);
    }

    public static KeyStroke getAddActionPropertyKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0);
    }

    public static KeyStroke getEditActionPropertyKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0);
    }

    public static KeyStroke getDeleteActionPropertyKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_DOWN_MASK);
    }

    public static String getKeyStrokeCaption(KeyStroke editKey) {
        return editKey.toString().replaceAll("typed ", "").replaceAll("pressed ", "").replaceAll("released ", "");
    }
}
