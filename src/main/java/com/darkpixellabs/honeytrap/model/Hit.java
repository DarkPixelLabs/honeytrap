package com.darkpixellabs.honeytrap.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="hits", indexes={@Index(name="idx_hit_timestamp", columnList="timestamp"), @Index(name="idx_hit_source_ip", columnList="sourceIp")})
public class Hit {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private Instant timestamp;
    @Column(length=128) private String sourceIp;
    @Column(length=128) private String forwardedFor;
    @Column(length=16, nullable=false) private String method;
    @Column(length=2048, nullable=false) private String path;
    @Column(length=512) private String userAgent;
    @Lob private String headersJson;
    @Lob private String body;
    protected Hit() {}
    public Hit(Instant timestamp,String sourceIp,String forwardedFor,String method,String path,String userAgent,String headersJson,String body){this.timestamp=timestamp;this.sourceIp=sourceIp;this.forwardedFor=forwardedFor;this.method=method;this.path=path;this.userAgent=userAgent;this.headersJson=headersJson;this.body=body;}
    public Long getId(){return id;} public Instant getTimestamp(){return timestamp;} public String getSourceIp(){return sourceIp;} public String getForwardedFor(){return forwardedFor;} public String getMethod(){return method;} public String getPath(){return path;} public String getUserAgent(){return userAgent;} public String getHeadersJson(){return headersJson;} public String getBody(){return body;}
}
