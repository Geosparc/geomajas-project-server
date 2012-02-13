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
package org.geomajas.sld.editor.client.view;

import java.util.List;

import org.geomajas.sld.client.presenter.StyledLayerDescriptorListPresenter;
import org.geomajas.sld.client.presenter.event.SldListPopupNewEvent;
import org.geomajas.sld.client.presenter.event.SldListPopupNewEvent.SldListPopupNewHandler;
import org.geomajas.sld.client.presenter.event.SldListRemoveEvent;
import org.geomajas.sld.client.presenter.event.SldListRemoveEvent.SldListRemoveHandler;
import org.geomajas.sld.client.presenter.event.SldListSelectEvent;
import org.geomajas.sld.client.presenter.event.SldListSelectEvent.SldListSelectHandler;
import org.geomajas.sld.client.view.ViewUtil;
import org.geomajas.sld.client.view.ViewUtil.YesNoCallback;
import org.geomajas.sld.editor.client.i18n.SldEditorMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.gwtplatform.mvp.client.ViewImpl;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * List view of SLD's.
 * 
 * @author Jan De Moerloose
 * 
 */
public class StyledLayerDescriptorListView extends ViewImpl implements StyledLayerDescriptorListPresenter.MyView {

	public static final String SLD_NAME_ATTRIBUTE_NAME = "SLDName";

	private SldEditorMessages sldEditorMessages = GWT.create(SldEditorMessages.class);

	private VLayout vLayout;

	private ListGrid listGrid;

	private boolean userFlagDuringSelect;

	private RecordList recordsSorted;

	private HLayout toolStrip;

	private AddButton addButton;

	private RemoveButton removeButton;

	private ListGridRecord[] recordsArray;

	private EventBus eventBus;

