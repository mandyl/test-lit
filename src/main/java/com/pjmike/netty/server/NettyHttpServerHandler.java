package com.pjmike.netty.server;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName NettyHttpServerHandler
 * @Description 自定义处理的Handler
 * @Author xl
 * @Date 2019/8/27 11:30
 * @Version 1.0
 */
public class NettyHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
  @Override
  protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
    System.out.println(fullHttpRequest);

    FullHttpResponse response = null;
    if(fullHttpRequest.method() == HttpMethod.GET){
      System.out.println(getGetParamsFromChannel(fullHttpRequest));
      String data = "GET method over";
      ByteBuf buf = copiedBuffer(data, CharsetUtil.UTF_8);
      response = responseOk(HttpResponseStatus.OK,buf);
    }else if(fullHttpRequest.method() == HttpMethod.POST){
      System.out.println(getPostParamsFromChannel(fullHttpRequest));
      String data = "POST method over";
      ByteBuf buf = copiedBuffer(data, CharsetUtil.UTF_8);
      response = responseOk(HttpResponseStatus.OK,buf);
    }else{
      response = responseOk(HttpResponseStatus.INTERNAL_SERVER_ERROR,null);
    }
    // 发送响应
    channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
  }

  private ByteBuf copiedBuffer(String data, Charset utf8) {
    return Unpooled.wrappedBuffer(data.getBytes());
  }

  private Map<String, Object> getPostParamsFromChannel(FullHttpRequest fullHttpRequest) {
    Map<String, Object> params = new HashMap<>();
    if(fullHttpRequest.method() == HttpMethod.POST){
      // 处理post 请求
      String strContentType = fullHttpRequest.headers().get("Content-Type").trim();
      if(StringUtil.isNullOrEmpty(strContentType)){
        return null;
      }
      if(strContentType.contains("x-www-form-urlencoded")){
        params = getFormParams(fullHttpRequest);
      }else if(strContentType.contains("application/json")){
        params = getJSONParams(fullHttpRequest);
      }else {
        return null;
      }
    }
    return params;
  }

  private Map<String, Object> getJSONParams(FullHttpRequest fullHttpRequest) {
    Map<String, Object> params = new HashMap<>();

    ByteBuf content = fullHttpRequest.content();
    byte[] reqContent = new byte[content.readableBytes()];
    content.readBytes(reqContent);
    String strContent = null;
    try {
      strContent = new String(reqContent, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    JSONObject jsonParams = JSONObject.parseObject(strContent);
    for (Object key : jsonParams.keySet()) {
      params.put(key.toString(), jsonParams.get(key));
    }

    return params;

  }

  private Map<String, Object> getFormParams(FullHttpRequest fullHttpRequest) {
    Map<String, Object> params = new HashMap<>();

    HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), fullHttpRequest);
    List<InterfaceHttpData> postData = decoder.getBodyHttpDatas();

    for (InterfaceHttpData data : postData) {
      if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
        MemoryAttribute attribute = (MemoryAttribute) data;
        params.put(attribute.getName(), attribute.getValue());
      }
    }

    return params;
  }

  private FullHttpResponse responseOk(HttpResponseStatus status, ByteBuf buf) {
    FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
        status,buf);
    if(buf != null){
      response.headers().set("Content-Type","text/plain;charset=UTF-8");
      response.headers().set("Content-Length",response.content().readableBytes());
    }
    return response;
  }

  private Map<String, Object> getGetParamsFromChannel(FullHttpRequest fullHttpRequest) {
    Map<String, Object> params = new HashMap<>();
    if(fullHttpRequest.method() == HttpMethod.GET){
      QueryStringDecoder decoder = new QueryStringDecoder(fullHttpRequest.uri());
      Map<String, List<String>> paramList = decoder.parameters();

      for(Map.Entry<String, List<String>> entry : paramList.entrySet()){
        params.put(entry.getKey(),entry.getValue().get(0));
      }
      return params;
    }
    return params;
  }
}