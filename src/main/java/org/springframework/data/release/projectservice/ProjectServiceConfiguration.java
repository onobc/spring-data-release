/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.release.projectservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.release.git.GitOperations;
import org.springframework.data.release.git.GitProperties;
import org.springframework.data.release.utils.Logger;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for the Sagan interaction subsystem.
 *
 * @author Oliver Gierke
 * @author Mark Paluch
 */
@Configuration(proxyBeanMethods = false)
class ProjectServiceConfiguration {

	@Autowired GitProperties gitProperties;
	@Autowired Logger logger;

	@Bean
	public ProjectServiceOperations saganOperations(GitOperations operations, ProjectService projectService) {
		return new ProjectServiceOperations(operations, projectService, logger);
	}

	@Bean
	ProjectService projectsService(ProjectServiceProperties properties) {

		RestTemplate restTemplate = new RestTemplateBuilder()
				.basicAuthentication(gitProperties.getUsername(), properties.key).build();

		return new DefaultProjectClient(restTemplate, properties, logger);
	}
}
