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
package de.hybris.platform.assistedserviceyprofilefacades.populator;

import de.hybris.platform.assistedserviceyprofilefacades.data.ProductAffinityData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.yaasyprofileconnect.yaas.Affinity;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class ProductAffinityPopulatorTest extends AbstractProfileAffinityTest
{
	@InjectMocks
	private final ProductAffinityPopulator productAffinityPopulator = new ProductAffinityPopulator();

	@Mock
	private ProductFacade productFacade;

	@InjectMocks
	private final AffinityPopulator affinityPopulator = new AffinityPopulator();
	
	@Test
	public void getProductAffinitiesTest()
	{
		final ProductData productData = Mockito.mock(ProductData.class);

		List<Map.Entry<String, Affinity>> parsedProducts =
				affinityProfile.getInsights().getAffinities().getProducts().entrySet().parallelStream().collect(Collectors.toList());

		assertEquals(1, parsedProducts.size());


		Map.Entry<String, Affinity> productAffinity = parsedProducts.get(0);

		Mockito
				.when(productFacade
						.getProductForCodeAndOptions(productAffinity.getKey(), Arrays.asList(ProductOption.BASIC, ProductOption.PRICE))).thenReturn(productData);

		final ProductAffinityData productAffinityData = new ProductAffinityData();

		productAffinityPopulator.populate(productAffinity, productAffinityData);
		affinityPopulator.populate(productAffinity, productAffinityData);

		assertEquals(productData, productAffinityData.getProductData());
		assertEquals(0.112, productAffinityData.getRecentScore().doubleValue(), 0.0000001);
		assertEquals((Integer) 4, productAffinityData.getRecentViewCount());
		assertEquals(0.3010299956639812, productAffinityData.getScore().doubleValue(), 0.0000001);

	}
}
