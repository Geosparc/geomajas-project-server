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
package org.geomajas.plugin.editing.server.command;

import org.geomajas.command.Command;
import org.geomajas.global.ExceptionCode;
import org.geomajas.global.GeomajasException;
import org.geomajas.plugin.editing.dto.GeometryMergeRequest;
import org.geomajas.plugin.editing.dto.GeometryMergeResponse;
import org.geomajas.service.DtoConverterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * <p>
 * This command merges multiple geometries into a single geometry.
 * </p>
 * 
 * @author Pieter De Graef
 */
@Component()
public class GeometryMergeCommand implements Command<GeometryMergeRequest, GeometryMergeResponse> {

	@Autowired
	private DtoConverterService converter;

	public GeometryMergeResponse getEmptyCommandResponse() {
		return new GeometryMergeResponse();
	}

	public void execute(GeometryMergeRequest request, GeometryMergeResponse response) throws Exception {
		if (request.getGeometries() == null || request.getGeometries().size() == 0) {
			throw new GeomajasException(ExceptionCode.PARAMETER_MISSING, "request");
		}
		if (request.getGeometries().size() == 1) {
			response.setGeometry(request.getGeometries().get(0));
			return;
		}

		Geometry[] geometries = new Geometry[request.getGeometries().size()];
		for (int i = 0; i < request.getGeometries().size(); i++) {
			geometries[i] = converter.toInternal(request.getGeometries().get(i));
		}

		int precision = request.getPrecision();
		PrecisionModel precisionModel = new PrecisionModel(Math.pow(10.0, precision));
		GeometryFactory factory = new GeometryFactory(precisionModel, geometries[0].getSRID());

		Geometry temp = factory.createGeometry(geometries[0]);
		for (int i = 1; i < geometries.length; i++) {
			Geometry geometry = factory.createGeometry(geometries[i]);
			temp = temp.union(geometry.buffer(Math.pow(10.0, -(precision - 1))));
		}

		response.setGeometry(converter.toDto(temp));
	}
}