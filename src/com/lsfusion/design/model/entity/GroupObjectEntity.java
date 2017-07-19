package com.lsfusion.design.model.entity;

import com.lsfusion.design.ui.ClassViewType;
import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.psi.LSFFormGroupObjectPageSize;
import com.lsfusion.lang.psi.LSFFormGroupObjectViewType;

import java.util.ArrayList;
import java.util.List;

import static com.lsfusion.design.ui.ClassViewType.*;

public class GroupObjectEntity {
    public String sID;

    public TreeGroupEntity treeGroup;
    
    public boolean isInTree() {
        return treeGroup != null;
    }

    public List<ObjectEntity> objects = new ArrayList<>();

    public ClassViewType initClassView = GRID;
    public List<ClassViewType> banClassView = new ArrayList<>();

    public int pageSize = 0;

    public boolean isParent = false;

    public GroupObjectEntity(String sID, LSFFormGroupObjectViewType viewType, LSFFormGroupObjectPageSize pageSize) {
        this.sID = sID;

        if (viewType != null) {
            ClassViewType type = valueOf(viewType.getClassViewType().getText());
            String mode = viewType.getFirstChild().getText();
            if (!"INIT".equals(mode)) {
                for (ClassViewType t : values()) {
                    if (type != t) {
                        banClassView.add(t);
                    }
                }
            }
            initClassView = type;
        }

        if (pageSize != null) {
            this.pageSize = Integer.parseInt(pageSize.getIntLiteral().getText());
        }
    }

    public String getCaption() {
        return objects.get(0).getCaption();
    }

    public LSFValueClass getValueClass() {
        return objects.get(0).valueClass;
    }

    public void add(ObjectEntity objectEntity) {
        objectEntity.groupTo = this;
        objects.add(objectEntity);
    }

    public boolean isAllowedClassView(ClassViewType type) {
        return !banClassView.contains(type);
    }

    public boolean isFixedPanel() {
        return initClassView == PANEL && !isAllowedClassView(GRID) && !isAllowedClassView(HIDE);
    }
}
