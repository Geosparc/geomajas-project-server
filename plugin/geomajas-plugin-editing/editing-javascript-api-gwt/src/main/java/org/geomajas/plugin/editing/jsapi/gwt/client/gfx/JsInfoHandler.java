/*
 * This is part of Geomajas, a GIS framework, http://www.geomajas.org/.
 *
 * Copyright 2008-2012 Geosparc nv, http://www.geosparc.com/, Belgium.
 *
 * The program is available in open source according to the GNU Affero
 * General Public License. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */
package org.geomajas.plugin.editing.jsapi.gwt.client.gfx;

import org.geomajas.annotation.Api;
import org.geomajas.geometry.Coordinate;
import org.geomajas.geometry.Geometry;
import org.geomajas.plugin.editing.gwt.client.GeometryEditor;
import org.geomajas.plugin.editing.gwt.client.gfx.InfoProvider;
import org.geomajas.plugin.editing.gwt.client.handler.InfoDragLineHandler;
import org.geomajas.plugin.editing.jsapi.gwt.client.JsGeometryEditor;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.NoExport;


/**
 * JavaScript wrapper of {@link InfoDragLineHandler}.
 * 
 * @author Jan De Moerloose
 * @since 1.0.0
 * 
 */
@Export("InfoHandler")
@ExportPackage("org.geomajas.plugin.editing.gfx")
@Api(allMethods = true)
public class JsInfoHandler implements Exportable, InfoProvider {

	private InfoDragLineHandler delegate;
	
	private GeometryEditor editor;

	private TitleCallback titleCallback;

	private HtmlCallback htmlCallback;
	
	/**
	 * Needed for exporter.
	 */
	public JsInfoHandler() {
	}
	
	@Export
	public JsInfoHandler(JsGeometryEditor jsEditor) {
		editor = jsEditor.getDelegate();
		delegate = new InfoDragLineHandler(editor.getMapWidget(), editor.getEditService());
	}


	public void register() {
		delegate.register();
	}

	public void unregister() {
		delegate.unregister();
	}

	public void setVisible(boolean visible) {
		delegate.setVisible(visible);
	}

	public boolean isVisible() {
		return delegate.isVisible();
	}

	public void setShowClose(boolean showClose) {
		delegate.setShowClose(showClose);
	}
	
	
	@NoExport
	public String getTitle() {
		return titleCallback == null ? "" : titleCallback.execute();
	}

	@NoExport
	public String getHtml(Geometry geometry, Coordinate dragPoint, Coordinate startA, Coordinate startB) {
		return htmlCallback == null ? "" : htmlCallback.execute(geometry, dragPoint, startA, startB);
	}

	/**
	 * Set the callback closure to get the window title.
	 * 
	 * @param titleCallback the callback
	 */
	public void setTitleCallBack(TitleCallback titleCallback) {
		this.titleCallback = titleCallback;
		delegate.setInfoProvider(this);
	}

	/**
	 * Set the callback closure to get the HTML content.
	 * 
	 * @param titleCallback the callback
	 */
	public void setHtmlCallBack(HtmlCallback htmlCallback) {
		this.htmlCallback = htmlCallback;
		delegate.setInfoProvider(this);
	}

	/**
	 * 
	 * Closure that returns a string value with the title.
	 * 
	 * @since 1.0.0
	 * 
	 */
	@Export
	@ExportClosure
	@Api(allMethods = true)
	public interface TitleCallback extends Exportable {

		String execute();
	}

	/**
	 * 
	 * Closure that returns an HTML fragment with information about the geometry.
	 * 
	 * @since 1.0.0
	 * 
	 */
	@Export
	@ExportClosure
	@Api(allMethods = true)
	public interface HtmlCallback extends Exportable {

		String execute(Geometry geometry, Coordinate dragPoint, Coordinate startA, Coordinate startB);
	}

	
	

}