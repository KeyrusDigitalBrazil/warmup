"use strict";
exports.__esModule = true;
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
var fs_1 = require("fs");
var ts = require("typescript");
var lodash = require("lodash");
/**
 * @SeInjectabe TypeScript instrumenter
 * This module visit the TypeScript source code to search for DI-specific class decorators like @SeInjectable() and @SeComponent;
 * It will instrument the class to as to be usable by Smartedit DI and, more precisely, by underlying
 * AngularJS 1.6 DI
 *
 */
module.exports = function (fileNames, injectableDecorators) {
    var DELEGATING_HINT = "/* @ngInject */";
    var program = ts.createProgram(fileNames, {
        target: ts.ScriptTarget.ES5, module: ts.ModuleKind.CommonJS
    });
    var checker = program.getTypeChecker(); // do not remove, it's necessary to have type check
    for (var _i = 0, _a = program.getSourceFiles(); _i < _a.length; _i++) {
        var sourceFile = _a[_i];
        ts.forEachChild(sourceFile, walkNode);
    }
    function walkNode(node) {
        if (!isNodeExported(node)) {
            return;
        }
        if (node.kind === ts.SyntaxKind.ClassDeclaration && ts.isClassDeclaration(node)) {
            var classDeclaration = node;
            if (classDeclaration.decorators && classDeclaration.decorators.length) {
                var classDecorators = classDeclaration.decorators.map(serializeDecorator);
                if (lodash.intersection(classDecorators, injectableDecorators).length) {
                    var fileName = node.getSourceFile().fileName;
                    var className = node.name.getFullText().trim();
                    fs_1.writeFileSync(fileName, getTransformedSource(fileName, className));
                }
            }
        }
        else if (node.kind === ts.SyntaxKind.ModuleDeclaration) {
            // This is a namespace, visit its children
            ts.forEachChild(node, walkNode);
        }
    }
    function serializeDecorator(decorator) {
        return checker.getSymbolAtLocation(decorator.expression.getFirstToken()).getName();
    }
    function getTransformedSource(fileName, className) {
        var fullText = fs_1.readFileSync(fileName).toString();
        return fullText.replace(new RegExp("(export class " + className + ")", 'g'), DELEGATING_HINT + "\n $1");
    }
    function isNodeExported(node) {
        return (ts.getCombinedModifierFlags(node) & ts.ModifierFlags.Export) !== 0 || (!!node.parent && node.parent.kind === ts.SyntaxKind.SourceFile);
    }
};
