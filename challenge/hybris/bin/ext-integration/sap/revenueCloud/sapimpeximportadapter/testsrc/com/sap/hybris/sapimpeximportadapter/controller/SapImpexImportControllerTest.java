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
package com.sap.hybris.sapimpeximportadapter.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hybris.sapimpeximportadapter.controllers.SapImpexImportController;
import com.sap.hybris.sapimpeximportadapter.facades.ImpexImportFacade;


/**
 * JUnit test suite for {@link SapImpexImportControllerTest}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapImpexImportControllerTest
{

	@Mock
	private ImpexImportFacade impexImportFacade;

	@Mock
	private SessionService sessionService;

	@InjectMocks
	private SapImpexImportController sapImpexImportController;



	@Test
	public void checkIfTheStreamIsClosedInNormalFlow() throws IOException
	{
		final InputStream is = new ByteArrayInputStream("dummy".getBytes());
		final InputStream isSpy = spy(is);
		doNothing().when(impexImportFacade).createAndImportImpexMedia(any(InputStream.class));
		sapImpexImportController.importFromStream(isSpy);
		verify(isSpy).close();
	}

}
