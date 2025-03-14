/*
 * Copyright (c) 2025 VMware, Inc. or its affiliates, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.netty.examples.documentation.http.client.proxy.deferred;

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

public class Application {

	public static void main(String[] args) {
		HttpClient client =
				HttpClient.create()
				          .proxyWhen((httpClientConfig, spec) -> {    // only applied
				              if (httpClientConfig.uri().startsWith("https://example.com")) {
				                  return Mono.justOrEmpty(
				                          spec.type(ProxyProvider.Proxy.HTTP)
				                              .host("proxy")
				                              .port(8080)
				                              .nonProxyHosts("localhost")
				                              .connectTimeoutMillis(20_000));
				              }

				              return Mono.empty();
				          })
				          .proxy(spec -> spec.type(ProxyProvider.Proxy.HTTP)
				                             .host("ignored-proxy-domain")
				                             .port(9000)
				                             .connectTimeoutMillis(20_000))   // ignored
				          .noProxy(); // ignored

		String response =
				client.get()
				      .uri("https://example.com/")
				      .responseContent()
				      .aggregate()
				      .asString()
				      .block();

		System.out.println("Response " + response);
	}
}