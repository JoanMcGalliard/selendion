package concordion.spec.examples;

import org.junit.runner.RunWith;
import org.selendion.integration.concordion.SelendionRunner;

@RunWith(SelendionRunner.class)
public class Demo {
    
    public String greetingFor(String firstName) {
        return String.format("Hello %s!", firstName);
    }
//    
//    @Test
//    public void anotherTest() {
//        System.out.println("another test");
//    }
//    
//    @Before
//    public void before() {
//        System.out.println("before");
//    }
//    
//    @After
//    public void after() {
//        System.out.println("after");
//    }
//    
//    @BeforeClass
//    public static void beforeClass() {
//        System.out.println("beforeClass");
//    }
//    
//    @AfterClass
//    public static void afterClass() {
//        System.out.println("afterClass");
//    }
}
