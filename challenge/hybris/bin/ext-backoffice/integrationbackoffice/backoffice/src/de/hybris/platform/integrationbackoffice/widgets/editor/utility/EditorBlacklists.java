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
package de.hybris.platform.integrationbackoffice.widgets.editor.utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Blacklists that will be used to exclude certain types from displaying in the UI tool.
 */
public final class EditorBlacklists {

    private static List<String> attributeBlackList;
    private static List<String> typesBlackList;

    private EditorBlacklists() {
        throw new IllegalStateException("Utility class");
    }

    public static List<String> getAttributeBlackList() {
        if (attributeBlackList == null) {
            attributeBlackList = new ArrayList<>();
            attributeBlackList.add("allDocuments");
            attributeBlackList.add("assignedCockpitItemTemplates");
            attributeBlackList.add("comments");
            attributeBlackList.add("creationtime");
            attributeBlackList.add("itemtype");
            attributeBlackList.add("modifiedtime");
            attributeBlackList.add("owner");
            attributeBlackList.add("pk");
            attributeBlackList.add("savedValues");
            attributeBlackList.add("sealed");
            attributeBlackList.add("synchronizationSources");
            attributeBlackList.add("synchronizedCopies");
        }
        return attributeBlackList;
    }

    public static List<String> getTypesBlackList() {
        if (typesBlackList == null) {
            typesBlackList = new ArrayList<>();
            typesBlackList.add("Item");
            typesBlackList.add("LogFile");
            typesBlackList.add("Trigger");
            typesBlackList.add("ItemSyncTimestamp");
            typesBlackList.add("ProcessTaskLog");
            typesBlackList.add("CronJob");
            typesBlackList.add("JobSearchRestriction");
            typesBlackList.add("Step");
        }
        return typesBlackList;
    }

}
