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
package org.geomajas.sld;

import java.io.Serializable;

import org.geomajas.global.Api;

/**
 * 
 An Extent gives feature/coverage/raster/matrix dimension extent.
 * 
 * 
 * Schema fragment(s) for this class:...
 * 
 * <pre>
 * &lt;xs:element
 * xmlns:ns="http://www.opengis.net/sld"
 * xmlns:xs="http://www.w3.org/2001/XMLSchema" name="Extent">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element ref="ns:Name"/>
 *       &lt;xs:element ref="ns:Value"/>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Jan De Moerloose
 * @since 1.10.0
 */
@Api(allMethods = true)
public class ExtentInfo implements Serializable {

	private static final long serialVersionUID = 1100;

	private NameInfo name;

	private ValueInfo value;

	/**
	 * Get the 'Name' element value.
	 * 
	 * @return value
	 */
	public NameInfo getName() {
		return name;
	}

	/**
	 * Set the 'Name' element value.
	 * 
	 * @param name
	 */
	public void setName(NameInfo name) {
		this.name = name;
	}

	/**
	 * Get the 'Value' element value.
	 * 
	 * @return value
	 */
	public ValueInfo getValue() {
		return value;
	}

	/**
	 * Set the 'Value' element value.
	 * 
	 * @param value
	 */
	public void setValue(ValueInfo value) {
		this.value = value;
	}
}
