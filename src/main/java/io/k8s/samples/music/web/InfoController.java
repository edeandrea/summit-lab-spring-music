package io.k8s.samples.music.web;

import io.k8s.samples.music.domain.ApplicationInfo;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {
	private final Environment springEnvironment;

	public InfoController(Environment springEnvironment) {
		this.springEnvironment = springEnvironment;
	}

	@GetMapping(value = "/appinfo")
	public ApplicationInfo info() {
//		return new ApplicationInfo(this.springEnvironment.getActiveProfiles(), getServiceNames());
		return new ApplicationInfo(this.springEnvironment.getActiveProfiles(), new String[0]);
	}

//	@GetMapping(value = "/service")
//	public List<ServiceInfo> showServiceInfo() {
//		return Optional.ofNullable(this.cloud)
//			.map(Cloud::getServiceInfos)
//			.orElseGet(Collections::emptyList);
//	}
//
//	private String[] getServiceNames() {
//		return Optional.ofNullable(this.cloud)
//			.map(Cloud::getServiceInfos)
//			.map(List::stream)
//			.orElseGet(Stream::empty)
//			.map(ServiceInfo::getId)
//			.toArray(String[]::new);
//	}
}
