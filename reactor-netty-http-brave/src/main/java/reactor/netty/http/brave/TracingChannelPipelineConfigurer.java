/*
 * Copyright (c) 2021-2025 VMware, Inc. or its affiliates, All Rights Reserved.
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
package reactor.netty.http.brave;

import brave.propagation.CurrentTraceContext;
import io.netty.channel.Channel;
import org.jspecify.annotations.Nullable;
import reactor.netty.ChannelPipelineConfigurer;
import reactor.netty.Connection;
import reactor.netty.ConnectionObserver;

import java.net.SocketAddress;

/**
 * {@link ChannelPipelineConfigurer} to extend the channel pipeline with
 * {@link TracingChannelInboundHandler} and {@link TracingChannelOutboundHandler}.
 *
 * @author Violeta Georgieva
 * @since 1.0.6
 */
final class TracingChannelPipelineConfigurer implements ChannelPipelineConfigurer {
	final TracingChannelInboundHandler inboundHandler;
	final TracingChannelOutboundHandler outboundHandler;

	TracingChannelPipelineConfigurer(CurrentTraceContext currentTraceContext) {
		this.inboundHandler = new TracingChannelInboundHandler(currentTraceContext);
		this.outboundHandler = new TracingChannelOutboundHandler(currentTraceContext);
	}

	@Override
	public void onChannelInit(ConnectionObserver connectionObserver, Channel channel, @Nullable SocketAddress socketAddress) {
		Connection.from(channel)
		          .addHandlerFirst(TracingChannelInboundHandler.NAME, inboundHandler)
		          .addHandlerLast(TracingChannelOutboundHandler.NAME, outboundHandler);
	}
}
