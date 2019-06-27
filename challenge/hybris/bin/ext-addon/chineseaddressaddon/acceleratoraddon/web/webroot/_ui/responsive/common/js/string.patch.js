// string.patch.js
// Author: Seven Zhang
/*
 * **************************************************************
 * plus functions for String type
 * **************************************************************
 */
String.prototype.trim = function() {
    return this.replace(/(^\s*)|(\s*$)/g, '');
};
String.prototype.trimAll = function() {
    var whitespace = /[\s\n\r]+/g;
    return this.replace(whitespace, "");
};
String.prototype.isEmpty = function() {
    return (this.length === 0 || !this.trim());
};
String.prototype.replaceAll = function(reallyDo, replaceWith, ignoreCase) {
    if (!RegExp.prototype.isPrototypeOf(reallyDo)) {
        return this.replace(new RegExp(reallyDo, (ignoreCase ? "gi": "g")), replaceWith);
    } else {
        return this.replace(reallyDo, replaceWith);
    }
};
String.prototype.startsWith = function(substring) {
    var reg = new RegExp("^" + substring);
    return reg.test(this);
};
String.prototype.endsWith = function(substring) {
    var reg = new RegExp(substring + "$");
    return reg.test(this);
};
String.prototype.contains = function(pattern) {
    return this.indexOf(pattern) >= 0;
};
String.prototype.containsIgnoreCase = function(pattern) {
    return this.toLowerCase().indexOf(pattern.toLowerCase()) >= 0;
};
String.prototype.substringBefore = function(char) {
    var index = this.indexOf(char);
    if (index < 0) {
        return this;
    } else {
        return this.substring(0, index);
    }
};
String.prototype.substringBeforeLast = function(char) {
    var index = this.lastIndexOf(char);
    if (index < 0) {
        return this;
    } else {
        return this.substring(0, index);
    }
};
String.prototype.substringAfter = function(char) {
    var index = this.indexOf(char);
    return this.substring(index, this.length);
};
String.prototype.substringAfterLast = function(char) {
    var index = this.lastIndexOf(char);
    return this.substring(index + 1, this.length);
};
String.prototype.substringBetween = function(beginChar, endChar) {
    var begin = this.indexOf(beginChar);
    var end = this.indexOf(endChar);
    return this.substring(begin + 1, end);
};
String.prototype.substringBetweenLast = function(beginChar, endChar) {
    var begin = this.lastIndexOf(beginChar);
    var end = this.lastIndexOf(endChar);
    return this.substring(begin + 1, end);
};