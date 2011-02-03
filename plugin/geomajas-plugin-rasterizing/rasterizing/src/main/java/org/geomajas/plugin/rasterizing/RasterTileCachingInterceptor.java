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
package org.geomajas.plugin.rasterizing;

import org.geomajas.global.GeomajasException;
import org.geomajas.layer.pipeline.GetTileContainer;
import org.geomajas.plugin.caching.service.CacheCategory;
import org.geomajas.plugin.caching.step.AbstractCachingInterceptor;
import org.geomajas.plugin.rasterizing.api.RasterizingContainer;
import org.geomajas.plugin.rasterizing.api.RasterizingPipelineCode;
import org.geomajas.service.TestRecorder;
import org.geomajas.service.pipeline.PipelineCode;
import org.geomajas.service.pipeline.PipelineContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Interceptor that caches the rasterized image.
 * 
 * @author Jan De Moerloose
 * 
 */
public class RasterTileCachingInterceptor extends AbstractCachingInterceptor<GetTileContainer> {

	private static final String[] KEYS = { PipelineCode.LAYER_ID_KEY, PipelineCode.TILE_METADATA_KEY };

	@Autowired
	private TestRecorder recorder;

	public ExecutionMode beforeSteps(PipelineContext context, GetTileContainer response) throws GeomajasException {
		RasterizingContainer rc = getContainer(RasterizingPipelineCode.IMAGE_ID_KEY, KEYS, CacheCategory.RASTER,
				context);
		if (null != rc) {
			recorder.record(CacheCategory.RASTER, "Got item from cache");
			context.put(RasterizingPipelineCode.CONTAINER_KEY, rc);
			return ExecutionMode.EXECUTE_NONE;
		}
		return ExecutionMode.EXECUTE_ALL;

	}

	public void afterSteps(PipelineContext context, GetTileContainer response) throws GeomajasException {
		recorder.record(CacheCategory.RASTER, "Put item in cache");
		// optionally put the image in the cache for later retrieval
		RasterizingContainer rc = context
				.getOptional(RasterizingPipelineCode.CONTAINER_KEY, RasterizingContainer.class);
		if (rc != null) {
			recorder.record(CacheCategory.RASTER, "Put item in cache");
			putContainer(context, CacheCategory.RASTER, KEYS, RasterizingPipelineCode.IMAGE_ID_KEY, rc, response
					.getTile().getBounds());
		}
	}

}
