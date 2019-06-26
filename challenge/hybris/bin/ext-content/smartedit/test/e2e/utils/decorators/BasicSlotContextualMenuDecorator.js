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
/**
 * @ngdoc overview
 * @name basicContextualMenuDecoratorModule
 * @description
 * Provides decorator for basic slot contextual menu.
 */
angular.module('basicContextualMenuDecoratorModule', ['decoratorServiceModule'])
    .run(function(decoratorService) {
        decoratorService.addMappings({
            '^.*Slot$': ['se.basicSlotContextualMenu']
        });
        decoratorService.enable('se.basicSlotContextualMenu');
    });
