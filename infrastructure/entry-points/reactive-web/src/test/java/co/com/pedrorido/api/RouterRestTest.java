package co.com.pedrorido.api;

import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {RouterRest.class, UserHandler.class})
@WebFluxTest
class RouterRestTest {



}
