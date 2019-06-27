/**
 *
 * We have an issue where if someone bring in a UMD loader like requireJS, then some of our thirdparty libraries
 * don't load onto the global variable that we are expecting in smartedit.
 *
 * To solve it, we will temporarily hide window.define or module.exports, so the modules will load as we expect them.
 * We use this file as the first file in the concatenated prelibraries.js, and the last file will be the unblockumb.js
 * which reverses this operation so that anyone expecting to use it can continue to do so afterwards.
 *
 * Remove commonJS (module.exports) and AMD (define) from the window namespace but keep it to put it back in unblockumd.js
 *
 */
window.seBlockUmd = {
    module: window.module,
    define: window.define
}
window.module = undefined;
window.define = undefined;

