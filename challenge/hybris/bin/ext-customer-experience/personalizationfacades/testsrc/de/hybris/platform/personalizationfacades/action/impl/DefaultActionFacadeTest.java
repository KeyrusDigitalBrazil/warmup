/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.personalizationfacades.action.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.personalizationfacades.data.ActionData;
import de.hybris.platform.personalizationfacades.exceptions.AlreadyExistsException;
import de.hybris.platform.personalizationfacades.exceptions.TypeConflictException;
import de.hybris.platform.personalizationservices.action.CxActionService;
import de.hybris.platform.personalizationservices.customization.CxCustomizationService;
import de.hybris.platform.personalizationservices.model.CxAbstractActionModel;
import de.hybris.platform.personalizationservices.model.CxCustomizationModel;
import de.hybris.platform.personalizationservices.model.CxVariationModel;
import de.hybris.platform.personalizationservices.variation.CxVariationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;


@UnitTest
public class DefaultActionFacadeTest
{
	private static final String ACTION_ID = "action";
	private static final String ACTION2_ID = "otherAction";
	private static final String NOTEXISTING_ACTION_ID = "nonExistAction";
	private static final String NOTEXISTING2_ACTION_ID = "nonExistAction2";
	private static final String NEW_ACTION_ID = "newAction";

	private static final String CATALOG_ID = "c1";
	private static final String CATALOG_VERSION_STAGE_ID = "stage";
	private static final String CUSTOMIZATION_ID = "customization";
	private static final String NOTEXISTING_CUSTOMIZATION_ID = "notExistingCustomization";
	private static final String VARIATION_ID = "variation";
	private static final String NOTEXISTING_VARIATION_ID = "notExistingVariation";

	private final DefaultActionFacade actionFacade = new DefaultActionFacade();
	@Mock
	private ModelService modelService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private CxActionService actionService;
	@Mock
	private CxVariationService variationService;
	@Mock
	private Converter<CxAbstractActionModel, ActionData> actionConverter;
	@Mock
	private Converter<ActionData, CxAbstractActionModel> actionReverseConverter;
	@Mock
	private KeyGenerator actionCodeGenerator;
	@Mock
	private CatalogVersionModel catalogVersionStage;
	@Mock
	private CatalogVersionModel catalogVersionOnline;
	@Mock
	private CxCustomizationService customizationService;

	private CxAbstractActionModel action, secondAction;
	private CxAbstractActionModel actionOnline;
	private ActionData actionData, secondActionData;
	private ActionData actionOnlineData;
	private CxVariationModel variation;
	private CxCustomizationModel customization;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		actionFacade.setModelService(modelService);
		actionFacade.setActionService(actionService);
		actionFacade.setVariationService(variationService);
		actionFacade.setCatalogVersionService(catalogVersionService);
		actionFacade.setActionReverseConverter(actionReverseConverter);
		actionFacade.setActionConverter(actionConverter);
		actionFacade.setCustomizationService(customizationService);
		action = new CxAbstractActionModel();
		action.setCode(ACTION_ID);
		action.setCatalogVersion(catalogVersionStage);

		secondAction = new CxAbstractActionModel();
		secondAction.setCode(ACTION2_ID);
		action.setCatalogVersion(catalogVersionStage);

		actionOnline = new CxAbstractActionModel();
		actionOnline.setCode("onlineActionId");
		actionOnline.setCatalogVersion(catalogVersionOnline);

		actionData = new ActionData();
		actionData.setCode(ACTION_ID);
		actionOnlineData = new ActionData();
		actionOnlineData.setCode("onlineActionId");

		secondActionData = new ActionData();
		secondActionData.setCode(ACTION2_ID);

		variation = new CxVariationModel();
		variation.setActions(Collections.singletonList(action));

		customization = new CxCustomizationModel();
		customization.setVariations(Collections.singletonList(variation));

		Mockito.when(catalogVersionStage.getPk()).thenReturn(PK.fromLong(1l));
		Mockito.when(catalogVersionOnline.getPk()).thenReturn(PK.fromLong(2l));

