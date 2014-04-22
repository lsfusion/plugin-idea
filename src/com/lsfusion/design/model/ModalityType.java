package com.lsfusion.design.model;

public enum ModalityType {
    DOCKED, MODAL, FULLSCREEN, DOCKEDMODAL, DIALOG_MODAL;

    public boolean isModal() {
        return this != DOCKED;
    }

    public boolean isDialog() {
        return this == DIALOG_MODAL;
    }

    public boolean isFullScreen() {
        return this == FULLSCREEN;
    }
}
