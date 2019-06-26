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
package de.hybris.platform.patches.actions.data

import de.hybris.platform.servicelayer.impex.ImportConfig
import de.hybris.platform.servicelayer.impex.ImportResult
import de.hybris.platform.servicelayer.impex.impl.DefaultImportService

/**
 * Sample impex import service which throws major exception during importing impex data, created for {@link ImportPatchActionErrorHandlingTest} purposes.
 */
class SampleImportServiceMajorExceptionForTests extends DefaultImportService {

    ImportResult importData(final ImportConfig config) {
        throw new UnsupportedOperationException("major exception")
    }
}
