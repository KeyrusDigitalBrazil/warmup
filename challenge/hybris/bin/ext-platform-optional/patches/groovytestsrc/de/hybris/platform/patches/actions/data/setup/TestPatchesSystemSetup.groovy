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
package de.hybris.platform.patches.actions.data.setup

import de.hybris.platform.core.initialization.SystemSetup
import de.hybris.platform.core.initialization.SystemSetupContext
import de.hybris.platform.patches.AbstractPatchesSystemSetup
import de.hybris.platform.util.SystemSetupUtils

/**
 * Test system setup for executing mocked patches
 */
public class TestPatchesSystemSetup extends AbstractPatchesSystemSetup {
    public void createProjectData() {
        SystemSetupUtils.setInitMethodInHttpSession("INIT")
        super.createProjectData(new SystemSetupContext(null, SystemSetup.Type.PROJECT, SystemSetup.Process.INIT, null))
    }
}
