# Introduction #

This code is an extension [Concordion](http://code.google.com/p/concordion/) but with changes: some required to allow the
subclass to do what it needs to do; some bug fixes; and some enhancements that belonged more easily or logically in the
original class.  The included concordion jar file is the [(r259)](http://concordion.googlecode.com/svn/tags/concordion-1.3.1/)
version of concordion with concordion.patch applied.  If you already have a customized version of concordion, then you
need to apply this patch to it.

**Note** Selendion is a superset of concordion.  Any concordion tests should run in selendion.

# Details #

The most important changes are access privileges changes to ConcordionBuilder, to allow the sub class to access methods and members:

```

+    protected SpecificationLocator specificationLocator = new ClassNameBasedSpecificationLocator();
+    protected Source source = new ClassPathSource();
+    protected Target target = null;
+    protected CommandRegistry commandRegistry = new CommandRegistry();
+    protected DocumentParser documentParser = new DocumentParser(commandRegistry);
+    protected EvaluatorFactory evaluatorFactory = new SimpleEvaluatorFactory();
+    protected AssertEqualsCommand assertEqualsCommand = new AssertEqualsCommand();
+    protected AssertTrueCommand assertTrueCommand = new AssertTrueCommand();
+    protected AssertFalseCommand assertFalseCommand = new AssertFalseCommand();
+    protected ExecuteCommand executeCommand = new ExecuteCommand();
+    protected RunCommand runCommand = new RunCommand();
+    protected VerifyRowsCommand verifyRowsCommand = new VerifyRowsCommand();
+    protected EchoCommand echoCommand = new EchoCommand();

+    protected ConcordionBuilder withApprovedCommand(String namespaceURI, String commandName, Command command) {
+    protected File getBaseOutputDir() {

```


Selendion creates tables within tables.  The method in org.concordion.internal.Table, _getRows_, gets all the rows in
both the main table and the sub tables, so this is modified to explicitly only get the rows in the table or in thead
and tbody if present.


```
+        for (Element subElement : tableElement.getChildElements()) {
+            if (subElement.getLocalName().equals("tr")) {
+                rows.add(new Row(subElement));
+            } else if (subElement.getLocalName().equals("tbody") || subElement.getLocalName().equals("thead")) {
+                for (Element subSubElement : subElement.getChildElements()) {
+                    if (subSubElement.getLocalName().equals("tr")) {
+                        rows.add(new Row(subSubElement));
-        }
+                    }
+                }
+            }
+        }
```


The new class TableTest tests the above functionality.

```
+public class TableTest extends TestCase {
+    public void testGetLastHeaderRow() {
+        Element tableElement = new Element("table");
+        Element header = new Element("tr");
+        header.appendChild(new Element("th").appendText("Col head1"));
+        header.appendChild(new Element("th").appendText("Col head2"));
+        tableElement.appendChild(header);
+        Element row = new Element("tr");
+        row.appendChild(new Element("td").appendText("Col 1"));
+        row.appendChild(new Element("td").appendText("Col 2"));
+        tableElement.appendChild(row);
+        Table table = new Table(tableElement);
+
+        // Get header of plain table
+        assertEquals(table.getLastHeaderRow().getElement(), header);
+
+
+
+        row = new Element("tr");
+        row.appendChild(new Element("td").appendText("Col 1"));
+        row.appendChild(subTable());
+        tableElement.appendChild(row);
+
+        // Get header of table with sub table
+        assertEquals(table.getLastHeaderRow().getElement(), header);
+
+        tableElement = new Element("table");
+        Element thead = new Element("thead");
+        Element tbody = new Element("tbody");
+        header = new Element("tr");
+        header.appendChild(new Element("th").appendText("Col head1"));
+        header.appendChild(new Element("th").appendText("Col head2"));
+        thead.appendChild(header);
+        tableElement.appendChild(thead);
+        row = new Element("tr");
+        row.appendChild(new Element("td").appendText("Col 1"));
+        row.appendChild(new Element("td").appendText("Col 2"));
+        tbody.appendChild(row);
+        tableElement.appendChild(tbody);
+        table = new Table(tableElement);
+
+        // Get header of table with tbody/thead
+        assertEquals(table.getLastHeaderRow().getElement(), header);
+
+
+        row = new Element("tr");
+        row.appendChild(new Element("td").appendText("Col 1"));
+        row.appendChild(subTable());
+        tbody.appendChild(row);
+
+        // Get header of table with tbody/thead and sub table
+        assertEquals(table.getLastHeaderRow().getElement(), header);
+    }
+
+    private Element subTable() {
+         Element tableElement = new Element("table");
+        Element subTableHeader = new Element("tr");
+        subTableHeader.appendChild(new Element("th").appendText("Sub Table Header"));
+        tableElement.appendChild(subTableHeader);
+        Element row = new Element("tr");
+        row.appendChild(new Element("td").appendText("Col 1"));
+        tableElement.appendChild(row);
+        return tableElement;
+    }
+
+}
```

Because Selendion passes variable back and forth between selenium and concordion, we need to know what variables are
defined in concordion so we can can set them in selenium.  To do this we need to add a getKeys to the Evaluator:
```
+    Set getKeys();
```

and to the OgnlEvaluator:
```
+    public Set getKeys() {
+        return ognlContext.keySet();
+    }
+
```
and change StubEvaluator to allow it to compile:
```
+    public Set getKeys() {
+        return null;
+    }
```

In concordion, if a link throws an exception, the view stack button is nearly useless, as it briefly shows the stack,
then follows the link.  Most Selendion calls are wrapped around links, so this made debugging a real problem.  The fix
was to insert the stack trace after the element that had the problem, instead of inside it.  So first add a method to
Element that inserts straight after the current element:
```
+    public void insertAfter(Element insert) {
+        ParentNode parent = xomElement.getParent();
+        for (int i=0; i < parent.getChildCount() ; i++) {
+            if (parent.getChild(i).equals(xomElement)) {
+                parent.insertChild(insert.xomElement, i+1) ;
+                break;
+            }
+        }
+    }
+
```

Then change ThrowableRenderer to use this new method:
```
+        Element span = new Element("span");
+        span.appendChild(stackTraceTogglingButton());
+        span.appendChild(stackTrace(event.getThrowable(), event.getExpression()));
+        element.insertAfter(span);
```
And modify the test for the new output:
```
+&lt;/p&gt; _
+&lt;span&gt; _
 [..]
-&lt;/p&gt;
+&lt;/span&gt;
```
Because we now need to test not only the element but the one after it, we need to modify ExceptionTest
```

+        Document document =  new TestRig()
             .processFragment(fragment)
+            .getXOMDocument();
+
+        Element element = new Element((nu.xom.Element) document
             .query("//p")
             .get(0));

         new ThrowableRenderer().throwableCaught(new ThrowableCaughtEvent(t, element, expression));

+        Nodes nodes = new TestRig().unwrapFragment(document);
+        String ret = "";
+        for (int i = 0; i< nodes.size(); i++) {
+            ret = ret + nodes.get(i).toXML();
+        }
+        return ret;
+    }
+}
```
And add the new method to TestRig
```
+    public Nodes unwrapFragment(Document document) {
+        return document.query("/html/body/fragment/*");
+    }
```

Allow literals to be passed as variables, as that can be useful, so we make these changes to SimpleEvaluator
```
+    private static String RHS_VARIABLE_PATTERN = "(" + LHS_VARIABLE_PATTERN + "|#TEXT|#HREF|'[^']*'|-?[0-9]+\\.?[0-9]*|\\-?.[0-9]*|true|false)";
+        regexs.add(LHS_VARIABLE_PATTERN + " *= *" + LHS_VARIABLE_PATTERN + "(\\." + PROPERTY_NAME_PATTERN +  ")+");
+        regexs.add(LHS_VARIABLE_PATTERN + " *= *" + RHS_VARIABLE_PATTERN);
+        regexs.add(LHS_VARIABLE_PATTERN + " *= *" + RHS_VARIABLE_PATTERN);
```

And test it in ExpressionTest:
```

+        , "'string value'"
+        , "100"
+        , "0"
+        , "10.00"
+        , "-.6"
+        , "-0.6"
+        , "true"
+        , "false"
+        , "myMethod(false)"
+        , "myMethod(10)"
+        , "myMethod('strng value')" };
[..]
+        assertValidSetVariableExpression("#var='bob'");
```
Also test it in Execute.html
```
+    <div class="example">
+        <h3>Example</h3>
+
+        <p>Variables should be passed in:</p>
+<pre class="html" concordion:execute="process(#TEXT)">
+
+&lt;p concordion:set="#var"&gt;myVariable&lt;/p&gt;
+&lt;p concordion:execute="myParameterMethod(#var)"&gt;Some text goes here.&lt;/p&gt;
+</pre>
+        <p>
+            Will call <code>myParameterMethod()</code> in the Java fixture code with parameter
+            <span concordion:assertEquals="getParameter()">myVariable</span>.
+        </p>
+
+    </div>
+    <div class="example">
+
+        <h3>Example</h3>
+
+        <p>Literals should be passed in:</p>
+<pre class="html" concordion:execute="process(#TEXT)">
+
+&lt;p concordion:execute="myParameterMethod('myOtherVariable')"&gt;Some text goes here.&lt;/p&gt;
+</pre>
+        <p>
+            Will call <code>myParameterMethod()</code> in the Java fixture code with parameter
+            <span concordion:assertEquals="getParameter()">myOtherVariable</span>.
+        </p>
+
+    </div>
```
and Set.html
```
+    <div class="example">
+        <p>This should also work with literals:</p>

+        <h3>Example</h3>

+        <p>The following instrumentation:</p>
+<pre class="html" concordion:set="#fragment">

+        &lt;i concordion:set="#value='bob'"&gt;explicitly set #value to 'bob'&lt;/i&gt;
+           should result in
+        &lt;b concordion:assertEquals="#value"&gt;bob&lt;/b&gt;.
+    </pre>
+        <p>
+            should <span concordion:assertEquals="process(#fragment) ? 'succeed' : 'fail'">succeed</span>
+        </p>
+    </div>
+
+<div class="example">
+    <p>Set should also work in a table:</p>
+    <table concordion:execute="doNothing(#val1)">
+        <tr>
+            <th concordion:set="#val1">Set val1</th>
+            <th concordion:assertEquals="#val1">So val1 should equal this</th>
+
+        </tr>
+        <tr>
+            <td>text1</td>
+            <td>text1</td>
+
+
+        </tr>
+    </table>
+
+</div>

```
Concordion just ignores unrecognized concordion tags, but they (similarly to unknown variables) would always represent
a typo, or using the wrong namespace.  This results in the test being ignored, which means it will pass.  This is fixed
by throwing an exception in DocumentParser if an unrecognised tag is seen:
```
+                } else {
+                    throw new RuntimeException("Unknown command " + commandName + " in namespace " + namespaceURI + ".");

```
HREFs can be passed in table columns, so added tests for this in AccessToLinkHref.html
```
+    <div class="example">
+        <h3>Example</h3>
+        <p>Similarly, we should be able to set the value of #HREF to another variable, eg </p>
+<pre class="html" concordion:set="#fragment">
+     &lt;a href="blah.html" concordion:set="#link=#HREF"&gt;this link&lt;/a&gt;
+        &lt;b concordion:assertEquals="#link"&gt;blah.html&lt;/b&gt;
+
+</pre>
+        <p>
+            #link should be set to the value of #HREF (blah.html), and so that the <code>assertEquals</code> should
+            <span concordion:assertEquals="fragmentSucceeds(#fragment) ? 'succeed' : 'fail'">succeed</span>.
+        </p>
+
+    </div>
+<div class="example">
+    <p>In a table, we should be able to set the HREF or TEXT or both to a variable:</p>
+    <table concordion:execute="myMethod(#val1)">
+        <tr>
+            <th concordion:set="#val1=#TEXT">Set val1 to #TEXT</th>
+            <th concordion:assertEquals="#val1">So val1 should equal this</th>
+            <th concordion:set="#val2=#HREF">Set val2 to #HREF</th>
+            <th concordion:assertEquals="#val2">So val2 should equal this</th>
+            <th concordion:set="#val3">Set val3</th>
+            <th concordion:assertEquals="#val3">So val3 should equal this</th>
+
+        </tr>
+        <tr>
+            <td><a href="href2.html">text2</a></td>
+            <td>text2</td>
+            <td><a href="href3.html">text3</a></td>
+            <td>href3.html</td>
+            <td><a href="href4.html">text4</a></td>
+            <td><a href="href4.html">text4</a></td>
+
+        </tr>
+    </table>
+
+</div>
```
Selendion has a facility similar to Concordion run command (implemented before that was fully available in Concordion.
The selendion version (runSelendion) can embed the secondary test in the main output.  To do this, we need to
be able to access the xml of the secondary file, so the following is added to Specification and XMLSpecification
```
+    public CommandCall getCommandCall() {
+        return rootCommandNode;
+    }
```
Finally added utility methods to Element, so that the selenium runner knows if it is being called in a table or not
(so it can add the selenium results as a _td_ if it is), and copy and getXomElement to support forEach command:
```

+    public Element getParent() {
+        return new Element((nu.xom.Element) xomElement.getParent());
+    }

+     public Element copy() {
+        return new Element((nu.xom.Element) xomElement.copy());
-}
+    }
+}

+    public nu.xom.Element getXomElement () {
+        return this.xomElement;
+    }


```