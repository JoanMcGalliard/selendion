/* Selenium Table Toggling */

function getElementById(id) {
  if (document.getElementById) { // standard
    return document.getElementById(id);
  } else if (document.all) { // old IE versions
    return document.all[id];
  } else if (document.layers) { // nn4
    return document.layers[id];
  }
  alert("Sorry, but your web browser is not supported by Concordion.");
    return null;
}

function isVisible(element) {
  return element.style.display;
}

function makeVisible(element) {
  element.style.display = "block";
}

function makeInvisible(element) {
  element.style.display = "";
}

function toggleSelendionElement(selendionHideViewElementNumber, selendionHideViewButtonTitle) {
  var selendionHideViewElement = getElementById("selendionHideViewElement" + selendionHideViewElementNumber);
  var selendionHideViewButton = getElementById("selendionHideViewButton" + selendionHideViewElementNumber);
  if (isVisible(selendionHideViewElement)) {
    makeInvisible(selendionHideViewElement);
    selendionHideViewButton.value = selendionHideViewButtonTitle.replace(/\|/g,"\n");
  } else {
    makeVisible(selendionHideViewElement);
    selendionHideViewButton.value = "hide";
  }
}
function toggleStackTrace(stackTraceNumber) {
  var stackTrace = getElementById("stackTrace" + stackTraceNumber);
  var stackTraceButton = getElementById("stackTraceButton" + stackTraceNumber);
  if (isVisible(stackTrace)) {
    makeInvisible(stackTrace);
    stackTraceButton.value = "View Stack";
  } else {
    makeVisible(stackTrace);
    stackTraceButton.value = "Hide Stack";
  }
}

