/**
 * See blockumd.js
 */
if (window.seBlockUmd) {
    if (!window.module) {
        window.module = window.seBlockUmd.module;
    }
    if (!window.define) {
        window.define = window.seBlockUmd.define;
    }
}
window.seBlockUmd = undefined;
delete window.seBlockUmd;