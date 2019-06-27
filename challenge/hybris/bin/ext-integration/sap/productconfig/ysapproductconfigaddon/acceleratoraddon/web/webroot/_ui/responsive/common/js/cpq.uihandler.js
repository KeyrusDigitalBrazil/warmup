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
CPQ.uihandler = {
    toggleExpandCollapseStyle: function (element, elementToBlind, prefix, noBlind) {
        element.toggleClass(prefix + "-open");
        element.toggleClass(prefix + "-close");
        if (noBlind) {
            elementToBlind.toggle();
        } else {
            elementToBlind.slideToggle(100);
        }
    },

    clickGroupHeader: function(postFx, e){
        // do nothing on tab click: e.which == 9
        if(e.which !== 9){
            var groupElement = $(e.currentTarget);
            var groupTitleId = groupElement.attr("id");
            var groupId = CPQ.idhandler.getGroupIdFromGroupTitleId(groupTitleId);
            CPQ.uihandler.toggleExpandCollapseStyle(groupElement, groupElement.next(), "cpq-group-title");
            $("#cpqAction").val("TOGGLE_GROUP");
            $("#autoExpand").val(false);
            $("#groupIdToToggle").val(groupId);
            var data = $(CPQ.core.formNameId).serialize();
            CPQ.core.firePost(postFx, [ e, data, groupTitleId ]);
        }
    },

    updateSlotContent: function(response, slotName, excludeId) {
        var newSlotContent = CPQ.uihandler.getNewSlotContent(response, slotName);
        $("#" + slotName).replaceWith(newSlotContent);
    },

    getNewSlotContent: function(response, slotName) {
        var startTag = '<div id="start:' + slotName + '" />';
        var endTag = '<div id="end:' + slotName + '" />';
        var newContent = "";

        var startIndex = response.indexOf(startTag);
        if (startIndex !== -1) {
            startIndex = startIndex + startTag.length;
            var endIndex = response.indexOf(endTag);
            if (endIndex !== -1) {
                newContent = response.substring(startIndex, endIndex);
            }
        }
        return newContent;
    },

    storeState: function(selector, callback) {
        var storedState = {};
        storedState.items = [];
        $(selector).each(function(){
            var item = {};
            if(callback){
                callback($(this), item);
            }else{
                item.id = $(this).attr('id');
                item.html = $(this).html();
            }
            storedState.items.push(item);
        });

        return storedState;
    },

    restoreState: function(storedState, callback){
        $.each(storedState.items,function(index, item){
            if(callback){
                callback(item);
            }else{
                var div = $(CPQ.core.encodeId(item.id));
                if(div.length > 0){
                    div.html(item.html);
                }
            }
        });
    }
};