package io.futokiyo.springwebfluxtrial1_webclient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@RestController
public class ReactiveWebClientController {
	
	  private static final String DELAY_SERVICE_URL_AA = "http://localhost:8080/spring-web-flux-trial1-0.0.1-SNAPSHOT/fluxtest1/aa";
	  
	  private static final String DELAY_SERVICE_URL_AIUEO = "http://localhost:8080/spring-web-flux-trial1-0.0.1-SNAPSHOT/fluxtest1/aiueo";
	  
	  @GetMapping("aa-reactive/{delayMillis}")
	  public Mono<String> getAa(@PathVariable String delayMillis) {
		WebClient webClient = WebClient.builder()
				  .baseUrl(DELAY_SERVICE_URL_AA)
				  .build();
		  
	    return webClient.get()
	        .uri("/" + delayMillis)
	        .retrieve()
	        .bodyToMono(String.class)
	        .map(s -> "Reactive:" + s);
	  }
	  
	  @GetMapping("aiueo-reactive/{delayMillis}")
	  public Flux<String> getAiueo(@PathVariable String delayMillis) {
		WebClient webClient = WebClient.builder()
				  .baseUrl(DELAY_SERVICE_URL_AIUEO)
				  .build();
		  
	    return webClient.get()
	        .uri("/" + delayMillis)
	        .retrieve()
	        .bodyToFlux(String.class)
	        .map(s -> "Reactive:" + s);
	  }
	  
}
