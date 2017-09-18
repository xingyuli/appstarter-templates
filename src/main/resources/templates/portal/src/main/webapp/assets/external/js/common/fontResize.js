/** 
* 移动端字体REM自适应脚本 
* By yikai.zhu 2017-03-23 
*/

(function (doc, win) {
  var docEl = doc.documentElement,
    resizeEvt = 'orientationchange' in window ? 'orientationchange' : 'resize',
    recalc = function () {
      var clientWidth = docEl.clientWidth;
      if (!clientWidth) return;
      //64是html中设置的font-size，640是psd中图像宽。如果PSD设计750px，那么这边就是75px。
      //CSS中原来的px单位除以64，换成rem，比如20px，就写.3rem
      docEl.style.fontSize = 24 * (clientWidth / 640) + 'px';
    };

  if (!doc.addEventListener) return;
  win.addEventListener(resizeEvt, recalc, false);
  doc.addEventListener('DOMContentLoaded', recalc, false);
})(document, window);