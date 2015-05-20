# Introduction #

Selendion has all Concordion commands plus it's own commands


# Details #

## Concordion Commands ##
See more detail on the [Concordion Site](http://concordion.org/Tutorial.html)
### run ###
This is a new command to Concordion 1.4 (not yet released).  For the present, please use **runSelendion**.
### execute ###
Execute a java method.
**Examples**
```
<a concordion:execute="#baseUrl=#HREF" href="http://localhost:55555">
<ul concordion:execute="#men =getList()">
<li concordion:execute="#men.add(#TEXT)">Bob</li>
<i class="attention" concordion:execute="setExpectedToPass(false)">
```
### set ###
### assertEquals ###
### assertTrue ###
### assertFalse ###
### verifyRows ###
### echo ###



## Selendion Commands ##

### startBrowser ###
### runSelenium ###
### stopBrowser ###
### addToSuite ###
### runSuite ###
### clearSuite ###
### forEach ###
### runSelendion ###