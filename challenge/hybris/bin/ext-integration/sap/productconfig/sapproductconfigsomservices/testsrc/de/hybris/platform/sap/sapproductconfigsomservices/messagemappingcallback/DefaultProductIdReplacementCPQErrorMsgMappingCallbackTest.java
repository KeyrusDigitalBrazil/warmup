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
package de.hybris.platform.sap.sapproductconfigsomservices.messagemappingcallback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.BackendMessage;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Locale;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultProductIdReplacementCPQErrorMsgMappingCallbackTest
{

	private static final String PRODUCT_DESCRIPTION = "Product Description";
	private static final String PRODUCT_CODE = "00000000123";
	private static final String PRODUCT_CODE_NOT_KNOWN = "ABCD";

	DefaultProductIdReplacementCPQErrorMsgMappingCallback classUnderTest = new DefaultProductIdReplacementCPQErrorMsgMappingCallback();
	private final ProductService productServiceMock = EasyMock.createNiceMock(ProductService.class);
	private final ProductModel productModelMock = EasyMock.createNiceMock(ProductModel.class);
	private final BackendMessage message = new BackendMessage("E", "AREA", "123", "V1", PRODUCT_CODE, "V3", "V4");
	private final BackendMessage messageNotFound = new BackendMessage("E", "AREA", "123", "V1", PRODUCT_CODE_NOT_KNOWN, "V3",
			"V4");


	@Before
	public void initialize()
	{
		EasyMock.expect(productServiceMock.getProductForCode(PRODUCT_CODE)).andReturn(productModelMock);
		EasyMock.expect(productServiceMock.getProductForCode(PRODUCT_CODE_NOT_KNOWN))
				.andThrow(new UnknownIdentifierException("Not found"));
		EasyMock.expect(productModelMock.getName(Locale.US)).andReturn(PRODUCT_DESCRIPTION);
		EasyMock.replay(productModelMock);
		EasyMock.replay(productServiceMock);
		classUnderTest.setProductService(productServiceMock);
		classUnderTest.setLocale(Locale.US);
	}

	@Test
	public void testProcess()
	{
		assertTrue(classUnderTest.process(message));
		assertEquals(PRODUCT_DESCRIPTION, message.getVars()[1]);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testProcessNotFound()
	{
		classUnderTest.process(messageNotFound);
	}
}
