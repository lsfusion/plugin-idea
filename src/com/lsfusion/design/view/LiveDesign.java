package com.lsfusion.design.view;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.ui.KeyStrokeAdapter;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefBrowserBase;
import com.intellij.ui.jcef.JBCefJSQuery;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.KeyStrokes;
import com.lsfusion.design.ui.FlexAlignment;
import com.lsfusion.design.ui.FlexConstraints;
import com.lsfusion.design.ui.FlexPanel;
import lsfusion.server.physics.dev.debug.DebuggerService;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandlerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.rmi.RemoteException;

import static com.lsfusion.debug.DebugUtils.debugProcess;
import static com.lsfusion.debug.DebugUtils.getDebuggerService;

public class LiveDesign extends FormDesign {
    private static final String LATEST_WEB_CLIENT_URL_PROPERTY_KEY = "lsfLiveDesignLatestWebClientUrl";
    private static final String MANUAL_MODE_PROPERTY_KEY = "lsfLiveDesignManualMode";
    
    private JBCefBrowser browser;
    private JBTextField addressBar;
    private boolean manualMode;
    
    public LiveDesign(@NotNull Project project, final ToolWindowEx toolWindow) {
        super(project, toolWindow);
        
        browser = new JBCefBrowser();
        Disposer.register(this, browser);

        // a form sent while the web client is loading reaches no connection, so resend it once the client has started
        JBCefJSQuery clientStartedQuery = JBCefJSQuery.create((JBCefBrowserBase) browser);
        clientStartedQuery.addHandler(s -> {
            ApplicationManager.getApplication().invokeLater(this::updateFormUnderCaret, project.getDisposed());
            return null;
        });
        browser.getJBCefClient().addLoadHandler(new CefLoadHandlerAdapter() {
            @Override
            public void onLoadEnd(CefBrowser cefBrowser, CefFrame frame, int httpStatusCode) {
                if (frame.isMain()) {
                    // main.nocache.js boots the web client (not a login page etc.), loadingWrapper is removed once its navigator is initialized
                    cefBrowser.executeJavaScript("(function poll() {" +
                            "  if (!document.querySelector('script[src$=\"main.nocache.js\"]')) return;" +
                            "  if (document.getElementById('loadingWrapper')) setTimeout(poll, 200);" +
                            "  else " + clientStartedQuery.inject("''") +
                            "})();", cefBrowser.getURL(), 0);
                }
            }
        }, browser.getCefBrowser());

        addressBar = new JBTextField();
        addressBar.getEmptyText().setText("Enter running web-client URL here");
        addressBar.addKeyListener(new KeyStrokeAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                if (KeyStrokes.isEnterEvent(event)) {
                    String url = addressBar.getText();
                    changeBrowserUrl(url);
                    PropertiesComponent.getInstance().setValue(LATEST_WEB_CLIENT_URL_PROPERTY_KEY, url);
                }
            }
        });
        addressBar.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                addressBar.selectAll();
            }
        });

        mainPanel = new FlexPanel(true);

        JButton reloadPageButton = new JButton(LSFIcons.Design.REFRESH);
        int reloadPageButtonPreferredSize = reloadPageButton.getPreferredSize().height;
        reloadPageButton.setPreferredSize(new Dimension(reloadPageButtonPreferredSize, reloadPageButtonPreferredSize));
        reloadPageButton.setToolTipText("Reload current page");
        reloadPageButton.addActionListener(e -> browser.getCefBrowser().reload());
        
        FlexPanel urlRow = new FlexPanel(false);
        urlRow.add(reloadPageButton);
        urlRow.add(addressBar, new FlexConstraints(FlexAlignment.CENTER, 1));

        manualMode = PropertiesComponent.getInstance().getBoolean(MANUAL_MODE_PROPERTY_KEY);
        JButton updateFormButton = new JButton("Update form");
        updateFormButton.addActionListener(e -> updateFormUnderCaret());
        updateFormButton.setVisible(manualMode);
        
        JBCheckBox manualModeCB = new JBCheckBox("Manual update");
        manualModeCB.setSelected(manualMode);
        manualModeCB.setToolTipText("Check to stop tracking caret position and form code changes");
        Border manualModeCBBorder = BorderFactory.createEmptyBorder(6, 0, 6, 0);
        manualModeCB.setBorder(manualMode ? null : manualModeCBBorder);
        manualModeCB.addActionListener(e -> {
            manualMode = manualModeCB.isSelected();
            updateFormButton.setVisible(manualMode);
            manualModeCB.setBorder(manualMode ? null : manualModeCBBorder);
            PropertiesComponent.getInstance().setValue(MANUAL_MODE_PROPERTY_KEY, manualMode);
        });
        
        FlexPanel manualModeRow = new FlexPanel(false);
        manualModeRow.add(manualModeCB, new FlexConstraints(FlexAlignment.CENTER, 0));
        manualModeRow.add(updateFormButton, new FlexConstraints(FlexAlignment.CENTER, 0));
        
        FlexPanel toolbar = new FlexPanel(true);
        toolbar.add(urlRow, new FlexConstraints(FlexAlignment.STRETCH, 0));
        toolbar.add(manualModeRow);

        mainPanel.add(toolbar, new FlexConstraints(FlexAlignment.STRETCH, 0));

        mainPanel.add(browser.getComponent(), new FlexConstraints(FlexAlignment.STRETCH, 1));
    }
    
    public void onActivated() {
        if (!wasActivated) {
            wasActivated = true;
            String latestUrl = PropertiesComponent.getInstance().getValue(LATEST_WEB_CLIENT_URL_PROPERTY_KEY);
            if (latestUrl != null) {
                final Timer timer = new Timer(700, evt -> changeBrowserUrl(latestUrl));
                timer.setRepeats(false);
                timer.start();

                addressBar.setText(latestUrl);
            }
        }
    }
    
    private void updateFormUnderCaret() {
        DesignView.openFormUnderCaretDesign(project, targetForm -> scheduleRebuild(targetForm.form, targetForm.file, false));
    }

    private void changeBrowserUrl(String url) {
        browser.getCefBrowser().loadURL(url);
        
        // we have to set background every time the url changes
        //noinspection UseJBColor
        browser.getComponent().setBackground(Color.WHITE);
    } 

    private void evalClient(DebuggerService debuggerService, String formName, String currentForm) throws RemoteException {
        String currentFormIndexName = "debug_" + System.currentTimeMillis();

        debuggerService.evalClient(
                "run(STRING form) { \n" +
                        "   showError() <- NULL;\n" +
                        "   TRY {\n" +
                        "       EVAL form + \'run() \\{ SHOW \\'" + currentFormIndexName + "\\' = " + formName + " NOWAIT; \\}\';\n" +
                        "       IF openedFormId() THEN { \n" +
                        "           EVAL \'run() \\{ CLOSE FORM \\'\' + openedFormId() + \'\\';\\}\';\n" +
                        "       }\n" +
                        "       APPLY {\n" +
                        "           openedFormId(currentConnection()) <- '" + currentFormIndexName + "';\n" +
                        "       }\n" +
                        "   } CATCH { \n" +
                        "       showError() <- TRUE;\n" +
                        "   }\n" +
                        "}", currentForm + "\n" +
                        "EXTEND FORM " + formName + "\n" +
                        "   PROPERTIES() messageCaughtException SHOWIF showError(); \n" +
                        "DESIGN " + formName + "{\n" +
                        "    MOVE PROPERTY(messageCaughtException()) FIRST {\n" +
                        "        background = RGB(245,0,0);\n" +
                        "        caption = '';\n" +
                        "        height = 70;\n" +
                        "        valueAlignment = CENTER;\n" +
                        "    }\n" +
                        "}");
    }

    public void scheduleRebuild(PsiElement element, PsiFile file) {
        scheduleRebuild(element, file, true);
    }

    public void scheduleRebuild(PsiElement element, PsiFile file, boolean checkFormEquility) {
        if (element != null && file != null) {
            scheduleRebuild("rebuildLive", file, checkFormEquility, formWithName -> {
                try {
                    DebuggerService debuggerService = getDebuggerService();

                    if (debugProcess != null && debuggerService != null) { //until there is a client debugProcess will be null
                        evalClient(debuggerService, formWithName.first, formWithName.second);
                    }
                } catch (Exception ignored) {
                }
            });
        }
    }
    
    public boolean isManualMode() {
        return manualMode;
    }
}
