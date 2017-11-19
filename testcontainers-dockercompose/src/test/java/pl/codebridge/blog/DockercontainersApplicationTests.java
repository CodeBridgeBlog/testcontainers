package pl.codebridge.blog;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.text.MessageFormat;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.shaded.com.google.common.collect.Iterables;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DockercontainersApplication.class)
@ContextConfiguration(initializers = DockercontainersApplicationTests.Initializer.class)
public class DockercontainersApplicationTests {

    public static final String SERVICE_NAME = "mongodb_1";
    private static final int PORT = 27017;
    private static final String DB_NAME = "test";
    @ClassRule
    public static DockerComposeContainer environment =
            new DockerComposeContainer(new File("src/test/resources/docker-compose.yml"))
                    .withExposedService(SERVICE_NAME, PORT)
                    .withPull(true);

    @Autowired
    private UserRepository repository;

    @Test
    public void should_return_newly_created_user() {
        final String name = "Test";
        User u = new User(name);
        u = repository.save(u);

        Iterable<User> users = repository.findAll();

        assertEquals(1, Iterables.size(users));
        assertEquals(name, users.iterator().next().getName());
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override

        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            final String mongoUri = MessageFormat.format("mongodb://{0}:{1}/{2}",
                    environment.getServiceHost("mongodb_1", PORT),
                    environment.getServicePort("mongodb_1", PORT).toString(),
                    DB_NAME);
            EnvironmentTestUtils.addEnvironment("testcontainers", configurableApplicationContext.getEnvironment(),
                    "spring.data.mongodb.uri=" + mongoUri
            );
        }
    }
}
