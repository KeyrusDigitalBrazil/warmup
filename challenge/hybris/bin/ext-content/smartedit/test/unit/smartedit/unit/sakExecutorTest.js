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
describe('sakExecutor service', function() {

    var $q, $rootScope;
    var sakExecutor, decorators, compiler, compiled, browserService;

    var smarteditComponentType;
    var smarteditComponentId;
    var decoratorService;
    var componentHandlerService;

    var element1 = {
        id: 'testElement1'
    };
    var element2 = {
        id: 'testElement2'
    };
    var scope1 = {
        $id: 'scopeId1',
        $destroy: angular.noop
    };
    var scope2 = {
        $id: 'scopeId2',
        $destroy: angular.noop
    };

    beforeEach(angular.mock.module("smarteditServicesModule", function($provide) {
        smarteditComponentType = "smarteditComponentType";
        smarteditComponentId = "smarteditComponentId";

        decoratorService = jasmine.createSpyObj('decoratorService', ['getDecoratorsForComponent']);
        decorators = ['decorator1', 'decorator2'];
        $provide.value('decoratorService', decoratorService);

        componentHandlerService = jasmine.createSpyObj('componentHandlerService', ['getFromSelector']);
        $provide.value('componentHandlerService', componentHandlerService);

        compiler = jasmine.createSpyObj('compiler', ['compile']);
        compiled = angular.element("<div>compiled</div>").html();
        compiler.compile.and.returnValue(compiled);
        $provide.value('$compile', function(template, transcludeFn) {
            return compiler.compile(template, transcludeFn);
        });

        browserService = jasmine.createSpyObj('browserService', ['isIE']);
        browserService.isIE.and.returnValue(true);
        $provide.value('browserService', browserService);

    }));

    beforeEach(inject(function(_sakExecutorService_, _$q_, _$rootScope_) {
        sakExecutor = _sakExecutorService_;
        $q = _$q_;
        $rootScope = _$rootScope_;
        decoratorService.getDecoratorsForComponent.and.returnValue($q.when(decorators));
    }));

    it('sakExecutor.wrapDecorators fetches eligible decorators for given component type and compiles a stack of those decorators around the clone + the sakDecorator', function() {

        //GIVEN
        var transcludeFn = function() {};

        //WHEN
        spyOn(sakExecutor, 'wrapDecorators').and.callThrough();

        //THEN
        expect(sakExecutor.wrapDecorators(transcludeFn, smarteditComponentId, smarteditComponentType, {})).toBeResolvedWithData(compiled);

        $rootScope.$digest();

        expect(compiler.compile).toHaveBeenCalledWith(
            "<div><div data-ng-if='componentDecoratorEnabled' " +
            "class='decorator2 se-decorator-wrap' data-active='active' " +
            "data-smartedit-component-id='{{$ctrl.smarteditComponentId}}' " +
            "data-smartedit-component-type='{{$ctrl.smarteditComponentType}}' " +
            "data-smartedit-container-id='{{$ctrl.smarteditContainerId}}' " +
            "data-smartedit-container-type='{{$ctrl.smarteditContainerType}}' " +
            "data-component-attributes='componentAttributes'>" +
            "<div data-ng-if='componentDecoratorEnabled' " +
            "class='decorator1 se-decorator-wrap' data-active='active' " +
            "data-smartedit-component-id='{{$ctrl.smarteditComponentId}}' " +
            "data-smartedit-component-type='{{$ctrl.smarteditComponentType}}' " +
            "data-smartedit-container-id='{{$ctrl.smarteditContainerId}}' " +
            "data-smartedit-container-type='{{$ctrl.smarteditContainerType}}' " +
            "data-component-attributes='componentAttributes'>" +
            "<div data-ng-if='componentDecoratorEnabled' data-ng-transclude></div>" +
            "</div>" +
            "</div>" +
            "<div data-ng-if='!componentDecoratorEnabled' data-ng-transclude></div>" +
            "</div>", transcludeFn);

    });

    it('should be able to register multiple entries in scopes', function() {
        sakExecutor.registerScope(scope1, element1);
        sakExecutor.registerScope(scope2, element2);

        expect(sakExecutor.getScopes()).toEqual([{
            scope: scope1,
            element: element1
        }, {
            scope: scope2,
            element: element2
        }]);
    });

    it('should be able to destroy a scope', function() {
        componentHandlerService.getFromSelector.and.callFake(function() {
            return {
                attr: function() {
                    return 'someUUID';
                }
            };
        });

        sakExecutor.registerScope(scope1, element1);
        sakExecutor.registerScope(scope2, element2);

        var destroySpy = spyOn(scope1, '$destroy');

        sakExecutor.destroyScope(element1);

        expect(sakExecutor.getScopes()).toEqual([{
            element: element2,
            scope: scope2
        }]);
        expect(destroySpy).toHaveBeenCalled();
    });
});
