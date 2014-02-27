package com.lsfusion;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.changes.CommitContext;
import com.intellij.openapi.vcs.changes.CommitExecutor;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory;
import com.intellij.util.PairConsumer;
import com.lsfusion.meta.MetaChangeDetector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFCheckInHandlerFactory extends CheckinHandlerFactory {
    @NotNull
    public CheckinHandler createHandler(final CheckinProjectPanel panel, CommitContext commitContext) {
        return new CheckinHandler() {

            @Override
            public ReturnResult beforeCheckin(@Nullable CommitExecutor executor, PairConsumer<Object, Object> additionalDataConsumer) {
                Project project = panel.getProject();
                MetaChangeDetector metaHandler = MetaChangeDetector.getInstance(project);
                if(metaHandler.getMetaEnabled()) {
                    if (Messages.showOkCancelDialog(project,
                            "Commit can't be performed when meta codes are enabled.\n" +
                                    "You can disable meta code and commit, or you can do it manually.",
                            "Commit Is not Possible Right Now",
                            "&Cancel", "&Disable", null) == DialogWrapper.OK_EXIT_CODE) {
                        return ReturnResult.CANCEL;
                    }
                    metaHandler.setMetaEnabled(false, true);
                    return ReturnResult.COMMIT;
                }
                    
                return super.beforeCheckin();
            }
        };
    }
}