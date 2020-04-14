package com.lsfusion;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;
import com.lsfusion.design.model.FilterView;

import javax.swing.*;

public final class LSFIcons {
    public static final Icon ABSTRACT_PROPERTY = IconLoader.getIcon("/images/abstract_property.png");
    public static final Icon ACTION = IconLoader.getIcon("/images/action.png");
    public static final Icon CLASS = AllIcons.Nodes.Class;
    public static final Icon CONSTRAINT = AllIcons.General.Warning;
    public static final Icon DATA_PROPERTY = IconLoader.getIcon("/images/data_property.png");
    public static final Icon EVENT = AllIcons.Actions.Execute;
    public static final Icon FILE = IconLoader.getIcon("/images/lsf_logo.png");
    public static final Icon MODULE = IconLoader.getIcon("/images/lsf_logo.png");
    public static final Icon MODULE_2X = IconLoader.getIcon("/images/lsf_logo_2x.png");
    public static final Icon LIBRARY = IconLoader.getIcon("/images/lsf_logo.png");
    public static final Icon RUN = IconLoader.getIcon("/images/lsf_logo.png");
    public static final Icon FOLLOWS = AllIcons.Duplicates.SendToTheRight;
    public static final Icon FORM = AllIcons.FileTypes.UiForm;
    public static final Icon GROUP = AllIcons.Actions.GroupByModuleGroup;
    public static final Icon FILTER = IconLoader.getIcon("/images/design/filt.png");
    public static final Icon GROUP_BY_CLASS = AllIcons.Actions.GroupByClass;
    public static final Icon GROUP_BY_MODULE = AllIcons.Actions.GroupByModule;
    public static final Icon GROUP_OBJECT = AllIcons.Actions.GroupByPrefix;
    public static final Icon INDEX = AllIcons.Graph.Layout;
    public static final Icon LOGGABLE = AllIcons.FileTypes.Archive;
    public static final Icon META_DECLARATION = AllIcons.Nodes.AbstractMethod;
    public static final Icon META_REFERENCE = AllIcons.Nodes.Method;
    public static final Icon NAMESPACE = AllIcons.Nodes.ModuleGroup;
    public static final Icon NAVIGATOR_ELEMENT = AllIcons.ObjectBrowser.FlattenPackages;
    public static final Icon NUMBER_SORT = IconLoader.getIcon("/images/number_sort.png");
    public static final Icon OVERRIDE = AllIcons.General.OverridingMethod;
    public static final Icon PARAMETER = AllIcons.Nodes.Parameter;
    public static final Icon LOCAL_PROPERTY = AllIcons.Nodes.Variable;
    public static final Icon STATIC_OBJECT = AllIcons.Nodes.Static;
    public static final Icon PRINT = IconLoader.getIcon("/images/print.png");
    public static final Icon PROPERTY = AllIcons.Nodes.Property;
    public static final Icon PROPERTY_DRAW = AllIcons.Nodes.PropertyRead;
    public static final Icon SHOW_DEP = AllIcons.Nodes.DataTables;
    public static final Icon TABLE = AllIcons.Nodes.DataTables;
    public static final Icon WINDOW = AllIcons.RunConfigurations.Applet;
    public static final Icon WRITE_WHEN = AllIcons.Nodes.PropertyWrite;
    
    public static final Icon DEPENDENCY_ZOOM_IN = AllIcons.Graph.ZoomIn;
    public static final Icon DEPENDENCY_ZOOM_OUT = AllIcons.Graph.ZoomOut;
    public static final Icon DEPENDENCY_ACTUAL_ZOOM = AllIcons.Graph.ActualZoom;

    public static final Icon GRAPH_EXPORT = AllIcons.ToolbarDecorator.Export;

    public static final class Design {
        public static final Icon DESIGN = AllIcons.FileTypes.UiForm;
        public static final Icon COMPONENT = AllIcons.RunConfigurations.Application; //todo:
        public static final Icon CONTAINER = AllIcons.RunConfigurations.Applet; //todo:
        public static final Icon CLASS_CHOOSER = GROUP_BY_CLASS;
        public static final Icon FILTER = IconLoader.getIcon("/images/design/filt.png");
        public static final Icon FILTER_ADD = IconLoader.getIcon("/images/design/filtadd.png");
        public static final Icon FILTER_DEL = IconLoader.getIcon("/images/design/filtdel.png");
        public static final Icon FILTER_GROUP = AllIcons.General.Filter; //todo:
        public static final Icon SHOW_TYPE = AllIcons.Graph.SnapToGrid; //todo:
        public static final Icon TREE_GROUP = AllIcons.Actions.ShowAsTree; //todo:
        public static final Icon TOOLBAR = AllIcons.Ide.Macro.Recording_1;
        public static final Icon GRID = AllIcons.Nodes.DataTables;
        public static final Icon PROPERTY = AllIcons.Nodes.Property;

        public static final Icon EXPERT_PROPS = AllIcons.General.Filter;
        public static final Icon FIND = AllIcons.Actions.Find;
        public static final Icon HIGHLIGHT = AllIcons.General.Locate;
        public static final Icon REFRESH = AllIcons.Actions.Refresh;

        public static final Icon VIEW_GRID = IconLoader.getIcon("/images/design/view_grid.png");
        public static final Icon VIEW_HIDE = IconLoader.getIcon("/images/design/view_hide.png");
        public static final Icon VIEW_PANEL = IconLoader.getIcon("/images/design/view_panel.png");

        public static final ImageIcon PRINT_XLS = new ImageIcon(FilterView.class.getResource("/images/design/excelbw.png"));
        public static final ImageIcon PRINT_GROUP = new ImageIcon(FilterView.class.getResource("/images/design/reportbw.png"));
        public static final ImageIcon GROUP_CHANGE = new ImageIcon(FilterView.class.getResource("/images/design/groupchange.png"));
        public static final ImageIcon QUANTITY = new ImageIcon(FilterView.class.getResource("/images/design/quantity.png"));
        public static final ImageIcon SUM = new ImageIcon(FilterView.class.getResource("/images/design/sum.png"));
        public static final ImageIcon GROUP = new ImageIcon(FilterView.class.getResource("/images/design/group.png"));
        public static final ImageIcon PREFERENCES = new ImageIcon(FilterView.class.getResource("/images/design/userPreferences.png"));
    }
}