		Mockito.when(customizationService.getCustomization(CUSTOMIZATION_ID, catalogVersionStage))
				.thenReturn(Optional.of(customization));
		Mockito.when(variationService.getVariation(VARIATION_ID, customization)).thenReturn(Optional.of(variation));
	}

	//Tests for getAction
	@Test
	public void getActionTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(actionService.getAction(ACTION_ID, variation)).thenReturn(Optional.of(action));
		Mockito.when(actionConverter.convert(action)).thenReturn(actionData);

		//when
		final ActionData result = actionFacade.getAction(CUSTOMIZATION_ID, VARIATION_ID, ACTION_ID, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(ACTION_ID, result.getCode());
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getNotExistingActionTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(actionService.getAction(NOTEXISTING_ACTION_ID, variation)).thenReturn(Optional.empty());

		//when
		actionFacade.getAction(CUSTOMIZATION_ID, VARIATION_ID, NOTEXISTING_ACTION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getActionWithNullCatalogTest()
	{
		//when
		actionFacade.getAction(CUSTOMIZATION_ID, VARIATION_ID, ACTION_ID, null, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getActionForNotExistingCatalogTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion("notExistingCatalog", CATALOG_VERSION_STAGE_ID))
				.thenThrow(new UnknownIdentifierException("CatalogVersion with catalogId 'notExistingCatalog' not found!"));

		//when
		actionFacade.getAction(CUSTOMIZATION_ID, VARIATION_ID, ACTION_ID, "notExistingCatalog", CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getActionWithNullActionIdTest()
	{
		//when
		actionFacade.getAction(CUSTOMIZATION_ID, VARIATION_ID, null, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	//Tests for getActions
	@Test
	public void getActionsTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(variationService.getVariation(VARIATION_ID, customization)).thenReturn(Optional.of(variation));
		Mockito.when(actionConverter.convertAll(Mockito.any())).thenReturn(Collections.singletonList(actionData));

		//when
		final List<ActionData> resultList = actionFacade.getActions(CUSTOMIZATION_ID, VARIATION_ID, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(resultList);
		Assert.assertEquals(1, resultList.size());
		final ActionData result = resultList.get(0);
		Assert.assertEquals(ACTION_ID, result.getCode());
	}


	@Test
	public void getActionsForVariationWithTwoCatalogVersionTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(variationService.getVariation(VARIATION_ID, customization)).thenReturn(Optional.of(variation));
		variation.setActions(Arrays.asList(action, actionOnline));
		Mockito.when(actionConverter.convertAll(Mockito.any())).thenAnswer(i -> {
			final Collection<CxAbstractActionModel> actions = ((Collection) i.getArguments()[0]);
			final List<ActionData> dataList = new ArrayList<ActionData>();
			for (final CxAbstractActionModel actionModel : actions)
			{
				if (actionModel == action)
				{
					dataList.add(actionData);
				}
				else if (actionModel == actionOnline)
				{
					dataList.add(actionOnlineData);
				}
			}
			return dataList;
		});

		//when
		final List<ActionData> resultList = actionFacade.getActions(CUSTOMIZATION_ID, VARIATION_ID, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(resultList);
		Assert.assertEquals(1, resultList.size());
		final ActionData result = resultList.get(0);
		Assert.assertEquals(ACTION_ID, result.getCode());
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getActionsForNotExistingVariationTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(variationService.getVariation(NOTEXISTING_VARIATION_ID, customization)).thenReturn(Optional.empty());

		//when
		actionFacade.getActions(CUSTOMIZATION_ID, NOTEXISTING_VARIATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);

	}

	@Test(expected = UnknownIdentifierException.class)
	public void getActionsWhenThereIsNoRelationBetweenCustomizationAndVariationTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(variationService.getVariation(VARIATION_ID, customization)).thenReturn(Optional.empty());

		//when
		actionFacade.getActions(CUSTOMIZATION_ID, VARIATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getActionsForNullParametersTest()
	{
		//when
		actionFacade.getActions(null, null, null, null);
	}

	//Tests for create method
	@Test
	public void createActionTest()
	{
		//given
		actionData.setCode(NEW_ACTION_ID);
		action.setCode(NEW_ACTION_ID);
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(variationService.getVariation(VARIATION_ID, customization)).thenReturn(Optional.of(variation));
		Mockito.when(actionService.getAction(NEW_ACTION_ID, variation)).thenReturn(Optional.empty());
		Mockito.when(actionReverseConverter.convert(actionData)).thenReturn(action);
		Mockito.when(actionService.createAction(action, variation)).thenReturn(action);
		Mockito.when(actionConverter.convert(action)).thenReturn(actionData);


		//when
		final ActionData result = actionFacade.createAction(CUSTOMIZATION_ID, VARIATION_ID, actionData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(NEW_ACTION_ID, result.getCode());
	}

	@Test(expected = AlreadyExistsException.class)
	public void createAlreadyExistedActionsTest()
	{
		//given
		actionData.setCode(NEW_ACTION_ID);
		action.setCode(NEW_ACTION_ID);
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(actionService.getAction(NEW_ACTION_ID, variation)).thenReturn(Optional.of(action));

		//when
		actionFacade.createAction(CUSTOMIZATION_ID, VARIATION_ID, actionData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createActionWithNullCustomizationTest()
	{
		//when
		actionFacade.createAction(null, VARIATION_ID, actionData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createActionWithNullVariationTest()
	{
		//when
		actionFacade.createAction(CUSTOMIZATION_ID, null, actionData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createActionForNullParametersTest()
	{
		//when
		actionFacade.createAction(CUSTOMIZATION_ID, null, actionData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void createActionForNotExistingVariarionTest()
	{
		//given
		actionData.setCode(NEW_ACTION_ID);
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(actionService.getAction(NEW_ACTION_ID, variation)).thenReturn(Optional.empty());
		Mockito.when(variationService.getVariation(NOTEXISTING_VARIATION_ID, customization)).thenReturn(Optional.empty());

		//when
		actionFacade.createAction(CUSTOMIZATION_ID, NOTEXISTING_VARIATION_ID, actionData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	//bulk create test
	//only validation can be tested
	//it make no sense to mock inner transaction
	@Test(expected = IllegalArgumentException.class)
	public void createActionsWithNullCustomizationTest()
	{
		//when
		actionFacade.createActions(null, VARIATION_ID, Lists.newArrayList(actionData, secondActionData), CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createActionsWithNullVariationTest()
	{
		//when
		actionFacade.createActions(CUSTOMIZATION_ID, null, Lists.newArrayList(actionData, secondActionData), CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createActionsForNullParametersTest()
	{
		//when
		actionFacade.createActions(CUSTOMIZATION_ID, null, Lists.newArrayList(actionData, secondActionData), CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void createActionsForNotExistingVariarionTest()
	{
		//given
		actionData.setCode(NEW_ACTION_ID);
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(actionService.getAction(NEW_ACTION_ID, variation)).thenReturn(Optional.empty());
		Mockito.when(variationService.getVariation(NOTEXISTING_VARIATION_ID, customization)).thenReturn(Optional.empty());

		//when
		actionFacade.createActions(CUSTOMIZATION_ID, NOTEXISTING_VARIATION_ID, Lists.newArrayList(actionData, secondActionData),
				CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}


	//update method tests
	@Test
	public void updateActionTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(actionService.getAction(ACTION_ID, variation)).thenReturn(Optional.of(action));
		Mockito.when(actionReverseConverter.convert(actionData, action)).thenReturn(action);
		Mockito.when(actionConverter.convert(action)).thenReturn(actionData);

		//when
		final ActionData result = actionFacade.updateAction(CUSTOMIZATION_ID, VARIATION_ID, ACTION_ID, actionData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(ACTION_ID, result.getCode());
	}

	@Test(expected = UnknownIdentifierException.class)
	public void updateNotExistingActionTest()
	{
		//given
		actionData.setCode(NOTEXISTING_ACTION_ID);
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(actionService.getAction(NOTEXISTING_ACTION_ID, variation)).thenReturn(Optional.empty());

		//when
		actionFacade.updateAction(CUSTOMIZATION_ID, VARIATION_ID, NOTEXISTING_ACTION_ID, actionData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = TypeConflictException.class)
	public void updateActionWhenActionTypeNotMatchTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(actionService.getAction(ACTION_ID, variation)).thenReturn(Optional.of(action));
		Mockito.when(actionReverseConverter.convert(actionData, action)).thenThrow(new TypeConflictException("typeconflict"));

		//when
		actionFacade.updateAction(CUSTOMIZATION_ID, VARIATION_ID, ACTION_ID, actionData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateActionWithNullIdTest()
	{
		//when
		actionFacade.updateAction(CUSTOMIZATION_ID, VARIATION_ID, null, actionData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateActionWithNullDataTest()
	{
		//when
		actionFacade.updateAction(CUSTOMIZATION_ID, VARIATION_ID, ACTION_ID, null, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateActionWithNullVariationTest()
	{
		//when
		actionFacade.updateAction(CUSTOMIZATION_ID, null, ACTION_ID, actionData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateActionWithNullCustomizationTest()
	{
		//when
		actionFacade.updateAction(null, VARIATION_ID, ACTION_ID, actionData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	//delete method tests
	@Test
	public void deleteActionTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(actionService.getAction(ACTION_ID, variation)).thenReturn(Optional.of(action));

		//when
		actionFacade.deleteAction(CUSTOMIZATION_ID, VARIATION_ID, ACTION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);

		//then
		Mockito.verify(actionService).deleteAction(action);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void deleteNotExistingActionTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(actionService.getAction(NOTEXISTING_ACTION_ID, variation)).thenReturn(Optional.empty());

		//when
		actionFacade.deleteAction(CUSTOMIZATION_ID, VARIATION_ID, NOTEXISTING_ACTION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void deleteActionForNotExistingVariationTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(variationService.getVariation(NOTEXISTING_VARIATION_ID, customization)).thenReturn(Optional.empty());

		//when
		actionFacade.deleteAction(CUSTOMIZATION_ID, NOTEXISTING_VARIATION_ID, ACTION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void deleteActionForNotExistingCustomizationTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(customizationService.getCustomization(NOTEXISTING_CUSTOMIZATION_ID, catalogVersionStage))
				.thenReturn(Optional.empty());

		//when
		actionFacade.deleteAction(NOTEXISTING_CUSTOMIZATION_ID, VARIATION_ID, ACTION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void deleteActionWithNullVariationTest()
	{
		//when
		actionFacade.deleteAction(CUSTOMIZATION_ID, null, ACTION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void deleteActionWithNullIdTest()
	{
		//when
		actionFacade.deleteAction(CUSTOMIZATION_ID, VARIATION_ID, null, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void deleteActionWithNullCustomizationTest()
	{
		//when
		actionFacade.deleteAction(CUSTOMIZATION_ID, null, ACTION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	//bulk delete method tests
	public void deleteNotExistingActionsTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(actionService.getAction(NOTEXISTING_ACTION_ID, variation)).thenReturn(Optional.empty());

		//when
		actionFacade.deleteActions(CUSTOMIZATION_ID, VARIATION_ID,
				Lists.newArrayList(NOTEXISTING_ACTION_ID, NOTEXISTING2_ACTION_ID), CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void deleteActionsForNotExistingVariationTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(variationService.getVariation(NOTEXISTING_VARIATION_ID, customization)).thenReturn(Optional.empty());

		//when
		actionFacade.deleteActions(CUSTOMIZATION_ID, NOTEXISTING_VARIATION_ID,
				Lists.newArrayList(NOTEXISTING_ACTION_ID, NOTEXISTING2_ACTION_ID), CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void deleteActionsForNotExistingCustomizationTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(customizationService.getCustomization(NOTEXISTING_CUSTOMIZATION_ID, catalogVersionStage))
				.thenReturn(Optional.empty());

		//when
		actionFacade.deleteActions(NOTEXISTING_CUSTOMIZATION_ID, VARIATION_ID,
				Lists.newArrayList(NOTEXISTING_ACTION_ID, NOTEXISTING2_ACTION_ID), CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void deleteActionsWithNullVariationTest()
	{
		//when
		actionFacade.deleteActions(CUSTOMIZATION_ID, null, Lists.newArrayList(NOTEXISTING_ACTION_ID, NOTEXISTING2_ACTION_ID),
				CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void deleteActionsWithNullIdTest()
	{
		//when
		actionFacade.deleteActions(CUSTOMIZATION_ID, VARIATION_ID, null, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void deleteActionsWithNullCustomizationTest()
	{
		//when
		actionFacade.deleteActions(CUSTOMIZATION_ID, null, Lists.newArrayList(NOTEXISTING_ACTION_ID, NOTEXISTING2_ACTION_ID),
				CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

}
