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

package org.geomajas.plugin.editing.client.merging.event;

import org.geomajas.annotation.Api;
import org.geomajas.annotation.UserImplemented;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Handler for catching geometry merging stop events.
 * 
 * @author Pieter De Graef
 * @since 1.0.0
 */
@Api(allMethods = true)
@UserImplemented
public interface GeometryMergingStopHandler extends EventHandler {

	GwtEvent.Type<GeometryMergingStopHandler> TYPE = new GwtEvent.Type<GeometryMergingStopHandler>();

	/**
	 * Executed when the merging process has ended.
	 * 
	 * @param event
	 *            The geometry merging stop event.
	 */
	void onGeometryMergingStop(GeometryMergingStopEvent event);
}