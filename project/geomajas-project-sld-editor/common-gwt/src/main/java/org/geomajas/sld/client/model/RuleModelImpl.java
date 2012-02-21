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

package org.geomajas.sld.client.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.xalan.xsltc.compiler.sym;
import org.geomajas.sld.GraphicInfo;
import org.geomajas.sld.LineSymbolizerInfo;
import org.geomajas.sld.PointSymbolizerInfo;
import org.geomajas.sld.PolygonSymbolizerInfo;
import org.geomajas.sld.RuleInfo;
import org.geomajas.sld.SymbolizerTypeInfo;
import org.geomajas.sld.client.model.FilterModel.FilterModelState;
import org.geomajas.sld.editor.client.GeometryType;
import org.geomajas.sld.editor.client.SldUtils;
import org.geomajas.sld.editor.client.i18n.SldEditorMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.util.tools.shared.StringUtils;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * @author An Buyle
 * 
 */
public class RuleModelImpl implements RuleModel {

	private String name;

	private String title;

	private RuleInfo ruleInfo;

	private FilterModel filterModel;

	private SymbolizerTypeInfo symbolizerTypeInfo;

	private GeometryType geometryType;

	private RuleGroup ruleGroup;
	// TODO: 2 creators with @AssistedInject
	/**
	 * Constructor for a) creating a default rule model for the specified geometry type. b) for specified RuleInfo (e.g.
	 * from an SLD object)
	 * 
	 * @param geometryType
	 */
	@Inject
	public RuleModelImpl(@Assisted RuleGroup ruleGroup, @Assisted RuleInfo ruleInfo, @Assisted GeometryType geometryType, SldEditorMessages messages) {
		this.ruleGroup = ruleGroup;
		if (null == ruleInfo) {

			RuleInfo defaultRule = new RuleInfo();

			this.geometryType = geometryType;

			defaultRule.setName(messages.ruleTitleDefault());
			this.name = messages.ruleTitleDefault();

			defaultRule.setTitle(messages.ruleTitleDefault());
			this.title = messages.ruleTitleDefault();

			List<SymbolizerTypeInfo> symbolizerList = new ArrayList<SymbolizerTypeInfo>();
			SymbolizerTypeInfo symbolizer = null;

			switch (geometryType) {
				case POINT:
					symbolizer = new PointSymbolizerInfo();

					((PointSymbolizerInfo) symbolizer).setGraphic(new GraphicInfo());
					List<org.geomajas.sld.GraphicInfo.ChoiceInfo> list = new ArrayList<org.geomajas.sld.GraphicInfo.ChoiceInfo>();

					org.geomajas.sld.GraphicInfo.ChoiceInfo choiceInfoGraphic = new org.geomajas.sld.GraphicInfo.ChoiceInfo();
					list.add(choiceInfoGraphic);
					((PointSymbolizerInfo) symbolizer).getGraphic().setChoiceList(list);

					break;
				case LINE:
					symbolizer = new LineSymbolizerInfo();
					break;
				case POLYGON:
					symbolizer = new PolygonSymbolizerInfo();
					break;

				default:
					GWT.log("createDefaultRule: unsupported geometrie type " + geometryType.toString()); // TODO
			}

			symbolizerList.add(symbolizer);

			defaultRule.setSymbolizerList(symbolizerList);
			this.symbolizerTypeInfo = symbolizer;
			this.filterModel = null;

			this.ruleInfo = defaultRule;
		} else {

			this.ruleInfo = ruleInfo;
			this.geometryType = SldUtils.GetGeometryType(ruleInfo);
			this.symbolizerTypeInfo = ruleInfo.getSymbolizerList().get(0); // retrieve the first symbolizer
																			// specification
			if (null != ruleInfo.getChoice() && ruleInfo.getChoice().ifFilter()) {
				this.filterModel = new FilterModelImpl(ruleInfo.getChoice().getFilter());
			} else {
				this.filterModel = new FilterModelImpl();
			}
			this.name = ruleInfo.getName();
			if (null == ruleInfo.getTitle() || ruleInfo.getTitle().length() == 0) {
				if (null != ruleInfo.getName() && ruleInfo.getName().length() > 0) {
					ruleInfo.setTitle(ruleInfo.getName());
				} else {
					ruleInfo.setTitle(messages.ruleTitleUnspecified());
				}
			}

			this.title = ruleInfo.getTitle();

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geomajas.sld.client.model.RuleModelIntf#getName()
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geomajas.sld.client.model.RuleModelIntf#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geomajas.sld.client.model.RuleModelIntf#getTitle()
	 */
	public String getTitle() {
		return title;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geomajas.sld.client.model.RuleModelIntf#setTitle(java.lang.String)
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	public RuleModelState getState() {
		if (getFilterModel().getState() == FilterModelState.INCOMPLETE) {
			return RuleModelState.INCOMPLETE;
		} else if (!isSymbolizerComplete()) {
			return RuleModelState.INCOMPLETE;
		} else {
			return RuleModelState.COMPLETE;
		}
	}

	private boolean isSymbolizerComplete() {
		if (symbolizerTypeInfo != null) {
			if (symbolizerTypeInfo instanceof PointSymbolizerInfo) {
				PointSymbolizerInfo pointSymbolizerInfo = (PointSymbolizerInfo) symbolizerTypeInfo;
				GraphicInfo graphic = pointSymbolizerInfo.getGraphic();
				for (GraphicInfo.ChoiceInfo choice : graphic.getChoiceList()) {
					if (choice.ifExternalGraphic()) {
						if (isEmptyString(choice.getExternalGraphic().getOnlineResource().getHref().getHref())) {
							return false;
						}
						if (isEmptyString(choice.getExternalGraphic().getFormat().getFormat())) {
							return false;
						}
					} else if (choice.ifMark()) {
							// TODO ?
					}
				}
			}
		}
		return true;
	}

	private boolean isEmptyString(String s) {
		return s == null || s.isEmpty();
	}

	public RuleInfo getRuleInfo() {
		return ruleInfo;
	}

	public FilterModel getFilterModel() {
		return filterModel;
	}

	public GeometryType getGeometryType() {
		return geometryType;
	}

	public SymbolizerTypeInfo getSymbolizerTypeInfo() {
		return symbolizerTypeInfo;
	}

	public void setSymbolizerTypeInfo(SymbolizerTypeInfo symbolizerTypeInfo) {
		this.symbolizerTypeInfo = symbolizerTypeInfo;
	}

	public void checkState() {
		ruleInfo.setTitle(getTitle());
	}

	public RuleReference getReference() {
		return new RuleReferenceImpl(ruleGroup.getRuleModelList().indexOf(this));
	}

}