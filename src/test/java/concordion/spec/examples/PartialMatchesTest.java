package concordion.spec.examples;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.selendion.integration.concordion.SelendionTestCase;

public class PartialMatchesTest extends SelendionTestCase {

    private Set<String> usernamesInSystem = new HashSet<String>();
    
    public void setUpUser(String username) {
        usernamesInSystem.add(username);
    }
    
    public SortedSet<String> getSearchResultsFor(String searchString) {
        SortedSet<String> matches = new TreeSet<String>();
        for (String username : usernamesInSystem) {
            if (username.contains(searchString)) {
                matches.add(username);
            }
        }
        return matches;
    }
}
