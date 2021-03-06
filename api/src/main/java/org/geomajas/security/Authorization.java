/*
 * This is part of Geomajas, a GIS framework, http://www.geomajas.org/.
 *
 * Copyright 2008-2014 Geosparc nv, http://www.geosparc.com/, Belgium.
 *
 * The program is available in open source according to the GNU Affero
 * General Public License. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.geomajas.security;

import org.geomajas.annotation.Api;

/**
 * Allows you to access the policies which are active.
 *
 * @author Joachim Van der Auwera
 * @since 1.6.0
 */
@Api(allMethods = true)
public interface Authorization extends BaseAuthorization, VectorLayerSelectFilterAuthorization, AreaAuthorization,
		FeatureAuthorization, AttributeAuthorization {
}
