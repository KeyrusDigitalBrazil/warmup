/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmswebservices.types.controller;

import static org.hamcrest.Matchers.emptyIterable;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import de.hybris.platform.cmsfacades.types.AbstractComponentTypeFacadeIntegrationTest;
import de.hybris.platform.cmsfacades.types.ComponentTypeNotFoundException;
import de.hybris.platform.cmsfacades.types.impl.DefaultComponentTypeFacade;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructureService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class ComponentTypeFacadeWithGenericServiceIntegrationTest extends ServicelayerTest
{

	private final AbstractComponentTypeFacadeIntegrationTest abstractIntegrationTest = new AbstractComponentTypeFacadeIntegrationTest();

	@Resource
	protected DefaultComponentTypeFacade componentTypeFacade;

	@Resource
	private ComponentTypeStructureService genericComponentTypeStructureService;

	@Resource
	private UserService userService;

	@Resource
	private TypeService typeService;

	@Before
	public void setup() throws ImpExException
	{
		importCsv("/cmswebservices/test/impex/essentialTestDataAuth.impex", "utf-8");

		final UserModel cmsmanager = userService.getUserForUID("cmsmanager");
		userService.setCurrentUser(cmsmanager);

		abstractIntegrationTest.setComponentTypeFacade(this.componentTypeFacade);
		abstractIntegrationTest.getComponentTypeFacade().setComponentTypeStructureService(genericComponentTypeStructureService);
		abstractIntegrationTest.setup();
	}

	@Test
	public void shouldGetCategoryPageComponentType_FromAllTypes()
	{
		abstractIntegrationTest.shouldGetCategoryPageComponentType_FromAllTypes();
	}

	@Test
	public void shouldGetCategoryPageComponentType_FromSingleType() throws ComponentTypeNotFoundException
	{
		abstractIntegrationTest.shouldGetCategoryPageComponentType_FromSingleType();
	}

	@Test
	public void shouldGetContentPageComponentType_FromSingleType() throws ComponentTypeNotFoundException
	{
		abstractIntegrationTest.shouldGetContentPageComponentType_FromSingleType();
	}

	@Test
	public void shouldFilterAnyAbstractComponentType()
	{
		//execute
		final List<ComponentTypeData> componentTypes = componentTypeFacade.getAllComponentTypes();
		final List<String> collectedTypes = componentTypes.stream().map(ComponentTypeData::getCode)
				.filter(typeCode -> typeService.getComposedTypeForCode(typeCode).getAbstract()).collect(Collectors.toList());

		//assert
		assertThat("AbstractComponentTypeFacadeIntegrationTest should filter all abstract classes", collectedTypes,
				emptyIterable());
	}
}
