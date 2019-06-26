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
package de.hybris.platform.personalizationservices.process;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.constants.PersonalizationservicesConstants;
import de.hybris.platform.personalizationservices.model.process.CxPersonalizationProcessModel;
import de.hybris.platform.personalizationservices.process.dao.CxPersonalizationBusinessProcessDao;
import de.hybris.platform.personalizationservices.process.impl.DefaultCxProcessService;
import de.hybris.platform.personalizationservices.segment.CxSegmentService;
import de.hybris.platform.personalizationservices.strategies.CxProcessKeyStrategy;
import de.hybris.platform.personalizationservices.strategies.ProcessSelectionStrategy;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.helpers.ProcessParameterHelper;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.site.BaseSiteService;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;


@UnitTest
public class DefaultCxProcessTest
{
	private static final String PROCESS_DEFINITION_NAME = "proces_definition_name";


	private final DefaultCxProcessService service = new DefaultCxProcessService();

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private BusinessProcessService businessProcessService;

	@Mock
	private ProcessSelectionStrategy processSelectionStrategy;

	@Mock
	private CxPersonalizationBusinessProcessDao cxPersonalizationBusinessProcessDao;

	@Mock
	private CxProcessKeyStrategy cxProcessKeyStrategy;

	@Mock
	private CxSegmentService cxSegmentService;

	@Mock
	private SessionService sessionService;

	@Mock
	private CatalogVersionModel catalogVersion;

	@Mock
	protected ProcessParameterHelper processParameterHelper;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		service.setBusinessProcessService(businessProcessService);
		service.setProcessSelectionStrategy(processSelectionStrategy);
		service.setCxPersonalizationBusinessProcessDao(cxPersonalizationBusinessProcessDao);
		service.setCxProcessKeyStrategy(cxProcessKeyStrategy);

		BDDMockito.given(sessionService.getAttribute(PersonalizationservicesConstants.SESSION_TOKEN)).willReturn("sessionToken");
	}

	@Test
	public void shouldNotCreateProcessForUpdateCustomerExperience()
	{
		//given
		final UserModel user = new UserModel();
		final List<CatalogVersionModel> cvs = Arrays.asList(catalogVersion);

		final CxPersonalizationProcessModel process = new CxPersonalizationProcessModel();

		given(processSelectionStrategy.retrieveProcessDefinitionName(user, cvs)).willReturn(PROCESS_DEFINITION_NAME);
		given(cxProcessKeyStrategy.getProcessKey(Mockito.any(), Mockito.any())).willReturn("test");
		given(cxPersonalizationBusinessProcessDao.findActiveBusinessProcesses(PROCESS_DEFINITION_NAME, "test"))
				.willReturn(Lists.newArrayList());
		given(businessProcessService.createProcess(BDDMockito.any(), BDDMockito.eq(PROCESS_DEFINITION_NAME))).willReturn(process);

		//when
		final CxPersonalizationProcessModel returnedProcess = service.startPersonalizationCalculationProcess(user, catalogVersion);

		//then
		assertNull(returnedProcess);
	}

	@Test
	public void shouldCreateProcessForUpdateCustomerExperience()
	{
		//given
		final UserModel user = new CustomerModel();
		final List<CatalogVersionModel> cvs = Arrays.asList(catalogVersion);

		final CxPersonalizationProcessModel process = new CxPersonalizationProcessModel();

		given(processSelectionStrategy.retrieveProcessDefinitionName(user, cvs)).willReturn(PROCESS_DEFINITION_NAME);
		given(cxProcessKeyStrategy.getProcessKey(Mockito.any(), Mockito.any())).willReturn("test");
		given(cxPersonalizationBusinessProcessDao.findActiveBusinessProcesses(PROCESS_DEFINITION_NAME, "test"))
				.willReturn(Lists.newArrayList());
		given(businessProcessService.createProcess(BDDMockito.any(), BDDMockito.eq(PROCESS_DEFINITION_NAME))).willReturn(process);

		//when
		final CxPersonalizationProcessModel returnedProcess = service.startPersonalizationCalculationProcess(user, catalogVersion);

		//then
		verify(processSelectionStrategy).retrieveProcessDefinitionName(user, cvs);
		verify(businessProcessService).createProcess(BDDMockito.anyString(), BDDMockito.eq(PROCESS_DEFINITION_NAME));
		assertNotNull(returnedProcess);
		assertEquals(process, returnedProcess);
		assertEquals(cvs, returnedProcess.getCatalogVersions());
		assertEquals(user, returnedProcess.getUser());
	}

	@Test
	public void shouldReturnEmptyListOfProcessesWithoutUpdateCustomerExperience()
	{
		//given
		final UserModel user = new UserModel();
		final List<CatalogVersionModel> cvs = Arrays.asList(catalogVersion);
		final CxPersonalizationProcessModel process = new CxPersonalizationProcessModel();
		process.setUser(user);
		process.setCatalogVersions(cvs);

		given(processSelectionStrategy.retrieveProcessDefinitionName(user, cvs)).willReturn(PROCESS_DEFINITION_NAME);
		given(cxProcessKeyStrategy.getProcessKey(Mockito.any(), Mockito.any())).willReturn("test");
		given(cxPersonalizationBusinessProcessDao.findActiveBusinessProcesses(PROCESS_DEFINITION_NAME, "test"))
				.willReturn(Lists.newArrayList(process));

		//when
		final CxPersonalizationProcessModel returnedProcesses = service.startPersonalizationCalculationProcess(user,
				catalogVersion);

		//then
		verify(processSelectionStrategy).retrieveProcessDefinitionName(user, cvs);
		verifyZeroInteractions(businessProcessService);
		assertNull(returnedProcesses);
	}

}
