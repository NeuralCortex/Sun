package com.fx.sun.dialog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

/**
 *
 * @author pscha
 */
public class CrossPainter implements Painter<JXMapViewer> {

    private final GeoPosition geoPosition;
    private final boolean antiAlias = true;

    public CrossPainter(GeoPosition geoPosition) {
        this.geoPosition = geoPosition;
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int i, int i1) {
        g = (Graphics2D) g.create();

        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);

        if (antiAlias) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1));

        drawCross(g, map);

        g.dispose();
    }

    private void drawCross(Graphics2D g, JXMapViewer map) {
        Rectangle bounds = map.getViewportBounds();
        Point2D point = map.getTileFactory().geoToPixel(geoPosition, map.getZoom());

        g.drawLine((int) (point.getX() - bounds.width), (int) point.getY(), (int) (point.getX() + bounds.width), (int) point.getY());
        g.drawLine((int) (point.getX()), (int) (point.getY() - bounds.height), (int) (point.getX()), (int) (point.getY() + bounds.height));
    }
}
