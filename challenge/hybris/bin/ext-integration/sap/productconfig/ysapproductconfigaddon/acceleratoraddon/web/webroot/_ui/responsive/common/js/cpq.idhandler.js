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
CPQ.idhandler = {
    getCsticIdFromCsticFieldId: function(csticFieldId) {
        // Id has format "conflict.1-WCEM_MULTILEVEL._GEN.EXP_NO_USERS.conflicts",
        // remove suffix and prefix "conflict"
        var csticId = csticFieldId.replace(/conflict.([0-9]*\.)?(.*).conflicts/g, "$2");
        return csticId;
    },

    getCsticIdFromViolatedCsticFieldId: function(csticFieldId) {
        // Id has format "1-WCEM_MULTILEVEL._GEN.EXP_NO_USERS.conflicts",
        // remove suffix 'conflict'
        var csticId = csticFieldId.substring(0, csticFieldId.length - 10);
        return csticId;
    },

    getGroupIdFromMenuNodeId: function(menuNodeId) {
        // Id has format menuNode_GROUPID,remove prefix
        // 'menuNode_' or "menuLeaf_"
        var groupTitleId = menuNodeId.substring(9, menuNodeId.length);
        return groupTitleId;
    },

    getCsticIdFromConflictCstic: function(csticId) {
        // Id has format conflict.1-WCEM_MULTILEVEL._GEN.EXP_NO_USERS
        // remove prefix "conflict."
        var cstic = csticId;
        if(csticId.indexOf("conflict.") === 0) {
            cstic = csticId.substring(9, csticId.length);
            cstic = cstic.replace(/([0-9]*\.)?(.*)/g, "$2");
        }
        return cstic;
    },

    getCsticIdFromLableId: function(csticFieldId) {
        // Id has format "1-WCEM_MULTILEVEL._GEN.EXP_NO_USERS.label",
        // remove suffix 'label'
        var csticId = csticFieldId.substring(0, csticFieldId.length - 6);
        return csticId;
    },

    getGroupIdFromGroupTitleId: function(groupTitleId) {
        // Id has format GROUPID_title, remove suffix
        // '_title'
        var groupId = groupTitleId.substring(0, groupTitleId.length - 6);
        return groupId;
    },

    getCsticIdFromFieldGeneric: function(fieldId){
        // id has format "1-WCEM_MULTILEVEL._GEN.EXP_NO_USERS.suffix"
        // removes '.suffix'
        var csticId = fieldId.substr(0, fieldId.lastIndexOf("."));
        return csticId;
    },

    getCsticIdFromMessageId: function (messageId){
        // Id has format ""1-CPQ_LAPTOP.1.CPQ_CPU.extendedMessage" for cstic,
        // or ""1-CPQ_LAPTOP.1.CPQ_CPU.INTELI7_40.extendedMessage" for csticvalue.
        // remove suffix 'extendedMessage'

        var csticId = messageId.substring(0, messageId.length - 16);
        return csticId;
    },
    
    getIdFromDDLB: function (optionId){
        // Id has format "1-CPQ_LAPTOP.1.CPQ_CPU.option"  for csticvalue.
        // remove suffix '.option'
        // Id has format "1-CPQ_LAPTOP.2.CPQ_MONITOR.ddlb"  for csticvalue.
        // remove suffix '.ddlb'

    	 var csticId = optionId.substr(0, optionId.lastIndexOf("."));
         return csticId;
    },

    getIdFromLinkMoreDetails: function(optionId) {
        // Id has format ""1-CPQ_LAPTOP.CORE.CPQ_RAM.32GB.0.linkMoreDetails" for link more details.
        // remove suffix 'linkMoreDetails'
        //remove suffix '0' index
        var valueId = optionId.substr(0, optionId.lastIndexOf("."));
        valueId = valueId.substr(0, valueId.lastIndexOf("."));
        return valueId;
    }

};
