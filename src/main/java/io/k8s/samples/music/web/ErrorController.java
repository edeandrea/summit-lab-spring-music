package io.k8s.samples.music.web;

import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/errors")
public class ErrorController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ErrorController.class);
	private List<int[]> junk = new ArrayList<>();

	@GetMapping(value = "/kill")
	public void kill() {
		LOGGER.info("Forcing application exit");
		System.exit(1);
	}

	@GetMapping(value = "/fill-heap")
	public void fillHeap() {
		LOGGER.info("Filling heap with junk, to initiate a crash");
		while (true) {
			junk.add(new int[9999999]);
		}
	}

	@GetMapping(value = "/throw")
	public void throwException() {
		LOGGER.info("Forcing an exception to be thrown");
		throw new NullPointerException("Forcing an exception to be thrown");
	}
}
