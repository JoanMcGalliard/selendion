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

function toggleSeleniumTable(seleniumTableNumber, seleniumTableName) {
  var seleniumTable = getElementById("seleniumTable" + seleniumTableNumber);
  var seleniumTableButton = getElementById("seleniumTableButton" + seleniumTableNumber);
  if (isVisible(seleniumTable)) {
    makeInvisible(seleniumTable);
    seleniumTableButton.value = seleniumTableName.replace(/\|/g,"\n");
  } else {
    makeVisible(seleniumTable);
    seleniumTableButton.value = "hide";
  }
}