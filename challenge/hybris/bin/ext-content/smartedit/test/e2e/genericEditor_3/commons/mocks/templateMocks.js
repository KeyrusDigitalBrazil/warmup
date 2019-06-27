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
angular
    .module('templateMocks', ['templateCacheDecoratorModule'])
    .run(function($templateCache) {
        //use a copy of select2
        $templateCache.put("pagedSelect2/match-multiple.tpl.html", $templateCache.get("select2/match-multiple.tpl.html"));
        $templateCache.put("pagedSelect2/match.tpl.html", $templateCache.get("select2/match.tpl.html"));
        $templateCache.put("pagedSelect2/no-choice.tpl.html", $templateCache.get("select2/no-choice.tpl.html"));
        $templateCache.put("pagedSelect2/select-multiple.tpl.html", $templateCache.get("select2/select-multiple.tpl.html"));
        $templateCache.put("pagedSelect2/select.tpl.html", $templateCache.get("select2/select.tpl.html"));

        //our own flavor of select2 for paging that makes use of yInfiniteScrolling component
        $templateCache.put("pagedSelect2/choices.tpl.html", $templateCache.get("uiSelectPagedChoicesTemplate.html"));

    });

angular.module('genericEditorApp').requires.push('templateMocks');
