# JBossのUndertowやServlet 3.1でWebFluxが使えるかなどを調べる（WebClient編）

## ビルド手順

```
mvn clean package
```

## デプロイ

warファイル「spring-web-flux-trial1_webclient-0.0.1-SNAPSHOT.war」をJBoss EAP/WildFlyにデプロイする。

## 動作確認

```
curl -i http://localhost:8080/spring-web-flux-trial1_webclient-0.0.1-SNAPSHOT/aa-reactive/500
```

Reactive:Result is aa!

```
curl -i http://localhost:8080/spring-web-flux-trial1_webclient-0.0.1-SNAPSHOT/aiueo-reactive/3000
```

Reactive:*aiueo*kakikukeko*sashisuseso*tatitsuteto

```
curl -i http://localhost:8080/spring-web-flux-trial1_webclient-0.0.1-SNAPSHOT/aiueo2-reactive
```
HTTP/1.1 200 OK
Connection: keep-alive
Transfer-Encoding: chunked
Content-Type: text/plain
Date: Sun, 29 Nov 2020 05:22:38 GMT

Reactive:aiueo
Reactive:kakikukeko
Reactive:sashisuseso
Reactive:tatitsuteto
Reactive:naninuneno
Reactive:hahihuheho
Reactive:mamimumemo
Reactive:yayiyuyeyo
Reactive:wawiwuwewo
Reactive:gagigugego

```
curl -i http://localhost:8080/spring-web-flux-trial1_webclient-0.0.1-SNAPSHOT/aiueo2-delay
```

HTTP/1.1 200 OK
Connection: keep-alive
Content-Type: text/plain;charset=UTF-8
Content-Length: 197
Date: Sun, 29 Nov 2020 06:46:46 GMT

Reactive:aiueo
Reactive:kakikukeko
Reactive:sashisuseso
Reactive:tatitsuteto
Reactive:naninuneno
Reactive:hahihuheho
Reactive:mamimumemo
Reactive:yayiyuyeyo
Reactive:wawiwuwewo
Reactive:gagigugego

非同期処理であっても、タイムアウトが発生すると、次のようなエラーが発生する。
HTTP/1.1 503 Service Unavailable
Connection: keep-alive
Transfer-Encoding: chunked
Content-Type: application/json
Date: Sun, 29 Nov 2020 06:35:45 GMT

{"timestamp":"2020-11-29T06:35:45.090+0000","status":503,"error":"Service Unavailable","message":"","path":"/spring-web-flux-trial1_webclient-0.0.1-SNAPSHOT/aiueo2-delay"}

この際、サーバーサイドではInterruptedExceptionがCompletableFuture#get()内から投げられて、
503エラーがRESTクライアントに返却される。

	  @GetMapping("aiueo2-delay")
	  public Mono<String> getAiueo2Delay() {
		  Future<String> future = retrieveAiueo2();
		  Mono<String> mono = Mono.fromCallable(future::get)
				  				.subscribeOn(Schedulers.fromExecutor(this.executorService))
				  				.log("FUTURE @ getAiueo2Delay");
		  return mono;
	  }

15:35:45,073 INFO  [FUTURE @ getAiueo2Delay] (default task-2) | cancel()
15:35:45,080 ERROR [FUTURE @ getAiueo2Delay] (EE-ManagedExecutorService-default-Thread-2) | onError(java.lang.InterruptedException)
15:35:45,082 WARN  [org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver] (default task-1) Resolved [org.springframework.web.context.request.async.AsyncRequestTimeoutException]
15:35:45,080 ERROR [FUTURE @ getAiueo2Delay] (EE-ManagedExecutorService-default-Thread-2) : java.lang.InterruptedException
        at java.util.concurrent.CompletableFuture.reportGet(CompletableFuture.java:358)
        at java.util.concurrent.CompletableFuture.get(CompletableFuture.java:1919)
        at io.futokiyo.springwebfluxtrial1_webclient.ReactiveWebClientController$$Lambda$6537/00000000611AFA80.call(Unknown Source)
        at reactor.core.publisher.MonoCallable.call(MonoCallable.java:91)
        at reactor.core.publisher.FluxSubscribeOnCallable$CallableSubscribeOnSubscription.run(FluxSubscribeOnCallable.java:227)
        at reactor.core.scheduler.SchedulerTask.call(SchedulerTask.java:68)
        at reactor.core.scheduler.SchedulerTask.call(SchedulerTask.java:28)
        at org.jboss.as.ee.concurrent.ControlPointUtils$ControlledCallable.call(ControlPointUtils.java:129)
        at java.util.concurrent.FutureTask.run(FutureTask.java:277)
        at org.glassfish.enterprise.concurrent.internal.ManagedFutureTask.run(ManagedFutureTask.java:141)
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1160)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)
        at java.lang.Thread.run(Thread.java:822)
        at org.glassfish.enterprise.concurrent.ManagedThreadFactoryImpl$ManagedThread.run(ManagedThreadFactoryImpl.java:250)



