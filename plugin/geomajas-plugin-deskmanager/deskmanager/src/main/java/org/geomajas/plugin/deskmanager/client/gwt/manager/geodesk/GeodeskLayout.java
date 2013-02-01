/*
 * This is part of Geomajas, a GIS framework, http://www.geomajas.org/.
 *
 * Copyright 2008-2013 Geosparc nv, http://www.geosparc.com/, Belgium.
 *
 * The program is available in open source according to the GNU Affero
 * General Public License. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */
package org.geomajas.plugin.deskmanager.client.gwt.manager.geodesk;

import org.geomajas.plugin.deskmanager.client.gwt.manager.common.AbstractConfigurationLayout;
import org.geomajas.plugin.deskmanager.client.gwt.manager.common.GeodeskLayoutPanel;
import org.geomajas.plugin.deskmanager.client.gwt.manager.common.SaveButtonBar;
import org.geomajas.plugin.deskmanager.client.gwt.manager.events.GeodeskEvent;
import org.geomajas.plugin.deskmanager.client.gwt.manager.events.GeodeskSelectionHandler;
import org.geomajas.plugin.deskmanager.client.gwt.manager.events.Whiteboard;
import org.geomajas.plugin.deskmanager.client.gwt.manager.service.DataCallback;
import org.geomajas.plugin.deskmanager.client.gwt.manager.service.ManagerCommandService;
import org.geomajas.plugin.deskmanager.client.gwt.manager.util.GeodeskDtoUtil;
import org.geomajas.plugin.deskmanager.command.manager.dto.SaveBlueprintRequest;
import org.geomajas.plugin.deskmanager.command.manager.dto.SaveGeodeskRequest;
import org.geomajas.plugin.deskmanager.configuration.client.GeodeskLayoutInfo;
import org.geomajas.plugin.deskmanager.domain.dto.GeodeskDto;

import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.events.ClickEvent;

/**
 * @author Kristof Heirwegh
 */
public class GeodeskLayout extends AbstractConfigurationLayout implements GeodeskSelectionHandler {

	private GeodeskDto geodesk;

	private GeodeskLayoutPanel layout;

	public GeodeskLayout() {
		super();
		setMargin(5);

		// setOverflow(Overflow.HIDDEN);

		SaveButtonBar buttonBar = new SaveButtonBar(this);
		addMember(buttonBar);

		// ----------------------------------------------------------

		layout = new GeodeskLayoutPanel();
		layout.setDisabled(true);
		layout.setWidth100();
		layout.setHeight100();
		layout.setOverflow(Overflow.AUTO);

		addMember(layout);
	}

	public void onGeodeskSelectionChange(GeodeskEvent geodeskEvent) {
		setGeodesk(geodeskEvent.getGeodesk());
	}

	private void setGeodesk(GeodeskDto geodesk) {
		this.geodesk = geodesk;
		GeodeskLayoutInfo geodeskLayout = (GeodeskLayoutInfo) GeodeskDtoUtil
				.getApplicationClientWidgetInfo(geodesk).get(GeodeskLayoutInfo.IDENTIFIER);

		if (geodeskLayout == null) {
			geodeskLayout = new GeodeskLayoutInfo();
		}
		layout.setGeodeskLayout(geodeskLayout);
		fireChangedHandler();
	}

	// -- SaveButtonBar events --------------------------------------------------------

	public boolean onEditClick(ClickEvent event) {
		layout.setDisabled(false);
		return true;
	}

	public boolean onSaveClick(ClickEvent event) {
		geodesk.getApplicationClientWidgetInfos().put(GeodeskLayoutInfo.IDENTIFIER, layout.getGeodeskLayout());
		
		ManagerCommandService.saveGeodesk(geodesk, SaveGeodeskRequest.SAVE_CLIENTWIDGETINFO);
		layout.setDisabled(true);
		return true;
	}

	public boolean onCancelClick(ClickEvent event) {
		setGeodesk(geodesk);
		layout.setDisabled(true);
		// Reload the geodesk
		ManagerCommandService.getGeodesk(geodesk.getId(), new DataCallback<GeodeskDto>() {

			public void execute(GeodeskDto result) {
				Whiteboard.fireEvent(new GeodeskEvent(result));
			}
		});
		return true;
	}
	
	public boolean onResetClick(ClickEvent event) {
		geodesk.getApplicationClientWidgetInfos().remove(GeodeskLayoutInfo.IDENTIFIER);
		ManagerCommandService.saveGeodesk(geodesk, SaveBlueprintRequest.SAVE_CLIENTWIDGETINFO);
		return true;
	}

	public boolean isDefault() {
		return !geodesk.getApplicationClientWidgetInfos().containsKey(GeodeskLayoutInfo.IDENTIFIER);
	}	
}