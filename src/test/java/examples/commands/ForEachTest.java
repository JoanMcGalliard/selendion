/*
	Copyright Joan McGalliard, 2008-9
*/

package examples.commands;

public class ForEachTest extends AbstractTestSupport {

    public String greetingFor(String firstName) {
        if (firstName.equals("Bob") || firstName.equals("John") || firstName.equals("Brian") || firstName.equals("Allen") || firstName.equals("James")) {
            return "Hello Sir!";
        } else {
            return "Hello Madam!";
        }
    }


}
