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

package org.geomajas.widget.featureinfo.client.action.toolbar;

import org.geomajas.gwt.client.action.ToolbarModalAction;
import org.geomajas.gwt.client.widget.MapWidget;
import org.geomajas.widget.featureinfo.client.FeatureInfoMessages;
import org.geomajas.widget.featureinfo.client.controller.TooltipOnMouseoverController;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.widgets.events.ClickEvent;

/**
 * Show a tooltip with the labels of features under mousepointer.
 * 
 * @author Kristof Heirwegh
 * 
 */
public class TooltipOnMouseoverModalAction extends ToolbarModalAction {

	private FeatureInfoMessages messages = GWT.create(FeatureInfoMessages.class);

	private MapWidget mapWidget;
	private int pixelTolerance = 5; // same as FeatureInfoModalAction
	private TooltipOnMouseoverController controller;

	public TooltipOnMouseoverModalAction(MapWidget mapWidget) {
		super("[ISOMORPHIC]/geomajas/osgeo/mouse_tooltip.png", null);
		this.mapWidget = mapWidget;
		this.setTooltip(messages.tooltipOnMouseoverActionTooltip());
		controller = new TooltipOnMouseoverController(mapWidget, pixelTolerance);
	}

	public void onSelect(ClickEvent event) {
		mapWidget.setController(controller);
	}

	public void onDeselect(ClickEvent event) {
		mapWidget.setController(null);
	}

	public TooltipOnMouseoverController getController() {
		return controller;
	}

}
