/*
 * Copyright 2014-2022 the original author or authors.
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
package org.springframework.data.release.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.With;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.util.Assert;

/**
 * @author Oliver Gierke
 */
@ToString
@EqualsAndHashCode
public class Project implements Comparable<Project>, Named {

	private final @Getter ProjectKey key;
	private final @Getter String name;
	private final @With String fullName;
	private final Collection<Project> dependencies;
	private final @Getter Tracker tracker;
	private final @With ArtifactCoordinates additionalArtifacts;
	private final @With boolean skipTests;
	private final @Getter @With boolean useShortVersionMilestones; // use a short version 2.3.0-RC1 instead of 2.3 RC1 if
	private final @Getter @With ProjectMaintainer maintainer;
																																	// true

	Project(String key, String name, Tracker tracker) {
		this(key, name, null, tracker);
	}

	private Project(String key, String name, String fullName, Tracker tracker) {
		this(new ProjectKey(key), name, fullName, Collections.emptySet(), tracker, ArtifactCoordinates.SPRING_DATA, true,
				false, ProjectMaintainer.CORE);
	}

	@java.beans.ConstructorProperties({ "key", "name", "fullName", "dependencies", "tracker", "additionalArtifacts",
			"skipTests", "plainVersionMilestones", "owner" })
	private Project(ProjectKey key, String name, String fullName, Collection<Project> dependencies, Tracker tracker,
			ArtifactCoordinates additionalArtifacts, boolean skipTests, boolean useShortVersionMilestones,
			ProjectMaintainer maintainer) {

		this.key = key;
		this.name = name;
		this.fullName = fullName;
		this.dependencies = dependencies;
		this.tracker = tracker;
		this.additionalArtifacts = additionalArtifacts;
		this.skipTests = skipTests;
		this.useShortVersionMilestones = useShortVersionMilestones;
		this.maintainer = maintainer;
	}

	public boolean uses(Tracker tracker) {
		return this.tracker.equals(tracker);
	}

	public String getFullName() {
		return fullName != null ? fullName : "Spring Data ".concat(name);
	}

	public String getFolderName() {
		return "spring-data-".concat(name.toLowerCase());
	}

	public String getDependencyProperty() {
		return "springdata.".concat(name.toLowerCase());
	}

	public void doWithAdditionalArtifacts(Consumer<ArtifactCoordinate> consumer) {
		additionalArtifacts.getCoordinates().forEach(consumer);
	}

	/**
	 * Returns whether the current project depends on the given one.
	 *
	 * @param project must not be {@literal null}.
	 * @return
	 */
	public boolean dependsOn(Project project) {

		Assert.notNull(project, "Project must not be null!");

		return dependencies.stream().anyMatch(dependency -> dependency.equals(project) || dependency.dependsOn(project));
	}

	public boolean skipTests() {
		return this.skipTests;
	}

	public Project withDependencies(Project... project) {
		return new Project(key, name, fullName, Arrays.asList(project), tracker, additionalArtifacts, skipTests,
				useShortVersionMilestones, maintainer);
	}

	/**
	 * Returns all dependencies of the current project including transitive ones.
	 *
	 * @return
	 */
	public Set<Project> getDependencies() {

		return dependencies.stream() //
				.flatMap(dependency -> Stream.concat(Stream.of(dependency), dependency.getDependencies().stream())) //
				.collect(Collectors.toSet());
	}

	public String getProjectDescriptor() {
		return this == Projects.BUILD ? "parent/pom.xml" : "pom.xml";
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Project that) {
		return Projects.PROJECTS.indexOf(this) - Projects.PROJECTS.indexOf(that);
	}
}
