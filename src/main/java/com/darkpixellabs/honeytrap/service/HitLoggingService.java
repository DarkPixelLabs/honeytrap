package com.darkpixellabs.honeytrap.service;

import com.darkpixellabs.honeytrap.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.*; import java.nio.charset.StandardCharsets; import java.time.Instant; import java.util.*;

@Service
public class HitLoggingService {
    public static final int MAX_BODY=2048, MAX_PATH=2048, MAX_HEADER_VALUE=512, MAX_HEADERS=64;
    private final HitRepository repo; private final ObjectMapper mapper; private final int retention;
    public HitLoggingService(HitRepository repo,ObjectMapper mapper,@Value("${honeytrap.retention.max-hits:50000}") int retention){this.repo=repo;this.mapper=mapper;this.retention=Math.max(100,retention);}
    public Hit log(HttpServletRequest req) {
        Map<String,String> headers=new LinkedHashMap<>(); Enumeration<String> names=req.getHeaderNames(); int n=0;
        while(names!=null&&names.hasMoreElements()&&n++<MAX_HEADERS){String k=names.nextElement(); headers.put(cap(k,128),cap(req.getHeader(k),MAX_HEADER_VALUE));}
        String body=""; try { body=readBody(req); } catch(IOException ignored) { body="[unavailable]"; }
        String path=cap(req.getRequestURI()+(req.getQueryString()==null?"":"?"+req.getQueryString()),MAX_PATH);
        String ua=cap(req.getHeader("User-Agent"),MAX_HEADER_VALUE); String xff=cap(req.getHeader("X-Forwarded-For"),512);
        String json; try{json=mapper.writeValueAsString(headers);}catch(JsonProcessingException e){json="{}";}
        String ip=cap(req.getRemoteAddr(),128);
        return repo.save(new Hit(Instant.now(),ip,xff,cap(req.getMethod(),16),path,ua,json,body));
    }
    private String readBody(HttpServletRequest req)throws IOException{if(req.getContentLengthLong()==0)return ""; InputStream in=req.getInputStream(); ByteArrayOutputStream out=new ByteArrayOutputStream(MAX_BODY); byte[] b=new byte[512]; int r,total=0; while(total<MAX_BODY&&(r=in.read(b,0,Math.min(b.length,MAX_BODY-total)))>0){out.write(b,0,r);total+=r;} return new String(out.toByteArray(),StandardCharsets.UTF_8);}
    private String cap(String s,int max){if(s==null)return ""; return s.length()<=max?s:s.substring(0,max);}
    @Scheduled(fixedDelayString="${honeytrap.retention.prune-delay-ms:3600000}")
    public void prune(){long count=repo.count(); if(count<=retention)return; long remove=count-retention; List<Hit> old=repo.oldest(PageRequest.of(0,(int)Math.min(remove,1000))); while(!old.isEmpty()){repo.deleteAllInBatch(old); remove-=old.size(); if(remove<=0)break; old=repo.oldest(PageRequest.of(0,(int)Math.min(remove,1000)));}}
}
