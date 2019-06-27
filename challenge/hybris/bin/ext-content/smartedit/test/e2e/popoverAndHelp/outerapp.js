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
angular.module('outerapp', ['smarteditServicesModule', 'yHelpModule', 'e2eBackendMocks', 'templateCacheDecoratorModule'])
    .run(function($templateCache) {

        $templateCache.put('web/body.html',
            "<b>some HTML body</b>"
        );
    })
    .controller("defaultController", function() {
        this.templateUrl = 'web/body.html';
        this.template = '<div>some inline template</div>';
        this.title = 'someTitle.key';
        this.placement1 = 'top';
        this.placement2 = 'right';
        this.trigger1 = 'hover';
        this.trigger2 = 'click';
    });
