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
package com.hybris.backoffice.widgets.actions.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import de.hybris.platform.auditreport.model.CreateAuditReportCronJobModel;
import de.hybris.platform.auditreport.model.CreateAuditReportJobModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.audit.AuditReportConfigModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.cockpitng.config.jaxb.wizard.CustomType;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.AbstractCockpitngUnitTest;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;


@ExtensibleWidget(level = ExtensibleWidget.ALL)
@RunWith(MockitoJUnitRunner.class)
public class CreateAuditReportWizardHandlerTest extends AbstractCockpitngUnitTest<CreateAuditReportWizardHandler>
{
	private static final String CONFIG_NAME = "configName";

	@Spy
	@InjectMocks
	private CreateAuditReportWizardHandler wizardHandler;

	@Mock
	private ModelService modelService;
	@Mock
	private KeyGenerator keyGenerator;
	@Mock
	private UserService userService;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private CronJobService cronJobService;
	@Mock
	private NotificationService notificationService;

	@Test
	public void testPerformSuccessfully()
	{
		// given
		final FlowActionHandlerAdapter adapter = mock(FlowActionHandlerAdapter.class);

		final AuditReportWizardData wizardData = mock(AuditReportWizardData.class);
		final ItemModel rootType = mock(ItemModel.class);
		when(wizardData.getSourceItem()).thenReturn(rootType);
		final String reportId = "reportId";
		when(wizardData.getReportName()).thenReturn(reportId);

		final WidgetInstanceManager wim = mock(WidgetInstanceManager.class);
		final WidgetModel wm = mock(WidgetModel.class);
		when(adapter.getWidgetInstanceManager()).thenReturn(wim);
		when(wim.getModel()).thenReturn(wm);
		when(wm.getValue(CreateAuditReportWizardHandler.MODEL_WIZARD_DATA, AuditReportWizardData.class)).thenReturn(wizardData);

		final CronJobModel cronJobModel = mock(CronJobModel.class);
		doReturn(cronJobModel).when(wizardHandler).createCronJobModel(wizardData);

		// when
		wizardHandler.perform(mock(CustomType.class), adapter, Collections.emptyMap());

		// then
		verify(adapter).done();
		verify(cronJobService).performCronJob(cronJobModel);
	}

	@Test
	public void testCreateCronJobModel()
	{
		// given
		final CreateAuditReportJobModel generateAuditReportJobModel = mock(CreateAuditReportJobModel.class);
		when(modelService.create(CreateAuditReportJobModel.class)).thenReturn(generateAuditReportJobModel);
		final CreateAuditReportCronJobModel generateAuditReportCronJobModel = mock(CreateAuditReportCronJobModel.class);
		when(modelService.create(CreateAuditReportCronJobModel.class)).thenReturn(generateAuditReportCronJobModel);

		final String id = "uuid-123";
		when(keyGenerator.generate()).thenReturn(id);

		final String springId = "springBeanId";
		when(wizardHandler.getCronJobPerformableSpringId()).thenReturn(springId);

		final UserModel user = mock(UserModel.class);
		when(userService.getCurrentUser()).thenReturn(user);
		final LanguageModel language = mock(LanguageModel.class);
		when(commonI18NService.getCurrentLanguage()).thenReturn(language);
		final CurrencyModel currency = mock(CurrencyModel.class);
		when(commonI18NService.getCurrentCurrency()).thenReturn(currency);

		final AuditReportWizardData wizardData = mock(AuditReportWizardData.class);
		final ItemModel rootType = mock(ItemModel.class);
		when(wizardData.getSourceItem()).thenReturn(rootType);
		final String reportId = "reportId";
		when(wizardData.getReportName()).thenReturn(reportId);
		final AuditReportConfigModel config = mock(AuditReportConfigModel.class);
		when(wizardData.getAuditReportConfig()).thenReturn(CONFIG_NAME);
		when(config.getCode()).thenReturn(CONFIG_NAME);

		// when
		final CronJobModel cronJobModel = wizardHandler.createCronJobModel(wizardData);

		// then
		assertThat(cronJobModel).isSameAs(generateAuditReportCronJobModel);
		verify(generateAuditReportCronJobModel).setCode(id);
		verify(generateAuditReportCronJobModel).setActive(Boolean.TRUE);
		verify(generateAuditReportCronJobModel).setJob(generateAuditReportJobModel);

		verify(generateAuditReportJobModel).setCode(id);
		verify(generateAuditReportJobModel).setSpringId(springId);

		verify(generateAuditReportCronJobModel).setSessionUser(user);
		verify(generateAuditReportCronJobModel).setSessionLanguage(language);
		verify(generateAuditReportCronJobModel).setSessionCurrency(currency);

		verify(generateAuditReportCronJobModel).setRootItem(rootType);
		verify(generateAuditReportCronJobModel).setReportId(reportId);
		verify(generateAuditReportCronJobModel).setAudit(false);
		verify(generateAuditReportCronJobModel).setConfigName(CONFIG_NAME);
		verify(generateAuditReportCronJobModel).setIncludedLanguages(any());
		verify(generateAuditReportCronJobModel).setAuditReportTemplate(any());

		verify(modelService).save(generateAuditReportJobModel);
		verify(modelService).save(generateAuditReportCronJobModel);
		verifyNoMoreInteractions(generateAuditReportCronJobModel, generateAuditReportJobModel);
	}
}
