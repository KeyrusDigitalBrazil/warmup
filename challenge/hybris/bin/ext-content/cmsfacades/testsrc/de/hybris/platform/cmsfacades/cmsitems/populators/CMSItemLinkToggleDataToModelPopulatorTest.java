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
package de.hybris.platform.cmsfacades.cmsitems.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2lib.model.components.FlashComponentModel;
import de.hybris.platform.cmsfacades.cmsitems.predicates.ModelContainsLinkTogglePredicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.*;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CMSItemLinkToggleDataToModelPopulatorTest
{
	@Spy
	@InjectMocks
	private CMSItemLinkToggleDataToModelPopulator populator;

	@Mock
	private ModelContainsLinkTogglePredicate predicate;

	private CMSItemModel itemModel = new FlashComponentModel();

	@Mock
	private Map<String, Object> source;

	@Test
	public void testWhenPredicateReturnsFalse_shouldSkipPopulator()
	{
		// GIVEN
		when(predicate.test(itemModel)).thenReturn(false);
		populator.setCmsModelContainsLinkTogglePredicate(predicate);

		// WHEN
		populator.populate(source, itemModel);

		// THEN
		verify(populator, never()).invokeMethod(source, itemModel, "setUrlLink", FIELD_URL_LINK_NAME, String.class);
		verify(populator, never()).invokeMethod(source, itemModel, "setExternal", FIELD_EXTERNAL_NAME, boolean.class);
	}

	@Test
	public void testWhenPredicateReturnsTrue_shouldInvokeTwoMethodsForUrlLinkAndExternalFields() throws NoSuchMethodException
	{
		// GIVEN
		when(predicate.test(itemModel)).thenReturn(true);
		populator.setCmsModelContainsLinkTogglePredicate(predicate);
		Map<String, Object> linkToggle = new HashMap<>();
		linkToggle.put("urlLink", "TestData");
		linkToggle.put("external", false);
		when(source.get("linkToggle")).thenReturn(linkToggle);

		// WHEN
		populator.populate(source, itemModel);

		// THEN
		verify(populator, times(1)).invokeMethod(source, itemModel, "setUrlLink", FIELD_URL_LINK_NAME, String.class);
		verify(populator, times(1)).invokeMethod(source, itemModel, "setExternal", FIELD_EXTERNAL_NAME, boolean.class);
	}
}
