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
package de.hybris.platform.patches.actions

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.patches.Patch
import de.hybris.platform.patches.actions.data.PatchActionData
import de.hybris.platform.patches.actions.data.PatchActionDataOption
import de.hybris.platform.patches.actions.data.SamplePatchActionForTests
import de.hybris.platform.patches.actions.data.setup.TestPatchesSystemSetup
import de.hybris.platform.patches.actions.utils.PatchExecutionUtils
import de.hybris.platform.patches.enums.ExecutionStatus
import de.hybris.platform.patches.exceptions.PatchActionException
import de.hybris.platform.patches.model.PatchExecutionModel
import de.hybris.platform.patches.model.PatchExecutionUnitModel
import de.hybris.platform.patches.service.PatchExecutionService
import de.hybris.platform.util.AppendSpringConfiguration
import de.hybris.platform.servicelayer.ServicelayerTransactionalSpockSpecification
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import org.junit.Test
import spock.lang.Shared

import javax.annotation.Resource

/**
 * Integration test for perform patch action error handling. Checks error handling and tracking for valid, invalid data and major exception.
 */
@IntegrationTest
@AppendSpringConfiguration(["patchestest-spring.xml"])
public class PatchActionPerformErrorHandlingTest extends ServicelayerTransactionalSpockSpecification {
    @Resource
    private SamplePatchActionForTests samplePatchActionForTests

    @Resource
    private PatchExecutionService patchExecutionService

    @Resource
    private FlexibleSearchService flexibleSearchService

    @Resource
    private TestPatchesSystemSetup testPatchesSystemSetup

    @Shared
    private Patch patch

    private PatchExecutionModel patchExecution

    def setupSpec() {
        patch = Mock() {
            getPatchId() >> "test_valid"
            getPatchName() >> "test_valid"
            getPatchDescription() >> "test_valid"
        }
    }

    def setup() {
        patchExecution = patchExecutionService.createPatchExecution(patch)
    }

    @Test
    def "test valid query"() {
        given: "patch data with valid SQL"
        PatchActionData patchData = Mock() {
            getName() >> "Executing a valid query"
            getOption(PatchActionDataOption.Sql.QUERY) >> "valid query"
            getPatch() >> patch
            //This config doesn't make sense but it's only for exception handling tests
            getOption(PatchActionDataOption.Impex.RUN_AGAIN) >> true
        }

        when: "call isRunAgain method - everything should be ok"
        samplePatchActionForTests.perform(patchData)

        then: "patch execution unit should has SUCCESS status"
        PatchExecutionUnitModel executionUnit = PatchExecutionUtils.getPatchExecutionUnits(patchExecution).get(0)
        executionUnit.getExecutionStatus() == ExecutionStatus.SUCCESS
        executionUnit.getErrorLog() == null
    }

    @Test
    def "test minor error"() {
        given: "patch data with minor exception"
        PatchActionData patchData = Mock() {
            getName() >> "Executing an invalid query"
            getOption(PatchActionDataOption.Sql.QUERY) >> "invalid query"
            getPatch() >> patch
            //This config doesn't make sense but it's only for exception handling tests
            getOption(PatchActionDataOption.Impex.RUN_AGAIN) >> { throw new PatchActionException("minor exception") }
        }

        when: "call isRunAgain method - exception should be caught by aspect"
        samplePatchActionForTests.perform(patchData)

        then: "patch execution unit should has ERROR status and log filled in"
        notThrown(PatchActionException)

        PatchExecutionUnitModel executionUnit = PatchExecutionUtils.getPatchExecutionUnits(patchExecution).get(0)
        executionUnit.getExecutionStatus() == ExecutionStatus.ERROR
        executionUnit.getErrorLog() == "minor exception"
    }

}