	@Inject
	public StyledLayerDescriptorListView(final EventBus eventBus, final ViewUtil viewUtil) {
		this.eventBus = eventBus;
		vLayout = new VLayout(10);
		vLayout.setLayoutTopMargin(10);
		vLayout.setLayoutBottomMargin(5);

		listGrid = new ListGrid();

		listGrid.setMinHeight(100);
		listGrid.setOverflow(Overflow.AUTO);
		listGrid.setLeaveScrollbarGap(true);

		listGrid.setShowAllRecords(true);
		listGrid.setSelectionType(SelectionStyle.SINGLE);
		listGrid.setPrompt(sldEditorMessages.listingOfSldsTooltip());

		ListGridField nameField = new ListGridField("SLDName", sldEditorMessages.listingOfSldsTitle());

		nameField.setAlign(Alignment.LEFT);
		listGrid.setFields(nameField);
		listGrid.setSelectionType(SelectionStyle.SINGLE);

		listGrid.addSelectionChangedHandler(new SelectionChangedHandler() {

			public void onSelectionChanged(SelectionEvent event) {
				ListGridRecord record = event.getSelectedRecord();
				enableRemoveButton(record != null);
				SldListSelectEvent.fire(StyledLayerDescriptorListView.this, record.getAttribute("SLDName"));
			}
		});

		vLayout.addMember(listGrid);
		listGrid.setHeight("*");

		toolStrip = new HLayout(10/* membersMargin */);
		toolStrip.setPadding(10);
		toolStrip.setHeight(40); /* fixed size for tool strip for SLD manager */

		addButton = new AddButton();

		addButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				SldListPopupNewEvent.fire(StyledLayerDescriptorListView.this);
			}

		});

		toolStrip.addMember(addButton);
		removeButton = new RemoveButton();

		removeButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				final ListGridRecord record = listGrid.getSelectedRecord();
				viewUtil.showYesNoMessage("Bent u zeker dat u de SLD '" + record.getAttribute(SLD_NAME_ATTRIBUTE_NAME)
						+ "' wilt verwijderen?", new YesNoCallback() {

					public void onYes() {
						SldListRemoveEvent.fire(StyledLayerDescriptorListView.this);
					}

					public void onNo() {
					}

					public void onCancel() {
					}
				});
			}

		});

		toolStrip.addMember(removeButton);
		vLayout.setWidth100();
		vLayout.setHeight100();

		vLayout.addMember(toolStrip);

	}

	public Widget asWidget() {
		return vLayout;
	}

	public Canvas getCanvas() {
		return vLayout;
	}

	public void setData(List<String> sldList) {

		recordsArray = new ListGridRecord[sldList.size()];

		int i = 0;
		for (String sldName : sldList) {
			recordsArray[i] = new ListGridRecord();
			recordsArray[i].setAttribute(SLD_NAME_ATTRIBUTE_NAME, sldName);
			i++;
		}

		listGrid.setData(recordsArray);
		listGrid.sort();
	}

	/**
	 * Select a certain SLD in the list (if present).
	 * 
	 * @param sldName
	 * @param userFlagDuringSelect
	 */
	public void selectSld(String sldName, boolean userFlagDuringSelect) {
		if (null != sldName) {
			recordsSorted = listGrid.getDataAsRecordList();

			Record record = recordsSorted.find(SLD_NAME_ATTRIBUTE_NAME, sldName);
			this.userFlagDuringSelect = userFlagDuringSelect; // This flag can be polled during the
			// subsequent execution of selectionChangedHandler.
			listGrid.selectSingleRecord(record);
			this.userFlagDuringSelect = false;
		} else {
			deselectAllSlds(userFlagDuringSelect);
		}
	}

	/**
	 * Do not select any SLD in the list.
	 */
	public void deselectAllSlds(boolean userFlagDuringSelect) {
		this.userFlagDuringSelect = userFlagDuringSelect; // This flag can be polled during the
		// subsequent execution of selectionChangedHandler.
		listGrid.deselectAllRecords();
		this.userFlagDuringSelect = false;
	}

	/**
	 * @return true if inside select()/deselectAll() of list grid record and <code>userFlagDuringSelect</code> argument
	 *         of triggering <code>selectSld</code> or <code>deselectAll</code> was set to value true, else false.
	 */
	public boolean getUserFlagDuringSelect() {
		return userFlagDuringSelect;
	}

	private void enableRemoveButton(boolean enable) {
		if (enable) {
			removeButton.enable();
		} else {
			removeButton.disable();
		}
	}

	// -------------------------------------------------------------------------
	// Private class AddButton:
	// -------------------------------------------------------------------------

	/** Definition of the Add button. */
	private class AddButton extends IButton {

		public static final String ICON = "[ISOMORPHIC]/" + "geomajas/icons/silk/add.png";

		public AddButton() {
			// @todo FIX OK ? setIcon(WidgetLayout.iconAdd);
			setIcon(ICON);
			setShowDisabledIcon(false);
			setPrompt(sldEditorMessages.addSldButtonTooltip());
			// TODO: validate form first
			setWidth(24);
			setDisabled(false);

			// TODO: setTitle(I18nProvider.getAttribute().btnSaveTitle());
			// TODO: setTooltip(I18nProvider.getAttribute().btnSaveTooltip());

		}
	}

	// -------------------------------------------------------------------------
	// Private class RemoveButton:
	// -------------------------------------------------------------------------

	/** Definition of the Add button. */
	private class RemoveButton extends IButton {

		public static final String ICON = "[ISOMORPHIC]/" + "geomajas/icons/silk/cancel.png";

		public RemoveButton() {
			// @todo FIX OK? setIcon(WidgetLayout.iconRemove);
			setIcon(ICON);
			setShowDisabledIcon(true);
			setPrompt(sldEditorMessages.removeSldButtonTooltip());
			setWidth(24);
			setDisabled(true);

		}
	}

	public void fireEvent(GwtEvent<?> event) {
		eventBus.fireEvent(event);
	}

	public HandlerRegistration addSldListPopupNewHandler(SldListPopupNewHandler handler) {
		return eventBus.addHandler(SldListPopupNewEvent.getType(), handler);
	}

	public HandlerRegistration addSldListRemoveHandler(SldListRemoveHandler handler) {
		return eventBus.addHandler(SldListRemoveEvent.getType(), handler);
	}

	public HandlerRegistration addSldListSelectHandler(SldListSelectHandler handler) {
		return eventBus.addHandler(SldListSelectEvent.getType(), handler);
	}

}