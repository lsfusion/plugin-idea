package com.lsfusion.design.model;

import com.lsfusion.design.ui.ClassViewType;
import com.lsfusion.lang.psi.LSFFormGroupObjectPageSize;
import com.lsfusion.lang.psi.LSFFormGroupObjectViewType;

import java.util.ArrayList;
import java.util.List;

public class GroupObjectView extends ArrayList<ObjectView> implements GroupView {
    public String sID;

    public GridView grid = new GridView();
    public ShowTypeView showType = new ShowTypeView();
    public ToolbarView toolbar = new ToolbarView();
    public FilterView filter = new FilterView();

    public Boolean needVerticalScroll = true;

    public ClassViewType initClassView = ClassViewType.GRID;
    public List<ClassViewType> banClassView = new ArrayList<ClassViewType>();

    public int pageSize = 0;

    public GroupObjectView(String sID) {
        this.sID = sID;
    }

    public String getCaption() {
        return get(0).getCaption();
    }

    public GroupObjectView(String sID, LSFFormGroupObjectViewType viewType, LSFFormGroupObjectPageSize pageSize) {
        this(sID);

        if (viewType != null) {
            ClassViewType type = ClassViewType.valueOf(viewType.getClassViewType().getText());
            String mode = viewType.getFirstChild().getText();
            if ("INIT".equals(mode)) {
                initClassView = type;
            } else {
                for (ClassViewType t : ClassViewType.values()) {
                    if (type != t) {
                        banClassView.add(t);
                    }
                }
            }
        }

        if (pageSize != null) {
            this.pageSize = Integer.parseInt(pageSize.getIntLiteral().getText());
        }
    }

    @Override
    public String getSID() {
        return sID;
    }
}
