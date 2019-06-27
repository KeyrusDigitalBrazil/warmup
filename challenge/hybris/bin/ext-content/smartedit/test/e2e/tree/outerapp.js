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
angular.module('outerapp', ['ui.bootstrap', 'ngRoute', 'treeModule', 'confirmationModalServiceModule'])
    .config(function($routeProvider) {
        $routeProvider.when('/tree', {
            templateUrl: 'web/treeView.html'
        });
    })
    .run(
        ['$templateCache', '$animate', function($templateCache, $animate) {
            $animate.enabled(false);

            $templateCache.put('web/treeView.html',
                "<div class='treeDemo'>\n" +
                "<label id='someTreeEditorMessage'>{{someTreeEditorMessage}}</label>" +
                "<some-tree-editor />" +
                "</div>"
            );

            $templateCache.put('someTreeNodeRenderTemplate.html',
                "<a class=\"pull-right btn btn-success btn-xs\" data-ng-click=\"$ctrl.log(this)\" style=\"margin-right: 8px;\">\n" +
                "    <span class=\"glyphicon glyphicon-th\"></span>\n" +
                "</a>\n" +
                "<a class=\"pull-right btn btn-danger btn-xs\" data-ng-click=\"$ctrl.remove(this)\">\n" +
                "    <span class=\"glyphicon glyphicon-remove\"></span>\n" +
                "</a>\n" +
                "<a class=\"pull-right btn btn-primary btn-xs\" data-ng-click=\"$ctrl.newChild(this)\" style=\"margin-right: 8px;\">\n" +
                "    <span class=\"glyphicon glyphicon-plus\">child</span>\n" +
                "</a>\n" +
                "<a class=\"pull-right btn btn-success btn-xs\" data-nodrag data-ng-click=\"$ctrl.newSibling(this)\" style=\"margin-right: 8px;\">\n" +
                "    <span class=\"glyphicon glyphicon-plus\">sibling</span>\n" +
                "</a>"
            );

        }]
    )
    .controller("someTreeEditorController", function($rootScope, confirmationModalService) {

        this.nodeTemplateUrl = 'someTreeNodeRenderTemplate.html';
        this.nodeURI = 'someNodeURI';
        this.rootNodeUid = 'root';

        this.actions = {

            log: function(treeService, handle) {
                var nodeData = handle.$modelValue;
                $rootScope.someTreeEditorMessage = "edit " + nodeData.name;
            },
            addSiblingToFirstChild: function() {
                this.newChild(this.root.nodes[0]);
            }

        };

        this.dragOptions = {
            onDropCallback: function() {
                confirmationModalService.confirm({
                    description: 'dropped node'
                });
            }.bind(this),
            allowDropCallback: function(event) {
                return event.sourceNode.parent.uid === event.destinationNodes[0].parent.uid;
            },
            beforeDropCallback: function(event) {
                if (event.position === 0) {
                    return true;
                }
                if (event.position === 1) {
                    return {
                        confirmDropI18nKey: 'se.tree.confirm.message'
                    };
                }
                return {
                    rejectDropI18nKey: 'se.tree.reject.message'
                };
            }

        };
    })
    .directive('someTreeEditor', function() {

        return {
            restrict: 'E',
            transclude: false,
            replace: false,
            template: "<button id='outsideActionButton' data-ng-click='ctrl.actions.addSiblingToFirstChild()'>add sibling to first child</button>" +
                "<ytree data-node-uri='ctrl.nodeURI'  data-root-node-uid='ctrl.rootNodeUid' data-node-template-url='ctrl.nodeTemplateUrl' data-node-actions='ctrl.actions' data-drag-options='ctrl.dragOptions'/>",
            controller: 'someTreeEditorController',
            controllerAs: 'ctrl',
            scope: {},
            bindToController: {}
        };
    });
