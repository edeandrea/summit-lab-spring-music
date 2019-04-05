package com.redhat.summit2019.springmusic.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/errors")
@Api("Error Simulation")
public class ErrorController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ErrorController.class);
	private List<int[]> junk = new ArrayList<>();

	@ApiOperation(value = "Kill the application", notes = "Kill the application", nickname = "kill-application")
	@ApiResponses({
									@ApiResponse(code = 200, message = "Success!")
	})
	@GetMapping("/kill")
	public void kill() {
		LOGGER.info("Forcing application exit");
		System.exit(1);
	}

	@ApiOperation(value = "Fill the heap with junk to initiate a crash", notes = "Fill the heap with junk to initiate a crash", nickname = "fill-heap")
	@ApiResponses({
									@ApiResponse(code = 200, message = "Success!")
								})
	@GetMapping("/fill-heap")
	public void fillHeap() {
		LOGGER.info("Filling heap with junk, to initiate a crash");
		while (true) {
			this.junk.add(new int[9999999]);
		}
	}

	@ApiOperation(value = "Throw an exception", notes = "Throw an exception", nickname = "throw-exception")
	@GetMapping("/throw")
	public void throwException() {
		LOGGER.info("Forcing an exception to be thrown");
		throw new NullPointerException("Forcing an exception to be thrown");
	}
}
