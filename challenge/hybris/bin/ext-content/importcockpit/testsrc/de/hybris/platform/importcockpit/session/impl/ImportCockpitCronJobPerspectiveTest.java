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
package de.hybris.platform.importcockpit.session.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.session.UIBrowserArea;
import de.hybris.platform.cockpit.session.impl.DefaultConfigurableBrowserModel;
import de.hybris.platform.importcockpit.model.ImportCockpitCronJobModel;
import de.hybris.platform.importcockpit.session.mapping.impl.MappingBrowserModel;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;


public class ImportCockpitCronJobPerspectiveTest
{

	@InjectMocks
	private final ImportCockpitCronJobPerspective perspective = spy(new ImportCockpitCronJobPerspective());

	@Mock
	private TypedObject typedObject;

	@Mock
	private ImportCockpitCronJobModel jobModel;

	private UIBrowserArea browserArea;

	@Mock
	private MappingBrowserModel mappingBrowserModel;

	@Mock
	private ImportCockpitWelcomeBrowserModel welcomeBrowserModel;

	@Mock
	private ImportCockpitBrowserModel importCockpitBrowserModel;

	@Mock
	private DefaultConfigurableBrowserModel configurableBrowserModel;

	@Before
	public void setUp()
	{
        initMocks(this);
        browserArea = mock(UIBrowserArea.class);
        doNothing().when(perspective).onCockpitEvent(any());
        perspective.setBrowserArea(browserArea);
        when(typedObject.getObject()).thenReturn(jobModel);
        when(browserArea.getBrowsers()).thenReturn(
				Arrays.asList(configurableBrowserModel, mappingBrowserModel, importCockpitBrowserModel, welcomeBrowserModel));
        when(mappingBrowserModel.getImportCockpitCronJob()).thenReturn(jobModel);
	}

	@Test
	public void handleItemRemoved()
	{
		perspective.handleItemRemoved(typedObject);
		verify(browserArea, times(1)).close(mappingBrowserModel);
	}

}
