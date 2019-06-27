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
 * @name yMessageModule
 * @description
 * This module provides the yMessage component, which is responsible for rendering contextual
 * feedback messages for the user actions.
 */
angular.module('yMessageModule', [])
    .controller('YMessageController', function() {
        this.$onInit = function() {
            this.messageId = this.messageId || 'y-message-default-id';
            switch (this.type) {
                case 'danger':
                    this.classes = 'y-message-danger';
                    this.icon = 'hyicon-msgdanger';
                    break;
                case 'info':
                    this.classes = 'y-message-info';
                    this.icon = 'hyicon-msginfo';
                    break;
                case 'success':
                    this.classes = 'y-message-success';
                    this.icon = 'hyicon-msgsuccess';
                    break;
                case 'warning':
                    this.classes = 'y-message-warning';
                    this.icon = 'hyicon-msgwarning';
                    break;
                default:
                    this.classes = 'y-message-info';
                    this.icon = 'hyicon-msginfo';
            }

            this.icon = 'hyicon ' + this.icon;
        };
    })
    /**
     *  @ngdoc directive
     *  @name yMessageModule.component:yMessage
     *  @scope
     *  @restrict E
     *  @element yMessage
     *
     *  @description
     *  This component provides contextual feedback messages for the user actions. To provide title and description for the yMessage
     *  use transcluded elements: message-title and message-description.
     *  @param {@String=} messageId Id for the component.
     *  @param {@String} type The type of the component (danger, info, success, warning). Default: info
     */
    .component('yMessage', {
        templateUrl: 'yMessage.html',
        controller: 'YMessageController',
        transclude: {
            messageTitle: '?messageTitle',
            messageDescription: '?messageDescription'
        },
        bindings: {
            messageId: '@?',
            type: '@'
        }
    });
