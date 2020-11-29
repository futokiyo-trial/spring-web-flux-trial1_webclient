package io.futokiyo.springwebfluxtrial1_webclient;

//import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.core.publisher.Flux;

@RestController
public class ReactiveWebClientController {
	
	  private static final String DELAY_SERVICE_URL_AA = "http://localhost:8080/spring-web-flux-trial1-0.0.1-SNAPSHOT/fluxtest1/aa";
	  
	  private static final String DELAY_SERVICE_URL_AIUEO = "http://localhost:8080/spring-web-flux-trial1-0.0.1-SNAPSHOT/fluxtest1/aiueo";
	
	  private static final String DELAY_SERVICE_URL_AIUEO2 = "http://localhost:8080/spring-web-flux-trial1-0.0.1-SNAPSHOT/fluxtest1/aiueo2";
	  
	  ExecutorService executorService;
	  
	  public ReactiveWebClientController(@Qualifier("jbossAsyncExecutorService") ExecutorService executorService) {
		  this.executorService = executorService;
	  }
	  
	  
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
	  
	  @GetMapping("aiueo2-reactive")
	  public Flux<String> getAiueo2() {
		WebClient webClient = WebClient.builder()
				  .baseUrl(DELAY_SERVICE_URL_AIUEO2)
				  .build();
		
		
		  
	    return webClient.get()
	        .retrieve()
	        .bodyToFlux(String.class)
	        .map(s -> "Reactive:" + s + "\n");
	  }
	  
	  @GetMapping("aiueo2-delay")
	  public Mono<String> getAiueo2Delay() {
		  System.out.println( "***start*** ReactiveWebClientController#getAiueo2Delay()" + ": Current Thread ID- " + Thread.currentThread().getId() + " For Thread- " + Thread.currentThread().getName() );
		  Future<String> future = retrieveAiueo2();
		  Mono<String> mono = Mono.fromCallable(future::get)
				  				.subscribeOn(Schedulers.fromExecutor(this.executorService))
				  				.log("FUTURE@getAiueo2Delay");
		  System.out.println( "***end*** ReactiveWebClientController#getAiueo2Delay()" + ": Current Thread ID- " + Thread.currentThread().getId() + " For Thread- " + Thread.currentThread().getName() );
		  return mono;
	  }
	  
	  private Future<String> retrieveAiueo2(){
		  System.out.println( "****ReactiveWebClientController#retrieveAiueo2()" + ": Current Thread ID- " + Thread.currentThread().getId() + " For Thread- " + Thread.currentThread().getName() );
		  System.out.println("****" + this.executorService);
		  CompletableFuture<String> future = new CompletableFuture<>();
		  
		  this.executorService.submit(() -> {
			  System.out.println( "+++start+++ lambdaExpression in ReactiveWebClientController#retrieveAiueo2()" + ": Current Thread ID- " + Thread.currentThread().getId() + " For Thread- " + Thread.currentThread().getName() );
			  WebClient webClient = WebClient.builder()
					  .baseUrl(DELAY_SERVICE_URL_AIUEO2)
					  .build();
			
			  Mono<String> reducedMono = 
			  webClient.get()
		        .retrieve()
		        .bodyToFlux(String.class)
		        .map(s -> "Reactive:" + s + "\n")
		        .reduce("", (outputMsg, str) -> {
		        	System.out.println( "~~~reducingSseResponse" + ": Current Thread ID- " + Thread.currentThread().getId() + " For Thread- " + Thread.currentThread().getName() );
		        	StringBuilder sb = new StringBuilder(outputMsg);
		        	sb.append(str);
		        	return sb.toString();
		        });
			  
			  reducedMono.subscribe(str -> {
				  System.out.println( "~~~afterComplete~~~ call subscriber" + ": Current Thread ID- " + Thread.currentThread().getId() + " For Thread- " + Thread.currentThread().getName() );
				  future.complete(str);
			  });
			  System.out.println( "+++call+++ reducedMono.subscribe" + ": Current Thread ID- " + Thread.currentThread().getId() + " For Thread- " + Thread.currentThread().getName() );
			  System.out.println( "+++end+++ lambdaExpression in ReactiveWebClientController#retrieveAiueo2()" + ": Current Thread ID- " + Thread.currentThread().getId() + " For Thread- " + Thread.currentThread().getName() );
		  });
		  return future;
	  }
	  
}
