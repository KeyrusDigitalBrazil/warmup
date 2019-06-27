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
import de.hybris.platform.patches.actions.utils.PatchExecutionUtils
import de.hybris.platform.patches.data.ImpexDataFile
import de.hybris.platform.patches.data.ImpexHeaderFile
import de.hybris.platform.patches.data.ImpexImportUnit
import de.hybris.platform.patches.enums.ExecutionStatus
import de.hybris.platform.patches.exceptions.PatchImportException
import de.hybris.platform.patches.model.PatchExecutionModel
import de.hybris.platform.patches.model.PatchExecutionUnitModel
import de.hybris.platform.patches.service.PatchExecutionService
import de.hybris.platform.patches.service.PatchImportService
import de.hybris.platform.util.AppendSpringConfiguration
import de.hybris.platform.servicelayer.ServicelayerTransactionalSpockSpecification
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import org.junit.Test
import spock.lang.Shared
import spock.lang.Title

import javax.annotation.Resource

/**
 * Integration test which is checking error handling for impex import.
 */
@IntegrationTest
@AppendSpringConfiguration(["patchestest-spring.xml"])
@Title("Integration test which is checking error handling for impex import")
class ImportPatchActionErrorHandlingTest extends ServicelayerTransactionalSpockSpecification {

    /** Tested class */
    @Resource
    private PatchImportService patchImportService

    @Resource
    private PatchImportService samplePatchImportServiceMinorExc

    @Resource
    private PatchImportService samplePatchImportServiceMajorExc

    @Resource
    protected FlexibleSearchService flexibleSearchService

    @Resource
    PatchExecutionService patchExecutionService

    @Shared
    Patch patch
    @Shared
    ImpexDataFile impexDataFile
    ImpexImportUnit impexImportUnit
    PatchExecutionModel patchExecution

    private static final String SAMPLE_DATA_PATH = "/tests/import/"

    def setupSpec() {
        impexDataFile = new ImpexDataFile(name: "data", filePath: SAMPLE_DATA_PATH + "testImpexData.impex")
        patch = Mock() {
            getPatchId() >> "1_0"
            getPatchName() >> "01_00"
            getPatchDescription() >> "patch description"
        }
    }

    def setup() {
        impexImportUnit = new ImpexImportUnit()
        impexImportUnit.setImpexDataFile(impexDataFile)
        patchExecution = patchExecutionService.createPatchExecution(patch)
    }

    /**
     * Test checks that PatchExecutionUnit was created with SUCCESS status and no errorLog when there was no problem during importing impex
     */
    @Test
    def "should check Patch tracking when there is no problem for impex import"() {

        when: "I import impex unit with valid data"
        patchImportService.importImpexUnit(impexImportUnit)

        then: "I have PatchExecutionUnit in DB with proper data"
        PatchExecutionUnitModel patchExecutionUnit = PatchExecutionUtils.getPatchExecutionUnits(patchExecution).get(0)
        patchExecutionUnit
        patchExecutionUnit.getExecutionStatus() == ExecutionStatus.SUCCESS
        !patchExecutionUnit.errorLog
    }

    /**
     * Test checks that PatchExecutionUnit was created with ERROR status and errorLog because path for mandatory headerFile is missing.
     * Import is not executed as there was a problem with generation of content hash.
     * It's also checking if there is a proper Exception and cause of the error thrown.
     */
    @Test
    def "should throw PatchImportException when mandatory file has wrong path"() {

        given: "I import impex unit with incorrect data"
        ImpexHeaderFile impexHeader = new ImpexHeaderFile(optional: false, filePath: "fakePath")
        impexImportUnit.addHeaderFile impexHeader

        when: "During generating content hash for patchExecutionUnit"
        patchImportService.importImpexUnit(impexImportUnit)

        then: "I have PatchExecutionUnit in DB with proper data, minor Exception is thrown"
        Exception e = thrown()
        e instanceof PatchImportException
        e.message == "java.io.FileNotFoundException: fakePath - mandatory file is not existing."
        e.cause instanceof FileNotFoundException
        PatchExecutionUnitModel patchExecutionUnit = PatchExecutionUtils.getPatchExecutionUnits(patchExecution).get(0)
        patchExecutionUnit
        patchExecutionUnit.executionStatus == ExecutionStatus.ERROR
        patchExecutionUnit.errorLog.contains("Problem while generating content hash")
    }

    /**
     * Test checks that PatchExecutionUnit was created with ERROR status and empty errorLog because minor exception occurred during import.
     * It's also checking if there is a proper Exception thrown further with proper cause.
     */
    @Test
    def "should throw PatchImportException when minor error appeared during impex import"() {

        when: "During import impex unit there was a minor exception thrown"

        samplePatchImportServiceMinorExc.importImpexUnit(impexImportUnit)

        then: "I have PatchExecutionUnit in DB with proper data, minor Exception is thrown"
        Exception e = thrown()
        e instanceof PatchImportException
        e.message == "minor exception"
        PatchExecutionUnitModel patchExecutionUnit = PatchExecutionUtils.getPatchExecutionUnits(patchExecution).get(0)
        patchExecutionUnit
        patchExecutionUnit.executionStatus == ExecutionStatus.ERROR
    }
    /**
     * Test checks that PatchExecutionUnit was created with UNKNOWN status and empty errorLog because major exception occurred during import.
     * It's also checking if there is a proper Exception thrown further with proper cause.
     */
    @Test
    def "should throw UnsupportedOperationException when major error appeared during impex import"() {

        when: "During import impex unit there was a major exception thrown"
        samplePatchImportServiceMajorExc.importImpexUnit(impexImportUnit)

        then: "I have PatchExecutionUnit in DB with proper data, major Exception is thrown"
        Exception e = thrown()
        e instanceof UnsupportedOperationException
        e.message == "major exception"
        PatchExecutionUnitModel patchExecutionUnit = PatchExecutionUtils.getPatchExecutionUnits(patchExecution).get(0)
        patchExecutionUnit
        patchExecutionUnit.executionStatus == ExecutionStatus.UNKNOWN
        !patchExecutionUnit.errorLog
    }
}
