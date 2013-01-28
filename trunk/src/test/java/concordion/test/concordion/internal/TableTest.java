package concordion.test.concordion.internal;

import junit.framework.TestCase;
import org.concordion.internal.Table;
import org.concordion.api.Element;

public class TableTest extends TestCase {
    public void testGetLastHeaderRow() {
        Element tableElement = new Element("table");
        Element header = new Element("tr");
        header.appendChild(new Element("th").appendText("Col head1"));
        header.appendChild(new Element("th").appendText("Col head2"));
        tableElement.appendChild(header);
        Element row = new Element("tr");
        row.appendChild(new Element("td").appendText("Col 1"));
        row.appendChild(new Element("td").appendText("Col 2"));
        tableElement.appendChild(row);
        Table table = new Table(tableElement);

        // Get header of plain table
        assertEquals(table.getLastHeaderRow().getElement(), header);



        row = new Element("tr");
        row.appendChild(new Element("td").appendText("Col 1"));
        row.appendChild(subTable());
        tableElement.appendChild(row);

        // Get header of table with sub table
        assertEquals(table.getLastHeaderRow().getElement(), header);

        tableElement = new Element("table");
        Element thead = new Element("thead");
        Element tbody = new Element("tbody");
        header = new Element("tr");
        header.appendChild(new Element("th").appendText("Col head1"));
        header.appendChild(new Element("th").appendText("Col head2"));
        thead.appendChild(header);
        tableElement.appendChild(thead);
        row = new Element("tr");
        row.appendChild(new Element("td").appendText("Col 1"));
        row.appendChild(new Element("td").appendText("Col 2"));
        tbody.appendChild(row);
        tableElement.appendChild(tbody);
        table = new Table(tableElement);

        // Get header of table with tbody/thead
        assertEquals(table.getLastHeaderRow().getElement(), header);


        row = new Element("tr");
        row.appendChild(new Element("td").appendText("Col 1"));
        row.appendChild(subTable());
        tbody.appendChild(row);

        // Get header of table with tbody/thead and sub table
        assertEquals(table.getLastHeaderRow().getElement(), header);
    }

    private Element subTable() {
         Element tableElement = new Element("table");
        Element subTableHeader = new Element("tr");
        subTableHeader.appendChild(new Element("th").appendText("Sub Table Header"));
        tableElement.appendChild(subTableHeader);
        Element row = new Element("tr");
        row.appendChild(new Element("td").appendText("Col 1"));
        tableElement.appendChild(row);
        return tableElement;
    }

}