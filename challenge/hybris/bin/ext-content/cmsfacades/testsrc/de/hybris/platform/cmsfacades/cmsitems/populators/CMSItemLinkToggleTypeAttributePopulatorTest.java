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
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CMSItemLinkToggleTypeAttributePopulatorTest
{
	@InjectMocks
	private CMSItemLinkToggleTypeAttributePopulator populator;

	@Mock
	private AttributeDescriptorModel source;

	@Mock
	private ComponentTypeAttributeData target;


	@Test
	public void testWhenProvidedSourceWithQualifierExternal_shouldNullifyStructureTypeOfTarget()
	{
		// GIVEN
		when(source.getQualifier()).thenReturn(CmsfacadesConstants.FIELD_EXTERNAL_NAME);

		// WHEN
		populator.populate(source, target);

		// THEN
		verify(target).setCmsStructureType(null);
	}

	@Test
	public void testWhenProvidedSourceWithQualifierUrlLink_shouldSubstituteTargetWithLinkToggleQualifier()
	{
		// GIVEN
		when(source.getQualifier()).thenReturn(CmsfacadesConstants.FIELD_URL_LINK_NAME);

		// WHEN
		populator.populate(source, target);

		// THEN
		verify(target).setCmsStructureType("LinkToggle");
		verify(target).setQualifier(CmsfacadesConstants.FIELD_LINK_TOGGLE_NAME);
		verify(target).setI18nKey("se.editor.linkto.label");
	}
}
