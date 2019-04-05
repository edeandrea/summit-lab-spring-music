package com.redhat.summit2019.springmusic.api;

import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.redhat.summit2019.springmusic.domain.ApplicationInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api("Application Information")
public class InfoController {
	private final Environment springEnvironment;

	public InfoController(Environment springEnvironment) {
		this.springEnvironment = springEnvironment;
	}

	@ApiOperation(value = "Get application information", notes = "Get application information", nickname = "app-info")
	@ApiResponses({
									@ApiResponse(code = 200, message = "Success!")
								})
	@GetMapping(path = "/appinfo", produces = MediaType.APPLICATION_JSON_VALUE)
	public ApplicationInfo info() {
		return new ApplicationInfo(this.springEnvironment.getActiveProfiles(), new String[0]);
	}
}
