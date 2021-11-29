package com.apress.v1.controller;

import com.apress.domain.Poll;
import com.apress.dto.error.ErrorDetail;
import com.apress.exception.ResourceNotFoundException;
import com.apress.repository.PollRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.inject.Inject;
import java.net.URI;
import java.util.Optional;

@RestController("pollControllerV1")
@RequestMapping("/v1/")
@Api(value = "polls", description = "Poll API")
public class PollController {

	@Inject
	private PollRepository pollRepository;

	@PostMapping("/polls")
	@ApiOperation(value = "Creates a new Poll", notes="The newly created poll Id will be sent in the location response header",
			response = Void.class)
	@ApiResponses(value = {@ApiResponse(code=201, message="Poll Created Successfully", response=Void.class),
			@ApiResponse(code=500, message="Error creating Poll", response= ErrorDetail.class) } )
	public ResponseEntity<?> createPoll(@RequestBody Poll poll) {
		poll = pollRepository.save(poll);

		// Set the location header for the newly created resource
		HttpHeaders responseHeaders = new HttpHeaders();
		URI newPollUri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(poll.getId()).toUri();
		responseHeaders.setLocation(newPollUri);

		return new ResponseEntity<>(null, responseHeaders, HttpStatus.CREATED);
	}

	@GetMapping("/polls")
	@ApiOperation(value = "Retrieves all the polls", response=Poll.class, responseContainer="List")
	public ResponseEntity<Iterable<Poll>> getAllPolls() {
		Iterable<Poll> allPolls = pollRepository.findAll();
		return new ResponseEntity<>(allPolls, HttpStatus.OK);
	}

	@GetMapping("/polls/{pollId}")
	public ResponseEntity<?> getPoll(@PathVariable Long pollId) throws Exception {
		return new ResponseEntity<> (verifyPoll(pollId), HttpStatus.OK);
	}

	@PutMapping("/polls/{pollId}")
	@ApiOperation(value = "Updates given Poll", response=Void.class)
	@ApiResponses(value = {@ApiResponse(code=200, message="", response=Void.class),
			@ApiResponse(code=404, message="Unable to find Poll", response=ErrorDetail.class) } )
	public ResponseEntity<?> updatePoll(@RequestBody Poll poll, @PathVariable Long pollId) {
		// Save the entity
		verifyPoll(pollId);
		pollRepository.save(poll);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/polls/{pollId}")
	@ApiOperation(value = "Deletes given Poll", response=Void.class)
	@ApiResponses(value = {@ApiResponse(code=200, message="", response=Void.class),
			@ApiResponse(code=404, message="Unable to find Poll", response=ErrorDetail.class) } )
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
