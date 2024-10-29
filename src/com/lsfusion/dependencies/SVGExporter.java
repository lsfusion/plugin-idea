package com.lsfusion.dependencies;


import com.jgraph.io.svg.SVGGraphWriter;
import com.jgraph.io.svg.SVGVertexRenderer;
import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class SVGExporter {

    /**
     * File chooser for exporting SVG. Note that it is lazily
     * instaniated, always call initFileChooser before use.
     */
    protected JFileChooser SVGFileChooser = null;

    public SVGExporter() {
    }

    /**
     * Utility method that ensures the file chooser is created. Start-up time
     * is improved by lazily instaniating choosers.
     *
     */
    protected void initSVGFileChooser() {
        if (SVGFileChooser == null) {
            SVGFileChooser = new JFileChooser();
            FileFilter fileFilter = new FileFilter() {
                /**
                 * @see javax.swing.filechooser.FileFilter#accept(File)
                 */
                public boolean accept(File f) {
                    if (f == null)
                        return false;
                    if (f.getName() == null)
                        return false;
                    if (f.getName().endsWith(".svg"))
                        return true;
                    if (f.isDirectory())
                        return true;

                    return false;
                }

                /**
                 * @see javax.swing.filechooser.FileFilter#getDescription()
                 */
                public String getDescription() {
                    return "SVG file (.SVG)";
                }
            };
            SVGFileChooser.setFileFilter(fileFilter);
        }
    }

    protected void exportSVG(JGraph graph) {
        if(graph != null) {
            int returnValue = JFileChooser.CANCEL_OPTION;
            initSVGFileChooser();
            returnValue = SVGFileChooser.showSaveDialog(graph);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                VertexRenderer oldRenderer = VertexView.renderer;
                try {
                    OutputStream out = new BufferedOutputStream(new FileOutputStream(
                            SVGFileChooser.getSelectedFile()));
                    // Set all vertices to use the SVG renderer
                    VertexView.renderer = new SVGVertexRenderer();
                    SVGGraphWriter writer = new SVGGraphWriter() {
                        @Override
                        public Object[] getLabels(CellView view) {
                            if (view instanceof VertexView)
                                return new Object[]{view.getCell().toString()};
                            return super.getLabels(view);
                        }
                    };
                    // SVG nodes will have no size at this point. They need
                    // to be given suitable bounds prior to being passed to the
                    // layout algorithm.
				    //graph.computeVertexSizes();
                    writer.write(new BufferedOutputStream(out), null,
                            graph.getGraphLayoutCache(), 35);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(graph, e.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
                VertexView.renderer = oldRenderer;
            }
        }
    }

}
