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
package org.geomajas.plugin.deskmanager.test.command.manager;

import org.geomajas.command.CommandDispatcher;
import org.geomajas.command.CommandResponse;
import org.geomajas.plugin.deskmanager.command.manager.dto.DeleteLayerModelRequest;
import org.geomajas.plugin.deskmanager.command.security.dto.RetrieveRolesRequest;
import org.geomajas.plugin.deskmanager.domain.LayerModel;
import org.geomajas.plugin.deskmanager.security.DeskmanagerSecurityService;
import org.geomajas.plugin.deskmanager.security.ProfileService;
import org.geomajas.plugin.deskmanager.service.common.LayerModelService;
import org.geomajas.security.GeomajasSecurityException;
import org.geomajas.security.SecurityManager;
import org.geomajas.security.SecurityService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Oliver May
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/org/geomajas/spring/geomajasContext.xml",
		"/org/geomajas/plugin/deskmanager/spring/**/*.xml", "/applicationContext.xml" })
public class DeleteLayerModelCommandTest {

	@Autowired
	private SecurityService securityService;

	@Autowired
	private ProfileService profileService;

	@Autowired
	private SecurityManager securityManager;

	@Autowired
	private CommandDispatcher dispatcher;

	@Autowired
	private LayerModelService layerModelService;

	private String userToken;

	private String guestToken;

	@Before
	public void setup() throws Exception {
		// First profile in list is admin
		userToken = ((DeskmanagerSecurityService) securityService).registerRole(RetrieveRolesRequest.MANAGER_ID,
				profileService.getProfiles(null).get(0));
		guestToken = ((DeskmanagerSecurityService) securityService).registerRole(RetrieveRolesRequest.MANAGER_ID,
				DeskmanagerSecurityService.createGuestProfile());

		// Log in
		securityManager.createSecurityContext(userToken);

	}

	@Test
	@Transactional
	public void testDeleteLayerModel() throws Exception {

		int layerModelsSize = layerModelService.getLayerModels().size();
		LayerModel layerModel = layerModelService.getLayerModels().get(0);

		DeleteLayerModelRequest request = new DeleteLayerModelRequest();
		request.setId(layerModel.getId());

		CommandResponse response = dispatcher.execute(DeleteLayerModelRequest.COMMAND, request, userToken, "en");

		Assert.assertTrue(response.getErrors().isEmpty());
		Assert.assertTrue(response.getErrorMessages().isEmpty());
		Assert.assertEquals(layerModelsSize - 1, layerModelService.getLayerModels().size());
	}

	/**
	 * Test security.
	 */
	@Test
	public void testNotAllowed() {
		CommandResponse response = dispatcher.execute(DeleteLayerModelRequest.COMMAND, new DeleteLayerModelRequest(),
				guestToken, "en");

		Assert.assertFalse(response.getExceptions().isEmpty());
		Assert.assertEquals(GeomajasSecurityException.class.getName(), response.getExceptions().get(0).getClassName());
	}
}
