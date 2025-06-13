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
package org.springframework.data.release.git;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import org.eclipse.jgit.lib.Repository;

import org.springframework.data.release.model.IterationVersion;
import org.springframework.data.release.model.ModuleIteration;
import org.springframework.data.release.model.Tracker;
import org.springframework.data.release.model.Version;
import org.springframework.data.release.model.VersionAware;
import org.springframework.util.Assert;

/**
 * Value type to represent an SCM branch.
 *
 * @author Oliver Gierke
 * @author Mark Paluch
 */
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Branch implements Comparable<Branch> {

	public static final Branch MAIN = new Branch("main");

	private final String name;

	/**
	 * Creates a new {@link Branch} from the given {@link IterationVersion}.
	 *
	 * @param iterationVersion must not be {@literal null}.
	 * @return
	 */
	public static Branch from(ModuleIteration iterationVersion) {

		Assert.notNull(iterationVersion, "Iteration version must not be null!");

		if (iterationVersion.isBranchVersion() || iterationVersion.isCommercial()) {
			return from((VersionAware) iterationVersion);
		}

		return MAIN;
	}

	/**
	 * Creates a new {@link Branch} from the given {@link IterationVersion}.
	 *
	 * @param iterationVersion must not be {@literal null}.
	 * @return
	 */
	public static Branch from(IterationVersion iterationVersion) {

		Assert.notNull(iterationVersion, "Iteration version must not be null!");

		if (iterationVersion.isBranchVersion()) {
			return from((VersionAware) iterationVersion);
		}

		return MAIN;
	}

	public static Branch from(VersionAware versioned) {
		return from(versioned.getVersion());
	}

	public static Branch from(Version version) {
		return from(version.toString().concat(".x"));
	}

	/**
	 * Creates a new {@link Branch} from the given name. Uses the local part of it only.
	 *
	 * @param name must not be {@literal null} or empty.
	 * @return
	 */
	public static Branch from(String name) {

		int slashIndex = name.lastIndexOf('/');

		return new Branch(slashIndex != -1 ? name.substring(slashIndex + 1) : name);
	}

	/**
	 * Associate the branch with the given {@link Repository} by prefixing the branch name with the remote name.
	 *
	 * @param repo
	 * @return
	 */
	public Branch withRemote(Repository repo) {

		String remote = repo.getRemoteNames().iterator().next();

		if (name.startsWith(remote + "/")) {
			return this;
		}

		return new Branch(remote + "/" + name);
	}

	public boolean isMainBranch() {
		return MAIN.equals(this);
	}

	/**
	 * Returns whether the current branch is an issue branch for the given {@link Tracker}.
	 *
	 * @param tracker must not be {@literal null}.
	 * @return
	 */
	public boolean isIssueBranch(Tracker tracker) {

		Assert.notNull(tracker, "Tracker must not be null!");
		return name.matches(tracker.getTicketPattern());
	}
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */

	@Override
	public String toString() {
		return name;
	}
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */

	@Override
	public int compareTo(Branch o) {
		return name.compareToIgnoreCase(o.name);
	}
}
