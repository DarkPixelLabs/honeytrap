package com.darkpixellabs.honeytrap.model;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.*;

public interface HitRepository extends JpaRepository<Hit,Long> {
    Page<Hit> findBySourceIpContainingIgnoreCaseAndPathContainingIgnoreCase(String ip,String path,Pageable pageable);
    Page<Hit> findBySourceIpContainingIgnoreCase(String ip,Pageable pageable);
    Page<Hit> findByPathContainingIgnoreCase(String path,Pageable pageable);
    long countByTimestampAfter(Instant since);
    @Query("select count(distinct h.sourceIp) from Hit h where h.timestamp >= :since") long countDistinctIps(@Param("since") Instant since);
    @Query("select h.path from Hit h group by h.path order by count(h) desc") List<String> topPaths(Pageable p);
    @Query("select h.sourceIp from Hit h group by h.sourceIp order by count(h) desc") List<String> topIps(Pageable p);
    @Query("select h.path, count(h) from Hit h group by h.path order by count(h) desc") List<Object[]> pathCounts(Pageable p);
    @Query("select h.sourceIp, count(h) from Hit h group by h.sourceIp order by count(h) desc") List<Object[]> ipCounts(Pageable p);
    @Query("select count(h), h.path from Hit h group by h.path order by count(h) desc") List<Object[]> mostTargeted();
    @Query("select count(h), h.sourceIp from Hit h group by h.sourceIp order by count(h) desc") List<Object[]> mostActiveIp();
    List<Hit> findAllByOrderByTimestampDesc(Pageable pageable);
    @Query("select h from Hit h order by h.timestamp asc") List<Hit> oldest(Pageable p);
}
