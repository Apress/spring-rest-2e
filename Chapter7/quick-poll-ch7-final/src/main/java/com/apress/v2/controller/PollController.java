package com.apress.v2.controller;

import java.net.URI;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.apress.domain.Poll;
import com.apress.dto.error.ErrorDetail;
import com.apress.exception.ResourceNotFoundException;
import com.apress.repository.PollRepository;

@RestController("pollControllerV2")
@RequestMapping("/v2/")
@Api(value = "polls", description = "Poll API")
public class PollController {

	@Inject
	private PollRepository pollRepository;
	
	@RequestMapping(value="/polls", method=RequestMethod.POST)
	@ApiOperation(value = "Creates a new Poll", notes="The newly created poll Id will be sent in the location response header",
					response = Void.class)
	@ApiResponses(value = {@ApiResponse(code=201, message="Poll Created Successfully", response=Void.class),
			@ApiResponse(code=500, message="Error creating Poll", response=ErrorDetail.class) } )
	public ResponseEntity<Void> createPoll(@Valid @RequestBody Poll poll) {
		poll = pollRepository.save(poll);
		
		// Set the location header for the newly created resource
		HttpHeaders responseHeaders = new HttpHeaders();
		URI newPollUri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(poll.getId()).toUri();
		responseHeaders.setLocation(newPollUri);
		
		return new ResponseEntity<>(null, responseHeaders, HttpStatus.CREATED);
	}

	@GetMapping("/polls/{pollId}")
	@ApiOperation(value = "Retrieves given Poll", response=Poll.class)
	@ApiResponses(value = {@ApiResponse(code=200, message="", response=Poll.class),  
			@ApiResponse(code=404, message="Unable to find Poll", response=ErrorDetail.class) } )
	public ResponseEntity<?> getPoll(@PathVariable Long pollId) {
		return new ResponseEntity<>(verifyPoll(pollId), HttpStatus.OK);
	}
	
	@GetMapping("/polls")
	@ApiOperation(value = "Retrieves all the polls", response=Poll.class, responseContainer="List")
	public ResponseEntity<Page<Poll>> getAllPolls(Pageable pageable) {
		Page<Poll> allPolls = pollRepository.findAll(pageable);
		return new ResponseEntity<>(allPolls, HttpStatus.OK);
	}

	@PutMapping("/polls/{pollId}")
	@ApiOperation(value = "Updates given Poll", response=Void.class)
	@ApiResponses(value = {@ApiResponse(code=200, message="", response=Void.class),  
			@ApiResponse(code=404, message="Unable to find Poll", response=ErrorDetail.class) } )
	public ResponseEntity<Void> updatePoll(@RequestBody Poll poll, @PathVariable Long pollId) {
		verifyPoll(pollId);
		pollRepository.save(poll);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@DeleteMapping("/polls/{pollId}")
	@ApiOperation(value = "Deletes given Poll", response=Void.class)
	@ApiResponses(value = {@ApiResponse(code=200, message="", response=Void.class),  
			@ApiResponse(code=404, message="Unable to find Poll", response=ErrorDetail.class) } )
	public ResponseEntity<Void> deletePoll(@PathVariable Long pollId) {
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
