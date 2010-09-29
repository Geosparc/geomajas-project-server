/*
 * This file is part of Geomajas, a component framework for building
 * rich Internet applications (RIA) with sophisticated capabilities for the
 * display, analysis and management of geographic information.
 * It is a building block that allows developers to add maps
 * and other geographic data capabilities to their web applications.
 *
 * Copyright 2008-2010 Geosparc, http://www.geosparc.com, Belgium
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.geomajas.plugin.caching.service;

import com.vividsolutions.jts.geom.Geometry;
import org.geomajas.global.Api;

import java.util.List;

/**
 * Spatial index for handling invalidation of entries in a {@link org.geomajas.plugin.caching.service.CacheService}.
 * This service is used by the {@link org.geomajas.plugin.caching.service.CacheManagerService}. 
 *
 * @author Joachim Van der Auwera
 * @since 1.0.0
 */
@Api(allMethods = true)
public interface CacheIndexService {

	/**
	 * Add a key/geometry pair to the spatial index.
	 * <p/>
	 * This will overwrite any pre-existing object for the key.
	 *
	 * @param key key for the spatial object
	 * @param geometry geometry for the object
	 */
	void put(String key, Geometry geometry);

	/**
	 * Remove a spatial object from the index.
	 *
	 * @param key key to remove
	 */
	void remove(String key);

	/**
	 * Drop the entire index. This is only useful when dropping the cache which is indexed.
	 */
	void drop();

	/**
	 * Get the keys for the objects which (may) overlap with the given geometry.
	 *
	 * @param geometry geometry to test
	 * @return list of keys of spatial objects which my overlap with the geometry
	 */
	List<String> getOverlappingKeys(Geometry geometry);
}
