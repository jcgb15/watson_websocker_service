/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web.service;

import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author jeanm
 */
//@Path("web.service")
@ServerEndpoint("/storyfile")
public class storyfile {
 SpeechToText service = new SpeechToText();
  RecognizeOptions options;
  private static CountDownLatch lock = new CountDownLatch(1);
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of storyfile
     */
    public storyfile() {
    }

    /**
     * Retrieves representation of an instance of web.service.storyfile
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(String nombre) {
        //TODO return proper representation object
        return "Hola";
    }

    /**
     * PUT method for updating or creating an instance of storyfile
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public String putJson(String content) {
       return "Hola: "+content;
    }
    
    @OnOpen
    public void open(){
        System.out.println("Ws: Connected");
         service.setUsernameAndPassword("b5bbbcad-d720-4847-8a74-019e06cd8c1f", "lEvUY83Fx25c");
    }
    
   
    @OnMessage
    public String send(InputStream audio) throws InterruptedException
    {
        try {
           
            options = new RecognizeOptions.Builder()
                    
                .continuous(true)
                .interimResults(true)
                .contentType(HttpMediaType.AUDIO_WAV)
                    
                .build();
            service.recognizeUsingWebSocket(audio, options, new BaseRecognizeCallback(){
                @Override
                public void onTranscription(SpeechResults speechResults) {
                  System.out.println(speechResults);
                  if (speechResults.isFinal())
                  {
                    lock.countDown();
                  }
                }
            
            });
            System.out.println(String.valueOf(audio.read()));
        } catch (IOException ex) {
          
           Logger.getLogger(storyfile.class.getName()).log(Level.SEVERE, null, ex);
        }
        lock.await(1, TimeUnit.MINUTES);
        return "listo";
    }
    
    @OnMessage
    public String send(String audio){
        return "Hola: "+audio;
    }

    
    
}
