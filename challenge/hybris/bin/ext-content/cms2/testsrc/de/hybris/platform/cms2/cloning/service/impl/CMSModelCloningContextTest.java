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
package de.hybris.platform.cms2.cloning.service.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.media.MediaContainerModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.BiPredicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class CMSModelCloningContextTest
{
	@Mock
	private BiPredicate<ItemModel, String> predicate;
	@Mock
	private MockSupplierPredicate supplierPredicate;
	@InjectMocks
	private CMSModelCloningContext modelCloningContext;

	@Before
	public void setUp()
	{
		modelCloningContext.setTreatAsPartOfPredicates(Arrays.asList(predicate));
		modelCloningContext.setPresetValuePredicates(Arrays.asList(supplierPredicate));
	}

	@Test
	public void shouldNotTreatAsPartOfWithEmptyPredicates()
	{
		modelCloningContext.setTreatAsPartOfPredicates(Collections.emptyList());

		final boolean result = modelCloningContext.treatAsPartOf(new CMSParagraphComponentModel(),
				CMSParagraphComponentModel.CONTENT);

		assertThat(result, is(false));
	}

	@Test
	public void shouldNotTreatAsPartOfWithPredicateFalse()
	{
		when(predicate.test(any(), anyString())).thenReturn(false);

		final boolean result = modelCloningContext.treatAsPartOf(new CMSParagraphComponentModel(),
				CMSParagraphComponentModel.CONTENT);

		assertThat(result, is(false));
	}

	@Test
	public void shouldTreatAsPartOfWithPredicateTrue()
	{
		when(predicate.test(any(), anyString())).thenReturn(true);

		final boolean result = modelCloningContext.treatAsPartOf(new CMSParagraphComponentModel(),
				CMSParagraphComponentModel.CONTENT);

		assertThat(result, is(true));
	}

	@Test
	public void shouldGetPresetValueForMediaContainer()
	{
		when(supplierPredicate.test(any(), anyString())).thenReturn(true);

		modelCloningContext.getPresetValue(new MediaContainerModel(), MediaContainerModel.QUALIFIER);

		verify(supplierPredicate).test(any(), anyString());
		verify(supplierPredicate).get();
	}

}
