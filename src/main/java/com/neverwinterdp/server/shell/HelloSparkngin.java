package com.neverwinterdp.server.shell;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;

import com.beust.jcommander.Parameter;
import com.neverwinterdp.message.Message;
import com.neverwinterdp.message.SampleEvent;
import com.neverwinterdp.netty.http.client.HttpClient;
import com.neverwinterdp.netty.http.client.ResponseHandler;
import com.neverwinterdp.sparkngin.SendAck;
import com.neverwinterdp.util.JSONSerializer;

public class HelloSparkngin {
  static public class Options {
    @Parameter(names = "-host", description = "Server host name or ip")
    String host = "127.0.0.1";
    
    @Parameter(names = "-port", description = "Server listen port")
    int port = 8080;
    
    @Parameter(names = "-topic", description = "Topic name")
    String topic = "Hello";
  
    @Parameter(
        names = "-num-message", 
        description = "Number of message to generate"
     )
    int numMessage = 30000 ;
  }

  static public class StatResponseHandler implements ResponseHandler {
    int okCount  = 0, errorCount = 0, count = 0 ;
    
    synchronized public void onResponse(HttpResponse response) {
      HttpContent content = (HttpContent) response; 
      ByteBuf bfuf = content.content() ;
      byte[] data = bfuf.array() ;
      SendAck ack = JSONSerializer.INSTANCE.fromBytes(data, SendAck.class) ; 
      if(ack.getStatus().equals(SendAck.Status.OK)) okCount++ ;
      else if(ack.getStatus().equals(SendAck.Status.ERROR)) errorCount++ ;
      count++ ;
    }
  }

  public void run(final Options options) throws Exception {
    final StatResponseHandler handler = new StatResponseHandler() ;
    final HttpClient client = new HttpClient (options.host, options.port, handler) ;
    Thread thread = new Thread() {
      public void run() {
        try {
          for(int i = 0; i < options.numMessage; i++) {
            SampleEvent event = new SampleEvent("event-" + i, "event " + i) ;
            Message message = new Message("m" + i, event, true) ;
            message.getHeader().setTopic(options.topic);
            client.post("/message", message);
          }
        } catch(Exception ex) {
          ex.printStackTrace();
        }
        System.out.println("Sent " + options.numMessage);
      }
    };
    thread.start();
    while(true) {
      Thread.sleep(1000);
      System.out.println("count = " + handler.count + ", ok = " + handler.okCount + ", error = " + handler.errorCount) ;
      if(options.numMessage == handler.count) break ;
    }
    System.out.println("Send thread alive = " + thread.isAlive());
    client.close();
    System.out.println("Exit!!!!");
  }
}