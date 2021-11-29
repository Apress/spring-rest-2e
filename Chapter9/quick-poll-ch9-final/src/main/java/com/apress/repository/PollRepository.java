package com.apress.repository;

import com.apress.domain.Poll;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PollRepository extends PagingAndSortingRepository<Poll, Long> {

}
