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

## 参考サイト

[spring-webからspring-webfluxへの移行](https://qiita.com/rhirabay/items/e829d1e5a0015daa1dd7)

[SpringBoot2のBlocking Web vs Reactive WebについてLTしてきた 2018-03-27](https://bufferings.hatenablog.com/entry/2018/03/27/233152)

[webflux-demo-201803](https://github.com/bufferings/webflux-demo-201803)


