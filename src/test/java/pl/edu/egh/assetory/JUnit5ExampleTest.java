package pl.edu.egh.assetory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class JUnit5ExampleTest {

    private static final Logger log = LoggerFactory.getLogger(JUnit5ExampleTest.class);

    @BeforeAll
    static void beforeAll() {
        log.info("Before all test methods");
    }

    @BeforeEach
    void beforeEach() {
        log.info("Before each test method");
    }

    @Test
    void exampleTest() {
        boolean ultimateQuestion = 2 + 2 == 7;
        Assertions.assertFalse(ultimateQuestion);
    }

    @AfterEach
    void afterEach() {
        log.info("After each test method");
    }

    @AfterAll
    static void afterAll() {
        log.info("After all test methods");
    }
}
