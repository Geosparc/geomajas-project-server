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
package org.geomajas.layer.geotools;

import org.geotools.data.jdbc.fidmapper.DefaultFIDMapperFactory;
import org.geotools.data.jdbc.fidmapper.FIDMapper;

import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Level;

/**
 * Improved fid mapper for gt-postgis.
 *
 * @author Jan De Moerloose
 * @author Pieter De Graef
 */
public class NonTypedFidMapperFactory extends DefaultFIDMapperFactory {

	public NonTypedFidMapperFactory() {
		super();
	}

	public NonTypedFidMapperFactory(boolean returnFIDColumnsAsAttributes) {
		super(returnFIDColumnsAsAttributes);
	}

	/**
	 * Get the appropriate FIDMapper for the specified table. Overridden to
	 * return a non-typed mapper !!!!
	 *
	 * @param catalog catalog
	 * @param schema schema
	 * @param tableName table name
	 * @param connection the active database connection to get table key information
	 *
	 * @return the appropriate FIDMapper for the specified table.
	 *
	 * @throws IOException if any error occurs.
	 */
	public FIDMapper getMapper(String catalog, String schema, String tableName, Connection connection)
			throws IOException {
		ColumnInfo[] colInfos = getPkColumnInfo(catalog, schema, tableName, connection);
		FIDMapper mapper;

		if (colInfos.length == 0) {
			mapper = buildNoPKMapper(schema, tableName, connection);
		} else if (colInfos.length > 1) {
			mapper = buildMultiColumnFIDMapper(schema, tableName, connection, colInfos);
		} else {
			ColumnInfo ci = colInfos[0];
			mapper = buildSingleColumnFidMapper(schema, tableName, connection, ci);
		}

		if (mapper == null) {
			mapper = buildLastResortFidMapper(schema, tableName, connection, colInfos);

			if (mapper == null) {
				String msg = "Cannot map primary key to a FID mapper, primary key columns are:\n"
						+ getColumnInfoList(colInfos);
				LOGGER.log(Level.SEVERE, msg);
				throw new IOException(msg);
			}
		}
		return mapper;
	}

}