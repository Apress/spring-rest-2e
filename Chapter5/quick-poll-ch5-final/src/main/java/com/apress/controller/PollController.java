package com.apress.controller;

import java.net.URI;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.apress.domain.Poll;
import com.apress.exception.ResourceNotFoundException;
import com.apress.repository.PollRepository;

@RestController
public class PollController {

	@Inject
	private PollRepository pollRepository;

	@PostMapping("/polls")
	public ResponseEntity<?> createPoll(@RequestBody Poll poll) {
		poll = pollRepository.save(poll);

		// Set the location header for the newly created resource
		HttpHeaders responseHeaders = new HttpHeaders();
		URI newPollUri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(poll.getId()).toUri();
		responseHeaders.setLocation(newPollUri);

		return new ResponseEntity<>(null, responseHeaders, HttpStatus.CREATED);
	}

	@GetMapping("/polls")
	public ResponseEntity<Iterable<Poll>> getAllPolls() {
		Iterable<Poll> allPolls = pollRepository.findAll();
		return new ResponseEntity<>(allPolls, HttpStatus.OK);
	}

	@GetMapping("/polls/{pollId}")
	public ResponseEntity<?> getPoll(@PathVariable Long pollId) throws Exception {
		return new ResponseEntity<> (verifyPoll(pollId), HttpStatus.OK);
	}

	@PutMapping("/polls/{pollId}")
	public ResponseEntity<?> updatePoll(@RequestBody Poll poll, @PathVariable Long pollId) {
		// Save the entity
		verifyPoll(pollId);
		pollRepository.save(poll);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/polls/{pollId}")
	public ResponseEntity<?> deletePoll(@PathVariable Long pollId) {
		verifyPoll(pollId);
		pollRepository.deleteById(pollId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	protected Poll verifyPoll(Long pollId) throws ResourceNotFoundException {
		Optional<Poll> poll = pollRepository.findById(pollId);
		if(!poll.isPresent()) {
			throw new ResourceNotFoundException("Poll with id " + pollId + " not found"); 
		}
		return poll.get();
	}
}
