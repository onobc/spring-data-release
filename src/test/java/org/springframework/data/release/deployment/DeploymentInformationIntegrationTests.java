/*
 * Copyright 2015-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.release.deployment;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.release.AbstractIntegrationTests;
import org.springframework.data.release.model.Iteration;
import org.springframework.data.release.model.ModuleIteration;
import org.springframework.data.release.model.Projects;
import org.springframework.data.release.model.ReleaseTrains;
import org.springframework.data.release.model.TrainIteration;

/**
 * Integration test for {@link DefaultDeploymentInformation}.
 *
 * @author Oliver Gierke
 */
class DeploymentInformationIntegrationTests extends AbstractIntegrationTests {

	@Autowired DeploymentProperties properties;

	@Test
	void createsDeploymentInformation() {

		TrainIteration iteration = new TrainIteration(ReleaseTrains.HOPPER, Iteration.M1);
		ModuleIteration buildModule = iteration.getModule(Projects.BUILD);

		DeploymentInformation information = new DefaultDeploymentInformation(buildModule, properties);

		assertThat(information.getBuildName()).isEqualTo("spring-data-build");
		assertThat(information.getBuildNumber()).startsWith("spring-data-build-main-release");
		assertThat(information.getTargetRepository()).contains("libs-milestone-local");
	}

	@Test
	void considersBranchName() {

		DeploymentInformation commercialSr1 = new DefaultDeploymentInformation(
				ReleaseTrains.TURING.getIteration(Iteration.SR1).getModule(Projects.BUILD), properties);
		assertThat(commercialSr1.getBuildNumber()).startsWith("spring-data-build-commercial-3.0.x-release");

		DeploymentInformation ossGA = new DefaultDeploymentInformation(
				ReleaseTrains.VAUGHAN.getIteration(Iteration.GA).getModule(Projects.BUILD), properties);
		assertThat(ossGA.getBuildNumber()).startsWith("spring-data-build-commercial-3.2.x-release");

		DeploymentInformation ossSr1 = new DefaultDeploymentInformation(
				ReleaseTrains.VAUGHAN.getIteration(Iteration.SR1).getModule(Projects.BUILD), properties);
		assertThat(ossSr1.getBuildNumber()).startsWith("spring-data-build-commercial-3.2.x");
	}
}
