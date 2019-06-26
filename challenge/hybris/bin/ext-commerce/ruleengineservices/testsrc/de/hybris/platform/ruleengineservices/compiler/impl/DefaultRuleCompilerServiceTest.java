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
package de.hybris.platform.ruleengineservices.compiler.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.model.AbstractRulesModuleModel;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContextFactory;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerListener;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerListenersFactory;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerProblem;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerResult;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerResultFactory;
import de.hybris.platform.ruleengineservices.compiler.RuleIrProcessor;
import de.hybris.platform.ruleengineservices.compiler.RuleIrProcessorFactory;
import de.hybris.platform.ruleengineservices.compiler.RuleIrVariablesGenerator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrVariablesGeneratorFactory;
import de.hybris.platform.ruleengineservices.compiler.RuleSourceCodeTranslator;
import de.hybris.platform.ruleengineservices.compiler.RuleSourceCodeTranslatorFactory;
import de.hybris.platform.ruleengineservices.compiler.RuleTargetCodeGenerator;
import de.hybris.platform.ruleengineservices.compiler.RuleTargetCodeGeneratorFactory;
import de.hybris.platform.ruleengineservices.maintenance.impl.DefaultRuleCompilationContext;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRuleCompilerServiceTest
{
	private static final String MODULE_NAME = "MODULE_NAME";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private AbstractRuleModel rule;

	@Mock
	private AbstractRulesModuleModel rulesModule;

	@Mock
	private RuleIrVariablesGenerator variablesGenerator;

	@Mock
	private DefaultRuleCompilerContext context;

	@Mock
	private RuleCompilerResult ruleCompilerResult;

	@Mock
	private RuleCompilerListenersFactory ruleCompilerListenersFactory;

	@Mock
	private RuleIrVariablesGeneratorFactory ruleIrVariablesGeneratorFactory;

	@Mock
	private RuleCompilerContextFactory<DefaultRuleCompilerContext> ruleCompilerContextFactory;

	@Mock
	private RuleSourceCodeTranslatorFactory ruleSourceCodeTranslatorFactory;

	@Mock
	private RuleSourceCodeTranslator ruleSourceCodeTranslator;

	@Mock
	private RuleIrProcessorFactory ruleIrProcessorFactory;

	@Mock
	private RuleIrProcessor ruleIrProcessor;

	@Mock
	private RuleTargetCodeGeneratorFactory ruleTargetCodeGeneratorFactory;

	@Mock
	private RuleTargetCodeGenerator ruleTargetCodeGenerator;

	@Mock
	private RuleCompilerResultFactory ruleCompilerResultFactory;

	@Mock
	private ModelService modelService;

	@InjectMocks
	private DefaultRuleCompilerService ruleCompilerService;
	@InjectMocks
	private DefaultRuleCompilationContext ruleCompilationContext;

	@Captor
	private ArgumentCaptor<List<RuleCompilerProblem>> ruleCompilerProblemsCaptor;

	@Before
	public void setUp() throws Exception
	{
		when(ruleCompilerListenersFactory.getListeners(RuleCompilerListener.class)).thenReturn(Collections.emptyList());
		when(ruleIrVariablesGeneratorFactory.createVariablesGenerator()).thenReturn(variablesGenerator);
		when(ruleCompilerContextFactory.createContext(ruleCompilationContext, rule, MODULE_NAME, variablesGenerator))
				.thenReturn(context);
		when(context.getProblems()).thenReturn(Collections.emptyList());
		when(ruleCompilerResultFactory.create(eq(rule), ruleCompilerProblemsCaptor.capture()))
				.thenReturn(ruleCompilerResult);

		when(ruleSourceCodeTranslatorFactory.getSourceCodeTranslator(context)).thenReturn(ruleSourceCodeTranslator);
		when(ruleIrProcessorFactory.getRuleIrProcessors()).thenReturn(Collections.singletonList(ruleIrProcessor));
		when(ruleTargetCodeGeneratorFactory.getTargetCodeGenerator(context)).thenReturn(ruleTargetCodeGenerator);

	}

	@Test
	public void testCompileFails()
	{
		when(ruleSourceCodeTranslator.translate(context)).thenThrow(NullPointerException.class);
		ruleCompilerService.compile(ruleCompilationContext, rule, MODULE_NAME);
		assertThat(ruleCompilerProblemsCaptor.getValue()).hasSize(1);
		assertThat(ruleCompilerProblemsCaptor.getValue().get(0).getSeverity()).isEqualTo(RuleCompilerProblem.Severity.ERROR);
	}
}
