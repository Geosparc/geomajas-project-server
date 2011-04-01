/*
 * This is part of Geomajas, a GIS framework, http://www.geomajas.org/.
 *
 * Copyright 2008-2011 Geosparc nv, http://www.geosparc.com/, Belgium.
 *
 * The program is available in open source according to the GNU Affero
 * General Public License. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.geomajas.widget.featureinfo.client.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.geomajas.command.CommandResponse;
import org.geomajas.command.dto.SearchByLocationRequest;
import org.geomajas.command.dto.SearchByLocationResponse;
import org.geomajas.geometry.Coordinate;
import org.geomajas.gwt.client.command.CommandCallback;
import org.geomajas.gwt.client.command.GwtCommand;
import org.geomajas.gwt.client.command.GwtCommandDispatcher;
import org.geomajas.gwt.client.controller.AbstractGraphicsController;
import org.geomajas.gwt.client.map.MapModel;
import org.geomajas.gwt.client.map.layer.Layer;
import org.geomajas.gwt.client.map.layer.VectorLayer;
import org.geomajas.gwt.client.spatial.Mathlib;
import org.geomajas.gwt.client.spatial.WorldViewTransformer;
import org.geomajas.gwt.client.spatial.geometry.Point;
import org.geomajas.gwt.client.util.GeometryConverter;
import org.geomajas.gwt.client.widget.MapWidget;
import org.geomajas.widget.featureinfo.client.widget.MultiLayerFeatureInfoWindow;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.smartgwt.client.widgets.Window;

/**
 * Shows information of the features (per visible vector layer) near the position where the user clicked on the map.
 * First a list of all the appropriate features is shown in a floating window. Clicking on a feature of the list shows
 * its attributes in a feature attribute window under the list window. As a starting point for this class the
 * org.geomajas.gwt.client.controller.FeatureInfoController was used.
 * 
 * @author An Buyle
 * @author Oliver May
 * @author Kristof Heirwegh
 */
public class MultiLayerFeatureInfoController extends AbstractGraphicsController {

	private boolean dragging;
	private boolean clickstart;

	/**
	 * Number of pixels that describes the tolerance allowed when trying to
	 * select features.
	 */
	private int pixelTolerance;

	public MultiLayerFeatureInfoController(MapWidget mapWidget, int pixelTolerance) {
		super(mapWidget);
		this.pixelTolerance = pixelTolerance;
	}

	public int getPixelTolerance() {
		return pixelTolerance;
	}

	public void setPixelTolerance(int pixelTolerance) {
		this.pixelTolerance = pixelTolerance;
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		if (event.getNativeButton() != NativeEvent.BUTTON_RIGHT) {
			clickstart = true;
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (clickstart) {
			dragging = true;
		}
	}

	/**
	 * On mouse up, execute the search by location, and display a
	 * {@link org.geomajas.widget.featureinfo.client.widget.MultiLayerFeaturesList}
	 * if features are found.
	 */
	public void onMouseUp(MouseUpEvent event) {
		if (clickstart && !dragging) {
			Coordinate worldPosition = getWorldPosition(event);
			Point point = mapWidget.getMapModel().getGeometryFactory().createPoint(worldPosition);

			SearchByLocationRequest request = new SearchByLocationRequest();
			request.setLocation(GeometryConverter.toDto(point));
			request.setCrs(mapWidget.getMapModel().getCrs());
			request.setQueryType(SearchByLocationRequest.QUERY_INTERSECTS);
			request.setSearchType(SearchByLocationRequest.SEARCH_ALL_LAYERS);
			request.setBuffer(calculateBufferFromPixelTolerance());
			request.setFeatureIncludes(GwtCommandDispatcher.getInstance().getLazyFeatureIncludesSelect());
			request.setLayerIds(getServerLayerIds(mapWidget.getMapModel()));
			Layer<?> layer = mapWidget.getMapModel().getSelectedLayer();
			if (null != layer && layer instanceof VectorLayer) {
				request.setFilter(((VectorLayer) layer).getFilter());
			}

			GwtCommand commandRequest = new GwtCommand("command.feature.SearchByLocation");
			// FIXME this is 1.9.0-SNAPSHOT functionality
			// GwtCommand commandRequest = new
			// GwtCommand(SearchByLocationRequest.COMMAND);
			commandRequest.setCommandRequest(request);
			GwtCommandDispatcher.getInstance().execute(commandRequest, new CommandCallback() {

				public void execute(CommandResponse commandResponse) {
					if (commandResponse instanceof SearchByLocationResponse) {
						SearchByLocationResponse response = (SearchByLocationResponse) commandResponse;
						Map<String, List<org.geomajas.layer.feature.Feature>> featureMap = response.getFeatureMap();

						Window window = new MultiLayerFeatureInfoWindow(mapWidget, featureMap);
						window.setPageTop(mapWidget.getAbsoluteTop() + 10);
						window.setPageLeft(mapWidget.getAbsoluteLeft() + 50);
						window.draw();
					}
				}
			});
		} else {
			dragging = false;
		}
		clickstart = false;
	}

	// -------------------------------------------------------------------------
	// Private methods:
	// -------------------------------------------------------------------------

	private String[] getServerLayerIds(MapModel mapModel) {
		List<String> layerIds = new ArrayList<String>();
		for (Layer<?> layer : mapModel.getLayers()) {
			if (layer.isShowing()) {
				layerIds.add(layer.getServerLayerId());
			}
		}
		return layerIds.toArray(new String[] {});
	}

	private double calculateBufferFromPixelTolerance() {
		WorldViewTransformer transformer = mapWidget.getMapModel().getMapView().getWorldViewTransformer();
		Coordinate c1 = transformer.viewToWorld(new Coordinate(0, 0));
		Coordinate c2 = transformer.viewToWorld(new Coordinate(pixelTolerance, 0));
		return Mathlib.distance(c1, c2);
	}
}