## 参考サイト

[spring-webからspring-webfluxへの移行](https://qiita.com/rhirabay/items/e829d1e5a0015daa1dd7)

[SpringBoot2のBlocking Web vs Reactive WebについてLTしてきた 2018-03-27](https://bufferings.hatenablog.com/entry/2018/03/27/233152)

[webflux-demo-201803](https://github.com/bufferings/webflux-demo-201803)

[Class Flux<T> ※マーブルダイアグラムの一覧がある](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)

[Reactor 3 Reference Guide](https://projectreactor.io/docs/core/release/reference/)

[Concurrency in Spring WebFlux](https://www.baeldung.com/spring-webflux-concurrency)

[reactor-core-playground](https://github.com/balamaci/reactor-core-playground)

[Future を Mono に変換する](https://mike-neck.hatenadiary.com/entry/2018/03/13/073000)

[第9章 コンカレンシーユーティリティー](https://access.redhat.com/documentation/ja-jp/red_hat_jboss_enterprise_application_platform/7.1/html/development_guide/concurrency_utilities)

[インタフェースExecutorService](https://docs.oracle.com/javase/jp/8/docs/api/java/util/concurrent/ExecutorService.html)

[ExecutorService#submit を使ったサンプル](http://a4dosanddos.hatenablog.com/entry/2016/04/03/233804)

[java.util.concurrent.Futureのガイド](https://www.codeflow.site/ja/article/java-future)

[Jboss Java EEコンテナとExecutorService](https://www.it-swarm-ja.tech/ja/java/jboss-java-ee%E3%82%B3%E3%83%B3%E3%83%86%E3%83%8A%E3%81%A8executorservice/1070158907/)

[JavaのFutureの取り回しがダルすぎるので色々工夫してみる](https://munchkins-diary.hatenablog.com/entry/2019/11/09/221708)

[はじめてのReactor Core 2016-08-20](https://kazuhira-r.hatenablog.com/entry/20160820/1471703174)

[Asynchronous REST API generating warning](https://stackoverflow.com/questions/52397752/asynchronous-rest-api-generating-warning)

[Spring MVC(+Spring Boot)上での非同期リクエストを理解する -前編-](https://qiita.com/kazuki43zoo/items/ce88dea403c596249e8a)

[Spring MVC(+Spring Boot)上での非同期リクエストを理解する -後編(HTTP Streaming)-](https://qiita.com/kazuki43zoo/items/53b79fe91c41cc5c2e59)

[Recurring AsyncRequestTimeoutException in Spring Boot Admin log](https://stackoverflow.com/questions/39856198/recurring-asyncrequesttimeoutexception-in-spring-boot-admin-log)

[The Spring @Qualifier Annotation](https://www.baeldung.com/spring-qualifier-annotation)

[spring-boot configure embeded server before async config](https://stackoverflow.com/questions/46569354/spring-boot-configure-embeded-server-before-async-config)

[Configure Executor service with spring boot application](https://onecompiler.com/posts/3ux4e2c6b/configure-executor-service-with-spring-boot-application)

[@Autowired ExecutorService , no need to repeatedly initialize the thread pool](https://www.programmersought.com/article/6392293378/)