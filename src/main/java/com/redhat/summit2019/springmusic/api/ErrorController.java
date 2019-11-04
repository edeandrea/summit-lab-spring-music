package com.redhat.summit2019.springmusic.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/errors")
@Tag(name = "errSim", description = "Error Simulation")
public class ErrorController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ErrorController.class);
	private List<int[]> junk = new ArrayList<>();

	@Operation(summary = "Kill the application", tags = { "errSim" })
	@ApiResponse(responseCode = "200", description = "Success!")
	@GetMapping("/kill")
	public void kill() {
		LOGGER.info("Forcing application exit");
		System.exit(1);
	}

	@Operation(summary = "Fill the heap with junk to initiate a crash", tags = { "errSim" })
	@ApiResponse(responseCode = "200", description = "Success!")
	@GetMapping("/fill-heap")
	public void fillHeap() {
		LOGGER.info("Filling heap with junk, to initiate a crash");
		while (true) {
			this.junk.add(new int[9999999]);
		}
	}

	@Operation(summary = "Throw an exception", tags = { "errSim" })
	@ApiResponse(responseCode = "200", description = "Success!")
	@GetMapping("/throw")
	public void throwException() {
		LOGGER.info("Forcing an exception to be thrown");
		throw new NullPointerException("Forcing an exception to be thrown");
	}
}
